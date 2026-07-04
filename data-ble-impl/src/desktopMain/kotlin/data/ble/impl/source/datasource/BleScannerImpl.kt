package data.ble.impl.source.datasource

import data.ble.api.source.datasource.BleScanner
import data.ble.api.source.resource.BleDeviceResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

/** Desktop stub: BLE is not available on the JVM Desktop target. */
internal class BleScannerImpl : BleScanner {
    override fun scan(): Flow<BleDeviceResource> = emptyFlow()
}
