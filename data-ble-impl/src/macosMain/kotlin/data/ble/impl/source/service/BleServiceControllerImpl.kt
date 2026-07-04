package data.ble.impl.source.service

import data.ble.api.source.datasource.BleServiceController

/**
 * macOS: CoreBluetooth handles background BLE natively via the bluetooth entitlement.
 * No explicit service lifecycle management is needed.
 */
internal class BleServiceControllerImpl : BleServiceController {
    override fun startConnectionService(deviceAddress: String) = Unit
    override fun stopConnectionService() = Unit
}
