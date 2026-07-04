# How to: add localisation

PromptKnob's strings and language switching live in the
**`presentation-core-localisation`** module. Strings are Compose Resources XML
files, and the active language is applied reactively at runtime — no restart and
no loss of the navigation back-stack.

The app ships in **four languages**: English (`en`), Ukrainian (`uk`), Spanish
(`es`), and German (`de`).

## Where strings live

Resource files are under
`presentation-core-localisation/src/commonMain/composeResources/`:

| Language | File |
|---|---|
| English (default) | `values/strings.xml` |
| Ukrainian | `values-uk/strings.xml` |
| Spanish | `values-es/strings.xml` |
| German | `values-de/strings.xml` |

`values/strings.xml` is the base/default locale. The module sets
`publicResClass = true` (in its `build.gradle.kts`), so the generated `Res`
class and `Res.string.*` accessors are visible to feature modules.

## How language switching works

`AppLocaleProvider`
(`src/commonMain/kotlin/presentation/core/localisation/source/provider/AppLocaleProvider.kt`)
is a composable that overrides the Compose Resources locale for its content:

```kotlin
AppLocaleProvider(languageCode = "uk") {
    // every stringResource(...) inside resolves Ukrainian strings
}
```

Internally it builds a `ResourceEnvironment` with a `LanguageQualifier` and
provides a custom `ComposeEnvironment` plus the current code via the
`LocalLocalization` composition local. Because it wraps content instead of using
a `key()`, the composition tree (and navigation back-stack) survives a language
change.

The app selects the language from the persisted preference: `App()` (in
`compose-application`) observes `ObserveApplicationLanguageUseCase`, defaulting to
`"en"`, and passes the code to `AppLocaleProvider`. Feature code that needs to
react to the current language can read `LocalLocalization.current`.

## Add a new string

1. Add the key to the base file `values/strings.xml`:

   ```xml
   <string name="my_new_label">MY LABEL</string>
   ```

2. Add the same key with a translation to **every** locale file
   (`values-uk/strings.xml`, `values-es/strings.xml`, `values-de/strings.xml`).
   Keep the `name` identical across all files.

3. Use it in a composable via the generated accessor:

   ```kotlin
   import org.jetbrains.compose.resources.stringResource
   import prompt_knob_kmp.presentation_core_localisation.generated.resources.Res
   import prompt_knob_kmp.presentation_core_localisation.generated.resources.my_new_label

   Text(text = stringResource(Res.string.my_new_label))
   ```

### Notes on formatting

- Placeholders use standard Android/Compose formatting, e.g.
  `%1$d DEVICES FOUND` or `PORT %1$s`. Pass arguments as
  `stringResource(Res.string.key, arg)`.
- Escape apostrophes and use `\n` for newlines, exactly as in the existing
  entries (see `settings_mcp_guide_how_body` for a multi-line example).
- Purely technical identifiers (e.g. `VOLUME_UP`, `PROMPT_KNOB_NODE_01`) are
  intentionally kept identical across locales.

## Add a new language

1. Create a new resource directory named `values-<code>` under
   `composeResources/`, where `<code>` is the ISO language code (e.g.
   `values-fr` for French). Add a `strings.xml` translating **all** keys from
   the base `values/strings.xml`.

2. Wire the language into the settings UI so users can pick it. The selectable
   language labels are themselves strings — see the `settings_language_*` keys
   in `values/strings.xml` (e.g. `settings_language_en`,
   `settings_language_uk`). Add a `settings_language_fr` label and expose the
   option in the settings feature and the language use case flow
   (`SetApplicationLanguageUseCase` / `ObserveApplicationLanguageUseCase`).

3. The stored code flows straight into `AppLocaleProvider`, so once the
   preference can be set to `"fr"`, `stringResource(...)` calls resolve the new
   locale automatically.

## Verify

```bash
./gradlew :aos-application:assembleDebug
```

Run the app, open Settings, and switch languages to confirm the new strings
resolve correctly in each locale.
