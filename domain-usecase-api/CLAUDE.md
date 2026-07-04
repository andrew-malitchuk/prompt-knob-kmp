# CLAUDE.md — domain-usecase-api

API-only module: single-responsibility use case interfaces, the sole entry point from presentation into the domain. Implementations live in `domain-usecase-impl`.

## Convention plugin

`dev.prompt.knob.io.convention.library` (+ kotlin-serialization). `explicitApi` enforced.

## Structure

Interfaces are grouped under `domain.usecase.api.source.usecase.<area>`: `ble`, `command`, `preset`, `configuration`, `mcp`, `history`, `executor`. Keep new use cases in the matching area package (create one if the area is new).

## Rules

- One interface = one operation. Name it `<Verb><Noun>UseCase`; add its impl to `domain-usecase-impl` and register it there in Koin.
- Speak only in `domain-core` types. Fallible ops return `Optional` / `Result<T>`; observable state returns `Flow<T>`.
- Platform-varying behavior goes through `expect`/`actual` — see `availableCommandTypes()` (common `expect`, per-target `actual` under `androidMain`/`iosMain`/`macosMain`/`desktopMain`).
- MCP use cases are declared in common here, but only wired/implemented on macOS in the impl module — don't assume they resolve on every target.
- Depends only on `domain-core`. No repository or data dependencies.
