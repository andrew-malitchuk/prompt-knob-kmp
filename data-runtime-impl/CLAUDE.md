# CLAUDE.md — data-runtime-impl

In-memory implementation of `data-runtime-api`. Applies `dev.prompt.knob.io.convention.library`.

## Responsibility

Provides the coroutine-backed implementations of the runtime data sources and the `dataRuntimeImplModule` Koin module.

## Rules

- **impl** half of the api/impl split. Only `dataRuntimeImplModule` is public API; the `*Impl` classes are `internal` and bound behind the `data-runtime-api` interfaces via `singleOf(...) bind ...`.
- All three sources are registered as Koin **singletons** — they hold shared session/cache state, so a single instance per graph is required. Do not change them to `factory`.

## Gotchas

- State here is purely in-memory: it does **not** survive process death or a BLE disconnect. Persist through `data-database-*` if durability is needed.
- Hot streams (selection, raw command id, motion) have no replay — honor that in the impls; don't switch to a replaying `SharedFlow`/`StateFlow` where the contract specifies fire-and-forget.
