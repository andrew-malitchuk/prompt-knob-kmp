# domain-usecase

The `domain-usecase` modules hold PromptKnob's business logic as single-purpose use cases. Each one wraps a repository (or executor/server) call and represents one action the app can perform.

The pair splits into:

- **domain-usecase-api** — the use-case interfaces, grouped by feature package.
- **domain-usecase-impl** — the implementations and their Koin wiring.

## Purpose

Presentation code (ViewModels) depends on use-case interfaces, never on repositories directly. This keeps each screen's dependencies explicit and each behaviour independently testable. Use cases are grouped by domain area: `ble`, `command`, `configuration`, `executor`, `history`, `preset`, and `mcp`.

## Use cases by package

### `ble`

Connection and knob interaction, backed by `BleRepository`:

`ScanForDevicesUseCase`, `ConnectToDeviceUseCase`, `DisconnectDeviceUseCase`, `ObserveConnectionStateUseCase`, `CheckBlePermissionsUseCase`, `RequestBlePermissionsUseCase`, `GetLastDeviceUseCase`, `ForgetDeviceUseCase`, `ConsumeSkipAutoReconnectUseCase`, `SyncCommandsUseCase`, `ObserveSelectedCommandUseCase`, `AwaitCommandSelectedUseCase`, `ObserveFirmwareVersionUseCase`, `ObserveBatteryLevelUseCase`, `StartOtaUseCase`, `SendShowApprovalUseCase`, `SendShowChoiceUseCase`, `SendNotifyUseCase`, `SendClaudeStateUseCase`.

### `command`

Command-tree CRUD plus import/export (`CommandRepository`):

`ObserveCommandsUseCase`, `GetCommandByIdUseCase`, `SaveCommandUseCase`, `DeleteCommandUseCase`, `ExportCommandsUseCase`, `ImportCommandsUseCase`.

### `configuration`

App settings (`ConfigureRepository`):

`GetThemeUseCase`, `SetThemeUseCase`, `ObserveThemeUseCase`, `GetOnboardingStatusUseCase`, `SetOnboardingStatusUseCase`, `GetApplicationLanguageUseCase`, `SetApplicationLanguageUseCase`, `ObserveApplicationLanguageUseCase`, `GetMcpEnabledUseCase`, `SetMcpEnabledUseCase`, `GetClaudeHookEnabledUseCase`, `SetClaudeHookEnabledUseCase`.

### `executor`

`ExecuteCommandUseCase` — runs a command via the platform `CommandExecutor`.

### `history`

`ObserveCommandHistoryUseCase`, `ClearCommandHistoryUseCase` (`CommandHistoryRepository`).

### `preset`

Preset management and the built-in gallery (`PresetRepository` + `CommandRepository`):

`ObservePresetsUseCase`, `SavePresetUseCase`, `LoadPresetUseCase`, `DeletePresetUseCase`, `ExportPresetUseCase`, `ImportPresetUseCase`, `GetGalleryPresetsUseCase`.

### `mcp` (macOS only)

Start/stop the desktop servers (`McpServer` / `ClaudeHookServer` from [data-mcp](../data/mcp.md)):

`StartMcpServerUseCase`, `StopMcpServerUseCase`, `StartClaudeHookServerUseCase`, `StopClaudeHookServerUseCase`.

## Dependency injection

| Koin module | Source set | Registers |
|-------------|------------|-----------|
| `domainUseCaseImplModule` | `commonMain` | All the `ble`, `command`, `configuration`, `executor`, `history`, and `preset` use cases. |
| `domainMcpUseCaseModule` | `macosMain` | The four `mcp` use cases (server lifecycle). |

## Platform notes

Both modules have `commonMain`, `androidMain`, `iosMain`, `desktopMain`, and `macosMain` source sets. The available command types (`AvailableCommandTypes`) differ per platform (for example, SHELL on macOS, SHORTCUT on iOS), and the `mcp` use cases exist only on macOS where the servers run.

## Dependencies

- `domain-repository-api`, `domain-core`
- `data-executor-api` (for `ExecuteCommandUseCase`)
- `data-mcp-api` (macOS, for the `mcp` use cases)
- Koin, kotlinx-coroutines
