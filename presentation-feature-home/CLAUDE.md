# CLAUDE.md — presentation-feature-home

## Purpose

Main home screen — placeholder landing page with settings navigation.

## Convention Plugins

- `dev.prompt.knob.io.convention.feature`
- `dev.prompt.knob.io.convention.di`

## Key Files

| File | Purpose |
|------|---------|
| `HomeScreen` | Entry point composable |
| `HomeViewModel` | Orbit MVI ViewModel |
| `HomeContract` | State / SideEffect / Intent definitions |
| `HomeSuccessContent` | Loaded state UI with settings button |

## Module Dependencies

- `domain-usecase-api`
- `presentation-core-localisation`
- `presentation-core-styling`
- `presentation-core-ui`
- `presentation-core-navigation-api`

## Build

```shell
./gradlew :presentation-feature-home:build
```
