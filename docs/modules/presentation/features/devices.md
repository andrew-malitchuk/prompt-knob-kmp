# presentation-feature-devices

The BLE scan-and-pair screen. It handles Bluetooth permissions, scans for nearby knobs, attempts a quick reconnect to the last known device, and connects on tap. When a connection is established it advances to the device dashboard. Orbit MVI structure.

Package root: `presentation.feature.devices`.

## Structure

| Layer | Type | Package |
|-------|------|---------|
| Screen | `DevicesScreen(viewModel = koinViewModel())` | `presentation.feature.devices.source.devices` |
| Content | `DevicesContent(state, onIntent, snackbarHostState)` | `presentation.feature.devices.source.devices` |
| ViewModel | `DevicesViewModel : ContainerHost<DevicesState, DevicesSideEffect>` | `presentation.feature.devices.source.devices` |
| Contract | `DevicesState`, `DevicesSideEffect` | `presentation.feature.devices.source.devices` |
| Intent | `DevicesIntent` | `presentation.feature.devices.source.devices` |
| DI | `presentationFeatureDevicesModule` | `presentation.feature.devices.di` |

The Screen dispatches `OnResume` via `LifecycleEventEffect(Lifecycle.Event.ON_RESUME)` and uses `rememberBlePermissionRequester()` (Android) and `rememberOpenBluetoothSettingsLauncher()`.

`DevicesContent` branches into three states:

1. `BleUnavailableContent` — platform without BLE (Desktop JVM).
2. `PermissionRequestContent` — missing permissions, with a "Grant Permission" call to action.
3. `BleDeviceContent` — scan progress, discovered devices, and connected status.

## Contract

`DevicesState`

| Field | Type | Notes |
|-------|------|-------|
| `isLoading` | `Boolean` | Connection / scan in progress. |
| `hasPermissions` | `Boolean` | BLE permission gate. |
| `isBleAvailable` | `Boolean` | Platform support flag. |
| `connectionState` | `ConnectionStateModel` | Current connection state. |
| `discoveredDevices` | `List<BleDeviceModel>` | Deduplicated by address, sorted by RSSI. |
| `connectedDevice` | `BleDeviceModel?` | The active connection, if any. |
| `lastSelectedCommand` | `CommandNodeModel?` | Most recent command from the knob. |
| `commandHistory` | `List<String>` | Last 20 command log lines. |
| `scanTimedOut` | `Boolean` | Set when a scan finds nothing in time. |
| `isBluetoothDisabled` | `Boolean` | Adapter is off. |

`DevicesSideEffect` (sealed): `RequestPermissions`, `ShowError(message)`, `NavigateToDevice`, `NavigateToSettings`, `NavigateBack`, `OpenBluetoothSettings`.

`DevicesIntent` (sealed): `StartScan`, `StopScan`, `OnRetryScan`, `OnDeviceTapped(address)`, `ConnectToDevice(address)`, `Disconnect`, `OnPermissionsResult(granted)`, `OnBackClick`, `OnSettingsClick`, `OnOpenBluetoothSettings`, `OnResume`.

## ViewModel

`DevicesViewModel` injects:

| Dependency | Role |
|------------|------|
| `CheckBlePermissionsUseCase` | Permission gate. |
| `RequestBlePermissionsUseCase` | Request permission dialog. |
| `ScanForDevicesUseCase` | Emits discovered `BleDeviceModel`s. |
| `ConnectToDeviceUseCase` | Establishes a connection. |
| `DisconnectDeviceUseCase` | Tears down a connection. |
| `ObserveConnectionStateUseCase` | Drives auto-navigation and reconnect detection. |
| `ObserveSelectedCommandUseCase` | Feeds the command history log. |
| `GetLastDeviceUseCase` | Quick-reconnect target address. |
| `ConsumeSkipAutoReconnectUseCase` | One-shot flag to skip auto-reconnect after an explicit disconnect. |
| `isBleSupported: Boolean` | Platform flag (`false` on Desktop). |

### Scan & pair flow

1. **Initialize** — check permissions (show rationale card if missing, no auto-request). If already connected, emit `NavigateToDevice`. Otherwise attempt a quick reconnect to the last device (short timeout); on success the connection observer drives navigation.
2. **Full scan** — start `ScanForDevicesUseCase`, with a scan timeout, periodic pruning of stale devices, dedup by address, and RSSI sort. Timeout with no results sets `scanTimedOut`; a disabled adapter surfaces `isBluetoothDisabled` and offers the Bluetooth settings sheet.
3. **Connection observer** — watches `ObserveConnectionStateUseCase`; on transition to Connected it cancels the scan, clears the list, and emits `NavigateToDevice`; a disconnect while scanning auto-restarts the scan.
4. **Command tracking** — observes `ObserveSelectedCommandUseCase` and maintains up to 20 recent command lines.

## DI notes

`presentationFeatureDevicesModule` registers `DevicesViewModel` and resolves the `isBleSupported` flag via a named qualifier (`named("isBleSupported")`) populated by a per-platform `expect fun Module.providePlatformBleSupportFlag()`.

## Navigation

- **In:** `Destination.Devices` (from Home).
- **Out:** `Destination.Device` (on connect), `Destination.Settings`, back (`popBackStack()`).

## Dependencies

- BLE use cases from `domain.usecase.api.source.usecase.ble`; models from `domain.core` (`ConnectionStateModel`, `BleDeviceModel`, `CommandNodeModel`).
- [navigation](../core/navigation.md), [styling](../core/styling.md), [ui](../core/ui.md).

## See also

- [device](device.md) · [home](home.md)
