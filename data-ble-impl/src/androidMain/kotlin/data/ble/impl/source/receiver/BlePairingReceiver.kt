package data.ble.impl.source.receiver

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * Intercepts the system Bluetooth pairing request for PromptKnob devices and
 * auto-confirms the fixed passkey (123456) without showing the system PIN dialog.
 *
 * The ESP32 firmware uses Passkey Entry with IO Capability = Display Only, which
 * maps to PAIRING_VARIANT_PIN on the Android side. The passkey is fixed at compile
 * time in the firmware (BLE_PAIRING_PIN = 123456).
 *
 * After the first successful pairing the bond is stored (ESP32 NVS + Android bond
 * database), so subsequent connections re-encrypt via LTK and never trigger this
 * receiver again.
 *
 * Declared in AndroidManifest so it works even when the app process is not running.
 */
public class BlePairingReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != BluetoothDevice.ACTION_PAIRING_REQUEST) return

        val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
            ?: return
        val variant = intent.getIntExtra(
            BluetoothDevice.EXTRA_PAIRING_VARIANT,
            BluetoothDevice.ERROR,
        )

        if (device.name != DEVICE_NAME) return
        if (variant != BluetoothDevice.PAIRING_VARIANT_PIN) return

        // ASCII bytes of "123456" — Android expects the passkey as a string byte array,
        // not as a 4-byte integer representation.
        val pinSet = device.setPin(PASSKEY.toByteArray(Charsets.US_ASCII))
        if (pinSet) {
            // Prevent the system Bluetooth Settings dialog from appearing on top.
            abortBroadcast()
            Log.d(TAG, "Auto-confirmed pairing for ${device.address}")
        } else {
            Log.w(TAG, "setPin() returned false for ${device.address} — system dialog will appear")
        }
    }

    private companion object {
        const val TAG = "BlePairingReceiver"
        const val DEVICE_NAME = "prompt-knob"
        const val PASSKEY = "123456"
    }
}
