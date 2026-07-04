# presentation-core-styling

The design system: theming, the Space Grotesk typography scale, and a full set of design tokens (color, spacing, corner, radius, size, stroke). Tokens are exposed through composition locals and accessed via the `Theme` singleton.

Package roots: `presentation.core.styling.core` (tokens + `Theme`) and `presentation.core.styling.source` (providers, theme, palettes).

## AppTheme

```kotlin
AppTheme(mode = themeMode) {
    // app content — all Theme tokens are now available
}
```

| Type | Package | Notes |
|------|---------|-------|
| `AppTheme(mode = ThemeMode.Dark, content)` | `presentation.core.styling.source.theme` | Provides every token via composition locals. Default mode `Dark`. |
| `ThemeMode` | `presentation.core.styling.core` | `Light`, `Dark`, `System`. |

`ThemeMode.System` follows the platform dark-mode preference; `Light`/`Dark` force a palette.

## Theme singleton

`Theme` (object, `presentation.core.styling.core`) is the central accessor for tokens. Each property is a `@Composable` getter reading the corresponding composition local:

| Property | Token type |
|----------|-----------|
| `color` | `ThemeColor` |
| `colorToken` | `ThemeColorToken` |
| `fontSize` | `ThemeFontSize` |
| `lineHeight` | `ThemeLineHeight` |
| `typography` | `ThemeTypography` |
| `spacing` | `ThemeSpacing` |
| `spacingToken` | `ThemeSpacingToken` |
| `radius` | `ThemeRadius` |
| `corner` | `ThemeCorner` |
| `cornerToken` | `ThemeCornerToken` |
| `size` | `ThemeSize` |
| `stroke` | `ThemeStroke` |

## Typography — Space Grotesk

The type system is built on the **Space Grotesk** font family (`SpaceGroteskFontFamily()`), bundling Light, Regular, Medium, and Bold weights.

`ThemeTypography` defines the roles: `display`, `title`, `label`, `body`, `bodyEmphasis`, `caption`, `action` — each a `TextStyle` composed from `ThemeFontSize` and `ThemeLineHeight` (for example `display` at 48sp, `title` at 20sp, `body` at 14sp, `caption`/`label` at 10sp).

## Token objects

All in `presentation.core.styling.core`:

| Token | Role |
|-------|------|
| `ThemeColor` | Raw semantic palette (brand, canvas, surface, ink, outline, success/error/warning, disabled, scrim). |
| `ThemeColorToken` | Role-based aliases (backgroundPrimary/Secondary/Tertiary, textPrimary/Secondary, accentPrimary/Secondary, borderSubtle/Strong, status, overlay). |
| `ThemeFontSize` / `ThemeLineHeight` | Type scale values. |
| `ThemeSpacing` | Spacing scale (`spacingXXS` 2dp … `spacing5XL` 80dp). |
| `ThemeSpacingToken` | Semantic spacing (contentPadding, itemGap, sectionGap, inlinePadding, compactGap). |
| `ThemeRadius` / `ThemeCorner` | Radius values and `RoundedCornerShape` presets. |
| `ThemeCornerToken` | Semantic corners (card, button, chip, badge, input). |
| `ThemeSize` | Icon sizes (`iconXXS` 12dp … `icon4XL` 120dp). |
| `ThemeStroke` | Border/stroke widths. |

## Palettes

Package: `presentation.core.styling.source.attribute.color`.

- `attributeLightColorPalette: ThemeColor` — warm off-white canvas with darkened accents.
- `attributeDarkColorPalette: ThemeColor` — the primary experience: deep charcoals with warm off-whites.

Tokens are provided with `staticCompositionLocalOf` (via `presentation.core.styling.source.provider`) to avoid per-recomposition lookup overhead.

## DI

None — everything is provided through `AppTheme`'s composition locals.

## See also

- [compose-application](../../application/compose-application.md) — wires `AppTheme` from the persisted `ThemeModel`.
- [ui](ui.md) — components built on these tokens.
