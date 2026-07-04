package data.runtime.api.source.datasource

import data.runtime.api.source.resource.BleSessionResource
import data.runtime.api.source.resource.FirmwareVersionResource
import kotlinx.coroutines.flow.Flow

/**
 * In-memory data source for BLE session state.
 *
 * Holds device identity, firmware version, and battery level for the duration
 * of an active BLE session. All values reset to null on disconnect.
 */
public interface BleSessionDataSource {

    public fun observeDevice(): Flow<BleSessionResource?>
    public suspend fun getDevice(): BleSessionResource?
    public suspend fun setDevice(device: BleSessionResource?)

    public fun observeFirmwareVersion(): Flow<FirmwareVersionResource?>
    public suspend fun getFirmwareVersion(): FirmwareVersionResource?
    public suspend fun setFirmwareVersion(version: FirmwareVersionResource?)

    public fun observeBatteryLevel(): Flow<Int?>
    public suspend fun setBatteryLevel(level: Int?)
}
