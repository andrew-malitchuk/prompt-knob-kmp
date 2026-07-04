# build-logic/convention

Gradle convention plugins that standardise module configuration across the PromptKnob project.
Included via `includeBuild("build-logic")` in the root `settings.gradle.kts`, so the plugins are
applied by ID from any module's `build.gradle.kts`.

## Registered plugins

All plugin IDs live under the `dev.prompt.knob.io.convention` namespace:

| Plugin ID | Implementation | Purpose |
|---|---|---|
| `dev.prompt.knob.io.convention.application` | `ApplicationConventionPlugin` | Android/iOS/macOS/Desktop app-module setup |
| `dev.prompt.knob.io.convention.feature` | `FeatureConventionPlugin` | Feature modules (Compose Multiplatform + Orbit MVI) |
| `dev.prompt.knob.io.convention.library` | `LibraryConventionPlugin` | Shared KMP library modules |
| `dev.prompt.knob.io.convention.di` | `DiConventionPlugin` | Koin dependency-injection wiring |

Implementation classes live under
`src/main/kotlin/dev/prompt/knob/io/convention/source/convention/<name>/`.

## BaseConventionPlugin

`ApplicationConventionPlugin`, `FeatureConventionPlugin`, and `LibraryConventionPlugin` all
extend `BaseConventionPlugin`, which provides ordered configuration hooks (plugin application,
platform/target setup, and dependency configuration) so each concrete plugin only overrides
what it needs.

Shared behaviour:

- **Module-name derivation** — the module name is derived from the Gradle project path
  (e.g. `:presentation-feature-home` → `presentation.feature.home`), used for namespaces and
  the `androidLibrary` namespace.
- **`explicitApi()`** — enforced in library, feature, and application modules.
- Common Kotlin/Compose/target configuration applied consistently across modules.

Extension helpers used by the plugins live in
`src/main/kotlin/dev/prompt/knob/io/convention/core/ext/Project.kt`.

## Usage

```kotlin
// A shared library module
plugins {
    id("dev.prompt.knob.io.convention.library")
}

// A Compose feature module
plugins {
    id("dev.prompt.knob.io.convention.feature")
}
```

See [`docs/architecture/convention-plugins.md`](../../docs/architecture/convention-plugins.md)
for the full reference.
