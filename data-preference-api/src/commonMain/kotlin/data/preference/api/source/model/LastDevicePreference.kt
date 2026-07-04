package data.preference.api.source.model

import data.core.source.resource.Resource

/**
 * Persisted preference holding the identity of the last successfully connected BLE device.
 *
 * @property address Device address string, or null when no device has ever been connected.
 *   On Android this is a MAC address (`"AA:BB:CC:DD:EE:FF"`).
 *   On iOS/macOS this is a CoreBluetooth peripheral UUID string.
 * @property name Human-readable device name at the time of last connection, or null if unknown.
 * @see data.preference.api.source.datasource.LastDevicePreferenceSource
 */
public data class LastDevicePreference(
    val address: String? = null,
    val name: String? = null,
) : Resource
