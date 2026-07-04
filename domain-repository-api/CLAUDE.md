# CLAUDE.md — domain-repository-api

API-only module: the five repository interfaces the domain layer depends on. Implementations live in `data-repository-impl`; this module contains **no** concrete classes.

## Convention plugin

`dev.prompt.knob.io.convention.library`. Common-only, `explicitApi` enforced.

## Rules

- Interfaces only. If you add a repository, add its interface here and its `*Impl` in `data-repository-impl`, then register it in that module's Koin module.
- Speak only in `domain-core` types (`domain.core.source.model.*`, `Optional`, `Failure`). Never reference data-layer entities/DTOs or platform types here.
- Observable state → `Flow<T>`; one-shot fallible ops → `Optional` / `Result<T>` (no thrown exceptions in the contract).
- Depends only on `domain-core`. Do not add data or use-case dependencies.

## Interfaces

`BleRepository`, `CommandRepository`, `CommandHistoryRepository`, `PresetRepository`, `ConfigureRepository`.
