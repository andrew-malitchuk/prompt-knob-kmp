package data.ble.api.source.resource

import data.core.source.resource.Resource

/**
 * A BLE peripheral discovered during a [data.ble.api.source.datasource.BleScanner] scan.
 *
 * Pass [address] to [data.ble.api.source.datasource.BleConnection.connect] to establish
 * a connection. [rssi] can be used to rank devices by proximity before connecting.
 *
 * @property name Advertised name of the device, or `null` if the peripheral did not include it.
 * @property address Platform-specific device identifier: MAC address on Android, UUID string on iOS/macOS.
 * @property rssi Received signal strength in dBm; more negative values indicate weaker signal.
 * @see data.ble.api.source.datasource.BleScanner
 * @see data.ble.api.source.datasource.BleConnection
 */
public data class BleDeviceResource(
    val name: String?,
    val address: String,
    val rssi: Int,
) : Resource
