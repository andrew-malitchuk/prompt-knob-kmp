# Module Structure

PromptKnob is built from **35 Gradle modules**. This page lists every module (as declared in `settings.gradle.kts`), grouped by layer, and describes how dependencies flow between them.

## Application

The platform entry points and the shared Compose application.

| Module | Role |
|--------|------|
| `compose-application` | Shared Compose Multiplatform app: `App()` composable, `initKoin()` DI aggregation, macOS entry point |
| `aos-application` | Android `Application`/`MainActivity` host |

## Presentation — feature modules

One module per screen (or small screen cluster), each an Orbit MVI feature. All apply the `feature` + `di` convention plugins.

| Module | Screen(s) |
|--------|-----------|
| `presentation-feature-splash` | Splash |
| `presentation-feature-onboarding` | Onboarding |
| `presentation-feature-home` | Home |
| `presentation-feature-settings` | Settings |
| `presentation-feature-about` | About |
| `presentation-feature-devices` | Device list / scanning |
| `presentation-feature-device` | Connected-device dashboard |
| `presentation-feature-preset` | Presets |
| `presentation-feature-command` | Command list and command form |

## Presentation — core modules

Cross-feature presentation infrastructure.

| Module | Role |
|--------|------|
| `presentation-core-navigation-api` | Sealed `Destination`, `AppNavigator` contract |
| `presentation-core-navigation-impl` | `NavigationHost`, `AppNavigatorImpl`, saved-state config |
| `presentation-core-ui` | Atomic-design UI kit (atoms / molecules / organisms) |
| `presentation-core-styling` | Theme, colors, typography (Space Grotesk) |
| `presentation-core-localisation` | Localised strings |

## Domain

Business logic — models, use cases, and repository contracts.

| Module | Role |
|--------|------|
| `domain-core` | Domain models (`CommandNodeModel`, `ThemeModel`, `PresetModel`, `ClaudeState`, …) and the `Optional` / `Failure` monads |
| `domain-repository-api` | Repository interfaces (`CommandRepository`, `PresetRepository`, `BleRepository`, `ConfigureRepository`, `CommandHistoryRepository`) |
| `domain-usecase-api` | Use-case interfaces (`ExecuteCommandUseCase`, `StartMcpServerUseCase`, `SetClaudeHookEnabledUseCase`, and many more) |
| `domain-usecase-impl` | Use-case implementations + `domainUseCaseImplModule` (and macOS-only `domainMcpUseCaseModule`) |

## Data

Persistence, hardware, execution, and integration.

| Module | Role |
|--------|------|
| `data-core` | Data-layer marker type (`Resource`) |
| `data-repository-impl` | Repository implementations + `dataRepositoryImplModule` |
| `data-ble-api` | YAMK protocol contracts: `YamkFrame`, `YamkCodec`, `YamkProtocol`, opcodes, scanner/connection interfaces |
| `data-ble-impl` | BLE implementation (Kable): `BleScannerImpl`, `BleConnectionImpl`, `BleConnectionService`, `YamkCodecImpl`, `YamkProtocolImpl` + `dataBleImplModule` |
| `data-database-api` | Database contracts (`CommandNodeDataSource`, `CommandHistoryDataSource`, `PresetDataSource`) |
| `data-database-impl` | Room `AppDatabase`, DAOs, data-source impls + `dataDatabaseImplModule` |
| `data-preference-api` | Preference contracts (`ThemePreferenceSource`, `McpPreferenceSource`, `ClaudeHookPreferenceSource`, …) |
| `data-preference-impl` | Settings-backed preference impls + `dataPreferenceImplModule` |
| `data-runtime-api` | In-memory session contracts (`BleSessionDataSource`, `CommandCacheDataSource`, `MoveDetectorDataSource`) |
| `data-runtime-impl` | Runtime data-source impls + `dataRuntimeImplModule` |
| `data-executor-api` | Command-execution contracts (`CommandExecutor`, `SystemSettingsOpener`) |
| `data-executor-impl` | Platform executors (`MacOsCommandRouter`, `ShellCommandExecutor`, …) + `dataExecutorImplModule` |
| `data-mcp-api` | MCP contracts (`McpServer`, `ClaudeHookServer` + configs) |
| `data-mcp-impl` | macOS Ktor servers (`McpServerImpl`, `ClaudeHookServerImpl`) + `dataMcpImplModule` |

## Common

| Module | Role |
|--------|------|
| `common-core` | Shared abstractions used across layers: `Mapper<I, O>`, `Execute` |

## Build logic (included build)

Not counted among the 35 project modules — the `build-logic/convention` included build hosts the [convention plugins](convention-plugins.md).

## Dependency direction

Dependencies flow strictly downward through the layers:

```
application  →  presentation-feature-*  →  presentation-core-*
                        │
                        ▼
                domain-usecase-api  →  domain-repository-api  →  domain-core
                        ▲
                        │ (impls wired only in compose-application DI)
                domain-usecase-impl
                        │
                        ▼
                data-*-impl  →  data-*-api  →  data-core
                        │
                        ▼
                    common-core
```

Key rules:

- **api vs impl.** Data and domain layers are split into `*-api` (contracts, models) and `*-impl` (implementations + Koin module). Consumers depend on `*-api`; the `*-impl` modules are referenced only by the DI aggregation in `compose-application`.
- **Presentation depends on domain-api.** Feature modules use `domain-usecase-api` and domain models, never data modules directly.
- **Domain depends on repository-api.** Use-case implementations depend on `domain-repository-api`; the concrete repositories live in `data-repository-impl` and are injected by Koin.
- **common-core is the floor.** Any layer may depend on `common-core`; it depends on nothing internal.
- **macOS-only modules.** `data-mcp-impl` and the macOS MCP use cases are loaded dynamically from the macOS entry point rather than being part of the common graph.

## Related pages

- [Architecture overview](overview.md)
- [Convention plugins](convention-plugins.md)
- [Patterns](patterns.md)
- [Adding a module](../development/add-module.md)
