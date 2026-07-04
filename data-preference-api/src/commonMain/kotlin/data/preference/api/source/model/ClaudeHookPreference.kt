package data.preference.api.source.model

import data.core.source.resource.Resource

/**
 * Persisted preference tracking whether the Claude Mode hook server is enabled.
 *
 * @property isEnabled `true` when the Claude hook HTTP server should run. Defaults to `false`
 *   because Claude Mode requires explicit setup (configuring hooks in ~/.claude/settings.json).
 * @see data.preference.api.source.datasource.ClaudeHookPreferenceSource
 */
public data class ClaudeHookPreference(
    val isEnabled: Boolean = false,
) : Resource
