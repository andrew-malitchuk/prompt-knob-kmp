# CLAUDE.md — data-database-api

Pure-contract module for local persistence. Applies `dev.prompt.knob.io.convention.library`. Explicit API mode is enforced — every declaration needs an explicit visibility modifier.

## Responsibility

Declares data-source interfaces and their `Resource` resource models. Contains **no implementation** — Room lives in `data-database-impl`.

## Rules

- This is the **api** half of the api/impl split. Keep it dependency-light: only `data-core` (for the `Resource` marker). No Room, no Koin, no platform code here.
- Data sources are composed from the small capability interfaces (`ObservableDataSource`, `ReadableDataSource`, `UpsertableDataSource`, `DeletableDataSource`, `ClearableDataSource`, `AppendableDataSource`). Reuse these when adding a new table instead of redeclaring CRUD methods.
- Resource models implement `data.core.source.resource.Resource` and must stay free of domain types. `commandType` is a `String` (serialized `CommandTypeModel` name), not the enum, to avoid a domain dependency.

## Gotchas

- Adding a field to `CommandNodeResource`/`PresetResource`/`CommandHistoryResource` implies a Room schema/entity change in `data-database-impl` (version bump + migration). Coordinate both modules.
- `id == 0` is the "not yet persisted" convention across resource models.
