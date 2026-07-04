# presentation-feature-home

The hub screen. On entry it inspects the current BLE connection state and routes the user either straight to the connected knob's dashboard or into the scan-and-pair flow. It also exposes entry points to Settings, Presets, and the internal Style Guide. Orbit MVI structure.

Package root: `presentation.feature.home`.

## Structure

| Layer | Type | Package |
|-------|------|---------|
| Screen | `HomeScreen(viewModel = koinViewModel())` | `presentation.feature.home.source.home` |
| Content | `HomeContent(state, onIntent, snackbarHostState)` (hosts `HomeSuccessContent`) | `presentation.feature.home.source.home.content` |
| ViewModel | `HomeViewModel : ContainerHost<HomeState, HomeSideEffect>` | `presentation.feature.home.source.home` |
| Contract | `HomeState`, `HomeSideEffect` | `presentation.feature.home.source.home` |
| Intent | `HomeIntent` | `presentation.feature.home.source.home` |
| DI | `presentationFeatureHomeModule` | `presentation.feature.home.di` |

## Contract

`HomeState`

| Field | Type | Notes |
|-------|------|-------|
| `isLoading` | `Boolean` | Defaults to `false`. |

`HomeSideEffect` (sealed)

| Variant | Target |
|---------|--------|
| `NavigateToSettings` | `Destination.Settings` |
| `NavigateToStyleguide` | `Destination.Styleguide` |
| `NavigateToDevices` | `Destination.Devices` (start scan) |
| `NavigateToDevice` | `Destination.Device` (skip to connected knob) |
| `NavigateToPresets` | `Destination.Presets` |
| `ShowError(messageId)` | error snackbar |

`HomeIntent` (sealed): `OnSettingsClick`, `OnStyleguideClick`, `OnDeviceClick`, `OnPresetsClick`.

## ViewModel

`HomeViewModel` injects `ObserveConnectionStateUseCase`. On creation it reads the current connection state (`observeConnectionState().first()`):

- **Connected** &rarr; posts `NavigateToDevice`; the Screen stacks `Destination.Device` on top of a freshly reset `Destination.Devices` so back navigation lands on the device list.
- **Otherwise** (disconnected / error / unavailable, including the Desktop JVM target where BLE throws) &rarr; posts `NavigateToDevices` to start scanning.

This is the "skip-to-device on reconnect" behavior: a returning user with a live connection bypasses the scan screen.

## Navigation

- **In:** `Destination.Home` (from Splash or Onboarding).
- **Out:** `Destination.Devices`, `Destination.Device`, `Destination.Settings`, `Destination.Presets`, `Destination.Styleguide`.

## Dependencies

- `ObserveConnectionStateUseCase` (`domain.usecase.api`).
- [navigation](../core/navigation.md), [styling](../core/styling.md), [ui](../core/ui.md).

## See also

- [devices](devices.md) · [device](device.md) · [preset](preset.md) · [settings](settings.md)
