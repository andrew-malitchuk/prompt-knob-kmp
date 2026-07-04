# presentation-core-styling — AI agent guide

The Kinetic Mono design system. Every colour, size, spacing and text style used in the app comes from here through the `Theme` object — components must never hardcode dp/sp/Color values.

Package root: `presentation.core.styling`. Applies `dev.prompt.knob.io.convention.feature`.

## Responsibility
- `AppTheme(mode) { }` resolves the light/dark palette from `mode: ThemeMode` (Light/Dark/System) and provides all token groups via CompositionLocals.
- `Theme` is the read accessor: `Theme.color`, `Theme.typography`, `Theme.spacing`, `Theme.cornerToken`, `Theme.stroke`, etc.

## Typography — do not get this wrong
- The ONLY typeface is **Space Grotesk** (Light/Regular/Medium/Bold), bundled as Compose Resources (`spacegrotesk_*`) and assembled in `AttributeTypography()` / `SpaceGroteskFontFamily()`. There is no serif and no second family.
- Roles: display, title (uppercase, wide tracking), label (firmware/technical), body, bodyEmphasis, caption, action.

## Gotchas
- Dark mode is the primary experience; verify contrast in both palettes (`LightColor.kt` / `DarkColor.kt`) when touching colours.
- Adding a token means updating the `Theme*` data class in `core/`, its attribute builder in `source/attribute/`, and the provider — otherwise `AppTheme` won't supply it.
- No DI, no ViewModel — pure Compose theming module.
