# data-ble-impl

> Per-platform BLE implementations for `data-ble-api`: scanning, GATT connection, the YAMK codec/protocol, and (on Android) a foreground connection service.

## Responsibility

Implements every `data-ble-api` contract with a platform backend and wires them into a Koin module. Apple targets (iOS + macOS) and Android use [Kable](https://github.com/JuulLabs/kable) over CoreBluetooth / Android Bluetooth; Desktop is a stub. The YAMK codec/protocol implementations are shared in `commonMain`.

## Dependencies

| Depends on | Purpose |
|---|---|
| `data-ble-api` | Contracts to implement (`api`) |
| `data-core` | `Resource` marker interface |
| Koin, Kermit | DI, logging |
| Kable (Android/iOS/macOS) | Multiplatform BLE stack |

## Public API

| Symbol | Description |
|---|---|
| `dataBleImplModule` | Koin module: binds `YamkCodec`→`YamkCodecImpl`, `YamkProtocol`→`YamkProtocolImpl`, plus the platform BLE adapter via `provideBleAdapter()` |
| `BleConnectionService` | **Android** foreground `Service` that keeps the GATT link alive and shows a persistent notification (Android only) |

### Internal implementations (per platform)

| Class | Implements |
|---|---|
| `YamkCodecImpl` | `YamkCodec` (common) |
| `YamkProtocolImpl` | `YamkProtocol` (common) |
| `BleScannerImpl` | `BleScanner` (android / ios / macos / desktop) |
| `BleConnectionImpl` | `BleConnection` (android / ios / macos / desktop) |
| `BleServiceControllerImpl` | `BleServiceController` (per platform) |
| `BlePermissionCheckerImpl` | `BlePermissionChecker` (per platform) |
| `BlePairingReceiver` | Android `BroadcastReceiver` handling pairing intents |

Device identity constants (service UUID, advertised name `prompt-knob`, manufacturer id/magic) live in the internal `PromptKnobBle` object.

## Platform notes

- **Android** — foreground service (`BleConnectionService`) + pairing receiver; needs Bluetooth/notification runtime permissions.
- **iOS / macOS** — CoreBluetooth via Kable; no long-running service (connection held in-process).
- **Desktop** — placeholder implementations (no BLE backend).

## Testing

```bash
./gradlew :data-ble-impl:test
```
