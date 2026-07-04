# data-runtime

The `data-runtime` modules hold PromptKnob's volatile, in-memory runtime state — the state that lives only for the current app session and never touches disk. This is the shared, observable "session scratchpad" that the BLE flow, command routing, and the UI read from.

The pair splits into:

- **data-runtime-api** — data-source contracts and their resource models.
- **data-runtime-impl** — coroutine `Flow`-backed implementations.

## Purpose

Some state is inherently transient: the currently connected knob, its firmware version and battery level, the in-memory command cache indexed by id, the currently selected command, and knob-motion pulses. This module exposes that state as hot `Flow`s so any layer can observe it without a database round-trip.

## Key types (data-runtime-api)

| Type | Kind | Responsibility |
|------|------|----------------|
| `BleSessionDataSource` | interface | Holds the connected device, firmware version, and battery level. `observe`/`get`/`set` for each. |
| `CommandCacheDataSource` | interface | In-memory command map (`Map<Int, CommandNodeCacheResource>`), the observable selected command, and the raw selected command id stream. |
| `MoveDetectorDataSource` | interface | Hot stream of knob-motion pulses: `observeMotion(): Flow<Unit>`, `emitMotion()`. |

### Resources

| Resource | Fields |
|----------|--------|
| `BleSessionResource` | `address` (MAC on Android, UUID on iOS/macOS), `name` |
| `FirmwareVersionResource` | `major`, `minor`, `patch` |
| `CommandNodeCacheResource` | `id`, `parentId`, `label`, `isFolder`, `isRotary`, `isMediaScreen`, `sortOrder`, `command`, `icon`, `commandType`, plus rotary/media ids (`cwCmdId`, `ccwCmdId`, `playPauseCmdId`, `prevCmdId`, `nextCmdId`) |

## Key types (data-runtime-impl)

| Type | Backing state |
|------|---------------|
| `BleSessionDataSourceImpl` | `MutableStateFlow` per field (device / firmware / battery). |
| `CommandCacheDataSourceImpl` | `MutableStateFlow` for the cache map; buffered `MutableSharedFlow` (buffer 64) for selected command and raw id. |
| `MoveDetectorDataSourceImpl` | Unbuffered hot `MutableSharedFlow<Unit>`. |

The Koin module `dataRuntimeImplModule` (`data.runtime.impl.di`) binds each `…DataSourceImpl` to its API interface as a singleton, so the runtime state is shared process-wide.

## Platform notes

Everything is in `commonMain` — the runtime state is pure Kotlin/coroutines with no platform-specific behaviour.

## Dependencies

- `data-core` (base `Resource` type)
- kotlinx-coroutines (`StateFlow` / `SharedFlow`)
- Koin
