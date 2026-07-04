# CLAUDE.md — domain-core

Base of the domain layer: pure Kotlin models, the `Failure`/`Optional` monads, and small parsers. No Android/iOS APIs, no DI, no I/O.

## Convention plugin

`dev.prompt.knob.io.convention.library` (+ kotlin-serialization). Common-only source set.

## Rules

- Every domain model implements `Model` (in `domain.core.source.model.base`). Keep new models here, not in feature modules.
- Only add `@Serializable` to models that are genuinely persisted/exported (currently the command export payloads). Keep serialization concerns out of the pure models.
- Represent fallible domain operations with `Optional` (= `Result<Unit>`) or `Result<T>`, and map platform/logic errors onto the `Failure` sealed hierarchy — do not invent parallel error types elsewhere.
- No dependencies on data, repository, or use-case modules. Everything above depends on this; keep it a leaf.

## Gotchas

- `CommandTypeModel` values map to platform-specific executors (`SYSTEM`/`PROMPT`/`SHELL`/`SHORTCUT`) — changing the enum ripples into every executor impl.
- `OtaStateModel` is a sealed class with data subclasses (`Receiving`, `Error`); handle new states exhaustively at call sites.
