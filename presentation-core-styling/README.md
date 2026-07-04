# presentation-core-styling

The Kinetic Mono design system for PromptKnob: a token-based visual language exposed through the `Theme` singleton and applied via the `AppTheme` composable. Dark mode is the primary experience; light mode is a supported alternative.

## Typography

Space Grotesk is the **exclusive** typeface (Light / Regular / Medium / Bold weights bundled as Compose Resources). Roles: Display (Light, tight tracking), Title (Medium, uppercase, wide tracking), Label (Bold, heavy tracking — firmware/technical metadata), plus Body / Body-emphasis / Caption / Action.

## Public API

| Type | Kind | Description |
|------|------|-------------|
| `AppTheme(mode) { }` | Composable | Top-level provider; resolves light/dark palette and distributes tokens via CompositionLocals |
| `Theme` | object | Token accessor: `color`, `colorToken`, `fontSize`, `lineHeight`, `typography`, `spacing`, `spacingToken`, `radius`, `corner`, `cornerToken`, `size`, `stroke` |
| `ThemeMode` | enum | `Light`, `Dark`, `System` |
| `ThemeColor` | data class | Palette: `brand`, `brandVariant`, `canvas`, `surface`, `surfaceVariant`, `surfaceInverse`, `inkMain`, `inkSubtle`, `inkOnBrand`, `outlineLow`, `outlineHigh`, `success`, `error`, `warning`, `disabled`, `scrim` |
| `ThemeTypography` | data class | `display`, `title`, `label`, `body`, `bodyEmphasis`, `caption`, `action` |
| `ThemeFontSize` / `ThemeLineHeight` | data class | Per-role size and matching line height |
| `ThemeSpacing` / `ThemeSpacingToken` | data class | Raw spacing scale and semantic spacing tokens |
| `ThemeCorner` / `ThemeCornerToken` / `ThemeRadius` | data class | Corner shapes, semantic corner tokens, radii |
| `ThemeSize` / `ThemeStroke` / `ThemeColorToken` | data class | Icon/component sizes, stroke widths, semantic colour tokens |

## Usage

```kotlin
AppTheme(mode = ThemeMode.Dark) {
    Text(
        text = "STATUS",
        style = Theme.typography.label,
        color = Theme.color.inkMain,
    )
    Spacer(Modifier.background(Theme.color.canvas))
}
```

## Dependencies

No internal module dependencies. Compose runtime/UI + Compose Resources for bundled fonts.
