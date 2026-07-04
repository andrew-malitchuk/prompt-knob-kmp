# data-repository-impl — Agent Guide

Impl module. Implements the five `domain-repository-api` interfaces and does all domain-model ↔ data-resource mapping. This is the only place where the domain layer meets the data layer.

## Responsibility

- `internal` `*RepositoryImpl` for each interface: `Ble`, `Command`, `CommandHistory`, `Preset`, `Configure`.
- `internal` mappers under `core/mapper`, based on the `internal` `ModelResourceMapper<MODEL, RESOURCE>` (bidirectional `toModel` / `toResource`, built on `common-core`'s `Mapper`).
- `dataRepositoryImplModule` (Koin) binds every impl as a `singleOf(...) bind Interface::class`.

## Conventions

- Applies `dev.prompt.knob.io.convention.library`.
- Depends on the `*-api` data modules (preference, database, ble, executor, runtime) + `domain-core` + `domain-repository-api`; never on `*-impl` data modules.
- Packages: `data.repository.impl.source` (repos), `data.repository.impl.core.mapper[.base]` (mappers), `data.repository.impl.di` (module).

## Gotchas

- Everything except `dataRepositoryImplModule` is `internal` — repos and mappers must not leak. Consumers inject the domain interface.
- Adding a repository = new `*RepositoryImpl` + a `singleOf(...) bind ...` line in `dataRepositoryImplModule`, or Koin fails at runtime.
- Not every mapper implements `ModelResourceMapper`; several (`BleSessionMapper`, `CommandNodeMapper`, `PresetMapper`, `FirmwareVersionMapper`, `CommandNodeCacheMapper`) just expose `toModel` / `toResource` `Mapper` fields. Match the neighbouring file when adding one.
- `BleRepositoryImpl` composes `CommandRepository` + `CommandHistoryRepository` rather than hitting sources directly — keep that layering.
