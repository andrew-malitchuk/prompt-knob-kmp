package data.ble.api.source.exception

/** Thrown by a [data.ble.api.source.datasource.BleScanner] when the Bluetooth adapter is off. */
public class BluetoothDisabledException : Exception("Bluetooth adapter is disabled")
