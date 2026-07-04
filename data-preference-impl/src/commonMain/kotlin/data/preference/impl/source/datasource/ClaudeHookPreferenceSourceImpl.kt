package data.preference.impl.source.datasource

import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import data.preference.api.source.datasource.ClaudeHookPreferenceSource
import data.preference.api.source.model.ClaudeHookPreference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class ClaudeHookPreferenceSourceImpl(
    private val settings: Settings,
) : ClaudeHookPreferenceSource {

    private val _flow = MutableStateFlow(readFromSettings())

    private fun readFromSettings(): ClaudeHookPreference =
        ClaudeHookPreference(isEnabled = settings.getBoolean(KEY_CLAUDE_HOOK_ENABLED, false))

    override suspend fun getData(): ClaudeHookPreference = readFromSettings()

    override suspend fun setData(data: ClaudeHookPreference) {
        settings[KEY_CLAUDE_HOOK_ENABLED] = data.isEnabled
        _flow.value = data
    }

    override fun observeData(): Flow<ClaudeHookPreference> = _flow.asStateFlow()

    private companion object {
        const val KEY_CLAUDE_HOOK_ENABLED = "pref_claude_hook_enabled"
    }
}
