package presentation.feature.settings.source.settings

/**
 * UI state for the Settings screen.
 *
 * @property isLoading Whether a loading indicator should be shown.
 * @property selectedLanguageIndex Index of the currently selected language in [languageOptions].
 * @property languageOptions Display labels for each supported language.
 * @property languageCodes ISO locale codes corresponding to [languageOptions].
 * @property selectedThemeIndex Index of the currently selected theme (0 = Dark/Monolith, 1 = Light/Brutalist).
 * @property themeOptions Display labels for each supported theme.
 * @property isBlePermissionGranted Whether Bluetooth permissions are currently granted.
 * @property isNotificationPermissionGranted Whether POST_NOTIFICATIONS is granted (Android 13+).
 * @property showEraseConfirmation Whether the erase-all-data confirmation sheet is visible.
 * @property connectionStateName Human-readable BLE connection status (e.g. "CONNECTED VIA BLE").
 * @property deviceName Name of the currently connected or last-seen device.
 * @property appVersion Application version string for display (e.g. "v0.0.1").
 * @property isMcpEnabled Whether the MCP server is currently enabled.
 * @property isMcpSectionVisible Whether the MCP section should be displayed (macOS only).
 * @property mcpPort The port on which the MCP server listens.
 * @property isClaudeModeEnabled Whether the Claude Mode hook server is currently enabled.
 * @property isClaudeSectionVisible Whether the Claude Mode section should be displayed (macOS only).
 * @property claudeHookPort The port on which the Claude hook server listens.
 */
public data class SettingsState(
    val isLoading: Boolean = false,
    val selectedLanguageIndex: Int = 0,
    val languageOptions: List<String> = listOf("English", "Українська", "Español", "Deutsch"),
    val languageCodes: List<String> = listOf("en", "uk", "es", "de"),
    val selectedThemeIndex: Int = 0,
    val themeOptions: List<String> = listOf("Light", "Dark"),
    val isBleSupported: Boolean = true,
    val isBlePermissionGranted: Boolean = false,
    /** Whether the app's AccessibilityService is enabled. Defaults to true on non-Android. */
    val isAccessibilityGranted: Boolean = true,
    /** Whether POST_NOTIFICATIONS permission is granted. Defaults to true on non-Android. */
    val isNotificationPermissionGranted: Boolean = true,
    /** Whether the erase-all-data confirmation sheet is currently shown. */
    val showEraseConfirmation: Boolean = false,
    val connectionStateName: String = "DISCONNECTED",
    val deviceName: String? = null,
    val appVersion: String = "v0.0.1",
    val isMcpEnabled: Boolean = true,
    val isMcpSectionVisible: Boolean = false,
    val mcpPort: Int = 7474,
    val isClaudeModeEnabled: Boolean = false,
    val isClaudeSectionVisible: Boolean = false,
    val claudeHookPort: Int = 7777,
)

/**
 * One-shot side-effects emitted by [SettingsViewModel].
 */
public sealed class SettingsSideEffect {

    /** Navigate back to the previous screen. */
    public data object GoBackEffect : SettingsSideEffect()

    /** Navigate to the Language Picker screen. */
    public data object NavigateToLanguagePicker : SettingsSideEffect()

    /** Navigate to the Device (BLE pairing) screen. */
    public data object NavigateToDevice : SettingsSideEffect()

    /** Navigate to the About / System Info screen. */
    public data object NavigateToAbout : SettingsSideEffect()

    /** Navigate to the Splash screen after all data has been erased. */
    public data object NavigateToSplash : SettingsSideEffect()

    /** Export is ready — provide the JSON string to the caller to save as a file. */
    public data class ExportReady(val json: String) : SettingsSideEffect()

    /** Import completed successfully — inform the user how many commands were loaded. */
    public data class ShowImportSuccess(val count: Int) : SettingsSideEffect()

    /**
     * Display an error message to the user.
     *
     * @property messageId String resource identifier for the error message.
     */
    public data class ShowError(val messageId: Int) : SettingsSideEffect()

    /** Display a generic error string (used for import/export failures). */
    public data class ShowErrorMessage(val message: String) : SettingsSideEffect()
}

/**
 * User-initiated actions on the Settings screen.
 */
public sealed class SettingsIntent {

    /** User tapped the back / navigation button. */
    public data object OnBackClick : SettingsIntent()

    /**
     * User tapped the language row to open the dedicated Language Picker screen.
     */
    public data object OnLanguagePickerClick : SettingsIntent()

    /**
     * User selected a theme at the given [index] in [SettingsState.themeOptions].
     */
    public data class SelectTheme(val index: Int) : SettingsIntent()

    /** User tapped "REQUEST" to ask for Bluetooth permissions. */
    public data object OnRequestBlePermission : SettingsIntent()

    /** User tapped the accessibility row to open system accessibility settings. */
    public data object OnOpenAccessibilitySettings : SettingsIntent()

    /** User tapped the System Registry card to go to the Device screen. */
    public data object OnDeviceClick : SettingsIntent()

    /** User tapped "SYSTEM INFO" in the footer to navigate to the About screen. */
    public data object OnSystemInfoClick : SettingsIntent()

    /** User tapped "ERASE ALL DATA" — shows the confirmation sheet. */
    public data object OnEraseAllDataClick : SettingsIntent()

    /** User dismissed the erase confirmation sheet without confirming. */
    public data object OnDismissEraseConfirmation : SettingsIntent()

    /** User confirmed "Erase all data" — clears all preferences and local database. */
    public data object OnEraseAllData : SettingsIntent()

    /** User tapped the notification permission row to open notification settings. */
    public data object OnOpenNotificationSettings : SettingsIntent()

    /** Screen resumed — re-checks permission states that may have changed in system settings. */
    public data object RefreshPermissions : SettingsIntent()

    /** User tapped "EXPORT COMMANDS" to serialize the command tree to JSON. */
    public data object OnExportCommands : SettingsIntent()

    /** User picked a file to import — [json] is the raw JSON string content. */
    public data class OnImportCommands(val json: String) : SettingsIntent()

    /** User toggled the MCP server switch — [enabled] reflects the new state. */
    public data class OnToggleMcp(val enabled: Boolean) : SettingsIntent()

    /** User toggled the Claude Mode switch — [enabled] reflects the new state. */
    public data class OnToggleClaudeMode(val enabled: Boolean) : SettingsIntent()
}
