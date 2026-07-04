package data.ble.impl.source.datasource

import android.bluetooth.BluetoothGatt
import co.touchlab.kermit.Logger
import com.juul.kable.Characteristic
import com.juul.kable.State
import com.juul.kable.WriteType
import com.juul.kable.characteristicOf
import com.juul.kable.peripheral
import com.juul.kable.write
import data.ble.api.source.datasource.BleConnection
import data.ble.api.source.resource.BleConnectionStateResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

// Custom GATT service (matches firmware ble_server.h)
private const val HID_SERVICE_UUID = "6e400001-b5a3-f393-e0a9-e50e24dcca9e"

// CMD_CHAR: ESP32 → Phone (Notify)
private const val CMD_CHAR_UUID = "6e400003-b5a3-f393-e0a9-e50e24dcca9e"

// SYNC_CHAR: Phone → ESP32 (Write)
private const val SYNC_CHAR_UUID = "6e400002-b5a3-f393-e0a9-e50e24dcca9e"

// Device Information Service — standard BLE DIS (0x180A / 0x2A26)
private const val DIS_SERVICE_UUID = "0000180a-0000-1000-8000-00805f9b34fb"
private const val FIRMWARE_REVISION_UUID = "00002a26-0000-1000-8000-00805f9b34fb"

// OTA GATT service (matches firmware ota_service.h)
private const val OTA_SERVICE_UUID = "6e410001-b5a3-f393-e0a9-e50e24dcca9e"
private const val OTA_CTRL_CHAR_UUID = "6e410002-b5a3-f393-e0a9-e50e24dcca9e"
private const val OTA_DATA_CHAR_UUID = "6e410003-b5a3-f393-e0a9-e50e24dcca9e"
private const val OTA_STATUS_UUID = "6e410004-b5a3-f393-e0a9-e50e24dcca9e"

/**
 * Android implementation of [BleConnection] backed by Kable.
 *
 * Manages connection lifecycle to the knob device, subscribes to CMD_CHAR
 * notifications, and writes to SYNC_CHAR using Write-With-Response.
 */
internal class BleConnectionImpl : BleConnection {

    private val log = Logger.withTag("BleConnectionImpl")
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var stateObserverJob: Job? = null

    private val _state = MutableStateFlow(BleConnectionStateResource.Disconnected)
    override val state: StateFlow<BleConnectionStateResource> = _state

    // Prevents BleConnectionService and BleRepositoryImpl from both calling connect()
    // simultaneously after the user taps a device. Without this, each call creates a
    // separate Kable Peripheral, the device accepts the first and rejects the second
    // (GATT status 19), causing a cascade of GATT_ERROR(133) failures.
    private val connectMutex = Mutex()

    private var kablePeripheral: com.juul.kable.Peripheral? = null
    private var cmdCharacteristic: Characteristic? = null
    private var syncCharacteristic: Characteristic? = null
    private var otaCtrlCharacteristic: Characteristic? = null
    private var otaDataCharacteristic: Characteristic? = null
    private var otaStatusCharacteristic: Characteristic? = null

    // Keeps OTA STATUS CCCD active for the lifetime of the connection. Without this, there is a
    // race between the CCCD write (triggered by the first observeOtaStatus() collector in startOta)
    // and the OTA_BEGIN write — both contend for Kable's serialized execute lock. If OTA_BEGIN wins
    // the lock first, the device sends OTA_READY before CCCD is active and the notification is lost.
    // By pre-subscribing here, CCCD is written during connection setup, well before OTA starts.
    // Kable's Observers class deduplicates: a second observe() call from startOta shares this
    // subscription without re-writing CCCD.
    @Suppress("unused")
    private val otaStatusPrimer: Job = scope.launch {
        _state
            .filter { it == BleConnectionStateResource.Connected }
            .flatMapLatest {
                val p = kablePeripheral ?: return@flatMapLatest emptyFlow<ByteArray>()
                val c = otaStatusCharacteristic ?: return@flatMapLatest emptyFlow()
                log.d { "priming OTA STATUS subscription" }
                p.observe(c)
            }
            .catch { }
            .collect {}
    }

    // Reactive: re-subscribes to CMD_CHAR notifications whenever the connection
    // reaches Connected state. Using flatMapLatest ensures the previous observation
    // is cancelled on disconnect and a fresh one starts on reconnect.
    // This also fixes the case where the flow is subscribed before connect() is called.
    override fun observeNotifications(): Flow<ByteArray> =
        _state
            .filter { it == BleConnectionStateResource.Connected }
            .flatMapLatest {
                val p = kablePeripheral ?: run {
                    log.w { "observeNotifications — peripheral is null, skipping" }
                    return@flatMapLatest emptyFlow()
                }
                val c = cmdCharacteristic ?: run {
                    log.w { "observeNotifications — cmdCharacteristic is null, skipping" }
                    return@flatMapLatest emptyFlow()
                }
                log.d { "subscribing to CMD_CHAR notifications" }
                p.observe(c).onEach { bytes ->
                    log.d { "notification ${bytes.size}B received" }
                }
            }

