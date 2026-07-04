# domain-core

> Pure domain models, error monads, and parsers shared across the domain and presentation layers.

## Responsibility

Holds the platform-agnostic vocabulary of the app: the domain models describing BLE devices, command trees, presets, OTA firmware updates, and app configuration, plus the `Failure` error hierarchy and `Optional` result alias used by all use cases and repositories.

## Dependencies

| Depends on | Purpose |
|---|---|
| `kotlinx-serialization-json` | `@Serializable` command export/import payload models |

No internal project dependencies — this is the base of the domain layer.

## Public API

### Base

| Type | Description |
|---|---|
| `Model` | Marker interface implemented by domain models |

### BLE & connection

| Type | Description |
|---|---|
| `BleDeviceModel` | A discovered/known BLE device |
| `ConnectionStateModel` (enum) | Connection lifecycle states |
| `DeviceConnectionModel` | Aggregated connection snapshot |
| `FirmwareVersionModel` | Parsed `major.minor.patch` firmware version |
| `OtaStateModel` (sealed) | OTA progress — includes `Receiving` and `Error` subclasses |
| `OtaErrorCodeModel` (enum) | OTA failure codes |

### Commands & history

| Type | Description |
|---|---|
| `CommandNodeModel` | A node in the command tree (folder or leaf command) |
| `CommandTypeModel` (enum) | `SYSTEM`, `PROMPT`, `SHELL`, `SHORTCUT` execution kinds |
| `SystemCommandOption` | A preset system command available on the platform |
| `CommandHistoryEntryModel` | A logged command execution |
| `CommandExportPayloadModel`, `CommandNodeExportModel` | Serializable command import/export payloads |

### Presets & config

| Type | Description |
|---|---|
| `PresetModel` | A saved command-tree preset |
| `GalleryPreset` | A curated built-in gallery preset |
| `ThemeModel` (enum) | App theme with `mode` string |
| `OnboardingModel` | Onboarding completion state |
| `ClaudeState` (enum) | Claude hook activity state |

### Monads & utilities

| Type | Description |
|---|---|
| `Failure` (sealed) | Error hierarchy: `Technical` (`Database`, `Platform`, `Network`, `Preference`) and `Logic` (`Business`) |
| `Optional` | `typealias` for `Result<Unit>` |
| `BluetoothUnavailableException` | Thrown when Bluetooth is off/unavailable |
| `parseFirmwareVersion(text)` | Parses a `FirmwareVersionModel?` from a version string |

## Testing

```bash
./gradlew :domain-core:test
```
