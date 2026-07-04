# presentation-feature-splash

The launch screen. It shows a branded splash animation, then decides whether the user should see onboarding or go straight into the app. It follows the project's Orbit MVI structure.

Package root: `presentation.feature.splash`.

## Structure

| Layer | Type | Package |
|-------|------|---------|
| Screen | `SplashScreen(viewModel = koinViewModel())` | `presentation.feature.splash.source.splash` |
| Content | `SplashContent(state, onIntent)` (hosts `SplashSuccessContent`) | `presentation.feature.splash.source.splash` |
| ViewModel | `SplashViewModel : ContainerHost<SplashState, SplashSideEffect>` | `presentation.feature.splash.source.splash` |
| Contract | `SplashState`, `SplashSideEffect` | `presentation.feature.splash.source.splash` |
| Intent | `SplashIntent` | `presentation.feature.splash.source.splash` |
| DI | `presentationFeatureSplashModule` | `presentation.feature.splash.di` |

## Contract

`SplashState`

| Field | Type | Notes |
|-------|------|-------|
| `isLoading` | `Boolean` | `true` while the splash animation runs. |

`SplashSideEffect` (sealed)

| Variant | Meaning |
|---------|---------|
| `NavigateToOnboarding` | First launch — onboarding not completed. |
| `NavigateToHome` | Onboarding already completed. |
| `ShowError(messageId)` | Reserved for error display. |

`SplashIntent` (sealed): `OnAnimationFinished`.

## ViewModel

`SplashViewModel` injects `GetOnboardingStatusUseCase`. On container creation it runs the splash delay, then reads `getOnboardingStatusUseCase().getOrDefault(false)` and posts either `NavigateToHome` (completed) or `NavigateToOnboarding` (not completed).

Routing is based purely on onboarding status. The "skip-to-device" reconnect decision (connected knob &rarr; jump straight to the device dashboard) is made later, by the Home feature — see [home](home.md).

## Navigation

- **In:** application start (root destination `Destination.Splash`).
- **Out:** `Destination.Onboarding` or `Destination.Home` (both replace the splash as a fresh task root).

## Dependencies

- `GetOnboardingStatusUseCase` (`domain.usecase.api`).
- [navigation](../core/navigation.md), [styling](../core/styling.md), [ui](../core/ui.md).

## See also

- [onboarding](onboarding.md) · [home](home.md) · [navigation](../core/navigation.md)
