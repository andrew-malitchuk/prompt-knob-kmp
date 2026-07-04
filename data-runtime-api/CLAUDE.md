# CLAUDE.md — data-runtime-api

Pure-contract module for volatile in-memory runtime state. Applies `dev.prompt.knob.io.convention.library`. Explicit API mode is enforced.

## Responsibility

Declares in-memory data-source interfaces (`BleSessionDataSource`, `CommandCacheDataSource`, `MoveDetectorDataSource`) and their `Resource` models. This is **runtime/session** state — nothing here is persisted. For durable storage use `data-database-api` instead.

## Rules

- **api** half of the api/impl split. Only `data-core` as a dependency. No Koin, no coroutine implementation details beyond `Flow` in signatures, no platform code.
- Resource models implement `data.core.source.resource.Resource` and stay free of domain types (`commandType` is a `String`, not `CommandTypeModel`).

## Gotchas

- These streams intentionally have **no replay buffer** — missed selection/motion events are by design (real-time signals). Don't add replay in the contract expecting late subscribers to catch up.
- BLE session values are nullable and reset on disconnect; treat `null` as "no active session", not an error.
