# presentation-core-navigation-impl

Concrete navigation graph for PromptKnob. Implements `AppNavigator` on top of Navigation 3 and assembles `NavigationHost`, which maps every `Destination` to its feature screen composable.

## Public API

| Type | Kind | Purpose |
|------|------|---------|
| `NavigationHost(startDestination)` | Composable | Root host: creates the Nav3 back stack, provides `LocalAppNavigator`/`LocalBackAction`, routes destinations. Defaults to `Destination.Splash`. |
| `AppNavigatorImpl` | class | Nav3-backed implementation of `AppNavigator` over a `NavBackStack`. |
| `navSavedStateConfiguration` | val (`SavedStateConfiguration`) | Registers all `Destination` subclasses for saved-state persistence. |

## Destination routing

| Destination | Screen composable | From module |
|-------------|-------------------|-------------|
| `Splash` | `SplashScreen()` | presentation-feature-splash |
| `Onboarding` | `OnboardingScreen()` | presentation-feature-onboarding |
| `Home` | `HomeScreen()` | presentation-feature-home |
| `Settings` | `SettingsScreen()` | presentation-feature-settings |
| `LanguagePicker` | `LanguagePickerScreen()` | presentation-feature-settings |
| `About` | `AboutScreen()` | presentation-feature-about |
| `Devices` | `DevicesScreen()` | presentation-feature-devices |
| `Device` | `DeviceDetailScreen()` | presentation-feature-device |
| `CommandList` | `CommandListScreen(parentId, isParentRotary)` | presentation-feature-command |
| `CommandForm` | `CommandFormScreen(commandId, isFolder, parentId, sessionId, sortOrder, isParentRotary)` | presentation-feature-command |
| `Presets` | `PresetListScreen()` | presentation-feature-preset |
| `Styleguide` | `StyleguideScreen(onBack)` | presentation-core-ui |

## Layout notes

The host draws a canvas-coloured outer `Box` behind the system bars for edge-to-edge displays, applies `statusBarsPadding` (no navigation-bar padding — screens extend to the bottom), and constrains content to `max 600.dp` width for tablets/foldables/desktop. All screen transitions are disabled (`EnterTransition.None`).

## Dependencies

`api` on `presentation-core-navigation-api`; `implementation` on every feature module (about, command, preset, devices, device, home, onboarding, settings, splash), plus `presentation-core-styling` and `presentation-core-ui`.
