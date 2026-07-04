# Project setup

This page walks you through cloning PromptKnob, opening it, and producing your
first build. Make sure you have the [prerequisites](prerequisites.md) installed
first.

## 1. Clone the repository

```bash
git clone <your-fork-or-origin-url> prompt-knob-kmp
cd prompt-knob-kmp
```

## 2. Open the project

### Android Studio (recommended)

1. Open Android Studio (Ladybug or newer).
2. Choose **Open** and select the `prompt-knob-kmp` directory.
3. Wait for the initial Gradle sync to finish. The wrapper downloads the pinned
   Gradle 9.1.0 distribution automatically on first sync.

The build uses convention plugins from `build-logic/` (an included build), so
the `build-logic` project is synced alongside the app modules.

### Command line only

You do not need an IDE to build. From the repository root, everything runs
through the Gradle wrapper:

```bash
./gradlew help
```

## 3. First build

The lightest way to confirm your toolchain works is to assemble the Android
debug APK:

```bash
./gradlew :aos-application:assembleDebug
```

The APK is written to
`aos-application/build/outputs/apk/debug/`.

> **Low-RAM machines:** avoid `./gradlew :compose-application:build`. Compiling
> and native-linking all targets at once can exhaust memory. Prefer targeted
> tasks such as `:aos-application:assembleDebug` and
> `:compose-application:compileCommonMainKotlinMetadata`. See
> [Build commands](build-commands.md) for the full list and the OOM note.

## 4. Run it

Pick your platform:

- [Run on Android](../how-to/run-android.md)
- [Run on iOS](../how-to/run-ios.md)
- For macOS and Desktop, see [Platforms](platforms.md).

## Module layout

PromptKnob is a modular KMP project (35 Gradle modules) split into `data-*`,
`domain-*`, `presentation-core-*`, and `presentation-feature-*` groups, plus the
`compose-application` and `aos-application` entry points. See the
[Module Structure](../architecture/module-structure.md) reference for the full
tree and dependency graph.
