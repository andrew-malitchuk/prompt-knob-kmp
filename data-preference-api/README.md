# data-preference-api

> Preference data source interfaces and models for persisting app configuration.

## Responsibility

Declares the contracts for reading, writing, and observing app preferences. Each preference type has a dedicated source interface extending `PreferenceSource<T>`, ensuring type-safe access to persisted values.

## Dependencies

| Depends on | Purpose |
|---|---|
| `data-core` | `Resource` marker interface for preference models |

## Public API

### Base Interface

| Interface | Description |
|---|---|
| `PreferenceSource<T>` | Generic contract: `getData(): T`, `setData(data: T)`, `observeData(): Flow<T>` |

### Preference Sources

| Interface | Type Parameter | Description |
|---|---|---|
| `LanguagePreferenceSource` | `LanguagePreference` | App language locale code |
| `ThemePreferenceSource` | `ThemePreference` | App theme (light, dark, system) |
| `OnboardingPreferenceSource` | `OnboardingPreference` | Onboarding completion flag |
| `LastDevicePreferenceSource` | `LastDevicePreference` | Last connected BLE device identity |
| `McpPreferenceSource` | `McpPreference` | MCP server enabled state |
| `ClaudeHookPreferenceSource` | `ClaudeHookPreference` | Claude hook server enabled state |

### Models

| Class | Properties | Description |
|---|---|---|
| `LanguagePreference` | `languageCode: String` (default: `"en"`) | ISO 639-1 language code |
| `ThemePreference` | `theme: String` (default: `"system"`) | Theme identifier |
| `OnboardingPreference` | `isCompleted: Boolean` (default: `false`) | Onboarding status |
| `LastDevicePreference` | `address: String?`, `name: String?` (default: `null`) | Address/name of last connected BLE device (MAC on Android, CoreBluetooth UUID on iOS/macOS) |
| `McpPreference` | `isEnabled: Boolean` (default: `true`) | Whether the MCP server should run |
| `ClaudeHookPreference` | `isEnabled: Boolean` (default: `false`) | Whether the Claude hook server should run |

## Usage

```kotlin
// Read and observe a preference
val source: ThemePreferenceSource = get()
val current = source.getData() // ThemePreference(theme = "dark")
source.observeData().collect { pref -> applyTheme(pref.theme) }

// Write a preference
source.setData(ThemePreference(theme = "light"))
```

## Testing

```bash
./gradlew :data-preference-api:test
```
