package data.preference.impl.source.datasource

import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import data.preference.api.source.datasource.McpPreferenceSource
import data.preference.api.source.model.McpPreference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Default implementation of [McpPreferenceSource].
 *
 * Persists the MCP server enabled flag via [Settings] and exposes
 * a [MutableStateFlow] for reactive observation.
 *
 * @property settings Platform settings store injected by Koin.
 * @see McpPreferenceSource
 */
internal class McpPreferenceSourceImpl(
    private val settings: Settings,
) : McpPreferenceSource {

    /** In-memory state flow initialised with the persisted value on construction. */
    private val _flow = MutableStateFlow(readFromSettings())

    /**
     * Reads the MCP enabled flag from the platform [Settings].
     *
     * Defaults to `true` (MCP server enabled) when no value has been persisted.
     *
     * @return A [McpPreference] reflecting the stored or default state.
     */
    private fun readFromSettings(): McpPreference {
        return McpPreference(
            isEnabled = settings.getBoolean(KEY_MCP_ENABLED, true),
        )
    }

    /**
     * Retrieves the current MCP preference from persistent storage.
     *
     * @return The latest [McpPreference].
     */
    override suspend fun getData(): McpPreference = readFromSettings()

    /**
     * Persists the given [data] and notifies observers.
     *
     * @param data The [McpPreference] to persist.
     */
    override suspend fun setData(data: McpPreference) {
        settings[KEY_MCP_ENABLED] = data.isEnabled
        _flow.value = data
    }

    /**
     * Observes the MCP preference as a reactive [Flow].
     *
     * @return A read-only [Flow] emitting [McpPreference] on every change.
     */
    override fun observeData(): Flow<McpPreference> = _flow.asStateFlow()

    private companion object {
        /** Settings key under which the MCP enabled flag is stored. */
        const val KEY_MCP_ENABLED = "mcp_enabled"
    }
}
