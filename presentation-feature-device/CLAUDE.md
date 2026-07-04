# presentation-feature-device — AI agent guide

Single Orbit MVI screen under `presentation.feature.device.source.device`: the connected-knob dashboard. It is the hub — most navigation to command/preset/settings flows originates here.

Applies `dev.prompt.knob.io.convention.feature` + `dev.prompt.knob.io.convention.di`.

## Structure
- `DeviceDetailScreen()` — collects `DeviceDetailState`, handles the 8 `DeviceDetailSideEffect` cases (navigation via `LocalAppNavigator.current`; `ShowFirmwarePicker` opens a FileKit picker), delegates to `DeviceDetailContent`.
- `DeviceDetailContent` — stateless; renders `MacroKeyUiModel`s, emits `DeviceDetailIntent`.
- `DeviceDetailViewModel : ContainerHost<DeviceDetailState, DeviceDetailSideEffect>`.

## Gotchas
- The firmware file picker is platform-specific (`filekit-dialogs-compose` in androidMain/iosMain) — guard/expect-actual any new picker usage; it is not in commonMain deps.
- Depends on live BLE connection state via domain use cases — handle disconnect (side effect `NavigateToDevices`) gracefully.
- `DeviceDetailViewModel` is registered as a viewModel single in `presentationFeatureDeviceModule`.
