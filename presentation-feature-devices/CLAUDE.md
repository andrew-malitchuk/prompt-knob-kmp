# presentation-feature-devices — AI agent guide

Single Orbit MVI screen under `presentation.feature.devices.source.devices`: BLE scan + connect list.

Applies `dev.prompt.knob.io.convention.feature` + `dev.prompt.knob.io.convention.di`.

## Structure
- `DevicesScreen()` — collects `DevicesState`, handles `DevicesSideEffect` (permission requests, opening Bluetooth settings, navigation), delegates to `DevicesContent`.
- `DevicesContent` — stateless UI; emits `DevicesIntent`.
- `DevicesViewModel : ContainerHost<DevicesState, DevicesSideEffect>`.

## Gotchas
- BLE support is platform-gated: `providePlatformBleSupportFlag()` is an `expect`/`actual` in the DI module, injected with the `isBleSupported` qualifier. Desktop/JVM has no real BLE — the screen must show the unavailable state, not scan.
- Permission flow: emit `RequestPermissions` / `OpenBluetoothSettings` as side effects; the Android launcher uses `androidx.activity.compose`.
- `DevicesViewModel` is registered as a viewModel single in `presentationFeatureDevicesModule`.
