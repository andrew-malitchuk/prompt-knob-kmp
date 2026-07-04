# presentation-feature-about — AI agent guide

Single Orbit MVI screen under `presentation.feature.about.source.about`: the app information screen.

Applies `dev.prompt.knob.io.convention.feature` + `dev.prompt.knob.io.convention.di`.

## Structure
- `AboutScreen(viewModel = koinViewModel())` — handles `AboutSideEffect` (`GoBackEffect`, `ShowError`) via `LocalAppNavigator.current`, delegates to `AboutContent` / `AboutSuccessContent` / `AboutShimmerContent`.
- `AboutViewModel : ContainerHost<AboutState, AboutSideEffect>` with `AboutIntent` (`OnBackClick`). `AboutState` is minimal (`isLoading`).

## Gotchas
- Read-only screen — keep it stateless beyond `isLoading`; no device/BLE dependencies.
- `AboutViewModel` is registered as a viewModel binding in `presentationFeatureAboutModule`.
- Reached via `Destination.About`; returns with `GoBackEffect`.
