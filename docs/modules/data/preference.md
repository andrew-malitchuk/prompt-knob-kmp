# data-preference

The `data-preference` modules provide typed, observable key-value storage for PromptKnob's small persistent settings — theme, language, onboarding state, the last connected knob, and feature toggles for the MCP and Claude hook servers.

The pair splits into:

- **data-preference-api** — the storage contract and preference models.
- **data-preference-impl** — the [multiplatform-settings](https://github.com/russhwolf/multiplatform-settings) backed implementations plus a per-platform `Settings` provider.

## Purpose

Each preference is exposed as a small typed source with `get`, `set`, and `observe` operations. Callers never touch raw keys or platform storage APIs; they depend on an interface from the API module and receive an implementation via Koin.

## Key types (data-preference-api)

| Type | Kind | Backing model | Responsibility |
|------|------|---------------|----------------|
| `PreferenceSource<T>` | interface | — | Base contract: `getData()`, `setData(data)`, `observeData(): Flow<T>`. |
| `LastDevicePreferenceSource` | interface | `LastDevicePreference` | Stores the last connected knob (address + name). |
| `LanguagePreferenceSource` | interface | `LanguagePreference` | Stores the selected UI language code. |
| `ThemePreferenceSource` | interface | `ThemePreference` | Stores the selected theme. |
| `OnboardingPreferenceSource` | interface | `OnboardingPreference` | Tracks whether onboarding is complete. |
| `McpPreferenceSource` | interface | `McpPreference` | Toggles the MCP server. |
| `ClaudeHookPreferenceSource` | interface | `ClaudeHookPreference` | Toggles the Claude hook integration. |

### Preference models

All models live in `data.preference.api.source.model` and implement `Resource`.

| Model | Fields | Default |
|-------|--------|---------|
| `LastDevicePreference` | `address: String?`, `name: String?` | both null |
| `LanguagePreference` | `languageCode: String` | `"en"` (`DEFAULT_LANGUAGE`) |
| `ThemePreference` | `theme: String` | `"system"` (`DEFAULT_THEME`) |
| `OnboardingPreference` | `isCompleted: Boolean` | `false` |
| `McpPreference` | `isEnabled: Boolean` | `true` |
| `ClaudeHookPreference` | `isEnabled: Boolean` | `false` |

## Key types (data-preference-impl)

Each API interface has a corresponding `…SourceImpl` in `data.preference.impl.source.datasource` (for example `ThemePreferenceSourceImpl`, `LastDevicePreferenceSourceImpl`, `McpPreferenceSourceImpl`). Every implementation wraps a shared `com.russhwolf.settings.Settings` instance and reads/writes a single well-known key.

| Implementation | Settings key(s) |
|----------------|-----------------|
| `ThemePreferenceSourceImpl` | `pref_theme` |
| `OnboardingPreferenceSourceImpl` | `pref_onboarding_completed` |
| `LanguagePreferenceSourceImpl` | `pref_language` |
| `LastDevicePreferenceSourceImpl` | `pref_last_device_address`, `pref_last_device_name` |
| `McpPreferenceSourceImpl` | `mcp_enabled` |
| `ClaudeHookPreferenceSourceImpl` | `pref_claude_hook_enabled` |

### Dependency injection

The Koin module `dataPreferenceImplModule` (`data.preference.impl.di`) registers the shared `Settings` and binds every source interface to its implementation as a singleton. The `Settings` instance is produced by `provideSettings()`, an `expect`/`actual` function resolved per platform.

## Platform notes

The storage backend differs per target, all bridged behind the multiplatform-settings `Settings` interface:

| Platform | Backend | `Settings` implementation |
|----------|---------|---------------------------|
| Android | AndroidX Preferences DataStore (`promptknob_prefs`) | `DataStoreSettings` (custom wrapper) |
| iOS | `NSUserDefaults.standardUserDefaults` | `NSUserDefaultsSettings` |
| macOS | `NSUserDefaults.standardUserDefaults` | `NSUserDefaultsSettings` |
| Desktop (JVM) | `java.util.prefs.Preferences` node `promptknob` | `PreferencesSettings` |

On Android, `DataStoreSettings` adapts the coroutine-based DataStore to the multiplatform-settings API so the shared source implementations remain identical across platforms.

## Dependencies

- `data-core` (base `Resource` type)
- multiplatform-settings (core + coroutines)
- Koin (core; `koin-android` and DataStore on Android)
