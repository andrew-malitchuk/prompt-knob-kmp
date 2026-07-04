package data.repository.impl.source

import co.touchlab.kermit.Logger
import data.ble.api.core.codec.OtaOpcode
import domain.core.source.model.ClaudeState
import data.ble.api.source.datasource.BleConnection
import data.ble.api.source.datasource.BlePermissionChecker
import data.ble.api.source.datasource.BleScanner
import data.ble.api.source.datasource.BleServiceController
import data.ble.api.source.resource.BleConnectionStateResource
import data.ble.api.core.resource.CommandEntry
import data.ble.api.core.resource.SyncAckResult
import data.ble.api.core.resource.YamkProtocol
import data.preference.api.source.datasource.LastDevicePreferenceSource
import data.preference.api.source.model.LastDevicePreference
import data.executor.api.source.executor.CommandExecutor
import data.repository.impl.core.mapper.BleSessionMapper
import data.repository.impl.core.mapper.CommandNodeCacheMapper
import data.repository.impl.core.mapper.FirmwareVersionMapper
import data.runtime.api.source.datasource.BleSessionDataSource
import data.runtime.api.source.datasource.CommandCacheDataSource
import data.runtime.api.source.resource.BleSessionResource
import domain.core.source.model.BleDeviceModel
import domain.core.source.model.CommandNodeModel
import domain.core.source.model.ConnectionStateModel
import domain.core.source.model.DeviceConnectionModel
import domain.core.source.model.FirmwareVersionModel
import domain.core.source.model.OtaErrorCodeModel
import domain.core.source.model.OtaStateModel
import domain.core.source.parser.parseFirmwareVersion
import domain.core.source.monad.Optional
import domain.repository.api.source.repository.BleRepository
import domain.repository.api.source.repository.CommandHistoryRepository
import domain.repository.api.source.repository.CommandRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.yield
import kotlin.concurrent.Volatile

/**
 * Default implementation of [BleRepository].
 *
 * Composes [BleScanner], [BleConnection], [BlePermissionChecker], and
 * [YamkProtocolImpl] to provide a unified BLE interface to the domain layer.
 *
 * Command lookup for [observeSelectedCommand] uses the last synced command list
 * cached in [CommandCacheDataSource]. Commands unknown to the cache are silently dropped.
 */
