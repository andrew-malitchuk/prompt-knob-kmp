# presentation-core-localisation — AI agent guide

Provides the current app locale to the composition so screens can resolve localised strings for the active language, independent of the system locale.

Package root: `presentation.core.localisation`. Applies `dev.prompt.knob.io.convention.feature`.

## Responsibility
- `AppLocaleProvider(languageCode) { }` — wraps content and provides the active language code down the tree.
- `LocalLocalization: ProvidableCompositionLocal<String>` — the current language code (e.g. `"en"`, `"uk"`), read by string-resolution call sites.

## Gotchas
- Language is app-controlled (driven by the persisted preference observed in `App()`), NOT the system locale — read it via `LocalLocalization.current`, not platform APIs.
- No DI module and no ViewModel here — it is a small Compose utility module.
- Keep language codes as plain strings matching the Compose Resources locale qualifiers.
