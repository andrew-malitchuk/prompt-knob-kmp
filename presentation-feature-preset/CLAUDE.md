# presentation-feature-preset — AI agent guide

Single Orbit MVI screen under `presentation.feature.preset.source.list`: preset gallery + management.

Applies `dev.prompt.knob.io.convention.feature` + `dev.prompt.knob.io.convention.di`.

## Structure
- `PresetListScreen()` — collects `PresetListState`, handles `PresetListSideEffect` (`NavigateBack`, `ShareExport`, `ShowError`), delegates to `PresetListContent`.
- `PresetListContent` — stateless UI; renders `PresetUiModel`s (mapped via `toUiModel()`), emits `PresetListIntent`.
- `PresetListViewModel : ContainerHost<PresetListState, PresetListSideEffect>`.

## Gotchas
- `ShareExport` side effect hands off to the system share sheet — keep the payload platform-agnostic in commonMain.
- `PresetListViewModel` is registered as a **factory** (not a viewModel single) in `presentationFeaturePresetModule` — a fresh instance per screen entry.
- Map domain models to `PresetUiModel` in the ViewModel; keep `PresetListContent` free of domain types.
