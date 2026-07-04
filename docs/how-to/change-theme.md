# How to: change the theme

PromptKnob's colours, typography, spacing, and other design tokens live in the
**`presentation-core-styling`** module. It implements the *Kinetic Mono* design
system: a token-based theme built on the **Space Grotesk** typeface with light
and dark palettes. This page shows where each token lives and how to change it.

All source paths below are relative to
`presentation-core-styling/src/commonMain/kotlin/presentation/core/styling/`.

## How the theme is wired

`AppTheme` (`source/theme/AppTheme.kt`) is the top-level composable. It selects
a palette from the requested `ThemeMode` and provides every token group through
`CompositionLocal`s:

```kotlin
AppTheme(mode = ThemeMode.Dark) {
    Scaffold(containerColor = Theme.color.canvas) { /* ... */ }
}
```

Inside any composable you read tokens through the `Theme` object
(`core/Theme.kt`):

```kotlin
val bg   = Theme.color.canvas
val gap  = Theme.spacing.spacingM
val type = Theme.typography.title
```

`ThemeMode` (`core/ThemeMode.kt`) has three values — `Light`, `Dark`, and
`System`. **Dark** is the primary experience; `System` follows the platform
dark-mode setting.

The app picks the mode from the persisted user preference. `App()` (in
`compose-application`) observes `ObserveThemeUseCase`, maps the stored
`ThemeModel` to a `ThemeMode`, and passes it to `AppTheme`. You normally do not
change the *selection* logic to restyle the app — you change the *token values*
described below.

## Where each token group lives

| Token group | Value file | Definition file (shape) |
|---|---|---|
| Colours (dark) | `source/attribute/color/DarkColor.kt` (`attributeDarkColorPalette`) | `core/ThemeColor.kt` |
| Colours (light) | `source/attribute/color/LightColor.kt` (`attributeLightColorPalette`) | `core/ThemeColor.kt` |
| Font sizes | `source/attribute/FontSize.kt` (`attributeFontSize`) | `core/ThemeFontSize.kt` |
| Line heights | `source/attribute/LineHeight.kt` | `core/ThemeLineHeight.kt` |
| Typography (`TextStyle`s + font family) | `source/attribute/Typography.kt` | `core/ThemeTypography.kt` |
| Spacing | `source/attribute/Spacing.kt` (`attributeSpacing`) | `core/ThemeSpacing.kt` |
| Corner / radius | `source/attribute/Corner.kt`, `Radius.kt` | `core/ThemeCorner.kt`, `ThemeRadius.kt` |
| Stroke | `source/attribute/Stroke.kt` | `core/ThemeStroke.kt` |
| Font files | `composeResources/font/spacegrotesk_*.ttf` | — |

The `Local*` `CompositionLocal`s that carry these down the tree are declared in
`source/provider/Provider.kt`.

## Change a colour

Colours are grouped into semantic roles in `ThemeColor` (brand, surface, ink,
outline, status, interaction). Edit the palette value, not the call site.

For example, to change the dark-mode background, edit
`source/attribute/color/DarkColor.kt`:

```kotlin
internal val attributeDarkColorPalette: ThemeColor =
    ThemeColor(
        // ...
        canvas = Color(0xFF0E0E0E),   // ← change this
        // ...
    )
```

Do the same in `LightColor.kt` for the light palette. Because every screen reads
`Theme.color.canvas`, the change propagates everywhere automatically.

## Change the typeface or type scale

- **Font family:** `Typography.kt` builds the family from the four bundled
  `spacegrotesk_*.ttf` files in `composeResources/font/`
  (`SpaceGroteskFontFamily()`). To swap the typeface, drop new `.ttf` files into
  that `font/` directory, regenerate the Compose Resources accessors (they are
  generated on build), and update the `Font(...)` references.
- **Sizes:** edit `attributeFontSize` in `FontSize.kt` (e.g. `display = 48.sp`).
- **Weights / letter-spacing per role:** edit the `TextStyle`s in
  `AttributeTypography()` in `Typography.kt` (display, title, label, body,
  bodyEmphasis, caption, action).

## Change spacing, corners, or strokes

Edit the corresponding `attribute*` value in `source/attribute/`. For instance,
the spacing scale is an 8 dp grid defined in `Spacing.kt`:

```kotlin
internal val attributeSpacing: ThemeSpacing =
    ThemeSpacing(
        spacingS = 8.dp,
        spacingM = 12.dp,
        // ...
    )
```

## Verify your change

Rebuild a target that includes the UI, for example the Android app:

```bash
./gradlew :aos-application:assembleDebug
```

Then run it ([Run on Android](run-android.md)) and toggle Light/Dark from the
in-app Settings screen to confirm both palettes.
