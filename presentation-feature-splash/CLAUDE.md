# CLAUDE.md — presentation-feature-splash

## Purpose

Initial app launch screen with loading/shimmer states and navigation to the next destination.

## Convention Plugins

- `dev.prompt.knob.io.convention.feature`
- `dev.prompt.knob.io.convention.di`

## Key Files

| File | Purpose |
|------|---------|
| `SplashScreen` | Entry point composable |
| `SplashViewModel` | Orbit MVI ViewModel with loading logic |
| `SplashContract` | State / SideEffect / Intent definitions |
| `SplashContent` | Main content composable |
| `SplashSuccessContent` | Success state UI |
| `SplashShimmerContent` | Loading shimmer placeholder |

## Module Dependencies

- `domain-usecase-api`
- `presentation-core-localisation`
- `presentation-core-styling`
- `presentation-core-ui`
- `presentation-core-navigation-api`

## Build

```shell
./gradlew :presentation-feature-splash:build
```