internal class BleRepositoryImpl(
    private val scanner: BleScanner,
    private val connection: BleConnection,
    private val permissionChecker: BlePermissionChecker,
    private val protocolHandler: YamkProtocol,
    private val serviceController: BleServiceController,
    private val lastDevicePreferenceSource: LastDevicePreferenceSource,
    private val commandExecutor: CommandExecutor,
    private val commandRepository: CommandRepository,
    private val commandHistoryRepository: CommandHistoryRepository,
    private val bleSessionDataSource: BleSessionDataSource,
    private val commandCacheDataSource: CommandCacheDataSource,
) : BleRepository {

    private val log = Logger.withTag("BleRepositoryImpl")
    // CoroutineExceptionHandler is required for Kotlin/Native (macOS/iOS): without it,
    // unhandled exceptions in SupervisorJob children call abort() instead of being logged.
    private val scope = CoroutineScope(
        SupervisorJob() + Dispatchers.Default +
            CoroutineExceptionHandler { _, t -> log.e { "unhandled BleRepository error: ${t.message}" } }
    )
    private var commandObserverJob: Job? = null
    private var commandSyncJob: Job? = null

    // Serializes all multi-frame protocol exchanges (SYNC / CLEAR). The reactive
    // Room observer and explicit UI-triggered syncs run on independent coroutines;
    // without this lock their frames interleave on the wire and the device rejects
    // the batch with SYNC_ACK status=0x01 (MALFORMED).
    private val protocolMutex = Mutex()
    private var versionObserverJob: Job? = null
    private var batteryObserverJob: Job? = null

    @Volatile private var skipAutoReconnect = false

    init {
        // Reactively restart command observers on every Connected transition.
        // This covers two cases that bypass BleRepositoryImpl.connect():
        //  1. App restart when BleConnectionService is already running (Splash skips connect).
        //  2. Auto-reconnect by BleConnectionService after a connection drop.
        // startCommandObserver / startCommandSyncObserver are idempotent (cancel + restart),
        // so calling them here as well as inside connect() is safe.
        connection.state
            .onEach { state ->
                if (state == BleConnectionStateResource.Connected) {
                    // Restore connectedDevice from prefs if it wasn't set by connect().
                    // This handles service-based reconnects that bypass connect().
                    if (bleSessionDataSource.getDevice() == null) {
                        val prefs = lastDevicePreferenceSource.getData()
                        val savedAddress = prefs.address
                        if (savedAddress != null) {
                            bleSessionDataSource.setDevice(
                                BleSessionResource(address = savedAddress, name = prefs.name ?: savedAddress)
                            )
                        }
                    }
                    startCommandObserver()
                    startCommandSyncObserver()
                    startVersionObserver()
                    startBatteryObserver()
                    scope.launch { readDisFallbackIfMissing() }
                    scope.launch {
                        runCatching { protocolHandler.sendSessionHello() }
                            .onFailure { log.w { "SESSION_HELLO failed: ${it.message}" } }
                    }
                } else if (state == BleConnectionStateResource.Disconnected) {
                    bleSessionDataSource.setDevice(null)
                    bleSessionDataSource.setFirmwareVersion(null)
                    bleSessionDataSource.setBatteryLevel(null)
                    versionObserverJob?.cancel()
                    versionObserverJob = null
                    batteryObserverJob?.cancel()
                    batteryObserverJob = null
                }
            }
            .catch { log.e { "connection state observer error: ${it.message}" } }
            .launchIn(scope)
    }

    override fun observeConnectionState(): Flow<DeviceConnectionModel> =
        combine(connection.state, bleSessionDataSource.observeDevice()) { state, session ->
            DeviceConnectionModel(
                device = session?.let { BleSessionMapper.toModel.map(it) },
                state = state.toDomainState(),
            )
        }

    override fun scanForDevices(): Flow<BleDeviceModel> =
        scanner.scan()
            .map { bleDevice ->
                BleDeviceModel(
                    name = bleDevice.name,
                    address = bleDevice.address,
                    rssi = bleDevice.rssi,
                )
            }
            .catch { t ->
                // Translate the data-layer BT disabled signal to a domain exception so the
                // presentation layer can catch it without depending on data-ble-api directly.
                if (t is data.ble.api.source.exception.BluetoothDisabledException) {
                    throw domain.core.source.exception.BluetoothUnavailableException()
                }
                throw t
            }

    override suspend fun connect(address: String, name: String?): Optional = runCatching {
        connection.connect(address)
        // TODO: read the actual negotiated MTU from Kable once the API exposes it,
        //  then call protocolHandler.updateMtu(). Until then, the handler uses the
        //  safe BLE default of 23 bytes per chunk.
        val resolvedName = name?.takeIf { it.isNotBlank() }
            ?: lastDevicePreferenceSource.getData().name?.takeIf { it.isNotBlank() }
            ?: address
        bleSessionDataSource.setDevice(BleSessionResource(address = address, name = resolvedName))
        lastDevicePreferenceSource.setData(LastDevicePreference(address = address, name = resolvedName))
        serviceController.startConnectionService(address)
        startCommandObserver()
        // Reactively observe Room: the first emission syncs the full list on
        // connect; every subsequent emission (create / edit / delete) re-syncs
        // automatically. Failures are swallowed so a bad sync never drops the
        // connection or prevents a save from completing.
        startCommandSyncObserver()
        // Start observers immediately so firmware notifications sent right after
        // auth-complete are captured before the async init-block handler fires.
        startVersionObserver()
        startBatteryObserver()
    }

    override suspend fun disconnect(): Optional = runCatching {
        serviceController.stopConnectionService()
        commandObserverJob?.cancel()
        commandObserverJob = null
        commandSyncJob?.cancel()
        commandSyncJob = null
        versionObserverJob?.cancel()
        versionObserverJob = null
        batteryObserverJob?.cancel()
        batteryObserverJob = null
        connection.disconnect()
        bleSessionDataSource.setDevice(null)
        bleSessionDataSource.setFirmwareVersion(null)
        bleSessionDataSource.setBatteryLevel(null)
    }

    override fun markSkipAutoReconnect() {
        skipAutoReconnect = true
    }

    override fun consumeSkipAutoReconnect(): Boolean {
        val was = skipAutoReconnect
        skipAutoReconnect = false
        return was
    }

    override suspend fun getLastConnectedDevice(): String? =
        lastDevicePreferenceSource.getData().address

    override suspend fun clearLastConnectedDevice(): Optional = runCatching {
        lastDevicePreferenceSource.setData(LastDevicePreference(address = null))
    }

    private fun startCommandObserver() {
        commandObserverJob?.cancel()
        commandObserverJob = protocolHandler.observeCommandSelected()
            .onEach { event ->
                val cachedResource = commandCacheDataSource.getCacheSnapshot()[event.cmdId]
                val node = cachedResource?.let { CommandNodeCacheMapper.toModel.map(it) } ?: CommandNodeModel(
                    id = event.cmdId,
                    parentId = 0,
                    label = "CMD ${event.cmdId}",
                    isFolder = false,
                    sortOrder = 0,
                )
                commandCacheDataSource.emitSelectedCommand(CommandNodeCacheMapper.toResource.map(node))
                commandCacheDataSource.emitRawCommandId(event.cmdId)
                if (cachedResource != null) {
                    val result = runCatching { commandExecutor.execute(node) }
                    commandHistoryRepository.logExecution(node, result.isSuccess)
                }
            }
            // catch must be AFTER onEach to also cover exceptions thrown inside the handler.
            // On Kotlin/Native, uncaught exceptions in launchIn(scope) call abort().
            .catch { e -> log.e { "command observer error: ${e.message}" } }
            .launchIn(scope)
    }

    private fun startCommandSyncObserver() {
        commandSyncJob?.cancel()
        commandSyncJob = commandRepository.observeAll()
            .conflate() // Drop intermediate emissions while a sync is in flight; only process the latest snapshot.
            .onEach { commands ->
                if (commands.isEmpty()) {
                    log.d { "skipping sync — no commands in Room" }
                    return@onEach
                }
                log.d { "syncing ${commands.size} commands" }
                syncCommands(commands)
                    .onSuccess { log.d { "SYNC_ACK OK" } }
                    .onFailure { e -> log.e { "SYNC_ACK ERROR — ${e.message}" } }
            }
            // catch must be AFTER onEach to also cover exceptions thrown inside the handler.
            // On Kotlin/Native, uncaught exceptions in launchIn(scope) call abort().
            .catch { e -> log.e { "DB error — ${e.message}" } }
            .launchIn(scope)
    }

    override suspend fun clearCommands(): Optional = runCatching {
        protocolMutex.withLock {
            val result = protocolHandler.clearCommands()
            if (result is SyncAckResult.Error) {
                error("CLEAR_COMMANDS ACK error: status=0x${result.statusCode.toString(16)}")
            }
        }
    }

    override suspend fun syncCommands(commands: List<CommandNodeModel>): Optional = runCatching {
        protocolMutex.withLock {
        // Build cache from ALL commands so CMD_SELECTED can resolve any cmd_id,
        // including virtual CW/CCW children and media screen virtual children.
        commandCacheDataSource.setCache(
            commands.associateBy { it.id }.mapValues { (_, v) -> CommandNodeCacheMapper.toResource.map(v) }
        )

        // Virtual children are stored under a rotary/media-screen leaf's id as parentId.
        // They are phone-only: commandCache resolves them for execution, but they are never
        // sent in the SYNC payload.
        val rotaryLeafIds = commands.filter { it.isRotary && !it.isFolder }.map { it.id }.toSet()
        val mediaScreenLeafIds = commands.filter { it.isMediaScreen && !it.isFolder }.map { it.id }.toSet()
        val virtualChildIds = commands
            .filter { it.parentId in rotaryLeafIds || it.parentId in mediaScreenLeafIds }
            .map { it.id }
            .toSet()

        val entries = mutableListOf<CommandEntry>()
        for (node in commands) {
            when {
                // Direct rotary leaf (firmware spec v2): flags=0x02, payload includes [cwCmdId][ccwCmdId].
                node.isRotary && !node.isFolder -> {
                    if (node.cwCmdId == 0 || node.ccwCmdId == 0) {
                        log.w { "ROTARY_SKIP: '${node.label}' (id=${node.id}) has no cwCmdId/ccwCmdId — skipped from sync" }
                    } else {
                        entries.add(
                            CommandEntry(
                                id = node.id,
                                parentId = node.parentId,
                                isFolder = false,
                                isRotary = true,
                                sortOrder = node.sortOrder,
                                label = node.label,
                                cwCmdId = node.cwCmdId,
                                ccwCmdId = node.ccwCmdId,
                            )
                        )
                    }
                }
                // Media screen leaf (firmware spec v3): flags=0x04, payload includes 5 extra id bytes.
                node.isMediaScreen && !node.isFolder -> {
                    if (node.cwCmdId == 0 || node.ccwCmdId == 0 || node.playPauseCmdId == 0 || node.prevCmdId == 0 || node.nextCmdId == 0) {
                        log.w { "MEDIA_SCREEN_SKIP: '${node.label}' (id=${node.id}) has incomplete media ids — skipped from sync" }
                    } else {
                        entries.add(
                            CommandEntry(
                                id = node.id,
                                parentId = node.parentId,
                                isFolder = false,
                                isMediaScreen = true,
                                sortOrder = node.sortOrder,
                                label = node.label,
                                cwCmdId = node.cwCmdId,
                                ccwCmdId = node.ccwCmdId,
                                playPauseCmdId = node.playPauseCmdId,
                                prevCmdId = node.prevCmdId,
                                nextCmdId = node.nextCmdId,
                            )
                        )
                    }
                }
                // Agent screen leaf (firmware spec v4): flags=0x08, no extra payload bytes.
                node.isAgentScreen && !node.isFolder -> {
                    entries.add(
                        CommandEntry(
                            id = node.id,
                            parentId = node.parentId,
                            isFolder = false,
                            isAgentScreen = true,
                            sortOrder = node.sortOrder,
                            label = node.label,
                        )
                    )
                }
                // Virtual children are phone-only — kept in cache, never synced.
                node.id in virtualChildIds -> { /* phone-only, skip BLE sync */ }
                // Everything else (normal leaves and folders) syncs normally.
                else -> entries.add(
                    CommandEntry(
                        id = node.id,
                        parentId = node.parentId,
                        isFolder = node.isFolder,
                        isRotary = false,
                        sortOrder = node.sortOrder,
                        label = node.label,
                    )
                )
            }
        }

        val result = protocolHandler.syncCommands(entries)
        if (result is SyncAckResult.Error) {
            error("SYNC_ACK error: status=0x${result.statusCode.toString(16)}")
        }
        }
    }

    override fun observeSelectedCommand(): Flow<CommandNodeModel> =
        commandCacheDataSource.observeSelectedCommand().map { CommandNodeCacheMapper.toModel.map(it) }

    override suspend fun sendShowApproval(): Optional = runCatching {
        protocolHandler.sendShowApproval()
    }

    override suspend fun sendShowChoice(options: List<String>): Optional = runCatching {
        protocolHandler.sendShowChoice(options)
    }

    override suspend fun sendNotify(message: String, level: Int): Optional = runCatching {
        protocolHandler.sendNotify(message, level)
    }

    override suspend fun sendClaudeState(state: ClaudeState): Optional = runCatching {
        protocolHandler.sendClaudeState(state)
    }

    override fun observeCommandSelectedRaw(): Flow<Int> =
        commandCacheDataSource.observeRawCommandId()

    override suspend fun checkPermissions(): Boolean = permissionChecker.hasPermissions()

    override suspend fun requestPermissions(): Boolean = permissionChecker.requestPermissions()

    override fun observeFirmwareVersion(): Flow<FirmwareVersionModel?> =
        bleSessionDataSource.observeFirmwareVersion().map { resource ->
            resource?.let { FirmwareVersionMapper.toModel.map(it) }
        }

    override fun observeBatteryLevel(): Flow<Int?> = bleSessionDataSource.observeBatteryLevel()

    override fun startOta(firmware: ByteArray, version: FirmwareVersionModel): Flow<OtaStateModel> = channelFlow {
        log.d { "OTA start — firmware ${firmware.size}B v${version.major}.${version.minor}.${version.patch}" }
        // Suspend command sync to free BLE bandwidth during OTA; restored in finally.
        commandSyncJob?.cancel()
        commandSyncJob = null
        val statusQueue = Channel<ByteArray>(Channel.UNLIMITED)
        val collectJob = launch {
            connection.observeOtaStatus()
                .onEach { packet -> statusQueue.send(packet) }
                .catch { e -> log.e { "OTA status observer error: ${e.message}" } }
                .collect {}
        }
        // If the BLE link drops while we're blocked on statusQueue.receive(), the status flow
        // stops emitting but never closes the channel — causing an infinite suspend. Monitor the
        // connection state and close the queue with a terminal exception on disconnect so every
        // statusQueue.receive() call unblocks immediately with CancellationException.
        val disconnectWatchJob = launch {
            connection.state
                .filter { it == BleConnectionStateResource.Disconnected }
                .first()
            log.w { "OTA — BLE disconnected, closing status queue" }
            statusQueue.close(CancellationException("BLE disconnected during OTA"))
        }
        // Yield once so the subscription coroutine above gets scheduled on Dispatchers.Default
        // and starts the CCCD write BEFORE we send OTA_BEGIN. Without this, computeCrc32 (CPU-bound)
        // can run to completion without ever yielding, causing OTA_READY to arrive before the
        // notification subscription is active and be missed by the Android BLE stack.
        yield()
        try {
            send(OtaStateModel.Waiting)
            log.d { "OTA Waiting — emitted" }

            val totalSize = firmware.size
            val crc32 = computeCrc32(firmware)

            // Diagnostic: compute CRC incrementally on chunks, exactly as the firmware
            // calls esp_rom_crc32_le chunk-by-chunk, to confirm both approaches agree.
            val chunkSizeDiag = connection.otaWriteSize()
            var diagCrc = 0xFFFFFFFFL
            for (chunk in firmware.chunked(chunkSizeDiag)) {
                for (b in chunk) {
                    diagCrc = diagCrc xor (b.toLong() and 0xFF)
                    repeat(8) {
                        diagCrc = if (diagCrc and 1L != 0L) (diagCrc ushr 1) xor 0xEDB88320L else diagCrc ushr 1
                    }
                }
            }
            val incCrc = diagCrc.inv() and 0xFFFFFFFFL
            val hexFirst = firmware.take(16).joinToString(" ") { it.toUByte().toString(16).padStart(2, '0') }
            log.d { "OTA firmware size=${firmware.size} first16=[$hexFirst]" }
            log.d { "OTA CRC whole=0x${crc32.toString(16).padStart(8, '0')} incremental=0x${incCrc.toString(16).padStart(8, '0')} match=${crc32 == incCrc}" }

            log.d { "OTA BEGIN — size=$totalSize crc32=0x${crc32.toString(16)}" }
            connection.writeOtaCtrl(buildOtaBeginPacket(totalSize, crc32, version))

            // Wait for OTA_READY or an early OTA_ERROR
            var gotReady = false
            while (!gotReady) {
                val packet = statusQueue.receive()
                if (packet.size < 2) continue
                when (packet[1].toUByte()) {
                    OtaOpcode.OTA_READY -> {
                        gotReady = true
                        log.d { "OTA READY — starting data transfer" }
                    }
                    OtaOpcode.OTA_ERROR -> {
                        val code = parseOtaErrorCode(packet)
                        log.e { "OTA ERROR before READY — code=$code" }
                        send(OtaStateModel.Error(code))
                        return@channelFlow
                    }
                    else -> Unit
                }
            }

            // Send firmware chunks. On Android, writeOtaData() uses Write-With-Response: the ESP32
            // sends ATT_WRITE_RSP only after esp_ota_write() completes (~10 ms/flash-page), giving
            // natural per-write backpressure with zero data loss. On iOS, Write-Without-Response is
            // used (OTA_DATA_CHAR has WRITE_NR; iOS CoreBluetooth manages flow control differently).
            // Chunk size = negotiated MTU − 3 bytes GATT overhead.
            val chunkSize = connection.otaWriteSize()
            log.d { "OTA chunk size: ${chunkSize}B (MTU=${chunkSize + 3})" }
            var bytesSent = 0L
            val totalLong = totalSize.toLong()
            for (chunk in firmware.chunked(chunkSize)) {
                connection.writeOtaData(chunk)
                bytesSent += chunk.size
                send(OtaStateModel.Receiving(bytesSent, totalLong))
            }

            log.d { "OTA data transfer complete — sending END" }
            connection.writeOtaCtrl(byteArrayOf(0x00, OtaOpcode.OTA_END.toByte()))
            send(OtaStateModel.Verifying)

            // Wait for OTA_DONE, OTA_PROGRESS, or OTA_ERROR
            while (true) {
                val packet = statusQueue.receive()
                if (packet.size < 2) continue
                when (packet[1].toUByte()) {
                    OtaOpcode.OTA_DONE -> {
                        log.d { "OTA DONE" }
                        send(OtaStateModel.Done)
                        return@channelFlow
                    }
                    OtaOpcode.OTA_PROGRESS -> {
                        if (packet.size >= 6) {
                            val confirmed = (packet[2].toLong() and 0xFF) or
                                ((packet[3].toLong() and 0xFF) shl 8) or
                                ((packet[4].toLong() and 0xFF) shl 16) or
                                ((packet[5].toLong() and 0xFF) shl 24)
                            log.d { "OTA PROGRESS — device confirmed ${confirmed}B of ${totalLong}B" }
                            send(OtaStateModel.Receiving(confirmed, totalLong))
                        }
                    }
                    OtaOpcode.OTA_ERROR -> {
                        val code = parseOtaErrorCode(packet)
                        log.e { "OTA ERROR after END — code=$code" }
                        send(OtaStateModel.Error(code))
                        return@channelFlow
                    }
                    else -> Unit
                }
            }
        } catch (e: CancellationException) {
            log.d { "OTA cancelled — sending ABORT" }
            runCatching { connection.writeOtaCtrl(byteArrayOf(0x00, OtaOpcode.OTA_ABORT.toByte())) }
            throw e
        } catch (e: Exception) {
            log.e { "OTA unexpected error: ${e::class.simpleName} — ${e.message}" }
            throw e
        } finally {
            disconnectWatchJob.cancel()
            collectJob.cancel()
            statusQueue.close()
            // Resume command sync now that the BLE channel is free again.
            startCommandSyncObserver()
        }
    }

    private fun startVersionObserver() {
        versionObserverJob?.cancel()
        versionObserverJob = protocolHandler.observeVersionInfo()
            .onEach { fw -> bleSessionDataSource.setFirmwareVersion(FirmwareVersionMapper.toResource.map(fw)) }
            .catch { e -> log.e { "version observer error: ${e.message}" } }
            .launchIn(scope)
    }

    private fun startBatteryObserver() {
        batteryObserverJob?.cancel()
        batteryObserverJob = protocolHandler.observeBatteryLevel()
            .onEach { pct -> bleSessionDataSource.setBatteryLevel(pct) }
            .catch { e -> log.e { "battery observer error: ${e.message}" } }
            .launchIn(scope)
    }

    private suspend fun readDisFallbackIfMissing() {
        if (bleSessionDataSource.getFirmwareVersion() != null) return
        val raw = connection.readFirmwareRevision() ?: return
        if (bleSessionDataSource.getFirmwareVersion() != null) return
        parseFirmwareVersion(raw)?.let { bleSessionDataSource.setFirmwareVersion(FirmwareVersionMapper.toResource.map(it)) }
    }
}

