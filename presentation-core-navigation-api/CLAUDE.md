# presentation-core-navigation-api — AI agent guide

Pure navigation contract module. No screens, no implementation — only the `Destination` sealed hierarchy, the `AppNavigator` interface + `NavOptions` enum, and the `LocalAppNavigator` / `LocalBackAction` composition locals.

Package root: `presentation.core.navigation.api`.

## Responsibility
- Own the single source of truth for the set of routes: `Destination` (12 members — Splash, Onboarding, Home, Settings, About, Styleguide, Devices, Device, LanguagePicker, Presets, `CommandList` [data class], `CommandForm` [data class]).
- Feature modules depend on THIS module (not impl) and trigger navigation via `LocalAppNavigator.current`.

## Gotchas
- Every `Destination` member is `@Serializable` and extends `NavKey` — keep it that way for saved-state persistence.
- Adding a destination is a 3-step change: add here, register in `navSavedStateConfiguration`, add an `entry<>` in `NavigationHost` (both in `presentation-core-navigation-impl`).
- `CommandList` / `CommandForm` carry arguments — read them from the `entry<>` `key` in the host; do not hardcode.
- No `feature` convention plugin here (no Compose UI, no ViewModel) — it is a plain API library module.
