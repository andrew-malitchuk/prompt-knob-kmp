# Architecture Overview

PromptKnob is a Kotlin Multiplatform companion app for the PromptKnob hardware — a BLE, ESP32-based rotary knob. The app runs on macOS and Android (its primary platforms) and also targets iOS and Desktop/JVM through Compose Multiplatform. The shared package is `dev.prompt.knob.io`.

The app lets you build a tree of commands, sync them to the knob over Bluetooth, and — on macOS — expose the knob to Claude Code and other tools through a Model Context Protocol (MCP) server and a lightweight Claude hook server.

## Layered, modular design

The codebase is organized into 35 Gradle modules that fall into a strict layering. Dependencies point downward only: presentation depends on domain, domain depends on data contracts, and everything can use the shared `common-core`.

```
┌───────────────────────────────────────────────────────────┐
│ presentation                                                │
│   presentation-feature-*   (Compose screens, Orbit MVI)     │
│   presentation-core-*      (navigation, ui kit, styling,    │
│                             localisation)                   │
└───────────────────────────────┬───────────────────────────┘
                                 │ depends on
┌───────────────────────────────▼───────────────────────────┐
│ domain                                                      │
│   domain-usecase-api / -impl   (business operations)        │
│   domain-repository-api        (repository contracts)       │
│   domain-core                  (models, Optional/Failure)   │
└───────────────────────────────┬───────────────────────────┘
                                 │ depends on
┌───────────────────────────────▼───────────────────────────┐
│ data                                                        │
│   data-repository-impl         (repositories)               │
│   data-ble-*                   (YAMK BLE protocol)          │
│   data-database-*              (Room)                       │
│   data-preference-*            (settings)                   │
│   data-runtime-*               (in-memory session state)    │
│   data-executor-*              (command execution)          │
│   data-mcp-*                   (MCP + Claude hook, macOS)    │
│   data-core                    (Resource marker)            │
└───────────────────────────────┬───────────────────────────┘
                                 │ depends on
┌───────────────────────────────▼───────────────────────────┐
│ common-core   (Mapper, Execute — shared abstractions)       │
└───────────────────────────────────────────────────────────┘
```

Most data and domain modules are split into an **api** module (public interfaces and models) and an **impl** module (implementations plus a Koin module). Callers depend on the api module; only the DI aggregation touches the impl modules. See [Patterns](patterns.md) and [Module structure](module-structure.md).

## Data flow: UI → domain → data → hardware

A typical interaction, taking "the user selects a command on the knob":

1. **Hardware → data.** The firmware sends a `CMD_SELECTED` frame over the CMD BLE characteristic. `data-ble-impl` (`BleConnectionImpl` + `YamkProtocolImpl`) decodes the [YAMK frame](../development/ble-protocol.md) and emits it.
2. **data → domain.** A repository in `data-repository-impl` (e.g. `BleRepositoryImpl`) exposes the event as a domain model. A use case in `domain-usecase-impl` — for example `ExecuteCommandUseCase` — reacts to it.
3. **domain → data (execution).** `ExecuteCommandUseCaseImpl` hands the `CommandNodeModel` to the platform `CommandExecutor` (`data-executor-impl`), which runs it — on macOS via `MacOsCommandRouter` (shell commands through `/bin/zsh -l -c`, or system commands).
4. **UI.** Screens (`presentation-feature-*`) observe use-case state through Orbit MVI ViewModels and render it with the shared UI kit.

Pushing commands the other way (phone → device) runs the reverse path: a use case calls the repository, which drives `YamkProtocolImpl.syncCommands` to write frames to the SYNC characteristic. The Android connection is kept alive by a foreground service (`BleConnectionService`) — see [Firmware BLE implementation](../development/firmware-ble-impl.md).

## MCP and Claude hook integration (macOS)

On macOS the app additionally hosts two local servers, implemented in `data-mcp-impl` and orchestrated by macOS-only use cases in `domain-usecase-impl`:

- **`McpServer`** — a Ktor server exposing MCP tools (`request_approval`, `request_choice`, `notify`) over an SSE endpoint. When a tool is called, it pushes a prompt to the knob (e.g. `BLE_OP_SHOW_APPROVAL`) and awaits the user's knob selection, then returns the result to the MCP client (Claude Code).
- **`ClaudeHookServer`** — an HTTP server (localhost, default port 7777) that accepts `POST /state` requests. Claude Code hooks configured in `~/.claude/settings.json` post the session state ("working", "waiting", "done", …); the server maps it to a `ClaudeState` and forwards it to the knob via `BLE_OP_CLAUDE_STATE`.

These servers are wired dynamically: `initKoin()` builds the common graph, and the macOS entry point (`compose-application/src/macosMain/.../entry/main.kt`) then calls `loadKoinModules(listOf(dataMcpImplModule, domainMcpUseCaseModule))` and starts the servers on background coroutines. This keeps MCP code off the other platforms. See the [MCP setup guide](../user-guide/mcp-setup.md) for the user-facing configuration.

## Dependency injection with Koin

All wiring is done with Koin. Each impl module owns a Koin `module { }`, and they are aggregated in dependency order in `initKoin()`
(`compose-application/src/commonMain/kotlin/dev/prompt/knob/io/source/di/InitKoin.kt`):

```kotlin
modules(
    dataPreferenceImplModule,
    dataBleImplModule,
    dataDatabaseImplModule,
    dataRuntimeImplModule,
    dataRepositoryImplModule,
    dataExecutorImplModule,
    domainUseCaseImplModule,
    presentationFeatureAboutModule,
    presentationFeatureCommandModule,
    presentationFeatureDevicesModule,
    presentationFeatureDeviceModule,
    presentationFeatureHomeModule,
    presentationFeatureOnboardingModule,
    presentationFeaturePresetModule,
    presentationFeatureSettingsModule,
    presentationFeatureSplashModule,
)
```

## Platform entry points

- **Android:** `MainActivity` (`aos-application`) hosts the shared `App()` composable.
- **macOS:** the `main()` entry initializes Koin, loads the MCP modules, starts the servers, and launches the Compose window.
- **iOS / Desktop:** the shared `App()` composable is wrapped by the platform host.

## Related pages

- [Module structure](module-structure.md)
- [Convention plugins](convention-plugins.md)
- [Patterns](patterns.md)
- [YAMK BLE protocol](../development/ble-protocol.md)
