# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**PromptKnob** — a Kotlin Multiplatform companion app for the PromptKnob hardware, a BLE
rotary knob built around an **ESP32**. The app connects to the knob over BLE (YAMK wire
protocol), lets the user bind rotation / tap / swipe to real actions (media, system, shell,
Google Assistant prompts), and manages devices, commands, and presets from a shared Compose
Multiplatform UI.

Primary product platforms are **macOS and Android**. The Compose module also builds **iOS**
and **Desktop/JVM** targets. On macOS the app doubles as a human-in-the-loop control surface
for Claude Code via an MCP server and a hook server.

Package / Android application ID: `dev.prompt.knob.io`.

The firmware is a **separate repository** (`github.com/andrew-malitchuk/prompt-knob-firmware`)
and is not built from this repo.

## Build Commands

```shell
# Build Android debug APK
./gradlew :aos-application:assembleDebug

# Run the macOS app (native, real CoreBluetooth BLE via Kable)
./gradlew :compose-application:runReleaseMacosArm64

# Build iOS framework (consumed by the Xcode project in ios-application/)
./gradlew :compose-application:linkDebugFrameworkIosArm64

# Run all checks
./gradlew check

# Clean build
./gradlew clean
```

The iOS app is built from `ios-application/` in Xcode 16+.

> Memory note: `:compose-application:build` can OOM on low-RAM machines. Prefer targeted
> tasks such as `:aos-application:assembleDebug` and
> `:compose-application:compileCommonMainKotlinMetadata`.

## Architecture

The project has **35 Gradle modules** plus the `build-logic` included build. It follows a
layered, API/Impl-split modular architecture with Koin DI.

### Layers

- **Application** — `compose-application` (KMP entry: `App()`, `initKoin`, platform mains for
  Android/iOS/macOS/Desktop) and `aos-application` (Android `Application`, `MainActivity`,
  Quick Settings tile, accessibility/device-admin config).
- **Presentation** — `presentation-feature-*` screens (Orbit MVI) and `presentation-core-*`
  infrastructure (ui, styling, localisation, navigation-api/impl).
- **Domain** — `domain-core` (models + `Failure`), `domain-repository-api` (repository
  interfaces), `domain-usecase-api` / `domain-usecase-impl` (use cases).
- **Data** — `data-repository-impl` plus feature data modules split api/impl:
  `data-ble-*` (scan/connect, YAMK codec, Android foreground service),
  `data-database-*` (Room: command tree + history), `data-preference-*` (settings),
  `data-executor-*` (per-platform command execution),
  `data-mcp-*` (macOS-only MCP server **and** Claude Code hook server),
  `data-runtime-*` (in-memory session / command-cache / move-detector state).
  `data-core` holds shared markers.
- **Common** — `common-core` shared utilities.

Dependency direction: presentation → domain → data → common-core. Modules depend on `-api`
contracts, never on `-impl`.

### Convention Plugins (build-logic)

Four plugins registered under `dev.prompt.knob.io.convention`:

| Plugin ID | Purpose |
|---|---|
| `dev.prompt.knob.io.convention.application` | Android/iOS/macOS/Desktop app module setup |
| `dev.prompt.knob.io.convention.feature` | Feature modules (Compose + Orbit MVI) |
| `dev.prompt.knob.io.convention.library` | Shared library modules |
| `dev.prompt.knob.io.convention.di` | Koin DI wiring |

All plugins extend `BaseConventionPlugin` (hooks for plugin, platform, and dependency config).
Module naming derives from project path (e.g. `:feature-login` → `feature.login`).
`explicitApi()` is enforced in library/feature/application modules.

### Presentation (Orbit MVI)

Each feature module uses `Screen` (route composable) → `Content` (stateless UI) → `ViewModel`
(`ContainerHost`, `reduce` / `postSideEffect`) → `Contract` (State/SideEffect) → `Intent`,
with its own Koin module. Navigation uses a sealed `Destination` (12 entries) over
Navigation 3, dispatched by `NavigationHost` in `presentation-core-navigation-impl`.

### macOS ↔ Claude Code integration

- **MCP server** — port `7474` (`/sse` + `/message`); tools `request_approval`,
  `request_choice`, `notify`.
- **Hook server** — port `7777` (`/state`); states `working` / `waiting` / `done` / `error` / `idle`.

Both servers live in `data-mcp-*` (`macosMain`) and are loaded lazily, macOS-only, from
`compose-application/.../macosMain/.../main.kt`.

### Key Dependencies

| Purpose | Library |
|---|---|
| UI | Compose Multiplatform 1.10.2, Material3 |
| State management | Orbit MVI 11.0.0 |
| DI | Koin 4.1.1 |
| Navigation | AndroidX Navigation 3 (+ navigation-compose) |
| Persistence | Room 2.7.1, multiplatform-settings |
| Networking (MCP/hooks) | Ktor 3.1.3 |
| Serialization | kotlinx-serialization |
| Coroutines | kotlinx-coroutines 1.9.0 |

### Toolchain

- Kotlin 2.3.20, Gradle 9.1.0, JVM target 21
- Android: minSdk 27, targetSdk 36
- iOS: static frameworks for arm64, x64, simulator arm64

## Documentation

Full docs live under `docs/` and are published with **MkDocs** (see `mkdocs.yml`). Each module
also carries its own `README.md` and `CLAUDE.md`. When you change a module's public API,
update that module's `README.md` and the matching page under `docs/modules/`.
