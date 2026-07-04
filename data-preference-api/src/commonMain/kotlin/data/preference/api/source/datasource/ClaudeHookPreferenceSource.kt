package data.preference.api.source.datasource

import data.preference.api.source.datasource.base.PreferenceSource
import data.preference.api.source.model.ClaudeHookPreference

/**
 * Preference data source for the Claude Mode hook server enabled/disabled state.
 *
 * @see ClaudeHookPreference
 */
public interface ClaudeHookPreferenceSource : PreferenceSource<ClaudeHookPreference>
