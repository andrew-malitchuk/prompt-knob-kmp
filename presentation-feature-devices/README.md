# presentation-feature-devices

BLE device discovery screen for PromptKnob: scan for nearby knob devices, request Bluetooth permissions, and connect. Single Orbit MVI screen.

## Public API

| Screen | Content | ViewModel | Contract |
|--------|---------|-----------|----------|
| `DevicesScreen()` | `DevicesContent` | `DevicesViewModel` | `DevicesState`, `DevicesIntent`, `DevicesSideEffect` |

Side effects: `RequestPermissions`, `OpenBluetoothSettings`, `NavigateToDevice`, `NavigateToSettings`, `NavigateBack`, `ShowError`.

## Destinations

Reached via `Destination.Devices`. Navigates to `Destination.Device` on connect and `Destination.Settings`.

## Dependencies

Orbit (core/viewmodel/compose), Compose Material3, `domain-core`, `domain-usecase-api`, `presentation-core-localisation`, `presentation-core-navigation-api`, `presentation-core-styling`, `presentation-core-ui`, Kermit. Android adds `androidx.activity.compose` (permission launcher).

Applies `dev.prompt.knob.io.convention.feature` + `dev.prompt.knob.io.convention.di`. DI module: `presentationFeatureDevicesModule`.

## Platform notes

DI provides a platform BLE-support flag (`expect fun Module.providePlatformBleSupportFlag()`, qualified `isBleSupported`) so the UI can render a "BLE unavailable" state on platforms/devices without Bluetooth support.
