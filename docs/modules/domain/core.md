# domain-core

`domain-core` defines PromptKnob's domain vocabulary: the models that describe the knob, its commands, presets, connection and firmware state, plus the base `Model` marker and the `Failure`/`Optional` result types every layer shares. It is the pure heart of the app — no platform, framework, or I/O concerns.

## Purpose

Every other layer speaks in these models. Data sources map their resources to them, use cases operate on them, and the UI renders them. Because the module is pure `commonMain` Kotlin, it compiles unchanged on every target.

## Base types

| Type | Package | Responsibility |
|------|---------|----------------|
| `Model` | `domain.core.source.model.base` | Marker interface all domain models implement. |
| `Failure` | `domain.core.source.monad` | Sealed error hierarchy (see below). |
| `Optional` | `domain.core.source.monad` | `typealias Optional = Result<Unit>` — a success/failure result carrying no value. |

### Failure hierarchy

`Failure : Throwable` splits into:

- `Failure.Technical` — `Database`, `Platform`, `Network`, `Preference` (each wraps a `cause`).
- `Failure.Logic` — `NotFound` (data object), `Business(message)`.

## Models

All models live in `domain.core.source.model` and implement `Model`.

| Model | Kind | Notes |
|-------|------|-------|
| `BleDeviceModel` | data class | `name?`, `address`, `rssi`. |
| `DeviceConnectionModel` | data class | `device: BleDeviceModel?`, `state: ConnectionStateModel`. |
| `ConnectionStateModel` | enum | `Disconnected`, `Connecting`, `Connected`, `Disconnecting`. |
| `FirmwareVersionModel` | data class | `major`, `minor`, `patch`. |
| `OtaStateModel` | sealed class | `Waiting`, `Receiving(bytesReceived, totalBytes)`, `Verifying`, `Done`, `Error(code)`. |
| `OtaErrorCodeModel` | enum | `CRC`, `FLASH`, `TOO_LARGE`, `BATTERY`, `BAD_BEGIN`, `DISCONNECTED`. |
| `ClaudeState` | enum | `IDLE`, `WORKING`, `WAITING`, `DONE`, `ERROR`. |
| `CommandNodeModel` | data class | A node in the command tree (folder, command, rotary, or media node). |
| `CommandTypeModel` | enum | `SYSTEM`, `PROMPT`, `SHELL`, `SHORTCUT`. |
| `CommandNodeExportModel` | `@Serializable` data class | Serialisable form of a command node. |
| `CommandExportPayloadModel` | `@Serializable` data class | `version`, `exportedAt`, `commands: List<CommandNodeExportModel>`. |
| `CommandHistoryEntryModel` | data class | A logged command execution. |
| `PresetModel` | data class | `id`, `name`, `createdAt`, `updatedAt`, `configJson`. |
| `GalleryPreset` | data class | `name`, `description`, `configJson` — a built-in starter preset. |
| `SystemCommandOption` | data class | `command`, `label`, `description` — a selectable system action. |
| `ThemeModel` | enum | `Light("light")`, `Dark("dark")`, `MaterialU("materialu")`. |
| `OnboardingModel` | data class | `isCompleted`. |

## Dependencies

- kotlinx-serialization (for the `@Serializable` export models)

Nothing else — no platform, database, or networking dependencies.

## Platform notes

Everything is in `commonMain`; there are no platform-specific source sets.
