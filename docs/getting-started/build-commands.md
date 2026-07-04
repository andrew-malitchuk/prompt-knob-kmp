# Build commands

All commands run through the Gradle wrapper (`./gradlew`) from the repository
root. The wrapper pins Gradle **9.1.0**; you do not need a system-wide Gradle.

## Common tasks

| Task | Command | Produces |
|---|---|---|
| Android debug APK | `./gradlew :aos-application:assembleDebug` | `aos-application/build/outputs/apk/debug/` |
| iOS debug framework | `./gradlew :compose-application:linkDebugFrameworkIosArm64` | Static framework consumed by the Xcode project |
| macOS app (release) | `./gradlew :compose-application:runReleaseMacosArm64` | Builds, bundles, and launches `PromptKnob.app` |
| Run all checks | `./gradlew check` | Runs the verification tasks (tests, lint) across modules |
| Clean | `./gradlew clean` | Deletes all `build/` output |

## macOS tasks in detail

The `compose-application` module registers custom tasks for the native macOS
target (`macosArm64` and `macosX64`) on top of the standard Kotlin/Native link
tasks. For each target and build type (`Debug` / `Release`) you get:

| Task pattern | What it does |
|---|---|
| `linkReleaseExecutableMacosArm64` | Standard Kotlin/Native link — produces `PromptKnob.kexe`. |
| `runReleaseMacosArm64` | Bundles the `.kexe` into a codesigned `PromptKnob.app` and opens it. |
| `packageReleaseDmgForMacosArm64` | Packages the `.app` into `PromptKnob.dmg`. |

Swap `Release` for `Debug`, or `MacosArm64` for `MacosX64`, to target other
combinations (e.g. `runDebugMacosX64`).

## iOS framework variants

`linkDebugFrameworkIosArm64` is the device debug framework. Other variants
follow the same pattern, e.g. `linkReleaseFrameworkIosArm64` or
`linkDebugFrameworkIosSimulatorArm64`. The Xcode project (see
[Run on iOS](../how-to/run-ios.md)) triggers the correct variant during its
build phase.

## Memory note (important)

> **Do not run `./gradlew :compose-application:build` on a low-RAM machine.**
> Building `compose-application` compiles and native-links every target (Android,
> iOS ×3, macOS ×2, Desktop JVM) at once, which can exhaust memory and OOM the
> Kotlin/Native linker.
>
> Instead, use targeted tasks for what you actually need:
>
> ```bash
> # Verify common code compiles (cheap, metadata-only)
> ./gradlew :compose-application:compileCommonMainKotlinMetadata
>
> # Build only the Android app
> ./gradlew :aos-application:assembleDebug
> ```

## See also

- [Platforms](platforms.md) — what each target build produces.
- [Project setup](setup.md) — first build walkthrough.
