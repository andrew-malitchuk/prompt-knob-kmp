package data.ble.api.source.datasource

/**
 * Controls the platform-specific background BLE service lifecycle.
 *
 * Platform behaviour:
 * - **Android**: starts/stops a foreground `Service` that keeps the BLE connection alive
 *   when the app is backgrounded.
 * - **iOS/macOS**: CoreBluetooth handles background connectivity natively — both methods
 *   are no-ops.
 * - **Desktop JVM**: no-op.
 *
 * @see BleConnection
 */
public interface BleServiceController {

    /**
     * Starts the background BLE connection service for the given device address.
     *
     * Safe to call even if the service is already running — duplicate starts are ignored.
     *
     * @param deviceAddress Platform-specific device identifier to connect to in the background.
     */
    public fun startConnectionService(deviceAddress: String)

    /**
     * Stops the background BLE connection service and disconnects from the device.
     *
     * Safe to call when the service is not running.
     */
    public fun stopConnectionService()
}