private fun BleConnectionStateResource.toDomainState(): ConnectionStateModel = when (this) {
    BleConnectionStateResource.Disconnected -> ConnectionStateModel.Disconnected
    BleConnectionStateResource.Connecting -> ConnectionStateModel.Connecting
    BleConnectionStateResource.Connected -> ConnectionStateModel.Connected
    BleConnectionStateResource.Disconnecting -> ConnectionStateModel.Disconnecting
}

private fun buildOtaBeginPacket(totalSize: Int, crc32: Long, version: FirmwareVersionModel): ByteArray {
    val packet = ByteArray(13)
    packet[0] = 0x00  // header: MORE=0, SEQ=0
    packet[1] = OtaOpcode.OTA_BEGIN.toByte()
    packet[2] = (totalSize and 0xFF).toByte()
    packet[3] = ((totalSize ushr 8) and 0xFF).toByte()
    packet[4] = ((totalSize ushr 16) and 0xFF).toByte()
    packet[5] = ((totalSize ushr 24) and 0xFF).toByte()
    packet[6] = (crc32 and 0xFF).toByte()
    packet[7] = ((crc32 ushr 8) and 0xFF).toByte()
    packet[8] = ((crc32 ushr 16) and 0xFF).toByte()
    packet[9] = ((crc32 ushr 24) and 0xFF).toByte()
    packet[10] = version.major.toByte()
    packet[11] = version.minor.toByte()
    packet[12] = version.patch.toByte()
    return packet
}

