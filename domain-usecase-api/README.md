# domain-usecase-api

> Single-responsibility use case interfaces — the domain API consumed by the presentation layer.

## Responsibility

Provides one interface per domain operation. Each use case is the sole entry point from presentation into the domain layer and typically wraps a single repository call. Grouped by area, matching the source package layout under `domain.usecase.api.source.usecase.*`.

## Dependencies

| Depends on | Purpose |
|---|---|
| `domain-core` | Domain models, `Optional`, and `Failure` types |
| `kotlinx-serialization-json` | For serializable command/preset export payloads |

## Public API

~50 use case interfaces across seven areas, plus a small platform-command helper.

| Area (package) | Count | Examples |
|---|---|---|
| `usecase/ble` | 19 | `ScanForDevicesUseCase`, `ConnectToDeviceUseCase`, `DisconnectDeviceUseCase`, `ForgetDeviceUseCase`, `SyncCommandsUseCase`, `ObserveConnectionStateUseCase`, `ObserveBatteryLevelUseCase`, `ObserveFirmwareVersionUseCase`, `StartOtaUseCase`, `CheckBlePermissionsUseCase`, `RequestBlePermissionsUseCase`, `SendClaudeStateUseCase`, `SendNotifyUseCase`, `SendShowApprovalUseCase`, `SendShowChoiceUseCase`, `AwaitCommandSelectedUseCase`, `ObserveSelectedCommandUseCase`, `GetLastDeviceUseCase`, `ConsumeSkipAutoReconnectUseCase` |
| `usecase/command` | 6 | `ObserveCommandsUseCase`, `GetCommandByIdUseCase`, `SaveCommandUseCase`, `DeleteCommandUseCase`, `ExportCommandsUseCase`, `ImportCommandsUseCase` |
| `usecase/preset` | 7 | `ObservePresetsUseCase`, `SavePresetUseCase`, `DeletePresetUseCase`, `LoadPresetUseCase`, `GetGalleryPresetsUseCase`, `ExportPresetUseCase`, `ImportPresetUseCase` |
| `usecase/configuration` | 12 | `GetThemeUseCase`/`SetThemeUseCase`/`ObserveThemeUseCase`, `GetApplicationLanguageUseCase`/`SetApplicationLanguageUseCase`/`ObserveApplicationLanguageUseCase`, `GetOnboardingStatusUseCase`/`SetOnboardingStatusUseCase`, `GetMcpEnabledUseCase`/`SetMcpEnabledUseCase`, `GetClaudeHookEnabledUseCase`/`SetClaudeHookEnabledUseCase` |
| `usecase/mcp` | 4 | `StartMcpServerUseCase`, `StopMcpServerUseCase`, `StartClaudeHookServerUseCase`, `StopClaudeHookServerUseCase` |
| `usecase/history` | 2 | `ObserveCommandHistoryUseCase`, `ClearCommandHistoryUseCase` |
| `usecase/executor` | 1 | `ExecuteCommandUseCase` |

### Platform command helper

| Symbol | Description |
|---|---|
| `availableCommandTypes(): List<CommandTypeModel>` | `expect` function; each target returns the `CommandTypeModel`s it supports (e.g. `SHELL` on macOS, `PROMPT` on Android, `SHORTCUT` on iOS) |

### Utilities

| Symbol | Description |
|---|---|
| `Optional` | `typealias` re-exporting `domain.core.source.monad.Optional` |

## Usage

```kotlin
class SettingsViewModel(
    private val getThemeUseCase: GetThemeUseCase,
    private val setThemeUseCase: SetThemeUseCase,
) : ViewModel() {
    fun loadTheme() = viewModelScope.launch {
        getThemeUseCase().onSuccess { /* reduce state */ }
    }
}
```

## Testing

```bash
./gradlew :domain-usecase-api:test
```
