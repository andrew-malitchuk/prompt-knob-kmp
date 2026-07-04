# CLAUDE.md — data-database-impl

Room implementation of `data-database-api`. Applies `dev.prompt.knob.io.convention.library`, plus the Room and KSP Gradle plugins.

## Responsibility

Provides the concrete Room `AppDatabase`, entities, DAOs, entity↔resource mappers, per-platform database builders, and the `dataDatabaseImplModule` Koin module.

## Rules

- This is the **impl** half. Only `dataDatabaseImplModule` is meant to be consumed externally; everything else (`AppDatabase`, entities, DAOs, impls) is `internal`. Do not leak Room types across the module boundary — expose data through the `data-database-api` interfaces only.
- Map at the boundary: DAOs return entities; data-source impls convert to/from `data-database-api` resources via the `*Mapper`s. Keep entities and resources separate — do not reuse one for the other.

## Gotchas

- `AppDatabase` is at schema **version 8** with `exportSchema = true`. Any entity change requires a version bump **and** a migration; schemas are written to `schemas/`.
- Room KMP requires KSP applied **per target** (see `build.gradle.kts`: `kspAndroid`, `kspIos*`, `kspDesktop`, `kspMacos*`). When adding a new target, add its `ksp<Target>` line or codegen silently won't run.
- `provideDatabaseBuilder()` is `expect`/`actual`; a new platform target needs an `actual` implementation or DI resolution fails at runtime.
