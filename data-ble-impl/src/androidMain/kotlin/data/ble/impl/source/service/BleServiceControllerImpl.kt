package data.ble.impl.source.service

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import data.ble.api.source.datasource.BleServiceController

internal class BleServiceControllerImpl(
    private val context: Context,
) : BleServiceController {

    override fun startConnectionService(deviceAddress: String) {
        val intent = Intent(context, BleConnectionService::class.java).apply {
            action = BleConnectionService.ACTION_CONNECT
            putExtra(BleConnectionService.EXTRA_DEVICE_ADDRESS, deviceAddress)
        }
        ContextCompat.startForegroundService(context, intent)
    }

    override fun stopConnectionService() {
        val intent = Intent(context, BleConnectionService::class.java).apply {
            action = BleConnectionService.ACTION_DISCONNECT
        }
        context.startService(intent)
    }
}
