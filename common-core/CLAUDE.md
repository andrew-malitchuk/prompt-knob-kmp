# common-core — Agent Guide

Leaf utility module. No internal project dependencies — nothing may make this module depend on another project module, or you create a cycle.

## Responsibility

Two things only:
- `Mapper<I, O>` — functional interface (`fun map(input: I): O`) used for model/resource conversions everywhere else in the codebase.
- `executeCoroutine` / `executeResult` — coroutine launch helpers in `common.core.source.execute` that route a suspend `request` to `loading` / `result` / `errorBlock` callbacks, with optional `debounce`.

## Conventions

- Applies `dev.prompt.knob.io.convention.library`.
- Explicit API mode is on — every new declaration needs an explicit `public` / `internal`.
- Package root: `common.core.source.*` (`mapper`, `execute`).

## Gotchas

- `executeResult` ignores a `null` `Result` — neither `result` nor `errorBlock` fires. This is intentional (caller opted out); do not "fix" it.
- `loading(false)` runs in `finally`, including on cancellation. Keep it that way so callers never stick in a loading state.
- `errorBlock` receives `CancellationException` too — do not add cancellation-swallowing here; that is the caller's choice.
- Keep this module dependency-free. If a helper needs a data/domain type, it belongs in a higher layer.
