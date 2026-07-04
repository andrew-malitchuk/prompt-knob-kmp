package data.preference.impl.source.datasource

import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import data.preference.api.source.datasource.LastDevicePreferenceSource
import data.preference.api.source.model.LastDevicePreference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class LastDevicePreferenceSourceImpl(
    private val settings: Settings,
) : LastDevicePreferenceSource {

    private val _flow = MutableStateFlow(readFromSettings())

    private fun readFromSettings(): LastDevicePreference = LastDevicePreference(
        address = settings.getStringOrNull(KEY_LAST_DEVICE_ADDRESS),
        name = settings.getStringOrNull(KEY_LAST_DEVICE_NAME),
    )

    override suspend fun getData(): LastDevicePreference = readFromSettings()

    override suspend fun setData(data: LastDevicePreference) {
        if (data.address != null) settings[KEY_LAST_DEVICE_ADDRESS] = data.address
        else settings.remove(KEY_LAST_DEVICE_ADDRESS)
        if (data.name != null) settings[KEY_LAST_DEVICE_NAME] = data.name
        else settings.remove(KEY_LAST_DEVICE_NAME)
        _flow.value = data
    }

    override fun observeData(): Flow<LastDevicePreference> = _flow.asStateFlow()

    private companion object {
        const val KEY_LAST_DEVICE_ADDRESS = "pref_last_device_address"
        const val KEY_LAST_DEVICE_NAME = "pref_last_device_name"
    }
}
