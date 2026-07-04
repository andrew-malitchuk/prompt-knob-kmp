# presentation-core-navigation

The navigation layer, split into an API and an implementation module built on AndroidX Navigation 3.

- **presentation-core-navigation-api** — the serializable `Destination` type, the `AppNavigator` interface, `NavOptions`, and the composition locals.
- **presentation-core-navigation-impl** — `AppNavigatorImpl` (backed by a Navigation 3 `NavBackStack`) and the `NavigationHost` composable that maps destinations to feature screens.

## Destination

`Destination` is a `@Serializable` sealed class (a `NavKey`) so each entry survives saved-state restoration.

Package: `presentation.core.navigation.api.source.destination`.

### Object destinations

| Destination | Screen |
|-------------|--------|
| `Splash` | Launch splash + routing. |
| `Onboarding` | First-run onboarding. |
| `Home` | Hub / connection router. |
| `Settings` | Application settings. |
| `About` | App info. |
| `Styleguide` | Design-system showcase (dev utility). |
| `Devices` | BLE scan & pair list. |
| `Device` | Connected-knob dashboard. |
| `LanguagePicker` | Full-screen language list. |
| `Presets` | Preset gallery + management. |

### Data-class destinations

| Destination | Parameters |
|-------------|------------|
| `CommandList` | `parentId: Int = 0`, `isParentRotary: Boolean = false` |
| `CommandForm` | `commandId: Int = -1`, `isFolder: Boolean = false`, `parentId: Int = 0`, `sessionId: String = ""`, `sortOrder: Int = 0`, `isParentRotary: Boolean = false` |

`CommandList` carries the folder level being browsed (`parentId = 0` is root). `CommandForm` carries full form context: `commandId = -1` means "create new"; `sessionId` is a random token that forces a fresh ViewModel per create operation; `sortOrder` places entries in a rotary folder (`0` = CW, `1` = CCW).

## AppNavigator

Interface in `presentation.core.navigation.api.source.destination`:

| Member | Purpose |
|--------|---------|
| `navigate(destination, options = NavOptions.Default)` | Navigate to a destination. |
| `popBackStack()` | Pop the top back-stack entry. |
| `backAction()` | Default back navigation (delegates to `popBackStack`). |

`NavOptions` (enum):

| Option | Behavior |
|--------|----------|
| `Default` | Push onto the back stack. |
| `SingleTop` | Push only if the top is not already the same destination. |
| `ClearTask` | Clear the entire back stack before pushing (used for Splash &rarr; Home/Onboarding). |

### Composition locals

Package: `presentation.core.navigation.api.core.composition`.

- `LocalAppNavigator: ProvidableCompositionLocal<AppNavigator?>` — the active navigator.
- `LocalBackAction: ProvidableCompositionLocal<() -> Unit>` — the current back handler.

Feature screens read `LocalAppNavigator.current` to navigate instead of taking navigation lambdas.

## Implementation

`presentation-core-navigation-impl`:

| Type | Package |
|------|---------|
| `AppNavigatorImpl(backStack)` | `presentation.core.navigation.impl.source.navigator` |
| `NavigationHost(startDestination = null)` | `presentation.core.navigation.impl.source.host` |

`NavigationHost` sets up the Navigation 3 back stack (default start `Splash`), provides `AppNavigator` and the back action through the composition locals, and maps every `Destination` to its feature screen composable. Transition animations are disabled (`EnterTransition.None` / `ExitTransition.None`), and content width is constrained to `600.dp` on large screens (tablets, foldables, desktop).

## DI

Neither module declares a Koin module — the navigator is provided via composition locals inside `NavigationHost`.

## See also

- [compose-application](../../application/compose-application.md) — hosts `NavigationHost`.
- Feature docs for each destination: [splash](../features/splash.md), [home](../features/home.md), [devices](../features/devices.md), [device](../features/device.md), [command](../features/command.md), [preset](../features/preset.md), [settings](../features/settings.md), [about](../features/about.md).
