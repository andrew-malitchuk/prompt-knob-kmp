package presentation.feature.settings.source.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.core.source.model.ConnectionStateModel
import domain.usecase.api.source.usecase.ble.CheckBlePermissionsUseCase
import domain.usecase.api.source.usecase.ble.ForgetDeviceUseCase
import domain.usecase.api.source.usecase.ble.ObserveConnectionStateUseCase
import domain.usecase.api.source.usecase.ble.RequestBlePermissionsUseCase
import domain.usecase.api.source.usecase.command.ExportCommandsUseCase
import domain.usecase.api.source.usecase.command.ImportCommandsUseCase
import domain.usecase.api.source.usecase.configuration.GetClaudeHookEnabledUseCase
import domain.usecase.api.source.usecase.configuration.GetMcpEnabledUseCase
import domain.usecase.api.source.usecase.configuration.SetClaudeHookEnabledUseCase
import domain.usecase.api.source.usecase.configuration.SetMcpEnabledUseCase
import domain.usecase.api.source.usecase.configuration.SetOnboardingStatusUseCase
import domain.core.source.model.ThemeModel
import data.executor.api.source.system.SystemSettingsOpener
import domain.usecase.api.source.usecase.configuration.GetThemeUseCase
import domain.usecase.api.source.usecase.configuration.ObserveApplicationLanguageUseCase
import domain.usecase.api.source.usecase.configuration.SetThemeUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.annotation.OrbitExperimental
import org.orbitmvi.orbit.viewmodel.container
import presentation.feature.settings.core.claude.startClaudeHookServer
import presentation.feature.settings.core.claude.stopClaudeHookServer
import presentation.feature.settings.core.mcp.startMcpServer
import presentation.feature.settings.core.mcp.stopMcpServer

/**
 * ViewModel for the Settings screen.
 *
 * Follows the MVI pattern via Orbit: UI dispatches [SettingsIntent] actions
 * through [handleIntent], the ViewModel reduces [SettingsState], and emits
 * [SettingsSideEffect] for one-shot events such as back navigation.
 *
 * Language preference is observed reactively via [ObserveApplicationLanguageUseCase],
 * so the selected language updates automatically when the user returns from the
 * [LanguagePicker][presentation.feature.settings.source.language.LanguagePickerScreen] screen.
 *
 * @property observeApplicationLanguage Observes live language code changes.
 * @property getThemeUseCase Retrieves the persisted theme preference.
 * @property setThemeUseCase Persists a new theme preference.
 * @property observeConnectionState Observes live BLE connection state changes.
 * @property getMcpEnabled Retrieves the persisted MCP server enabled state.
 * @property setMcpEnabled Persists a new MCP server enabled state.
 * @property isMcpAvailable Whether the MCP section should be shown on this platform.
 *
 * @see SettingsContract
 * @see SettingsScreen
 */
