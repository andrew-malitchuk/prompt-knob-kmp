package data.ble.impl.source.datasource

import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanSettings
import android.content.Context
import com.juul.kable.Filter
import com.juul.kable.ObsoleteKableApi
import com.juul.kable.Scanner
import com.juul.kable.logs.Logging
import data.ble.api.source.datasource.BleScanner
import data.ble.api.source.exception.BluetoothDisabledException
import data.ble.api.source.resource.BleDeviceResource
import data.ble.impl.source.protocol.PromptKnobBle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach

/**
 * Android implementation of [BleScanner] backed by Kable's [Scanner].
 *
 * Emits already-bonded prompt-knob devices immediately (RSSI = Int.MIN_VALUE), then
 * continues emitting actively advertising devices filtered by manufacturer data so
 * only the prompt-knob hardware reaches the caller.
 *
 * Bonded devices often stop advertising after pairing, so listing them separately
 * ensures they remain selectable without re-scanning.
 */
internal class BleScannerImpl(
    private val context: Context,
) : BleScanner {

    @OptIn(ObsoleteKableApi::class)
    override fun scan(): Flow<BleDeviceResource> = channelFlow {
        // Fail fast if Bluetooth is turned off so the UI can show a targeted error.
        val btAdapter = context.getSystemService(BluetoothManager::class.java)?.adapter
        if (btAdapter?.isEnabled != true) throw BluetoothDisabledException()

        // Emit already-bonded prompt-knob devices first.
        val bondedDevices = btAdapter.bondedDevices.orEmpty()
        for (device in bondedDevices) {
            if (device.name == PromptKnobBle.DEVICE_NAME) {
                send(
                    BleDeviceResource(
                        name = device.name,
                        address = device.address,
                        rssi = Int.MIN_VALUE,
                    )
                )
            }
        }

        // Hardware-level manufacturer-data filter so only prompt-knob advertisements
        // reach the app. LOW_LATENCY mode is required to trigger scan responses, which
        // is where the device name lives (scan record type 0x09).
        Scanner {
            filters = listOf(
                Filter.ManufacturerData(
                    id = PromptKnobBle.MFR_ID,
                    data = PromptKnobBle.MFR_MAGIC,
                    dataMask = byteArrayOf(0xFF.toByte()),
                )
            )
            scanSettings = ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build()
            logging { level = Logging.Level.Warnings }
        }.advertisements
            .onEach { advertisement ->
                val address = advertisement.address
                // Prefer scan-response name; fall back to system-cached device.name on
                // first scan before the scan response has arrived.
                val displayName = advertisement.name?.takeIf { it.isNotBlank() }
                    ?: advertisement.peripheralName?.takeIf { it.isNotBlank() }
                    ?: address
                send(
                    BleDeviceResource(
                        name = displayName,
                        address = address,
                        rssi = advertisement.rssi,
                    )
                )
            }
            .collect()
    }
}
