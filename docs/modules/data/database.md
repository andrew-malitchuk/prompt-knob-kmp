# data-database

The `data-database` modules persist PromptKnob's local data with [Room](https://developer.android.com/kotlin/multiplatform/room) (Room KMP): the **command tree** the knob displays, the saved **presets**, and the **command history** log.

The pair splits into:

- **data-database-api** — data-source contracts and their resource models.
- **data-database-impl** — the Room database, entities, DAOs, and per-platform database builders.

## Purpose

The database stores three things:

- **Command nodes** — a hierarchical tree of folders and commands (each node has a `parentId`), including rotary and media-screen nodes and their per-direction command ids.
- **Presets** — named, JSON-serialised snapshots of a command configuration.
- **Command history** — an append-only log of executed commands with a status and timestamp.

Callers depend only on the API data-source interfaces and receive Room-backed implementations via Koin.

## Key types (data-database-api)

Data sources are composed from small capability interfaces in `data.database.api.source.datasource`:

| Base interface | Capability |
|----------------|-----------|
| `ReadableDataSource<T>` | one-shot reads |
| `ObservableDataSource<T>` | `Flow`-based observation |
| `UpsertableDataSource<T>` | insert/update |
| `DeletableDataSource` | delete by id |
| `ClearableDataSource` | delete all |
| `AppendableDataSource<T>` | append a single item |

The three domain data sources combine them:

| Data source | Composed from | Resource |
|-------------|---------------|----------|
| `CommandNodeDataSource` | Observable + Readable + Upsertable + Deletable | `CommandNodeResource` |
| `PresetDataSource` | Observable + Readable + Upsertable + Deletable | `PresetResource` |
| `CommandHistoryDataSource` | Observable + Appendable + Clearable | `CommandHistoryResource` |

### Resources

| Resource | Notable fields |
|----------|----------------|
| `CommandNodeResource` | `id`, `parentId`, `label`, `command`, `icon`, `isFolder`, `isRotary`, `isMediaScreen`, `isAgentScreen`, `sortOrder`, `commandType`, plus rotary/media ids (`cwCmdId`, `ccwCmdId`, `playPauseCmdId`, `prevCmdId`, `nextCmdId`) |
| `PresetResource` | `id`, `name`, `createdAt`, `updatedAt`, `configJson` |
| `CommandHistoryResource` | `id`, `commandId`, `commandLabel`, `commandType`, `executedAt`, `status` |

## Key types (data-database-impl)

| Type | Kind | Responsibility |
|------|------|----------------|
| `AppDatabase` | `@Database(version = 8)` RoomDatabase | Root database, constructed via `@ConstructedBy(AppDatabaseConstructor::class)`. |
| `CommandNodeEntity` / `PresetEntity` / `CommandHistoryEntity` | `@Entity` | Room row mappings. |
| `CommandNodeRoomDao` / `PresetRoomDao` / `CommandHistoryRoomDao` | `@Dao` | Query/mutation surface, including `deleteRecursive` for subtree deletion. |
| `CommandNodeDataSourceImpl` / `PresetDataSourceImpl` / `CommandHistoryDataSourceImpl` | class | Adapt DAOs to the API data-source interfaces. |

### Dependency injection

The Koin module `dataDatabaseImplModule` (`data.database.impl.di`) wires the platform database builder, exposes the `AppDatabase` and each DAO as singletons, and binds each `…DataSourceImpl` to its API interface.

## Platform notes

Room is instantiated per platform via `provideDatabaseBuilder()`. All platforms use the `BundledSQLiteDriver`. The database file is `prompt_knob.db`.

| Platform | Location |
|----------|----------|
| Android | `context.getDatabasePath("prompt_knob.db")` |
| iOS | `NSHomeDirectory()/Documents/prompt_knob.db` |
| macOS | `NSHomeDirectory()/Documents/prompt_knob.db` |
| Desktop (JVM) | `~/.promptknob/prompt_knob.db` |

Non-Android platforms register the `MIGRATION_6_7` and `MIGRATION_7_8` migrations.

## Dependencies

- `data-core` (base data-source interfaces / `Resource`)
- Room KMP + SQLite (bundled driver)
- Koin
