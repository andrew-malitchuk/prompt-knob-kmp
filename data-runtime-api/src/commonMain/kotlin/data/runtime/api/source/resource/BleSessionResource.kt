package data.runtime.api.source.resource

import data.core.source.resource.Resource

/**
 * Runtime snapshot of the currently connected BLE device.
 *
 * @property address Platform-specific device identifier (MAC on Android, UUID on iOS/macOS).
 * @property name Human-readable device name at the time of connection.
 */
public data class BleSessionResource(
    val address: String,
    val name: String,
) : Resource
