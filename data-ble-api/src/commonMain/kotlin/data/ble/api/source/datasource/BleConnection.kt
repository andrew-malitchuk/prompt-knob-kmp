package data.ble.api.source.datasource

import data.ble.api.source.resource.BleConnectionStateResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * Manages a BLE connection to a single peripheral (the knob device).
 *
 * Implementations are platform-specific: Kable on Android/iOS/macOS,
 * no-op stub on Desktop JVM. Obtain a device address from [BleScanner]
 * before calling [connect].
 *
 * @see BleScanner
 * @see BleServiceController
 */
public interface BleConnection {

    /**
     * Current connection state as a hot [StateFlow].
     *
     * Always holds the latest [BleConnectionStateResource]; never null.
     */
    public val state: StateFlow<BleConnectionStateResource>

    /**
     * Returns a cold [Flow] that emits raw notification bytes received on CMD_CHAR.
     *
     * Active only while connected; emits nothing while disconnected. Subscribe
     * after [connect] returns to avoid missing the first notification.
     *
     * @return A flow of raw byte arrays; each emission is one complete BLE notification.
     */
    public fun observeNotifications(): Flow<ByteArray>

    /**
     * Connects to the peripheral identified by [address].
     *
     * Suspends until the connection is established or throws on failure.
     * Use [BleScanner.scan] to obtain a valid [address].
     *
     * @param address Platform-specific device identifier from [data.ble.api.source.resource.BleDeviceResource.address].
     * @throws Exception if the connection attempt fails (platform-specific exception type).
     */
    public suspend fun connect(address: String)

    /**
     * Gracefully disconnects from the currently connected peripheral.
     *
     * Suspends until disconnection is complete. Safe to call when already disconnected.
     */
    public suspend fun disconnect()

    /**
     * Writes [data] to SYNC_CHAR using Write-With-Response.
     *
     * Suspends until the peripheral acknowledges the write.
     * Chunks must fit within the negotiated MTU minus 3 bytes GATT overhead.
     *
     * @param data Raw bytes to write. Must not exceed `negotiatedMtu - 3` bytes.
     * @throws Exception if the write fails or the device is not connected.
     */
    public suspend fun write(data: ByteArray)

    /**
     * Requests an MTU of [mtu] bytes from the peripheral.
     *
     * The granted MTU may be lower than requested depending on the peripheral's capability.
     * Call this once after [connect] and before the first [write].
     *
     * @param mtu Desired MTU size in bytes.
     * @return The MTU actually granted by the peripheral.
     */
    public suspend fun negotiateMtu(mtu: Int): Int

    /**
     * Reads the Device Information Service Firmware Revision String (service 0x180A,
     * characteristic 0x2A26) as a UTF-8 string.
     *
     * Returns null if the DIS service is not available, the read fails, or BLE is
     * not supported on this platform. Callers treat this as a best-effort fallback
     * to the VERSION_INFO notify (opcode 0xF1).
     */
    public suspend fun readFirmwareRevision(): String?

    /**
     * Writes [data] to OTA_CTRL_CHAR using Write-With-Response.
     *
     * Used for OTA control frames: OTA_BEGIN, OTA_END, OTA_ABORT.
     * The device acknowledges each write before the next frame is sent.
     */
    public suspend fun writeOtaCtrl(data: ByteArray)

    /**
     * Writes [data] to OTA_DATA_CHAR.
     *
     * Android uses Write-With-Response: the ESP32 sends ATT_WRITE_RSP only after
     * [esp_ota_write] completes (~10 ms/flash-page), giving natural per-write backpressure
     * and guaranteed delivery without packet loss or CRC failures.
     * iOS uses Write-Without-Response (OTA_DATA_CHAR also has PROPERTY_WRITE_NR).
     * Chunk size must not exceed [otaWriteSize] bytes.
     */
    public suspend fun writeOtaData(data: ByteArray)

    /**
     * Returns the maximum safe payload size for a single [writeOtaData] call.
     *
     * Derived from the negotiated ATT_MTU: `mtu - 3` bytes of GATT overhead.
     * Write-Without-Response payloads larger than this are silently truncated by
     * the BLE controller, causing data loss and CRC failures.
     *
     * Platform implementations that can read the live MTU should override this.
     * The default is 20 bytes (BLE minimum MTU of 23, minus 3 bytes overhead).
     */
    public fun otaWriteSize(): Int = 20

    /**
     * Returns a cold [Flow] that emits raw notification bytes received on OTA_STATUS_CHAR.
     *
     * Emits nothing while disconnected. Subscribe before sending OTA_BEGIN to avoid
     * missing OTA_READY. Each emission is one complete BLE notification.
     */
    public fun observeOtaStatus(): Flow<ByteArray>
}
