# presentation-feature-onboarding

The first-run onboarding screen shown once, before the user reaches the app. Tapping "Get Started" persists the onboarding flag and moves on to Home. Orbit MVI structure.

Package root: `presentation.feature.onboarding`.

## Structure

| Layer | Type | Package |
|-------|------|---------|
| Screen | `OnboardingScreen(viewModel = koinViewModel())` | `presentation.feature.onboarding.source.onboarding` |
| Content | `OnboardingContent(state, onIntent, snackbarHostState)` (hosts `OnboardingSuccessContent`) | `presentation.feature.onboarding.source.onboarding.content` |
| ViewModel | `OnboardingViewModel : ContainerHost<OnboardingState, OnboardingSideEffect>` | `presentation.feature.onboarding.source.onboarding` |
| Contract | `OnboardingState`, `OnboardingSideEffect` | `presentation.feature.onboarding.source.onboarding` |
| Intent | `OnboardingIntent` | `presentation.feature.onboarding.source.onboarding` |
| DI | `presentationFeatureOnboardingModule` | `presentation.feature.onboarding.di` |

## Contract

`OnboardingState`

| Field | Type | Notes |
|-------|------|-------|
| `isLoading` | `Boolean` | Defaults to `false`. |

`OnboardingSideEffect` (sealed): `NavigateToHome`, `ShowError(messageId)`.

`OnboardingIntent` (sealed): `OnGetStartedClick`.

## ViewModel

`OnboardingViewModel` injects `SetOnboardingStatusUseCase`. On `OnGetStartedClick` it persists the flag via `setOnboardingStatusUseCase(true)`. On success it posts `NavigateToHome`; on failure it retries once and then navigates anyway so the user is never locked on the onboarding screen.

## Navigation

- **In:** `Destination.Onboarding` (from Splash on first launch).
- **Out:** `Destination.Home` (replaces onboarding as a fresh task root).

## Dependencies

- `SetOnboardingStatusUseCase` (`domain.usecase.api`).
- [navigation](../core/navigation.md), [styling](../core/styling.md), [ui](../core/ui.md).

## See also

- [splash](splash.md) · [home](home.md)
