# data-preference-api — Agent Guide

API module: declares preference source contracts and their models only. No implementations, no Koin — those live in `data-preference-impl`.

## Responsibility

- `PreferenceSource<T>` base contract: `suspend getData()`, `suspend setData(data)`, `observeData(): Flow<T>`.
- One `*PreferenceSource` interface per preference type, each a typed `PreferenceSource<*Preference>`.
- Matching `*Preference` data classes (each implements `data-core`'s `Resource`, with sensible defaults).

Current preference types: Theme, Onboarding, Language, LastDevice (last connected BLE device), Mcp (MCP server enabled), ClaudeHook (Claude hook server enabled).

## Conventions

- Applies `dev.prompt.knob.io.convention.library`.
- Explicit API mode — interfaces and models are `public`.
- Packages: `data.preference.api.source.datasource[.base]` for contracts, `...source.model` for models.
- Only project dependency is `data-core` (for `Resource`).

## Gotchas

- Adding a preference is a two-step edit: add the model + source interface HERE, then add the `*Impl` and register it in `dataPreferenceImplModule` in `data-preference-impl`. Do not put storage logic in this module.
- Keep defaults in the model's constructor / companion constants — impls read them via `getData()`.
