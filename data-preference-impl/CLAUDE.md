# data-preference-impl — Agent Guide

Impl module: platform storage for every `PreferenceSource` declared in `data-preference-api`. Backed by multiplatform-settings; all impls are `internal`.

## Responsibility

- One `internal *PreferenceSourceImpl` per source interface, reading/writing via `com.russhwolf.settings.Settings` and exposing reactive `observeData()`.
- `dataPreferenceImplModule` (Koin) — calls `provideSettings()` (expect/actual per platform) and binds every `*SourceImpl` to its interface as a singleton.

## Platform Settings backends (`SettingsProvider.<platform>.kt`)

- Android — Preferences DataStore via the custom `DataStoreSettings` bridge.
- iOS / macOS — `NSUserDefaultsSettings`.
- Desktop — `PreferencesSettings` (`java.util.prefs`).

## Conventions

- Applies `dev.prompt.knob.io.convention.library`.
- `api(projects.dataPreferenceApi)` (re-exported), `implementation(projects.dataCore)`.
- Packages: `data.preference.impl.source.datasource` (impls), `data.preference.impl.di` (module + `SettingsProvider`).

## Gotchas

- When you add a preference in the API module, you MUST add the `*Impl` here AND register it in `dataPreferenceImplModule` — otherwise Koin resolution fails at runtime.
- `provideSettings()` is an expect/actual in the `di` package; add new platforms there, not in the module DSL.
- Impls are `internal` — never expose them; consumers depend on the API interfaces.
