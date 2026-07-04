# Prerequisites

Before you build PromptKnob, install the tooling below. PromptKnob is a Kotlin
Multiplatform project (package `dev.prompt.knob.io`) whose primary platforms are
**macOS** and **Android**, with additional iOS and Desktop/JVM Compose targets.

## Required for every platform

| Tool | Version | Notes |
|---|---|---|
| JDK | 21 | The project targets JVM 21. Use a JDK 21 distribution (Temurin, Zulu, or the one bundled with Android Studio). |
| Gradle | 9.1.0 | Provided by the Gradle wrapper (`./gradlew`) — you do **not** need a system-wide Gradle. |
| Kotlin | 2.3.20 | Pulled in automatically by the build; no separate install. |

You never invoke `gradle` directly — always use the wrapper script `./gradlew`,
which downloads the pinned 9.1.0 distribution on first run.

## Android

- **Android Studio** — Ladybug or newer, which ships the Android SDK, an
  embedded JDK, and the Kotlin Multiplatform tooling.
- **Android SDK** — compile/target SDK 36, minimum SDK 27 (Android 8.1).
- A physical device or emulator running API 27+ to run the app.

## iOS

- **macOS** with **Xcode 16+**.
- Command Line Tools for Xcode (`xcode-select --install`).
- The iOS app is built from the `ios-application/` Xcode project; the Kotlin
  side is compiled into a static framework by Gradle (see
  [Run on iOS](../how-to/run-ios.md)).

## macOS (native desktop app)

- **macOS** on Apple Silicon (`macosArm64`) or Intel (`macosX64`).
- Xcode Command Line Tools — the native macOS build codesigns the produced
  `.app` bundle with an ad-hoc signature and needs `codesign` / `hdiutil`.

## Desktop (JVM)

- Only JDK 21 is required. The Desktop target runs on the current OS via the
  Compose Desktop runtime.

## Optional — firmware only

You only need these if you intend to build or flash the **PromptKnob firmware**
onto the ESP32 hardware. They are **not** required to build or run the companion
app.

- **PlatformIO** (for compiling and flashing the ESP32 firmware).

See the [Firmware](../firmware/overview.md) section for hardware and flashing
details.

## Next steps

Once the tooling is in place, continue to [Project setup](setup.md).
