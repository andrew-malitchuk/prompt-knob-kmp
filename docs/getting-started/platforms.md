# Supported platforms

PromptKnob shares one Compose Multiplatform UI (`App()`, in the
`compose-application` module) across four platform families. **macOS** and
**Android** are the primary targets for the companion app; iOS and Desktop/JVM
are also supported.

| Platform | Kotlin target(s) | Entry point | Build output |
|---|---|---|---|
| Android | `androidTarget` | `MainActivity` → `setContent { App() }` | Debug/release APK from `aos-application` |
| iOS | `iosArm64`, `iosX64`, `iosSimulatorArm64` | `MainViewController()` → `ComposeUIViewController { App() }` | Static framework linked into the Xcode app |
| macOS | `macosArm64`, `macosX64` | Kotlin/Native `main` entry | Native `PromptKnob.app` (and optional `.dmg`) |
| Desktop | `jvm("desktop")` | Compose Desktop `application` | Runnable JVM app / native distributable |

## Android

`aos-application` is the Android application module. Its single activity,
`MainActivity`, enables edge-to-edge rendering and hands off to the shared
`App()` composable. See [Run on Android](../how-to/run-android.md).

```bash
./gradlew :aos-application:assembleDebug
```

## iOS

Feature and app modules register three iOS targets (`iosArm64`, `iosX64`,
`iosSimulatorArm64`) as **static** frameworks. The shared UI is exposed through
`MainViewController()`, which is hosted in SwiftUI via
`UIViewControllerRepresentable`. The app itself is built from the
`ios-application/` Xcode project. See [Run on iOS](../how-to/run-ios.md).

```bash
./gradlew :compose-application:linkDebugFrameworkIosArm64
```

## macOS (native)

The `compose-application` module builds a native macOS executable
(`PromptKnob.kexe`), bundles it into a codesigned `PromptKnob.app`, and can
package a `.dmg`. This is the desktop control surface — including the MCP server
used for Claude Code integration (the `data-mcp-impl` module is only linked into
the macOS targets).

```bash
# Build and launch the app
./gradlew :compose-application:runReleaseMacosArm64
```

## Desktop (JVM)

Feature modules also expose a `jvm("desktop")` target, and
`compose-application` wires a Compose Desktop `application` with
`mainClass = "dev.prompt.knob.io.source.entry.MainKt"`. The Desktop target is
useful for fast iteration on shared UI, using the current OS's Compose runtime.

## A note on memory

Building all targets at once (`:compose-application:build`) is heavy and can
OOM on low-RAM machines. Prefer the targeted tasks listed in
[Build commands](build-commands.md).
