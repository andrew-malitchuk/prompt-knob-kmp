package data.ble.impl.source.service

import data.ble.api.source.datasource.BleServiceController

/** Desktop: BLE is not supported, so service control is a no-op. */
internal class BleServiceControllerImpl : BleServiceController {
    override fun startConnectionService(deviceAddress: String) = Unit
    override fun stopConnectionService() = Unit
}
