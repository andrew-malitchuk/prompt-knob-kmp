package data.preference.api.source.model

import data.core.source.resource.Resource

/**
 * Persisted preference tracking whether the MCP server is enabled.
 *
 * @property isEnabled `true` when the MCP server should run, `true` by default.
 * @see data.preference.api.source.datasource.McpPreferenceSource
 */
public data class McpPreference(
    val isEnabled: Boolean = true,
) : Resource
