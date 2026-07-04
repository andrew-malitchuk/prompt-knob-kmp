# presentation-core-navigation-impl — AI agent guide

Wires the whole app together. This is the ONE module that depends on every feature module, so it is where a new screen becomes reachable.

Package root: `presentation.core.navigation.impl`. Applies `dev.prompt.knob.io.convention.feature`.

## Responsibility
- `NavigationHost` — Nav3 `NavDisplay` with an `entryProvider` mapping each `Destination` to its feature `*Screen()`.
- `AppNavigatorImpl` — mutates the `NavBackStack` for `navigate`/`popBackStack`, honouring `NavOptions` (Default / SingleTop / ClearTask).
- `navSavedStateConfiguration` — the `SavedStateConfiguration` listing all `Destination` subclasses for process-death restore.

## Wiring a new screen (do all three)
1. Add the `Destination` member in `presentation-core-navigation-api`.
2. Register it in `navSavedStateConfiguration` here.
3. Add an `entry<Destination.X> { ... XScreen() }` block in `NavigationHost`, and add the feature module to this module's `build.gradle.kts`.

## Gotchas
- Data-class destinations (`CommandList`, `CommandForm`) must forward `key.*` args into the screen — see the existing `entry<>` blocks.
- Transitions are intentionally `None`; do not add animations without a reason.
- The `max 600.dp` width clamp and `statusBarsPadding` are deliberate for adaptive/edge-to-edge layout — keep them.
