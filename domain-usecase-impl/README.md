# domain-usecase-impl

> Concrete use case implementations that bridge repository contracts to the presentation layer, plus their Koin wiring.

## Responsibility

Implements every use case interface from `domain-usecase-api` by delegating to the corresponding repository (or executor / MCP server). Fallible calls run through the internal `resultLauncher` helper, which maps infrastructure exceptions into the `Failure` hierarchy. Exposes Koin modules that register all implementations as singletons.

## Dependencies

| Depends on | Purpose |
|---|---|
| `domain-usecase-api` | Use case interfaces to implement (`api(...)`) |
| `domain-repository-api` | Repository contracts to delegate to |
| `domain-core` | Domain models, `Optional`, `Failure` |
| `data-executor-api` | Command execution on the host platform |
| `data-mcp-api` | MCP / Claude-hook servers (**macOS only**) |
| Koin, kotlinx-serialization, kotlinx-datetime | DI, export payloads, timestamps |

## Public API

The public surface is the Koin modules; individual `*Impl` classes are registered internally.

| Symbol | Source set | Description |
|---|---|---|
| `domainUseCaseImplModule` | commonMain | Registers all common use case impls (ble, command, preset, configuration, history, executor) as singletons bound to their `domain-usecase-api` interfaces |
| `domainMcpUseCaseModule` | macosMain | Registers the MCP / Claude-hook use case impls: `StartMcpServerUseCaseImpl`, `StopMcpServerUseCaseImpl`, `StartClaudeHookServerUseCaseImpl`, `StopClaudeHookServerUseCaseImpl` |

### Internal building blocks

| Symbol | Description |
|---|---|
| `resultLauncher(errorMapper, block)` | `internal` helper: runs `block`, re-emits thrown `Failure`s as-is, and maps other `Throwable`s via `errorMapper` |
| `CommandNode` mapper | `internal` `toExportModel()` / `toModel()` extensions bridging `CommandNodeModel` and `CommandNodeExportModel` |
| Gallery presets | Built-in `GalleryPreset` data backing `GetGalleryPresetsUseCaseImpl` |

There are ~47 `*Impl` classes under `domain.usecase.impl.source.usecase.<area>`, one per interface.

## Usage

```kotlin
startKoin {
    modules(
        domainUseCaseImplModule,
        // on macOS only:
        // domainMcpUseCaseModule,
    )
}
```

## Testing

```bash
./gradlew :domain-usecase-impl:test
```
