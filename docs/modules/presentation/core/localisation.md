# presentation-core-localisation

Runtime language switching for the shared UI. It exposes a Compose provider that overrides the Compose Resources locale for every `stringResource()` call, so the app can change language without restarting.

Package root: `presentation.core.localisation.source.provider`.

## Key types

| Type | Kind | Package |
|------|------|---------|
| `AppLocaleProvider(languageCode, content)` | `@Composable` provider | `presentation.core.localisation.source.provider` |
| `LocalLocalization` | `ProvidableCompositionLocal<String>` (default `"en"`) | `presentation.core.localisation.source.provider` |

### AppLocaleProvider

```kotlin
AppLocaleProvider(languageCode = languageCode) {
    // app content — every stringResource() now resolves against `languageCode`
}
```

It overrides the Compose Resources locale for the wrapped subtree and provides the current code through `LocalLocalization`, while preserving the composition tree across language changes. It is wired at the root in the shared [`App()`](../../application/compose-application.md) composable, driven by `ObserveApplicationLanguageUseCase`.

## Supported languages

Strings live in the shared Compose Resources of this module:

| Code | Language | Resource |
|------|----------|----------|
| `en` | English (default / fallback) | `values/strings.xml` |
| `uk` | Ukrainian | `values-uk/strings.xml` |
| `es` | Spanish | `values-es/strings.xml` |
| `de` | German | `values-de/strings.xml` |

## DI

None. This module provides functionality purely through the composable provider and its `CompositionLocal`.

## See also

- [compose-application](../../application/compose-application.md) — wires `AppLocaleProvider` at the root.
- [settings](../features/settings.md) — the language picker that persists the selected code.
