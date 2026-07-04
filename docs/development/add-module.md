# Adding a Module

PromptKnob is split into 35 Gradle modules. New modules follow a small set of conventions so their build scripts stay tiny and the dependency direction stays clean. This page covers the mechanics of adding a module; for the module map see [Module structure](../architecture/module-structure.md), and for adding a UI feature specifically see [Adding a feature](add-feature.md).

## 1. Include the module in settings

Add the module to `settings.gradle.kts`, under the group comment that matches its layer:

```kotlin
// data
include(":data-example-api")
include(":data-example-impl")
```

The project uses type-safe project accessors (`enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")`), so `:data-example-api` becomes `projects.dataExampleApi` in dependency declarations.

## 2. Create the build script

Create `build.gradle.kts` and apply the appropriate convention plugin by `id`. Pick the plugin by layer:

| Module kind | Plugin(s) |
|-------------|-----------|
| App entry point | `dev.prompt.knob.io.convention.application` |
| `presentation-feature-*` | `dev.prompt.knob.io.convention.feature` (+ `…di`) |
| domain / data / core library | `dev.prompt.knob.io.convention.library` |
| Any module that needs Koin | add `dev.prompt.knob.io.convention.di` on top |

A non-UI library module looks like this (mirroring `domain-repository-api`):

```kotlin
plugins {
    id("dev.prompt.knob.io.convention.library")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.domainCore)
        }
    }
}
```

The convention plugin configures Kotlin Multiplatform (Android + iOS + macOS + Desktop targets), the JVM 21 target, the Android namespace derived from the module path, and `explicitApi()`. You never repeat that setup. See [Convention plugins](../architecture/convention-plugins.md).

If the module needs Koin, add the `di` plugin and it will pull in `koin-core`, `koin-compose`, `koin-viewmodel` (and the Android Koin artifacts). Some modules, like `data-ble-impl`, instead declare `libs.koin.core` directly in their source set — either is fine, but prefer the `di` plugin for modules that own a Koin `module { }`.

## 3. Follow the API / Impl split

Data and domain layers are split into paired modules: an **api** module holding the public contracts (interfaces, models) and an **impl** module holding the implementations plus a Koin module that binds them. For example:

- `domain-repository-api` — repository interfaces (`CommandRepository`, `PresetRepository`, …).
- `data-repository-impl` — implementations (`CommandRepositoryImpl`, …) and `dataRepositoryImplModule`.

Rules of thumb:

- The **api** module depends only on `*-core` modules and, at most, other api modules.
- The **impl** module depends on the matching api module (via `api(projects.…Api)` so consumers get the contracts transitively) and on lower data-layer modules.
- Callers (domain use cases, presentation features) depend on **api** modules, never on **impl** modules. Only the DI aggregation in `compose-application` references the impl Koin modules.

The `data-ble-impl` build script shows the pattern:

```kotlin
plugins {
    id("dev.prompt.knob.io.convention.library")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.dataBleApi)          // re-export the contracts
            implementation(projects.dataCore)
            implementation(libs.koin.core)
            implementation(libs.kermit)
        }
        androidMain.dependencies {
            implementation(libs.kable.core)
            implementation(libs.koin.android)
        }
    }
}
```

## 4. Package layout

Inside each module, source lives under a package derived from the module path with a `core` / `source` / `di` split, for example:

```
data/ble/api/core/...     // pure types: codecs, opcodes, constants
data/ble/api/source/...   // data-source / service contracts, resources
data/ble/impl/source/...  // implementations
data/ble/impl/di/...      // the module's Koin module
```

## 5. Register the Koin module (impl modules)

If the module contributes a Koin `module { }`, add it to `initKoin()`
(`compose-application/src/commonMain/kotlin/dev/prompt/knob/io/source/di/InitKoin.kt`) in dependency order — data modules first, then domain, then presentation. See the [architecture overview](../architecture/overview.md) for the full module ordering. macOS-only modules (e.g. `data-mcp-impl`) are instead loaded dynamically from the macOS entry point.

## Related pages

- [Module structure](../architecture/module-structure.md)
- [Convention plugins](../architecture/convention-plugins.md)
- [Adding a feature](add-feature.md)
- [Patterns](../architecture/patterns.md)
