# CLAUDE.md — domain-usecase-impl

Implements the interfaces from `domain-usecase-api`. Each `*Impl` delegates to a repository / executor / MCP server; the module's only public surface is its Koin modules.

## Convention plugin

`dev.prompt.knob.io.convention.library` (+ kotlin-serialization). `explicitApi` enforced.

## Structure

`*Impl` classes sit under `domain.usecase.impl.source.usecase.<area>` (ble, command, preset, configuration, history, executor; mcp lives in `macosMain`). Shared helpers are in `domain.usecase.impl.core`. DI is in `domain.usecase.impl.di`.

## Rules

- Adding a use case: implement the API interface as `<Name>Impl`, then register it in `domainUseCaseImplModule` (commonMain) — or `domainMcpUseCaseModule` for MCP work (macosMain).
- Wrap fallible delegation in the internal `resultLauncher(errorMapper) { ... }`. Domain `Failure`s thrown inside propagate as-is; map other throwables to a `Failure` subtype via `errorMapper`. Do not use bare `runCatching`.
- Return only `domain-core` types (`Optional`, `Result<T>`, domain models). No data-layer entities leak out.
- Impls are `internal`; only the Koin modules are `public`.

## Gotchas

- `data-mcp-api` and `domainMcpUseCaseModule` are **macOS-only** — MCP use cases won't resolve on Android/iOS/Desktop. Wire `domainMcpUseCaseModule` only in the macOS Koin setup.
- Two Koin modules exist: `domainUseCaseImplModule` (commonMain) and `domainMcpUseCaseModule` (macosMain). Register both on macOS.