@OrbitExperimental
public class SettingsViewModel(
    private val observeApplicationLanguage: ObserveApplicationLanguageUseCase,
    private val getThemeUseCase: GetThemeUseCase,
    private val setThemeUseCase: SetThemeUseCase,
    private val checkBlePermissions: CheckBlePermissionsUseCase,
    private val requestBlePermissions: RequestBlePermissionsUseCase,
    private val observeConnectionState: ObserveConnectionStateUseCase,
    private val forgetDevice: ForgetDeviceUseCase,
    private val setOnboardingStatus: SetOnboardingStatusUseCase,
    private val systemSettingsOpener: SystemSettingsOpener,
    private val isBleSupported: Boolean,
    private val exportCommands: ExportCommandsUseCase,
    private val importCommands: ImportCommandsUseCase,
    private val getMcpEnabled: GetMcpEnabledUseCase,
    private val setMcpEnabled: SetMcpEnabledUseCase,
    private val isMcpAvailable: Boolean,
    private val getClaudeHookEnabled: GetClaudeHookEnabledUseCase,
    private val setClaudeHookEnabled: SetClaudeHookEnabledUseCase,
    private val isClaudeAvailable: Boolean,
) : ContainerHost<SettingsState, SettingsSideEffect>, ViewModel() {

    override val container: Container<SettingsState, SettingsSideEffect> =
        container<SettingsState, SettingsSideEffect>(SettingsState(isBleSupported = isBleSupported)) {
            loadCurrentTheme()
            if (isBleSupported) loadBlePermissionState()
            loadAccessibilityState()
            loadNotificationPermissionState()
            startObservingLanguage()
            startObservingConnectionState()
            loadMcpState()
            loadClaudeModeState()
        }

    /** Dispatches a [SettingsIntent] to the appropriate handler. */
    public fun handleIntent(intent: SettingsIntent) {
        when (intent) {
            SettingsIntent.OnBackClick -> onBackClick()
            SettingsIntent.OnLanguagePickerClick -> onLanguagePickerClick()
            is SettingsIntent.SelectTheme -> selectTheme(intent.index)
            SettingsIntent.OnRequestBlePermission -> onRequestBlePermission()
            SettingsIntent.OnOpenAccessibilitySettings -> onOpenAccessibilitySettings()
            SettingsIntent.OnDeviceClick -> onDeviceClick()
            SettingsIntent.OnSystemInfoClick -> onSystemInfoClick()
            SettingsIntent.OnEraseAllDataClick -> onEraseAllDataClick()
            SettingsIntent.OnDismissEraseConfirmation -> onDismissEraseConfirmation()
            SettingsIntent.OnEraseAllData -> onEraseAllData()
            SettingsIntent.OnOpenNotificationSettings -> onOpenNotificationSettings()
            SettingsIntent.RefreshPermissions -> onRefreshPermissions()
            SettingsIntent.OnExportCommands -> onExportCommands()
            is SettingsIntent.OnImportCommands -> onImportCommands(intent.json)
            is SettingsIntent.OnToggleMcp -> onToggleMcp(intent.enabled)
            is SettingsIntent.OnToggleClaudeMode -> onToggleClaudeMode(intent.enabled)
        }
    }


    /**
     * Observes the language preference flow so the selected-language indicator
     * in the Settings row updates automatically when returning from [LanguagePickerScreen].
     */
    private fun startObservingLanguage() {
        viewModelScope.launch {
            observeApplicationLanguage()
                .onEach { result ->
                    val code = result.getOrNull() ?: "en"
                    val codes = container.stateFlow.value.languageCodes
                    val index = codes.indexOf(code).coerceAtLeast(0)
                    intent { reduce { state.copy(selectedLanguageIndex = index) } }
                }
                .catch { /* preference store unavailable — keep default */ }
                .collect {}
        }
    }

    /** Loads the persisted theme preference and resolves its index in [THEME_MODELS]. */
    private fun loadCurrentTheme() = intent {
        val theme = getThemeUseCase().getOrNull() ?: ThemeModel.Dark
        val index = THEME_MODELS.indexOf(theme).coerceAtLeast(0)
        reduce { state.copy(selectedThemeIndex = index) }
    }

    private fun loadBlePermissionState() = intent {
        val granted = checkBlePermissions().getOrDefault(false)
        reduce { state.copy(isBlePermissionGranted = granted) }
    }

    private fun loadAccessibilityState() = intent {
        val granted = systemSettingsOpener.isAccessibilityServiceEnabled()
        reduce { state.copy(isAccessibilityGranted = granted) }
    }

    private fun loadNotificationPermissionState() = intent {
        val granted = systemSettingsOpener.isNotificationPermissionGranted()
        reduce { state.copy(isNotificationPermissionGranted = granted) }
    }

    /**
     * Observes BLE connection state changes and updates [SettingsState.connectionStateName]
     * and [SettingsState.deviceName]. Errors are swallowed gracefully to handle
     * Desktop/macOS platforms where BLE is a no-op.
     */
    private fun startObservingConnectionState() {
        viewModelScope.launch {
            observeConnectionState()
                .onEach { model ->
                    val statusName = when (model.state) {
                        ConnectionStateModel.Connected -> "CONNECTED VIA BLE"
                        ConnectionStateModel.Connecting -> "CONNECTING"
                        ConnectionStateModel.Disconnecting -> "DISCONNECTING"
                        ConnectionStateModel.Disconnected -> "DISCONNECTED"
                    }
                    intent {
                        reduce {
                            state.copy(
                                connectionStateName = statusName,
                                deviceName = model.device?.name,
                            )
                        }
                    }
                }
                .catch { /* BLE not available on this platform — keep default state */ }
                .collect {}
        }
    }


    /** Navigates to the Language Picker screen. */
    private fun onLanguagePickerClick() = intent {
        postSideEffect(SettingsSideEffect.NavigateToLanguagePicker)
    }

    /** Updates the selected theme in state and persists the new preference. */
    private fun selectTheme(index: Int) = intent {
        reduce { state.copy(selectedThemeIndex = index) }
        setThemeUseCase(THEME_MODELS[index])
    }

    private fun onRequestBlePermission() = intent {
        val granted = requestBlePermissions().getOrDefault(false)
        reduce { state.copy(isBlePermissionGranted = granted) }
    }

    private fun onOpenAccessibilitySettings() = intent {
        systemSettingsOpener.openAccessibilitySettings()
        // State refreshes on screen resume via LifecycleEventEffect(ON_RESUME) in SettingsScreen.
    }

    private fun onOpenNotificationSettings() = intent {
        systemSettingsOpener.openNotificationSettings()
        // State refreshes on screen resume via LifecycleEventEffect(ON_RESUME) in SettingsScreen.
    }

    private fun onRefreshPermissions() {
        if (isBleSupported) loadBlePermissionState()
        loadAccessibilityState()
        loadNotificationPermissionState()
    }

    /** Navigates to the Device screen. */
    private fun onDeviceClick() = intent {
        postSideEffect(SettingsSideEffect.NavigateToDevice)
    }

    /** Navigates to the About / System Info screen. */
    private fun onSystemInfoClick() = intent {
        postSideEffect(SettingsSideEffect.NavigateToAbout)
    }

    /** Emits [SettingsSideEffect.GoBackEffect] to trigger back navigation. */
    private fun onBackClick() = intent {
        postSideEffect(SettingsSideEffect.GoBackEffect)
    }

    private fun onEraseAllDataClick() = intent {
        reduce { state.copy(showEraseConfirmation = true) }
    }

    private fun onDismissEraseConfirmation() = intent {
        reduce { state.copy(showEraseConfirmation = false) }
    }

    /** Erases all local data and navigates to Splash so onboarding re-runs. */
    private fun onEraseAllData() = intent {
        reduce { state.copy(showEraseConfirmation = false) }
        forgetDevice()
        setOnboardingStatus(false)
        postSideEffect(SettingsSideEffect.NavigateToSplash)
    }

    private fun onExportCommands() = intent {
        exportCommands()
            .onSuccess { json ->
                postSideEffect(SettingsSideEffect.ExportReady(json))
            }
            .onFailure { t ->
                postSideEffect(SettingsSideEffect.ShowErrorMessage(t.message ?: "Export failed"))
            }
    }

    private fun onImportCommands(json: String) = intent {
        importCommands(json)
            .onSuccess { count ->
                postSideEffect(SettingsSideEffect.ShowImportSuccess(count))
            }
            .onFailure { t ->
                postSideEffect(SettingsSideEffect.ShowErrorMessage(t.message ?: "Import failed"))
            }
    }

    /** Loads the persisted MCP enabled state and makes the section visible on supported platforms. */
    private fun loadMcpState() = intent {
        val enabled = getMcpEnabled().getOrDefault(true)
        reduce {
            state.copy(
                isMcpSectionVisible = isMcpAvailable,
                isMcpEnabled = enabled,
            )
        }
    }

    /** Persists the new MCP enabled [enabled] state and starts/stops the server immediately. */
    private fun onToggleMcp(enabled: Boolean) = intent {
        reduce { state.copy(isMcpEnabled = enabled) }
        setMcpEnabled(enabled)
        viewModelScope.launch(Dispatchers.Default) {
            if (enabled) startMcpServer() else stopMcpServer()
        }
    }

    private fun loadClaudeModeState() = intent {
        val enabled = getClaudeHookEnabled().getOrDefault(false)
        reduce {
            state.copy(
                isClaudeSectionVisible = isClaudeAvailable,
                isClaudeModeEnabled = enabled,
            )
        }
    }

    private fun onToggleClaudeMode(enabled: Boolean) = intent {
        reduce { state.copy(isClaudeModeEnabled = enabled) }
        setClaudeHookEnabled(enabled)
        viewModelScope.launch(Dispatchers.Default) {
            if (enabled) startClaudeHookServer() else stopClaudeHookServer()
        }
    }

    private companion object {
        /**
         * Ordered list of theme models matching [SettingsState.themeOptions] by index.
         * Index 0 = Dark (Monolith), Index 1 = Light (Brutalist).
         */
        val THEME_MODELS = listOf(ThemeModel.Dark, ThemeModel.Light)
    }
}
