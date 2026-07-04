# How to: use this as a template

PromptKnob's modular Kotlin Multiplatform structure — convention plugins, an
api/impl split across data and domain, a token-based design system, and reactive
localisation — is a solid starting point for a new Compose Multiplatform app.
This guide shows how to reuse the architecture.

## What you are reusing

- **Convention plugins** (`build-logic/convention`) that standardise every
  module's Kotlin/Compose/Android configuration.
- A **layered module structure**: `data-*` (api + impl), `domain-*` (api +
  impl), `presentation-core-*` (styling, localisation, navigation, UI), and
  `presentation-feature-*` screens.
- Cross-platform entry points for Android, iOS, macOS, and Desktop that all
  render one shared `App()`.
- Design-system (`presentation-core-styling`) and localisation
  (`presentation-core-localisation`) infrastructure.

## 1. Copy the project and rename it

1. Copy the repository to a new directory.
2. Set the root project name in `settings.gradle.kts`:

   ```kotlin
   rootProject.name = "your-app-kmp"
   ```

## 2. Change the application identity

The base identity lives in the version catalog
`gradle/libs.versions.toml`:

```toml
applicationId = "dev.prompt.knob.io"
versionCode = "1"
versionName = "0.0.1"
```

Change `applicationId` to your own reverse-domain package (e.g.
`com.example.app`). This drives the Android application ID and the macOS
`CFBundleIdentifier` (set in `compose-application/build.gradle.kts`).

## 3. Rename the source package

The Kotlin package for the app entry points is `dev.prompt.knob.io`
(directories such as `dev/prompt/knob/io/...`). Rename it project-wide — an IDE
"Rename package" refactor is safest. Also update:

- The `build-logic/convention` plugin package
  (`dev.prompt.knob.io.convention.*`) **and** the four registered plugin IDs in
  `build-logic/convention/build.gradle.kts`:

  ```kotlin
  id = "dev.prompt.knob.io.convention.feature"
  id = "dev.prompt.knob.io.convention.application"
  id = "dev.prompt.knob.io.convention.library"
  id = "dev.prompt.knob.io.convention.di"
  ```

  Every module applies these by ID (e.g.
  `id("dev.prompt.knob.io.convention.feature")`), so update the `plugins { }`
  blocks to match.
- macOS entry references in `compose-application/build.gradle.kts`
  (`entryPoint = "dev.prompt.knob.io.source.entry.main"` and
  `mainClass = "dev.prompt.knob.io.source.entry.MainKt"`).

> **Note on module namespaces:** the convention plugins derive each Android
> library `namespace` (and iOS framework `baseName`) from the Gradle project
> **path**, not from your app package. `moduleName` maps
> `:data-ble-impl` → `data.ble.impl` (see
> `build-logic/convention/.../core/ext/Project.kt`). This is intentional and
> keeps module names decoupled from the app package — you generally do **not**
> need to touch it when rebranding.

## 4. Understand the convention plugins

Four plugins are registered under the `*.convention` namespace. All extend
`BaseConventionPlugin`, which exposes hooks for plugin, platform, and dependency
configuration.

| Plugin ID (suffix) | Purpose |
|---|---|
| `.application` | Android/iOS/macOS/Desktop app module setup |
| `.feature` | Feature/library modules with Compose + Orbit MVI |
| `.library` | Shared non-Compose library modules |
| `.di` | Koin dependency-injection wiring |

The `feature` plugin registers the KMP targets (`iosX64`, `iosArm64`,
`iosSimulatorArm64`, `macosX64`, `macosArm64`, `jvm("desktop")`, and
`androidTarget`), turns on `explicitApi()`, and enables context receivers. Apply
the right plugin from a module's `build.gradle.kts` and you inherit all of it.

## 5. Add a new module

1. Create the module directory and a `build.gradle.kts` applying the appropriate
   convention plugin, e.g.:

   ```kotlin
   plugins {
       id("dev.prompt.knob.io.convention.feature")
   }
   ```

2. Register it in `settings.gradle.kts`:

   ```kotlin
   include(":presentation-feature-yourscreen")
   ```

3. Depend on it from consumers via the type-safe project accessors that the
   project enables (`TYPESAFE_PROJECT_ACCESSORS`), e.g.
   `implementation(projects.presentationFeatureYourscreen)`.

See [Development: Add a Module](../development/add-module.md) and
[Development: Add a Feature](../development/add-feature.md) for the full
walkthrough, and the [Module Structure](../architecture/module-structure.md)
reference for the layering rules.

## 6. Strip the product specifics

Once the skeleton is renamed, replace the PromptKnob-specific pieces with your
own domain:

- Rewrite the strings in `presentation-core-localisation` (see
  [Add localisation](add-localisation.md)).
- Restyle `presentation-core-styling` tokens (see
  [Change the theme](change-theme.md)).
- Replace or remove `presentation-feature-*` screens you do not need, and the
  `data-*` modules tied to hardware you are not using (BLE, executor, MCP, OTA).

## 7. Verify the rename

```bash
./gradlew clean
./gradlew :aos-application:assembleDebug
```

> On low-RAM machines, avoid `:compose-application:build`; use targeted tasks
> instead — see [Build commands](../getting-started/build-commands.md).
