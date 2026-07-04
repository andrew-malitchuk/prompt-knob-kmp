package domain.core.source.model

import domain.core.source.model.base.Model

/**
 * Domain model for a discovered BLE device.
 *
 * @property name Advertised device name, or null if unavailable.
 * @property address Platform-specific device identifier (MAC on Android, UUID on iOS/macOS).
 * @property rssi Signal strength in dBm. Higher (less negative) is stronger.
 */
public data class BleDeviceModel(
    public val name: String?,
    public val address: String,
    public val rssi: Int,
) : Model