// CRC32/ISO-HDLC — same polynomial as java.util.zip.CRC32 (0xEDB88320 reflected).
private fun computeCrc32(data: ByteArray): Long {
    var crc = 0xFFFFFFFFL
    for (b in data) {
        crc = crc xor (b.toLong() and 0xFF)
        repeat(8) {
            crc = if (crc and 1L != 0L) (crc ushr 1) xor 0xEDB88320L else crc ushr 1
        }
    }
    return crc.inv() and 0xFFFFFFFFL
}

private fun parseOtaErrorCode(packet: ByteArray): OtaErrorCodeModel =
    if (packet.size < 3) OtaErrorCodeModel.BAD_BEGIN
    else when (packet[2].toInt() and 0xFF) {
        0x01 -> OtaErrorCodeModel.CRC
        0x02 -> OtaErrorCodeModel.FLASH
        0x04 -> OtaErrorCodeModel.TOO_LARGE
        0x05 -> OtaErrorCodeModel.BATTERY
        0x06 -> OtaErrorCodeModel.BAD_BEGIN
        0x07 -> OtaErrorCodeModel.DISCONNECTED
        else -> OtaErrorCodeModel.BAD_BEGIN
    }

private fun ByteArray.chunked(size: Int): List<ByteArray> {
    if (isEmpty()) return emptyList()
    val chunks = mutableListOf<ByteArray>()
    var offset = 0
    while (offset < this.size) {
        val end = minOf(offset + size, this.size)
        chunks.add(copyOfRange(offset, end))
        offset = end
    }
    return chunks
}
