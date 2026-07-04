package data.ble.api.source.datasource

import data.ble.api.source.resource.BleDeviceResource
import kotlinx.coroutines.flow.Flow

/**
 * Scans for nearby BLE peripherals and emits discovered devices as a cold flow.
 *
 * @see BlePermissionChecker
 * @see BleConnection
 */
public interface BleScanner {

    /**
     * Returns a cold [Flow] that emits [BleDeviceResource] entries as they are discovered nearby.
     *
     * The scan starts when the flow is collected and stops when the collector is cancelled.
     * Call [BlePermissionChecker.hasPermissions] before collecting — scanning without
     * the required permissions will throw a platform-specific exception.
     *
     * @return A never-completing [Flow] of discovered [BleDeviceResource] entries. Devices may be
     *   emitted more than once as RSSI updates arrive.
     */
    public fun scan(): Flow<BleDeviceResource>
}
