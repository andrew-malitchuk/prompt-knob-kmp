package data.ble.impl.source.datasource

import com.juul.kable.Characteristic
import com.juul.kable.Scanner
import com.juul.kable.State
import com.juul.kable.WriteType
import com.juul.kable.characteristicOf
import com.juul.kable.peripheral
import data.ble.api.source.datasource.BleConnection
import data.ble.api.source.resource.BleConnectionStateResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withTimeout

// Custom GATT service (matches firmware ble_server.h)
private const val HID_SERVICE_UUID = "6e400001-b5a3-f393-e0a9-e50e24dcca9e"
private const val CMD_CHAR_UUID = "6e400003-b5a3-f393-e0a9-e50e24dcca9e"
private const val SYNC_CHAR_UUID = "6e400002-b5a3-f393-e0a9-e50e24dcca9e"
private const val DIS_SERVICE_UUID = "0000180a-0000-1000-8000-00805f9b34fb"
private const val FIRMWARE_REVISION_UUID = "00002a26-0000-1000-8000-00805f9b34fb"
private const val OTA_SERVICE_UUID = "6e410001-b5a3-f393-e0a9-e50e24dcca9e"
private const val OTA_CTRL_CHAR_UUID = "6e410002-b5a3-f393-e0a9-e50e24dcca9e"
private const val OTA_DATA_CHAR_UUID = "6e410003-b5a3-f393-e0a9-e50e24dcca9e"
private const val OTA_STATUS_UUID = "6e410004-b5a3-f393-e0a9-e50e24dcca9e"

/**
 * iOS implementation of [BleConnection] backed by Kable (CoreBluetooth).
 *
 * CoreBluetooth handles background reconnection natively when
 * UIBackgroundModes contains "bluetooth-central" in Info.plist.
 */
internal class BleConnectionImpl : BleConnection {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var stateObserverJob: Job? = null

    private val _state = MutableStateFlow(BleConnectionStateResource.Disconnected)
    override val state: StateFlow<BleConnectionStateResource> = _state

    private var kablePeripheral: com.juul.kable.Peripheral? = null
    private var cmdCharacteristic: Characteristic? = null
    private var syncCharacteristic: Characteristic? = null
    private var otaCtrlCharacteristic: Characteristic? = null
    private var otaDataCharacteristic: Characteristic? = null
    private var otaStatusCharacteristic: Characteristic? = null

    override fun observeNotifications(): Flow<ByteArray> =
        _state
            .filter { it == BleConnectionStateResource.Connected }
            .flatMapLatest {
                val p = kablePeripheral ?: return@flatMapLatest emptyFlow()
                val c = cmdCharacteristic ?: return@flatMapLatest emptyFlow()
                p.observe(c)
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
        _state.value = BleConnectionStateResource.Connecting
        try {
            // Scan for the peripheral with the given CoreBluetooth UUID.
            // withTimeout ensures the caller (BleRepositoryImpl.connect → runCatching) receives
            // a TimeoutCancellationException if the device is not advertising within 10 seconds,
            // allowing Splash to fall through to the scan screen rather than hanging forever.
            val advertisement = withTimeout(SCAN_TIMEOUT_MS) {
                Scanner()
                    .advertisements
                    .first { it.identifier.toString() == address }
            }

            val peripheral = scope.peripheral(advertisement)
            kablePeripheral = peripheral

            // Set before connect() so they're ready when Connected state fires.
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
        } catch (t: Throwable) {
            _state.value = BleConnectionStateResource.Disconnected
            throw t
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
        // iOS/CoreBluetooth manages MTU automatically; report the default ATT_MTU
        return 185 // typical CoreBluetooth MTU
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
        peripheral.write(characteristic, data, WriteType.WithoutResponse)
    }

    // CoreBluetooth typically negotiates 185-byte ATT_MTU; subtract 3 bytes GATT overhead.
    override fun otaWriteSize(): Int = 182
}

private const val SCAN_TIMEOUT_MS = 10_000L

private fun State.toConnectionState(): BleConnectionStateResource = when (this) {
    is State.Connecting -> BleConnectionStateResource.Connecting
    is State.Connected -> BleConnectionStateResource.Connected
    is State.Disconnecting -> BleConnectionStateResource.Disconnecting
    is State.Disconnected -> BleConnectionStateResource.Disconnected
}
