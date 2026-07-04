package data.ble.impl.source.datasource

import com.benasher44.uuid.uuidFrom
import com.juul.kable.Filter
import com.juul.kable.Scanner
import com.juul.kable.logs.Logging
import data.ble.api.source.datasource.BleScanner
import data.ble.api.source.resource.BleDeviceResource
import data.ble.impl.source.protocol.PromptKnobBle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * macOS implementation of [BleScanner] backed by Kable (CoreBluetooth).
 *
 * Filters by the prompt-knob GATT service UUID so CoreBluetooth only delivers
 * advertisements from our hardware. This maps to `CBCentralManager
 * .scanForPeripherals(withServices:)` and is the recommended Apple-platform
 * filter strategy — manufacturer-data filtering is not available on CoreBluetooth.
 *
 * The app must have the `com.apple.security.device.bluetooth` entitlement
 * and, on macOS 12+, the user must grant Bluetooth permission via System Settings.
 */
internal class BleScannerImpl : BleScanner {

    override fun scan(): Flow<BleDeviceResource> =
        Scanner {
            filters = listOf(Filter.Service(uuidFrom(PromptKnobBle.SERVICE_UUID)))
            logging { level = Logging.Level.Warnings }
        }.advertisements.map { advertisement ->
            val address = advertisement.identifier.toString()
            BleDeviceResource(
                name = advertisement.name?.takeIf { it.isNotBlank() } ?: address,
                address = address,
                rssi = advertisement.rssi,
            )
        }
}
