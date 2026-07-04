# data-runtime-api

> Contracts for volatile, in-memory runtime state that lives only for the duration of the app / BLE session.

## Responsibility

Declares the in-memory data sources that hold transient state — the active BLE session, the command-tree cache and selection events, and move-detector motion signals. None of this is persisted; it resets on disconnect / process death. Implementations live in `data-runtime-impl`.

## Dependencies

| Depends on | Purpose |
|---|---|
| `data-core` | `Resource` marker interface for resource models |

## Public API

### Data sources

| Interface | Purpose | Key operations |
|---|---|---|
| `BleSessionDataSource` | Active BLE session state; all values reset to null on disconnect | `observe/get/setDevice`, `observe/get/setFirmwareVersion`, `observeBatteryLevel`, `setBatteryLevel` |
| `CommandCacheDataSource` | In-memory command-tree cache + selection events (no replay) | `observe/getCacheSnapshot`, `setCache`, `observe/emitSelectedCommand`, `observe/emitRawCommandId` |
| `MoveDetectorDataSource` | Real-time motion events, no replay buffer | `observeMotion(): Flow<Unit>`, `emitMotion()` |

### Resource models

| Model | Fields |
|---|---|
| `BleSessionResource` | `address` (MAC on Android, UUID on iOS/macOS), `name` |
| `FirmwareVersionResource` | `major`, `minor`, `patch` |
| `CommandNodeCacheResource` | `id`, `parentId`, `label`, `isFolder`, `isRotary`, `isMediaScreen`, `sortOrder`, `command`, `icon`, `commandType` (String), rotary/media `cmdId` fields |

## Notes

`commandType` on `CommandNodeCacheResource` is a `String` (not the domain enum) so this module stays domain-free; callers convert via `CommandTypeModel.valueOf()` with a safe fallback. Selection and motion streams are hot with no replay — late subscribers only see events emitted after they subscribe.

## Testing

```bash
./gradlew :data-runtime-api:test
```
