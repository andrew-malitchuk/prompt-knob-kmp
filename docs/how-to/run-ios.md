# How to: run on iOS

The iOS app wraps the shared Compose UI in SwiftUI. The Kotlin side is compiled
into a **static framework** by Gradle; the app itself is built and run from the
Xcode project in `ios-application/`.

## Prerequisites

- macOS with **Xcode 16+**.
- Xcode Command Line Tools (`xcode-select --install`).

See [Prerequisites](../getting-started/prerequisites.md).

## How the pieces fit together

- The shared UI entry point is `MainViewController()` (in
  `compose-application`, package
  `dev.prompt.knob.io.source.viewcontroller`), which returns a
  `UIViewController` built with `ComposeUIViewController { App() }`.
- SwiftUI hosts that controller via `UIViewControllerRepresentable`.
- Gradle produces the framework the Xcode project links against.

## 1. Build the Kotlin framework

For a device (arm64) debug build:

```bash
./gradlew :compose-application:linkDebugFrameworkIosArm64
```

Other variants follow the same naming, for example:

- `linkReleaseFrameworkIosArm64` — device release framework.
- `linkDebugFrameworkIosSimulatorArm64` — Apple-Silicon simulator.

In normal use you do **not** need to run this by hand — the Xcode build phase
invokes the correct Gradle link task automatically. Running it manually is handy
for verifying the shared code compiles for iOS.

## 2. Open and run from Xcode

1. Open `ios-application/ios-application.xcodeproj` in Xcode 16+.
2. Select a simulator or a connected device as the run destination.
3. Press **Run** (⌘R).

Xcode triggers the Gradle framework build, links it, and launches the app.

## Troubleshooting

- **Framework not found / stale symbols:** clean and rebuild the framework:
  ```bash
  ./gradlew clean :compose-application:linkDebugFrameworkIosArm64
  ```
- **Wrong architecture:** make sure the run destination (device vs. simulator,
  arm64 vs. x64) matches the framework variant Xcode is building.

## Next steps

- [Run on Android](run-android.md)
- [Platforms overview](../getting-started/platforms.md)
