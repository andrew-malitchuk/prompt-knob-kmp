# presentation-feature-device

Detail dashboard for the currently connected PromptKnob (BLE) device: macro keys, profile/firmware actions, and entry points into command, preset and settings flows. Single Orbit MVI screen.

## Public API

| Screen | Content | ViewModel | Contract |
|--------|---------|-----------|----------|
| `DeviceDetailScreen()` | `DeviceDetailContent` | `DeviceDetailViewModel` | `DeviceDetailState`, `DeviceDetailIntent`, `DeviceDetailSideEffect` |

Supporting type: `MacroKeyUiModel`.

Side effects: `NavigateBack`, `NavigateToDevices`, `NavigateToSettings`, `NavigateToCommandList`, `NavigateToCommandForm`, `NavigateToPresets`, `ShowFirmwarePicker`, `ShowError`.

## Destinations

Reached via `Destination.Device`. Navigates out to `Destination.Devices`, `Destination.Settings`, `Destination.CommandList(...)`, `Destination.CommandForm(...)`, and `Destination.Presets`.

## Dependencies

Orbit (core/viewmodel/compose), Compose Material3, `domain-core`, `domain-usecase-api`, `presentation-core-localisation`, `presentation-core-navigation-api`, `presentation-core-styling`, `presentation-core-ui`. Android/iOS add `filekit-dialogs-compose` for the firmware file picker.

Applies `dev.prompt.knob.io.convention.feature` + `dev.prompt.knob.io.convention.di`. DI module: `presentationFeatureDeviceModule`.
