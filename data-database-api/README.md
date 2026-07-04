# data-database-api

> Local persistence data-source contracts and resource models for the command tree, command history, and presets.

## Responsibility

Declares the interfaces (data sources) and resource models for everything PromptKnob stores locally. Implementations live in `data-database-impl` (Room). Data sources are composed from small capability interfaces (`ObservableDataSource`, `ReadableDataSource`, `UpsertableDataSource`, `DeletableDataSource`, `ClearableDataSource`, `AppendableDataSource`) so each table exposes only the operations it needs.

## Dependencies

| Depends on | Purpose |
|---|---|
| `data-core` | `Resource` marker interface for resource models |

## Public API

### Capability interfaces

| Interface | Contract |
|---|---|
| `ObservableDataSource<T>` | `observeAll(): Flow<List<T>>` |
| `ReadableDataSource<T>` | `getAll(): List<T>`, `getById(id: Int): T?` |
| `UpsertableDataSource<T>` | `upsert(resource: T): Int` |
| `DeletableDataSource` | `deleteById(id: Int)`, `deleteAll()` |
| `ClearableDataSource` | `clearAll()` |
| `AppendableDataSource<T>` | `insert(resource: T): Long` (append-only) |

### Table data sources

| Interface | Composed from | Notes |
|---|---|---|
| `CommandNodeDataSource` | Observable + Readable + Upsertable + Deletable | Adds `observeByParentId`, `getByParentId`, `deleteByParentIdRecursive` |
| `CommandHistoryDataSource` | Observable + Appendable + Clearable | Append-only execution log |
| `PresetDataSource` | Observable + Readable + Upsertable + Deletable | Saved command-tree snapshots |

### Resource models

| Model | Key fields |
|---|---|
| `CommandNodeResource` | `id`, `parentId`, `label`, `command`, `isFolder`, `isRotary`, `isMediaScreen`, `isAgentScreen`, `commandType`, rotary/media `cmdId` fields |
| `CommandHistoryResource` | `id`, `commandId`, `commandLabel`, `commandType`, `executedAt`, `status` |
| `PresetResource` | `id`, `name`, `createdAt`, `updatedAt`, `configJson` |

## Testing

```bash
./gradlew :data-database-api:test
```
