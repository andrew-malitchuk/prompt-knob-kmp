# CLAUDE.md — build-logic/convention

Guidance for working in the Gradle convention-plugin included build.

## Responsibility

Centralises Gradle configuration for the whole PromptKnob project. Instead of repeating
Kotlin/Compose/Android setup in every module, each module applies one convention plugin by ID.
This is a `includeBuild("build-logic")` composite build — changes here affect how **every**
module is configured.

## Layout

- `source/convention/base/BaseConventionPlugin.kt` — shared hooks (plugin/platform/dependency
  configuration) extended by the concrete plugins.
- `source/convention/{application,feature,library,di}/*ConventionPlugin.kt` — the four plugins.
- `core/ext/Project.kt` — extension helpers (module-name derivation, target/dependency setup).
- `build.gradle.kts` — registers the four plugins under `dev.prompt.knob.io.convention.*`.

## Rules & gotchas

- Plugin IDs use the `dev.prompt.knob.io.convention.` prefix. Never introduce another package
  prefix.
- `application`/`feature`/`library` extend `BaseConventionPlugin`; add shared behaviour to the
  base and override per-plugin only what differs.
- `explicitApi()` is enforced in library/feature/application modules — new public API needs
  explicit visibility modifiers.
- Module name is derived from the project path (`:a-b-c` → `a.b.c`); keep module directory
  names aligned with their intended namespace.
- After changing a plugin, sanity-check a representative module of each kind
  (`:common-core` = library, `:presentation-feature-about` = feature, `:compose-application`
  = application) still configures and builds.
- When you register or rename a plugin, update this module's `README.md` and
  `docs/architecture/convention-plugins.md`.
