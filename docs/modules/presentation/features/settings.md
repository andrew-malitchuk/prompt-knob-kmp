# presentation-feature-settings

The settings feature. It contains **two** screens: the main settings dashboard (`SettingsScreen`) and a full-screen language picker (`LanguagePickerScreen`). It controls theme, language, BLE permissions, command import/export, data erasure, and — on macOS — the MCP and Claude-hook servers. Orbit MVI structure.

Package root: `presentation.feature.settings`.

## Screens

| Destination | Screen | ViewModel |
|-------------|--------|-----------|
| `Settings` | `SettingsScreen` | `SettingsViewModel` |
| `LanguagePicker` | `LanguagePickerScreen` | `LanguagePickerViewModel` |

DI module: `presentationFeatureSettingsModule` (`presentation.feature.settings.di`) registers `SettingsViewModel` (with platform qualifiers for MCP/Claude availability) and `LanguagePickerViewModel`.

## Settings dashboard

`SettingsScreen(viewModel = koinViewModel())` refreshes permission state on `Lifecycle.Event.ON_RESUME` and sets up import/export file launchers. Content: `SettingsContent(state, onIntent, onImportClick, snackbarHostState)`, cross-fading between a loading shimmer and `SettingsSuccessContent`.

`SettingsViewModel` injects:

| Dependency | Role |
|------------|------|
| `ObserveApplicationLanguageUseCase` | Reactive language code (auto-updates when returning from the picker). |
| `GetThemeUseCase` / `SetThemeUseCase` | Theme selection. |
| `CheckBlePermissionsUseCase` / `RequestBlePermissionsUseCase` | BLE permission state. |
| `ObserveConnectionStateUseCase` | Device name / connection status. |
| `ForgetDeviceUseCase` | Forget the paired device. |
| `SetOnboardingStatusUseCase` | Reset onboarding on erase-all. |
| `SystemSettingsOpener` | Open accessibility / notification settings. |
| `ExportCommandsUseCase` / `ImportCommandsUseCase` | Command backup and restore. |
| `GetMcpEnabledUseCase` / `SetMcpEnabledUseCase` | MCP server control (macOS). |
| `GetClaudeHookEnabledUseCase` / `SetClaudeHookEnabledUseCase` | Claude-hook server control (macOS). |
| `isBleSupported`, `isMcpAvailable`, `isClaudeAvailable` | Platform visibility flags. |

`SettingsState` (grouped): language/theme (`selectedLanguageIndex`, `languageOptions`, `languageCodes`, `selectedThemeIndex`, `themeOptions`); permissions (`isBleSupported`, `isBlePermissionGranted`, `isAccessibilityGranted`, `isNotificationPermissionGranted`); device info (`connectionStateName`, `deviceName`, `appVersion`); MCP/Claude (`isMcpEnabled`, `isMcpSectionVisible`, `mcpPort`, `isClaudeModeEnabled`, `isClaudeSectionVisible`, `claudeHookPort`); UI (`showEraseConfirmation`).

`SettingsSideEffect`: `GoBackEffect`, `NavigateToLanguagePicker`, `NavigateToDevice`, `NavigateToAbout`, `NavigateToSplash`, `ExportReady(json)`, `ShowImportSuccess(count)`, `ShowError(messageId)`, `ShowErrorMessage(message)`.

`SettingsIntent`: `OnBackClick`, `OnLanguagePickerClick`, `SelectTheme(index)`, `OnRequestBlePermission`, `OnOpenAccessibilitySettings`, `OnDeviceClick`, `OnSystemInfoClick`, `OnEraseAllDataClick`, `OnDismissEraseConfirmation`, `OnEraseAllData`, `OnOpenNotificationSettings`, `RefreshPermissions`, `OnExportCommands`, `OnImportCommands(json)`, `OnToggleMcp(enabled)`, `OnToggleClaudeMode(enabled)`.

The MCP and Claude sections are only visible on macOS; toggling them starts/stops the corresponding servers.

## Language picker

`LanguagePickerScreen(viewModel = koinViewModel())` with Content `LanguagePickerContent(state, onIntent)` renders a scrollable list of languages with a selection indicator.

`LanguagePickerViewModel` injects `GetApplicationLanguageUseCase` and `SetApplicationLanguageUseCase`.

`LanguagePickerState`: `selectedIndex`, `options: List<String>`, `codes: List<String>`.

`LanguagePickerSideEffect`: `GoBack`. `LanguagePickerIntent`: `OnBackClick`, `SelectLanguage(index)` — persists the choice immediately, then navigates back. Because Settings observes the language reactively, the change is reflected without an explicit result callback.

## Navigation

- **In:** `Destination.Settings` (from Home / Devices / Device) and `Destination.LanguagePicker` (from the Settings language row).
- **Out:** `Destination.LanguagePicker`, `Destination.Device`, `Destination.About`, and `Destination.Splash` after erase-all-data.

## Dependencies

- Configuration, BLE, command, and MCP use cases from `domain.usecase.api`.
- [localisation](../core/localisation.md), [navigation](../core/navigation.md), [styling](../core/styling.md), [ui](../core/ui.md).

## See also

- [about](about.md) · [home](home.md) · [device](device.md)
