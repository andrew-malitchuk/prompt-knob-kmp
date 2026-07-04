# common-core

`common-core` is the lowest-level shared module in PromptKnob. It holds tiny, dependency-light building blocks that every other module can reuse without dragging in platform or domain concerns.

## Purpose

The module standardises two recurring patterns across the codebase:

- **Mapping** between layers (for example, database entity to domain model) through a single functional interface.
- **Launching coroutine work** with a consistent shape for loading state, results, error handling, and optional debouncing.

It has no dependencies of its own and lives entirely in `commonMain`, so it compiles on every target (Android, iOS, macOS, Desktop).

## Key types

| Type | Kind | Package | Responsibility |
|------|------|---------|----------------|
| `Mapper<I, O>` | fun interface | `common.core.source.mapper` | Converts an input `I` into an output `O` via `map(input: I): O`. |
| `executeCoroutine<T>` | function | `common.core.source.execute` | Launches a suspend `request` on a scope, wiring optional `loading`, `result`, and `errorBlock` callbacks with optional `debounce`. Returns a `Job`. |
| `executeResult<T>` | function | `common.core.source.execute` | Same shape as `executeCoroutine`, but `request` returns a `Result<T>?`, so success and failure are unwrapped for the caller. Returns a `Job`. |

### Mapper

```kotlin
public fun interface Mapper<I, O> {
    public fun map(input: I): O
}
```

### Execute helpers

Both helpers take a `CoroutineContext`, a `CoroutineScope`, an optional `debounce` delay, and `loading` / `result` / `errorBlock` callbacks. `executeCoroutine` runs a plain suspend block; `executeResult` runs a block returning `Result<T>?` and dispatches to `result` on success or `errorBlock` on failure.

```kotlin
val job = executeResult(
    context = Dispatchers.Default,
    scope = viewModelScope,
    loading = { isLoading -> /* update state */ },
    result = { value -> /* handle value */ },
    errorBlock = { throwable -> /* handle error */ },
) {
    repository.doWork()
}
```

## Dependencies

None beyond the Kotlin standard library and coroutines. Keeping `common-core` free of external dependencies lets any module depend on it safely.

## Platform notes

Everything is in `commonMain`; there are no platform-specific source sets.
