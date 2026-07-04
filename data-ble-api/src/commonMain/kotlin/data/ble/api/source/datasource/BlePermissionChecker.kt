package data.ble.api.source.datasource

/**
 * Checks and requests the platform-specific permissions required for BLE scanning and connecting.
 *
 * Platform behaviour:
 * - **Android**: requires `BLUETOOTH_SCAN`, `BLUETOOTH_CONNECT`, `ACCESS_FINE_LOCATION`
 * - **iOS/macOS**: `NSBluetoothAlwaysUsageDescription` is implicit via `CBCentralManager`; [hasPermissions] reflects authorization status
 * - **Desktop JVM**: always returns `false` — BLE is not available on the desktop target
 *
 * @see BleScanner
 */
public interface BlePermissionChecker {

    /**
     * Returns `true` if all required BLE permissions are currently granted.
     *
     * Does not trigger a permission prompt; use [requestPermissions] to prompt the user.
     *
     * @return `true` if the app can proceed with [BleScanner.scan] without requesting permissions.
     */
    public suspend fun hasPermissions(): Boolean

    /**
     * Requests all required BLE permissions from the user.
     *
     * On Android, runtime permissions require an `Activity` context and
     * `ActivityResultLauncher`. The data-layer implementation returns the current
     * grant status only; the UI layer is responsible for launching the system dialog
     * and calling this method afterwards to get the updated result.
     * On iOS/macOS it triggers `CBCentralManager` authorization and suspends until
     * the user responds. On Desktop it always returns `false`.
     *
     * @return `true` if all required permissions are currently granted; `false` otherwise.
     */
    public suspend fun requestPermissions(): Boolean
}
