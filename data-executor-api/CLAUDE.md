# data-executor-api — agent guide

Command-execution contract module. Interfaces only, no implementation. Applies the `dev.prompt.knob.io.convention.library` convention plugin.

## Responsibility

- `CommandExecutor` — the single entry point for running a command chosen on the device.
- `SystemSettingsOpener` — check/open OS settings (accessibility, notifications) the executor depends on.

## Rules

- API/Impl split: this is the **api** side; both interfaces are implemented per platform in `data-executor-impl`. Keep this module `commonMain`-only — no platform code, no Koin.
- `explicitApi` is enforced — mark declarations `public`/`internal`.
- Depends on `domain-core` and `data-core` (`implementation`). Return domain types (`Optional`) rather than platform results.

## Gotchas

- `execute` is `suspend` and returns `Optional`, not `Unit` — surface success/failure, don't throw for expected outcomes.
- Add new executor capabilities here first, then implement across all four platforms (Android/iOS/macOS/Desktop) so the `expect`/`actual` DI wiring stays complete.
