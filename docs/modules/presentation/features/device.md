# presentation-feature-device

The connected-knob dashboard. It shows connection status, battery and firmware, the macro grid, command history, and hosts the firmware OTA flow. From here the user edits macros or opens the preset gallery. Orbit MVI structure.

Package root: `presentation.feature.device`.

## Structure

| Layer | Type | Package |
|-------|------|---------|
| Screen | `DeviceDetailScreen(viewModel = koinViewModel())` | `presentation.feature.device.source` |
| Content | `DeviceDetailContent(state, onIntent, snackbarHostState, modifier)` | `presentation.feature.device.source` |
| ViewModel | `DeviceDetailViewModel : ContainerHost<DeviceDetailState, DeviceDetailSideEffect>` | `presentation.feature.device.source` |
| Contract | `DeviceDetailState`, `DeviceDetailSideEffect` | `presentation.feature.device.source` |
| Intent | `DeviceDetailIntent` | `presentation.feature.device.source` |
| DI | `presentationFeatureDeviceModule` | `presentation.feature.device.di` |

The Screen uses `LocalAppNavigator.current` and reacts to side effects for navigation. The Content renders the device header, status/battery card, macro grid, profile-load indicator, history, and the firmware OTA overlay.

## Contract

`DeviceDetailState` fields: `isLoading`, `deviceName`, `deviceAddress`, `connectionState`, `batteryPercentage`, `firmwareVersion`, `macroKeys: List<MacroKeyUiModel>`, `profileLoadPercentage`, `lastSelectedCommand`, `commandHistory: List<CommandHistoryEntryModel>`, `otaState: OtaStateModel?`.

`DeviceDetailSideEffect` (sealed): `NavigateBack`, `NavigateToDevices`, `NavigateToSettings`, `NavigateToCommandList(parentId)`, `NavigateToCommandForm(commandId)`, `ShowError(message)`, `ShowFirmwarePicker`, `NavigateToPresets`.

`DeviceDetailIntent` (sealed): `OnBackClick`, `OnDisconnect`, `OnMacroKeyClick(commandId)`, `OnAddMacroClick`, `OnSettingsClick`, `OnForgetDevice`, `OnForceSync`, `OnClearHistory`, `OnUpdateFirmwareClick`, `OnFirmwareFileSelected(firmware, version)`, `OnAbortOta`, `OnPresetsClick`.

## ViewModel

`DeviceDetailViewModel` injects:

| Dependency | Role |
|------------|------|
| `ObserveConnectionStateUseCase` | Live connection state. |
| `ObserveSelectedCommandUseCase` | Physical knob button presses. |
| `DisconnectDeviceUseCase` | Disconnect. |
| `ForgetDeviceUseCase` | Forget the paired device. |
| `ObserveCommandsUseCase` | Macro keys at `parentId = 0`. |
| `SyncCommandsUseCase` | Push commands to the device after edits. |
| `ObserveCommandHistoryUseCase` / `ClearCommandHistoryUseCase` | Command history. |
| `ObserveFirmwareVersionUseCase` | Firmware version. |
| `ObserveBatteryLevelUseCase` | Battery level. |
| `StartOtaUseCase` | Firmware OTA session. |

## Navigation

- **In:** `Destination.Device` (from Devices on successful connect, or straight from Home when already connected).
- **Out:** `Destination.CommandList(parentId = 0)` (add macro), `Destination.CommandForm(commandId)` (edit a macro key), `Destination.Settings`, `Destination.Presets`, and back to `Destination.Devices` on disconnect/forget.

## Dependencies

- BLE and command use cases from `domain.usecase.api`.
- [navigation](../core/navigation.md), [styling](../core/styling.md), [ui](../core/ui.md).

## See also

- [command](command.md) · [preset](preset.md) · [devices](devices.md) · [settings](settings.md)
