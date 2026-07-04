# Convention Plugins

PromptKnob has 35 Gradle modules. To keep their build scripts short and consistent, all shared build configuration lives in a set of **convention plugins** under `build-logic/`. Each module's `build.gradle.kts` applies one (or two) of these plugins instead of repeating Kotlin Multiplatform, Android, Compose, and Koin setup.

The plugins live in the `build-logic` included build and share the package `dev.prompt.knob.io.convention`.

## The four plugins

| Plugin ID | Implementation class | Used by |
|-----------|----------------------|---------|
| `dev.prompt.knob.io.convention.application` | `ApplicationConventionPlugin` | App entry-point modules |
| `dev.prompt.knob.io.convention.feature` | `FeatureConventionPlugin` | `presentation-feature-*` modules |
| `dev.prompt.knob.io.convention.library` | `LibraryConventionPlugin` | domain / data / core modules |
| `dev.prompt.knob.io.convention.di` | `DiConventionPlugin` | Any module needing Koin |

All four are registered in `build-logic/convention/build.gradle.kts`:

```kotlin
gradlePlugin {
    plugins {
        register("application") {
            id = "dev.prompt.knob.io.convention.application"
            implementationClass =
                "dev.prompt.knob.io.convention.source.convention.application.ApplicationConventionPlugin"
        }
        register("feature") {
            id = "dev.prompt.knob.io.convention.feature"
            implementationClass =
                "dev.prompt.knob.io.convention.source.convention.feature.FeatureConventionPlugin"
        }
        register("library") {
            id = "dev.prompt.knob.io.convention.library"
            implementationClass =
                "dev.prompt.knob.io.convention.source.convention.library.LibraryConventionPlugin"
        }
        register("di") {
            id = "dev.prompt.knob.io.convention.di"
            implementationClass =
                "dev.prompt.knob.io.convention.source.convention.di.DiConventionPlugin"
        }
    }
}
```

## BaseConventionPlugin

Every concrete plugin extends `BaseConventionPlugin`
(`build-logic/convention/src/main/kotlin/dev/prompt/knob/io/convention/source/convention/base/BaseConventionPlugin.kt`).

`BaseConventionPlugin` implements `Plugin<Project>` and defines a fixed sequence of **open hook methods**. Subclasses override only the hooks they care about; each hook is a no-op (`= Unit`) by default:

```kotlin
open class BaseConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.logger.lifecycle("> Applied convention plugin for module: ${target.path}")
        with(target) {
            logger.lifecycle("> Module name: $moduleName")
            configurePlugin()
            configureAndroidPlatform()
            configureIOsPlatform()
            configureDesktopPlatform()
            configureCommonDependencies()
            configureAndroidDependencies()
            configureIOsDependencies()
            configureDesktopDependencies()
        }
    }

    open fun Project.configurePlugin() = Unit
    open fun Project.configureAndroidPlatform() = Unit
    open fun Project.configureIOsPlatform() = Unit
    open fun Project.configureDesktopPlatform() = Unit
    open fun Project.configureCommonDependencies() = Unit
    open fun Project.configureAndroidDependencies() = Unit
    open fun Project.configureIOsDependencies() = Unit
    open fun Project.configureDesktopDependencies() = Unit
}
```

The hooks always run in this order:

1. `configurePlugin()` — apply the required Gradle plugin IDs (must run first, before any plugin extension is touched).
2. `configureAndroidPlatform()` — Android KMP library target.
3. `configureIOsPlatform()` — iOS targets.
4. `configureDesktopPlatform()` — macOS + Desktop JVM targets.
5. `configureCommonDependencies()` — `commonMain` dependencies.
6. `configureAndroidDependencies()` — `androidMain` dependencies.
7. `configureIOsDependencies()` — `iosMain` dependencies.
8. `configureDesktopDependencies()` — `desktopMain` dependencies.

## Module-name derivation

Both the Android namespace and the iOS framework `baseName` are derived from the Gradle project path by the `moduleName` extension in
`build-logic/convention/src/main/kotlin/dev/prompt/knob/io/convention/core/ext/Project.kt`:

```kotlin
internal val Project.moduleName: String
    get() = path.replace(":", "").replace("-", ".")
```

So the path is stripped of colons and its hyphens become dots:

| Project path | `moduleName` |
|--------------|--------------|
| `:presentation-feature-about` | `presentation.feature.about` |
| `:data-ble-impl` | `data.ble.impl` |

Because the root project path is `:`, its `moduleName` is empty — never apply a convention plugin to the root project.

The same file also exposes the version catalog:

```kotlin
internal val Project.libs: VersionCatalog
    get() = extensions.getByType<VersionCatalogsExtension>().named("libs")
```

## What each plugin configures

### ApplicationConventionPlugin

Applies `kotlinMultiplatform`, `androidMultiplatformLibrary`, `composeMultiplatform`, and `composeCompiler`. Configures:

- **Android**: `namespace = moduleName`, `compileSdk`/`minSdk` from the version catalog, `androidResources { enable = true }`, `jvmTarget = JVM_21`.
- **iOS**: `iosX64()`, `iosArm64()`, `iosSimulatorArm64()`, static framework with the hardcoded `baseName = "application_compose"`.
- **Desktop**: `macosX64()`, `macosArm64()`, `jvm("desktop")`.
- **Common dependencies**: Compose runtime/foundation/material/ui, Compose resources + UI-tooling-preview, `kotlinx-coroutines-core`, AndroidX `lifecycle-viewmodel` and `lifecycle-runtimeCompose`.
- Calls `explicitApi()`.

### FeatureConventionPlugin

Same plugin set and common dependencies as the application plugin, and also calls `explicitApi()`. Differences:

- iOS framework `baseName = moduleName.replace(".", "-")` (per-module, not hardcoded).
- Desktop adds `compose.desktop.currentOs` in addition to `kotlinx-coroutines-swing`.

Used by all `presentation-feature-*` modules.

### LibraryConventionPlugin

The base for non-UI modules (domain, data, core). Applies only `kotlinMultiplatform` and `androidMultiplatformLibrary` — **no Compose**. It does **not** enable `androidResources`. Common dependencies are limited to `kotlinx-coroutines-core` and the AndroidX lifecycle libraries, and it calls `explicitApi()`. iOS/Desktop targets are registered exactly as in the feature plugin.

### DiConventionPlugin

A **dependency-only** plugin: it applies no Gradle plugins and registers no platform targets. Apply it *on top of* `library` or `feature` when a module needs Koin. It adds:

- `commonMain`: `koin-core` (as `api()`, so it is transitive), `koin-compose` and `koin-viewmodel` (as `implementation()`).
- `androidMain`: `koin-android` and `koin-android-compose`.

## Explicit API mode

`ApplicationConventionPlugin`, `FeatureConventionPlugin`, and `LibraryConventionPlugin` all call `explicitApi()`. This forces every public declaration in those modules to have an explicit visibility modifier and an explicit return type, which keeps the API/Impl boundary of each module intentional. See [Patterns](patterns.md) for how the API/Impl split relies on this.

## Related pages

- [Architecture overview](overview.md)
- [Module structure](module-structure.md)
- [Adding a module](../development/add-module.md)
