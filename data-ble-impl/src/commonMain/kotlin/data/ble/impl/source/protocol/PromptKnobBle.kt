package data.ble.impl.source.protocol

internal object PromptKnobBle {
    const val SERVICE_UUID = "6e400001-b5a3-f393-e0a9-e50e24dcca9e"
    const val MFR_ID = 0xFFFF
    val MFR_MAGIC = byteArrayOf(0xAB.toByte())
    const val DEVICE_NAME = "prompt-knob"
}
