# data-runtime-impl

> In-memory implementations of the runtime state contracts from `data-runtime-api`.

## Responsibility

Implements the three runtime data sources using coroutine state/flow holders, and exposes a Koin module binding each implementation to its `data-runtime-api` interface. All state is process-local and non-persistent.

## Dependencies

| Depends on | Purpose |
|---|---|
| `data-runtime-api` | Interfaces and resource models to implement (`api` dependency) |
| `data-core` | `Resource` marker interface |
| Koin | DI wiring |

## Public API

| Symbol | Description |
|---|---|
| `dataRuntimeImplModule` | Koin module binding `BleSessionDataSourceImpl`, `CommandCacheDataSourceImpl`, and `MoveDetectorDataSourceImpl` to their `data-runtime-api` interfaces |

### Internal implementations

| Class | Implements |
|---|---|
| `BleSessionDataSourceImpl` | `BleSessionDataSource` |
| `CommandCacheDataSourceImpl` | `CommandCacheDataSource` |
| `MoveDetectorDataSourceImpl` | `MoveDetectorDataSource` |

## Testing

```bash
./gradlew :data-runtime-impl:test
```
