# presentation-core-navigation-api

> Navigation abstractions — sealed destinations, navigator interface, and composition locals.

## Responsibility

Defines the app's navigation graph as a sealed `Destination` hierarchy and provides the `AppNavigator` interface for type-safe navigation. Feature modules depend on this module to declare navigation targets without coupling to the navigation implementation.

## Dependencies

| Depends on | Purpose |
|---|---|
| Navigation3-ui | Navigation framework types |
| kotlinx-serialization-core | Serializable destinations for saved state |

## Public API

### Navigator

| Interface | Description |
|---|---|
| `AppNavigator` | Navigation contract: `navigate(destination, options)`, `popBackStack()`, `backAction()` |
| `AppNavigator.NavOptions` | Enum: `Default`, `SingleTop`, `ClearTask` |

### Destinations

| Destination | Description |
|---|---|
| `Destination.Splash` | Launch splash screen |
| `Destination.Onboarding` | First-time onboarding flow |
| `Destination.Home` | Main home screen |
| `Destination.Settings` | App settings |
| `Destination.About` | App information |

### Composition Locals

| Local | Description |
|---|---|
| `LocalAppNavigator` | Provides `AppNavigator?` to the composition tree |
| `LocalBackAction` | Provides back-navigation callback `() -> Unit` |

## Usage

```kotlin
// Navigate from a feature screen
val navigator = LocalAppNavigator.current
navigator?.navigate(Destination.Settings)

// Navigate with options
navigator?.navigate(
    destination = Destination.Home,
    options = AppNavigator.NavOptions.ClearTask,
)

// Handle back action
val backAction = LocalBackAction.current
backAction()
```

## Testing

```bash
./gradlew :presentation-core-navigation-api:test
```
