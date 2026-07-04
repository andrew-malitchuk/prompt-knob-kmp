# data-database-impl

> Room-based implementation of the local persistence contracts declared in `data-database-api`.

## Responsibility

Implements every `data-database-api` data source on top of a Room `AppDatabase` with three tables: command nodes, command history, and presets. Provides the per-platform database builder and a Koin module that wires DAOs and data-source implementations to their interfaces.

## Dependencies

| Depends on | Purpose |
|---|---|
| `data-database-api` | Interfaces and resource models to implement (`api` dependency) |
| `data-core` | `Resource` marker interface |
| `common-core` | `Mapper` helpers used by entity↔resource mappers |
| Room + bundled SQLite | Multiplatform local database engine |
| Koin | DI wiring |

## Public API

| Symbol | Description |
|---|---|
| `dataDatabaseImplModule` | Koin module registering the platform database builder, the three Room DAOs, and the data-source impls bound to their `data-database-api` interfaces |

### Internal building blocks

| Component | Role |
|---|---|
| `AppDatabase` | Room `@Database` (version 8) aggregating all entities and DAOs |
| `CommandNodeDataSourceImpl` / `CommandHistoryDataSourceImpl` / `PresetDataSourceImpl` | Data-source implementations backed by Room DAOs |
| `CommandNodeEntity` / `CommandHistoryEntity` / `PresetEntity` | Room `@Entity` rows |
| `CommandNodeRoomDao` / `CommandHistoryRoomDao` / `PresetRoomDao` | Room DAOs |
| `CommandNodeMapper` / `CommandHistoryMapper` / `PresetMapper` | Entity ↔ resource mappers |
| `provideDatabaseBuilder()` | `expect`/`actual` Koin extension supplying the platform `AppDatabase` builder |

## Platform notes

Room is configured for KMP: the schema directory is `schemas/` and KSP (`room.compiler`) is applied per target (Android, iOS arm64/x64/simulator, Desktop, macOS x64/arm64). `provideDatabaseBuilder()` has an `actual` per platform (`androidMain`, `iosMain`, `desktopMain`, `macosMain`) selecting the correct file location / driver.

## Testing

```bash
./gradlew :data-database-impl:test
```
