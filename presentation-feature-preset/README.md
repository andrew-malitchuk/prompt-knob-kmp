# presentation-feature-preset

Preset gallery and management for PromptKnob: browse built-in and user presets, apply them to a device, and export/share presets. Single Orbit MVI screen.

## Public API

| Screen | Content | ViewModel | Contract |
|--------|---------|-----------|----------|
| `PresetListScreen()` | `PresetListContent` | `PresetListViewModel` | `PresetListState`, `PresetListIntent`, `PresetListSideEffect` |

Supporting types: `PresetUiModel` and its `toUiModel()` mapper.

Side effects: `NavigateBack`, `ShareExport`, `ShowError`.

## Destinations

Reached via `Destination.Presets`. Returns via back navigation (`LocalAppNavigator.current`); export uses the system share sheet.

## Dependencies

Orbit (core/viewmodel/compose), Compose Material3, `domain-core`, `domain-usecase-api`, `presentation-core-localisation`, `presentation-core-navigation-api`, `presentation-core-styling`, `presentation-core-ui`, Kermit.

Applies `dev.prompt.knob.io.convention.feature` + `dev.prompt.knob.io.convention.di`. DI module: `presentationFeaturePresetModule`.
