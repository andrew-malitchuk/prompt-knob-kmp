# data-ble-api

> Contracts and models for BLE scanning, connection, and the YAMK wire protocol used to talk to the PromptKnob device.

## Responsibility

Declares the platform-agnostic BLE surface: data sources for scanning/connecting, a permission checker, a background-service controller, and the YAMK codec/protocol that frames commands and decodes device notifications. Implementations live in `data-ble-impl`.

## Dependencies

| Depends on | Purpose |
|---|---|
| `domain-core` | `ClaudeState`, `FirmwareVersionModel` domain types |
| `data-core` | `Resource` marker interface for BLE models |

## Public API

### Data sources

| Interface | Description |
|---|---|
| `BleScanner` | `scan(): Flow<BleDeviceResource>` — advertises nearby devices |
| `BleConnection` | GATT connection: `state`, `observeNotifications()`, `connect(address)`, `disconnect()` |
| `BlePermissionChecker` | `hasPermissions()`, `requestPermissions()` |
| `BleServiceController` | Starts/stops the platform connection service |

### YAMK protocol / codec

| Type | Description |
|---|---|
| `YamkProtocol` | High-level ops: `syncCommands`, `clearCommands`, `observeCommandSelected`, `observeVersionInfo`, `observeBatteryLevel`, `sendShowApproval`, `sendShowChoice`, `sendNotify`, `sendClaudeState`, `sendSessionHello`, `sendDisconnectCommand`, MTU handling |
| `YamkCodec` | `encode(YamkFrame): ByteArray` / `decode(ByteArray): YamkFrame` |
| `YamkFrame` | Wire frame: `more`, `sequence`, `opcode: UByte`, `payload: ByteArray` |
| `PhoneToDeviceOpcode` | Outbound opcode constants (SYNC_*, CLEAR_COMMANDS, BLE_OP_*, SESSION_HELLO) |
| `DeviceToPhoneOpcode` | Inbound opcode constants (CMD_SELECTED, SYNC_ACK, VERSION_INFO, BATTERY_LEVEL) |
| `OtaOpcode` | OTA-firmware opcode constants |
| `SyncAckStatus` | Sync-ACK status-code constants (OK, MALFORMED, …) |
| `ClaudeState.blePayloadByte` | Extension mapping `ClaudeState` → single wire byte |

### Models / resources

| Type | Description |
|---|---|
| `BleDeviceResource` | Scanned device: `name`, `address`, `rssi` |
| `BleConnectionStateResource` | Enum: Disconnected / Connecting / Connected / Disconnecting |
| `CommandEntry` | A command/folder row synced to the device |
| `CommandSelectedEvent` | Carries `cmdId` from a CMD_SELECTED notification |
| `SyncAckResult` | Sealed: `Ok` / `Error(statusCode: UByte)` |
| `BluetoothDisabledException` | Thrown when the adapter is off |

## Platform notes

Pure `commonMain` contracts — no platform code. Applies to Android, iOS, macOS, and Desktop targets.

## Testing

```bash
./gradlew :data-ble-api:test
```
