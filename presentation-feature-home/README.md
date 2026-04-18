# presentation-feature-home

Main home screen feature module.

## Module Type

Feature (KMP + Compose Multiplatform)

## Key Classes

| Class | Description |
|-------|-------------|
| `HomeScreen` | Home screen composable entry point |
| `HomeViewModel` | Orbit MVI ViewModel |
| `HomeContract` | MVI contract: `State`, `SideEffect`, `Intent` |

## Architecture

Follows Orbit MVI pattern. Currently a placeholder landing screen with a settings navigation button — ready to host any content.

## Dependencies

- `domain-usecase-api` — use case interfaces
- `presentation-core-*` — shared UI, styling, localisation, navigation
- Orbit MVI — state management
- Koin — dependency injection

## Build

```shell
./gradlew :presentation-feature-home:build
```
