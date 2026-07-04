package data.ble.api.source.resource

import data.core.source.resource.Resource

/**
 * Lifecycle states of a BLE connection to the knob device.
 *
 * Transitions follow the standard BLE peripheral connection model:
 * `Disconnected` → `Connecting` → `Connected` → `Disconnecting` → `Disconnected`.
 *
 * @see data.ble.api.source.datasource.BleConnection.state
 */
public enum class BleConnectionStateResource : Resource {
    /** No active connection; [data.ble.api.source.datasource.BleConnection.connect] has not been called or has failed. */
    Disconnected,

    /** A connection attempt is in progress; waiting for the peripheral to respond. */
    Connecting,

    /** The peripheral is connected and ready for GATT operations. */
    Connected,

    /** A graceful disconnect is in progress; pending writes will be flushed first. */
    Disconnecting,
}
