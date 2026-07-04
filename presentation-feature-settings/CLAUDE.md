# presentation-feature-settings — AI agent guide

Two Orbit MVI screens under `presentation.feature.settings`: the main settings screen (`source/settings`) and the full-screen language picker (`source/language`).

Applies `dev.prompt.knob.io.convention.feature` + `dev.prompt.knob.io.convention.di`.

## Structure
- `SettingsScreen(viewModel = koinViewModel())` → `SettingsContent` / `SettingsSuccessContent` / `SettingsShimmerContent`; `SettingsViewModel : ContainerHost<SettingsState, SettingsSideEffect>` with `SettingsIntent`.
- `LanguagePickerScreen(...)` → `LanguagePickerContent`; `LanguagePickerViewModel` with `LanguagePickerState/Intent/SideEffect`.
- Covers language, theme, BLE/accessibility/notification permission state, connection state, MCP + Claude-mode toggles, export/import, and erase-all-data.

## Gotchas
- Navigation via side effects (`GoBackEffect`, `NavigateToLanguagePicker`, `NavigateToDevice`, `NavigateToAbout`, `NavigateToSplash`) handled in the screen using `LocalAppNavigator.current`.
- MCP/Claude availability is platform-gated — the DI module provides qualified `isMcpAvailable` / `isClaudeAvailable` flags; guard the toggles on them (only macOS hosts these servers).
- Export/import uses `filekit-dialogs-compose` (platform-specific) and `data-executor-api`.
- Register both ViewModels in `presentationFeatureSettingsModule`.
