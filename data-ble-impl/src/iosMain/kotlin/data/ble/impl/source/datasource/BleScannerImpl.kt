package data.ble.impl.source.datasource

import com.juul.kable.Scanner
import com.juul.kable.logs.Logging
import data.ble.api.source.datasource.BleScanner
import data.ble.api.source.resource.BleDeviceResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

/**
 * iOS implementation of [BleScanner] backed by Kable (CoreBluetooth).
 *
 * CoreBluetooth does not expose manufacturer-data scan filters the way Android does.
 * We filter post-reception by name: only advertisements that include our device name
 * prefix are forwarded. Devices that omit the name from their advertisement packet are
 * silently dropped; they will reappear once CoreBluetooth caches the name (usually on
 * the next scan cycle or after a brief connection).
 *
 * CoreBluetooth prompts the user for permission on first scan.
 */
internal class BleScannerImpl : BleScanner {

    override fun scan(): Flow<BleDeviceResource> =
        Scanner {
            logging {
                level = Logging.Level.Warnings
            }
        }.advertisements
            .filter { advertisement ->
                advertisement.name?.contains(DEVICE_NAME_PREFIX, ignoreCase = true) == true
            }
            .map { advertisement ->
                val address = advertisement.identifier.toString()
                BleDeviceResource(
                    // CoreBluetooth may omit the name from advertisement packets; fall back to UUID.
                    name = advertisement.name?.takeIf { it.isNotBlank() } ?: address,
                    address = address,
                    rssi = advertisement.rssi,
                )
            }

    private companion object {
        const val DEVICE_NAME_PREFIX = "prompt"
    }
}
