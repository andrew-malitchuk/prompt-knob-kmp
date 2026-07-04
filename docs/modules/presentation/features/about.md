# presentation-feature-about

The About screen: a simple static page showing app information (version, links, credits). Orbit MVI structure, kept minimal as an extension point.

Package root: `presentation.feature.about`.

## Structure

| Layer | Type | Package |
|-------|------|---------|
| Screen | `AboutScreen(viewModel = koinViewModel())` | `presentation.feature.about.source` |
| Content | `AboutContent(state, onIntent)` (delegates to `AboutSuccessContent`) | `presentation.feature.about.source` |
| ViewModel | `AboutViewModel : ContainerHost<AboutState, AboutSideEffect>` | `presentation.feature.about.source` |
| Contract | `AboutState`, `AboutSideEffect` | `presentation.feature.about.source` |
| Intent | `AboutIntent` | `presentation.feature.about.source` |
| DI | `presentationFeatureAboutModule` | `presentation.feature.about.di` |

`AboutSuccessContent` renders the app version, links, and credits as a static layout (no shimmer variant).

## Contract

`AboutState`: `isLoading: Boolean`.

`AboutSideEffect` (sealed): `GoBackEffect`, `ShowError(messageId)`.

`AboutIntent` (sealed): `OnBackClick`.

## ViewModel

`AboutViewModel` takes no constructor dependencies — it is intentionally minimal and exists as an extension point for future features. It is registered with `viewModelOf(::AboutViewModel)`.

## Navigation

- **In:** `Destination.About` (from Settings, via system info).
- **Out:** back on `OnBackClick`.

## Dependencies

- [navigation](../core/navigation.md), [styling](../core/styling.md), [ui](../core/ui.md).

## See also

- [settings](settings.md)
