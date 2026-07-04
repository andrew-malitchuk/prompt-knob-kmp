# presentation-core-navigation-api

Public navigation contract for PromptKnob: the sealed `Destination` hierarchy, the `AppNavigator` interface, and the composition locals used to reach them from any feature module. Implementation lives in `presentation-core-navigation-impl`.

## Public API

| Type | Kind | Purpose |
|------|------|---------|
| `Destination` | sealed class : `NavKey` | All navigable screens (see below) |
| `AppNavigator` | interface | `navigate(destination, options)`, `popBackStack()`, `backAction()` |
| `AppNavigator.NavOptions` | enum | `Default`, `SingleTop`, `ClearTask` |
| `LocalAppNavigator` | CompositionLocal | Provides the active `AppNavigator` |
| `LocalBackAction` | CompositionLocal | Provides the default back callback |

## Destinations

`Destination` has 12 members:

| Destination | Kind | Notes |
|-------------|------|-------|
| `Splash` | data object | Launch splash with animation |
| `Onboarding` | data object | First-time onboarding flow |
| `Home` | data object | Main home screen |
| `Settings` | data object | App settings (theme, language) |
| `About` | data object | App information |
| `Styleguide` | data object | Design-system showcase (dev utility) |
| `Devices` | data object | BLE device scan/connect list |
| `Device` | data object | Connected-knob detail dashboard |
| `LanguagePicker` | data object | Full-screen locale picker |
| `Presets` | data object | Preset gallery and management |
| `CommandList(parentId, isParentRotary)` | data class | Command/folder list for a folder level; `parentId = 0` is root |
| `CommandForm(commandId, isFolder, parentId, sessionId, sortOrder, isParentRotary)` | data class | Create/edit a command or folder; `commandId = -1` means create |

Every member is `@Serializable` for Navigation 3 saved-state persistence. New destinations must also be registered in `navSavedStateConfiguration` and `NavigationHost` (impl module).

## Usage

```kotlin
val navigator = LocalAppNavigator.current
navigator?.navigate(Destination.Settings)
navigator?.navigate(Destination.CommandForm(commandId = -1, parentId = 0))
```

## Dependencies

AndroidX Navigation 3 runtime (`NavKey`), kotlinx-serialization-core. No internal module dependencies.
