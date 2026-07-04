# data-ble

The `data-ble` modules own all Bluetooth Low Energy communication with the PromptKnob ESP32 rotary knob: scanning, connection, the YAMK wire protocol, and — on Android — a foreground service that keeps the connection alive.

The pair splits into:

- **data-ble-api** — BLE data-source contracts, the YAMK codec/protocol interfaces, and their resources.
- **data-ble-impl** — the [Kable](https://github.com/JuulLabs/kable) backed scanner/connection implementations plus the YAMK codec and protocol implementations.

## Purpose

The knob and the app exchange framed messages over a custom GATT service. This module:

- **Discovers** knobs (filtered by the PromptKnob service UUID and manufacturer data).
- **Connects** and manages the GATT link, MTU negotiation, notifications, and OTA characteristics.
- **Encodes/decodes** YAMK frames and exposes higher-level protocol operations (sync commands, session hello, approvals/choices, notify, Claude state, battery, firmware version).

## Key types (data-ble-api)

### Connection and discovery

| Type | Kind | Responsibility |
|------|------|----------------|
| `BleScanner` | interface | `scan(): Flow<BleDeviceResource>`. |
| `BleConnection` | interface | GATT link: `connect`, `disconnect`, `write`, `negotiateMtu`, `readFirmwareRevision`, OTA writes, `observeNotifications`, `observeOtaStatus`; exposes `state: StateFlow<BleConnectionStateResource>`. |
| `BlePermissionChecker` | interface | `hasPermissions()`, `requestPermissions()`. |
| `BleServiceController` | interface | `startConnectionService()`, `stopConnectionService()`. |

### YAMK codec and protocol

| Type | Kind | Responsibility |
|------|------|----------------|
| `YamkCodec` | interface | `encode(frame: YamkFrame): ByteArray`, `decode(bytes: ByteArray): YamkFrame`. |
| `YamkFrame` | data class | Wire frame: `more`, `sequence`, `opcode: UByte`, `payload: ByteArray`. |
| `YamkProtocol` | interface | High-level operations: `syncCommands`, `clearCommands`, `sendSessionHello`, `sendDisconnectCommand`, `sendShowApproval`, `sendShowChoice`, `sendNotify`, `sendClaudeState`, `updateMtu`, plus `observeCommandSelected`, `observeVersionInfo`, `observeBatteryLevel`. |

### Opcodes and status

Opcodes are grouped into `UByte` constant objects: `PhoneToDeviceOpcode` (e.g. `SYNC_BEGIN`, `SYNC_COMMAND`, `SYNC_END`, `CLEAR_COMMANDS`, `SESSION_HELLO`, `BLE_OP_SHOW_APPROVAL`, `BLE_OP_SHOW_CHOICE`, `BLE_OP_NOTIFY`, `BLE_OP_CLAUDE_STATE`), `DeviceToPhoneOpcode` (`CMD_SELECTED`, `SYNC_ACK`, `VERSION_INFO`, `BATTERY_LEVEL`), and `OtaOpcode` (`OTA_BEGIN`, `OTA_END`, `OTA_ABORT`, `OTA_READY`, `OTA_PROGRESS`, `OTA_DONE`, `OTA_ERROR`). `SyncAckStatus` enumerates ACK codes (`OK`, `MALFORMED`, `NVS_WRITE_FAILED`, `TOO_MANY_COMMANDS`, `ORPHAN_COMMANDS`).

### Resources

`BleDeviceResource` (`name`, `address`, `rssi`), `BleConnectionStateResource` (`Disconnected` / `Connecting` / `Connected` / `Disconnecting`), `CommandEntry` (the wire form of a command node), `CommandSelectedEvent`, and `SyncAckResult` (sealed: `Ok` / `Error(statusCode)`). `BluetoothDisabledException` signals a disabled adapter.

## Key types (data-ble-impl)

| Type | Location | Responsibility |
|------|----------|----------------|
| `YamkCodecImpl` | commonMain | Implements `YamkCodec`. |
| `YamkProtocolImpl` | commonMain | Implements `YamkProtocol` on top of `BleConnection` + codec. |
| `PromptKnobBle` | commonMain | Constants: `SERVICE_UUID`, `MFR_ID`, `MFR_MAGIC`, `DEVICE_NAME`. |
| `BleScannerImpl`, `BleConnectionImpl`, `BlePermissionCheckerImpl`, `BleServiceControllerImpl` | per platform | `expect`/`actual` implementations. |

The Koin module `dataBleImplModule` (`data.ble.impl.di`) binds the codec and protocol and calls `provideBleAdapter()` to register the platform scanner, connection, permission checker, and service controller.

## Platform notes

| Platform | Scanner / connection | Service controller |
|----------|----------------------|--------------------|
| Android | Kable-backed | Starts/stops `BleConnectionService`, a foreground service that maintains the link and auto-reconnects on drops (`ACTION_CONNECT` / `ACTION_DISCONNECT`, `EXTRA_DEVICE_ADDRESS`). A `BlePairingReceiver` handles pairing broadcasts. |
| iOS / macOS | Kable-backed (CoreBluetooth) | No-op — CoreBluetooth maintains the connection natively. |
| Desktop (JVM) | No-op stubs | No-op — BLE is not supported on Desktop. |

On Android, addresses are MAC addresses; on iOS/macOS they are CoreBluetooth peripheral UUIDs.

## Dependencies

- `data-core`
- Kable (BLE)
- Koin (with `koin-android` on Android for the foreground service)