    override fun observeOtaStatus(): Flow<ByteArray> =
        _state
            .filter { it == BleConnectionStateResource.Connected }
            .flatMapLatest {
                val p = kablePeripheral ?: return@flatMapLatest emptyFlow()
                val c = otaStatusCharacteristic ?: return@flatMapLatest emptyFlow()
                p.observe(c)
            }

    override suspend fun connect(address: String) {
        connectMutex.withLock {
            val s = _state.value
            if (s == BleConnectionStateResource.Connected ||
                s == BleConnectionStateResource.Connecting) {
                return
            }

            _state.value = BleConnectionStateResource.Connecting

            // Connect directly by MAC address — works for bonded devices that are not advertising.
            val peripheral = scope.peripheral(address)
            kablePeripheral = peripheral

            // Set characteristic descriptors BEFORE connect() so they are non-null when Kable
            // emits State.Connected, which fires observeNotifications()'s flatMapLatest.
            // characteristicOf() is a pure descriptor — it does not communicate with the device.
            cmdCharacteristic = characteristicOf(HID_SERVICE_UUID, CMD_CHAR_UUID)
            syncCharacteristic = characteristicOf(HID_SERVICE_UUID, SYNC_CHAR_UUID)
            otaCtrlCharacteristic = characteristicOf(OTA_SERVICE_UUID, OTA_CTRL_CHAR_UUID)
            otaDataCharacteristic = characteristicOf(OTA_SERVICE_UUID, OTA_DATA_CHAR_UUID)
            otaStatusCharacteristic = characteristicOf(OTA_SERVICE_UUID, OTA_STATUS_UUID)

            stateObserverJob?.cancel()
            stateObserverJob = peripheral.state
                .map { it.toConnectionState() }
                .onEach { _state.value = it }
                .catch { _state.value = BleConnectionStateResource.Disconnected }
                .launchIn(scope)

            peripheral.connect()
            negotiateMtuWith(peripheral)

            // Android caches the GATT service table. After a firmware update that adds a new
            // service (OTA), the cache may still show the old table. Detect this by checking
            // whether the main service is present (device identity confirmed) but the OTA
            // service is absent (stale cache). If so, clear the cache via reflection and
            // reconnect so Kable performs a fresh service discovery.
            if (refreshGattCacheIfNeeded(peripheral)) {
                log.d { "GATT cache cleared — reconnecting for fresh service discovery" }
                // Cancel state observer to hide the transient disconnect from upstream observers.
                stateObserverJob?.cancel()
                runCatching { peripheral.disconnect() }
                delay(600L)
                peripheral.connect()
                negotiateMtuWith(peripheral)
                stateObserverJob = peripheral.state
                    .map { it.toConnectionState() }
                    .onEach { _state.value = it }
                    .catch { _state.value = BleConnectionStateResource.Disconnected }
                    .launchIn(scope)
            }
        }
    }

    /**
     * Requests MTU 512 from the peripheral and logs the negotiated value.
     *
     * Kable's [com.juul.kable.AndroidPeripheral.mtu] StateFlow starts at the BLE default of 23
     * and only updates when [com.juul.kable.AndroidPeripheral.requestMtu] is explicitly called.
     * Android's automatic ATT_MTU exchange does not update Kable's StateFlow, so we must call
     * this after every [com.juul.kable.Peripheral.connect] to give [otaWriteSize] a real value.
     */
    private suspend fun negotiateMtuWith(peripheral: com.juul.kable.Peripheral) {
        try {
            val mtu = (peripheral as? com.juul.kable.AndroidPeripheral)?.requestMtu(512) ?: return
            log.d { "MTU negotiated: ${mtu}B (max OTA write: ${mtu - 3}B)" }
        } catch (e: Exception) {
            log.w { "MTU negotiation failed: ${e.message}" }
        }
    }

    /**
     * Returns `true` if the Android GATT cache was successfully cleared.
     *
     * Detects two staleness conditions (both require the main service to be present first):
     * 1. OTA service absent — firmware was updated after the last Android discovery.
     * 2. OTA_DATA_CHAR lacks the Write property (0x08) — cache was built from an older firmware
     *    version that declared OTA_DATA with WRITE_NR only; current firmware has both WRITE and
     *    WRITE_NR. Without refresh, Kable throws NoSuchElementException on WriteType.WithResponse.
     */
    private fun refreshGattCacheIfNeeded(peripheral: com.juul.kable.Peripheral): Boolean {
        val services = peripheral.services ?: return false
        val hasMainService = services.any {
            it.serviceUuid.toString().equals(HID_SERVICE_UUID, ignoreCase = true)
        }
        if (!hasMainService) return false

        val otaService = services.firstOrNull {
            it.serviceUuid.toString().equals(OTA_SERVICE_UUID, ignoreCase = true)
        }

        if (otaService == null) {
            log.w { "OTA service absent — clearing stale GATT cache" }
        } else {
            val otaDataHasWrite = otaService.characteristics.any {
                it.characteristicUuid.toString().equals(OTA_DATA_CHAR_UUID, ignoreCase = true) &&
                    it.properties.write
            }
            if (otaDataHasWrite) return false
            log.w { "OTA_DATA_CHAR lacks Write property — clearing stale GATT cache" }
        }

        return runCatching {
            val gatt = resolveBluetoothGatt(peripheral) ?: run {
                log.e { "BluetoothGatt not accessible via reflection — skipping cache refresh" }
                return false
            }
            val result = BluetoothGatt::class.java.getMethod("refresh").invoke(gatt) as Boolean
            log.d { "BluetoothGatt.refresh() = $result" }
            result
        }.getOrElse { e ->
            log.w { "GATT cache refresh failed (${e::class.simpleName}): ${e.message}" }
            false
        }
    }

