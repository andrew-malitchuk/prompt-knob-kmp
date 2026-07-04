package data.preference.api.source.datasource

import data.preference.api.source.datasource.base.PreferenceSource
import data.preference.api.source.model.LastDevicePreference

/**
 * Preference data source for the last successfully connected BLE device address.
 *
 * @see LastDevicePreference
 */
public interface LastDevicePreferenceSource : PreferenceSource<LastDevicePreference>
