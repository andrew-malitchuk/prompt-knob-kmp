package data.preference.api.source.datasource

import data.preference.api.source.datasource.base.PreferenceSource
import data.preference.api.source.model.McpPreference

/**
 * Preference data source for MCP server enabled/disabled state.
 *
 * @see McpPreference
 */
public interface McpPreferenceSource : PreferenceSource<McpPreference>