    /**
     * Resolves the [BluetoothGatt] held by Kable's internal connection object.
     *
     * Path (verified against Kable 0.32.0 bytecode):
     * `BluetoothDeviceAndroidPeripheral.access$get_connection$p(instance)` → `Connection`
     * `Connection.getBluetoothGatt$core_release()` → `BluetoothGatt`
     */
    private fun resolveBluetoothGatt(peripheral: com.juul.kable.Peripheral): BluetoothGatt? {
        return runCatching {
            val peripheralClass = peripheral.javaClass
            // Kotlin generates a static synthetic accessor for private fields accessed by lambdas.
            val connectionAccessor = peripheralClass.getDeclaredMethod(
                "access\$get_connection\$p",
                peripheralClass,
            )
            connectionAccessor.isAccessible = true
            val connection = connectionAccessor.invoke(null, peripheral) ?: return null

            val gattGetter = connection.javaClass.getMethod("getBluetoothGatt\$core_release")
            gattGetter.isAccessible = true
            gattGetter.invoke(connection) as? BluetoothGatt
        }.getOrElse { e ->
            log.w { "resolveBluetoothGatt reflection failed (${e::class.simpleName}): ${e.message}" }
            null
        }
    }

    override suspend fun disconnect() {
        _state.value = BleConnectionStateResource.Disconnecting
        kablePeripheral?.disconnect()
        kablePeripheral = null
        cmdCharacteristic = null
        syncCharacteristic = null
        otaCtrlCharacteristic = null
        otaDataCharacteristic = null
        otaStatusCharacteristic = null
        _state.value = BleConnectionStateResource.Disconnected
    }

    override suspend fun write(data: ByteArray) {
        val peripheral = checkNotNull(kablePeripheral) { "Not connected" }
        val characteristic = checkNotNull(syncCharacteristic) { "SYNC_CHAR not discovered" }
        peripheral.write(characteristic, data, WriteType.WithResponse)
    }

    override suspend fun negotiateMtu(mtu: Int): Int {
        // Kable 0.32.x negotiates MTU automatically during connect(); this method is a no-op.
        // Use otaWriteSize() to read the actual negotiated MTU via AndroidPeripheral.mtu.
        return mtu
    }

    override suspend fun readFirmwareRevision(): String? = runCatching {
        val peripheral = kablePeripheral ?: return null
        val char = characteristicOf(DIS_SERVICE_UUID, FIRMWARE_REVISION_UUID)
        peripheral.read(char).decodeToString().trim()
    }.getOrNull()

    override suspend fun writeOtaCtrl(data: ByteArray) {
        val peripheral = checkNotNull(kablePeripheral) { "Not connected" }
        val characteristic = checkNotNull(otaCtrlCharacteristic) { "OTA_CTRL_CHAR not discovered" }
        peripheral.write(characteristic, data, WriteType.WithResponse)
    }

    override suspend fun writeOtaData(data: ByteArray) {
        val peripheral = checkNotNull(kablePeripheral) { "Not connected" }
        val characteristic = checkNotNull(otaDataCharacteristic) { "OTA_DATA_CHAR not discovered" }
        // Write-With-Response: ESP32 sends ATT_WRITE_RSP only after esp_ota_write() completes,
        // giving true per-write backpressure. This prevents the silent WNR packet loss that
        // causes CRC failures. The firmware declares OTA_DATA_CHAR with both PROPERTY_WRITE and
        // PROPERTY_WRITE_NR; refreshGattCacheIfNeeded() ensures the cache has the Write property.
        peripheral.write(characteristic, data, WriteType.WithResponse)
    }

    /**
     * Returns ATT_MTU − 3 bytes of GATT overhead, clamped to [20, 512].
     *
     * Reads the live negotiated MTU from Kable's [com.juul.kable.AndroidPeripheral.mtu]
     * StateFlow so the chunk size adapts to the MTU Android and the device negotiate
     * (typically 512 on Android 12+ with a capable peripheral, giving 509-byte payloads).
     */
    override fun otaWriteSize(): Int {
        val mtu = (kablePeripheral as? com.juul.kable.AndroidPeripheral)?.mtu?.value ?: 23
        return (mtu - 3).coerceIn(20, 512)
    }
}

private fun State.toConnectionState(): BleConnectionStateResource = when (this) {
    is State.Connecting -> BleConnectionStateResource.Connecting
    is State.Connected -> BleConnectionStateResource.Connected
    is State.Disconnecting -> BleConnectionStateResource.Disconnecting
    is State.Disconnected -> BleConnectionStateResource.Disconnected
}
