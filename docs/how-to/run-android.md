# How to: run on Android

The Android app lives in the `aos-application` module. Its single activity,
`MainActivity`, hosts the shared Compose `App()` composable.

## Prerequisites

- Android Studio (Ladybug or newer) with the Android SDK installed.
- A device or emulator running **API 27+** (Android 8.1 or newer — this is the
  project's `minSdk`).

See [Prerequisites](../getting-started/prerequisites.md) for the full list.

## Option A — Android Studio

1. Open the project in Android Studio and let Gradle sync.
2. Select the **`aos-application`** run configuration.
3. Choose a connected device or emulator.
4. Press **Run**.

Android Studio assembles the debug APK, installs it, and launches
`MainActivity`.

## Option B — command line

Assemble the debug APK:

```bash
./gradlew :aos-application:assembleDebug
```

The APK is written to `aos-application/build/outputs/apk/debug/`.

To build **and install** onto a connected device or running emulator in one
step:

```bash
./gradlew :aos-application:installDebug
```

Then launch it from the device app drawer, or via `adb`:

```bash
adb shell am start -n dev.prompt.knob.io/dev.prompt.knob.io.source.activity.MainActivity
```

The application ID is `dev.prompt.knob.io` and the activity class is
`MainActivity` in package `dev.prompt.knob.io.source.activity`.

## Permissions

To pair with the PromptKnob hardware over BLE, and to run commands, the app
requests Bluetooth, notification, and accessibility permissions at runtime. Grant
them from the in-app **Settings** screen when prompted.

## Next steps

- [Run on iOS](run-ios.md)
- [Change the theme](change-theme.md)
- [Add localisation](add-localisation.md)
