# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

PromptKnob KMP — a Kotlin Multiplatform (KMP) project targeting Android and iOS, using Compose Multiplatform for shared UI. Package: `dev.prompt.knob.io` (source and Android app ID).

## Build Commands

```shell
# Build Android debug APK
./gradlew :aos-application:assembleDebug

# Run all checks
./gradlew check

# Build iOS framework (used by Xcode)
./gradlew :compose-application:linkDebugFrameworkIosArm64

# Clean build
./gradlew clean
```

iOS app is built via Xcode from the `ios-application/` directory.

## Architecture

### Module Structure

- **compose-application** — Shared KMP module containing app entry points (common, Android, iOS, Desktop source sets)
- **aos-application** — Android-specific Application/Activity entry point
- **build-logic/convention** — Gradle convention plugins for standardized module configuration
- **ios-application** — Xcode project wrapping the Compose UI in SwiftUI via `ComposeUIViewController`

### Convention Plugins (build-logic)

Four registered plugins under `dev.prompt.knob.io.convention`:
- `application` — Android/iOS app module setup
- `feature` — Feature modules with Compose dependencies
- `library` — Shared library modules
- `di` — Koin dependency injection wiring

All plugins extend `BaseConventionPlugin` which provides hooks for plugin, platform, and dependency configuration. Module naming derives from project path (e.g., `:feature-login` → `feature.login`). Context receivers are enabled globally. Explicit API mode is enforced in all KMP modules.

### Platform Entry Points

- **Android:** `MainActivity` → `setContent { App() }`
- **iOS:** `MainViewController()` → `ComposeUIViewController { App() }`, wrapped in SwiftUI via `ComposeView` (UIViewControllerRepresentable)
- **Common:** `App()` composable with Material3 theming

### Key Dependencies

| Purpose | Library |
|---------|---------|
| UI | Compose Multiplatform, Material3 |
| DI | Koin (core, compose, viewmodel) |
| Navigation | AndroidX Navigation 3 |
| Settings | multiplatform-settings |
| Serialization | kotlinx-serialization |
| Coroutines | kotlinx-coroutines-core |

### Toolchain

- Kotlin 2.3.20, Gradle 9.1.0, JVM target 21
- Android: minSdk 27, targetSdk 36
- iOS: Static frameworks for arm64, x64, simulator arm64

<!-- update README (2026-04-08) -->
