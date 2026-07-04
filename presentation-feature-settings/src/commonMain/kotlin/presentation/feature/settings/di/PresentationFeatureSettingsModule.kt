package presentation.feature.settings.di

import data.executor.api.source.system.SystemSettingsOpener
import domain.usecase.api.source.usecase.ble.CheckBlePermissionsUseCase
import domain.usecase.api.source.usecase.ble.ForgetDeviceUseCase
import domain.usecase.api.source.usecase.ble.ObserveConnectionStateUseCase
import domain.usecase.api.source.usecase.ble.RequestBlePermissionsUseCase
import domain.usecase.api.source.usecase.command.ExportCommandsUseCase
import domain.usecase.api.source.usecase.command.ImportCommandsUseCase
import domain.usecase.api.source.usecase.configuration.GetClaudeHookEnabledUseCase
import domain.usecase.api.source.usecase.configuration.GetMcpEnabledUseCase
import domain.usecase.api.source.usecase.configuration.GetThemeUseCase
import domain.usecase.api.source.usecase.configuration.ObserveApplicationLanguageUseCase
import domain.usecase.api.source.usecase.configuration.SetClaudeHookEnabledUseCase
import domain.usecase.api.source.usecase.configuration.SetMcpEnabledUseCase
import domain.usecase.api.source.usecase.configuration.SetOnboardingStatusUseCase
import domain.usecase.api.source.usecase.configuration.SetThemeUseCase
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module
import presentation.feature.settings.source.language.LanguagePickerViewModel
import presentation.feature.settings.source.settings.SettingsViewModel

/**
 * Koin module for the Settings feature.
 *
 * Registers [SettingsViewModel] and [LanguagePickerViewModel] so they can be
 * injected via `koinViewModel()` in their respective screen composables.
 *
 * [SettingsViewModel] receives the BLE platform support flag via the shared
 * `"isBleSupported"` qualifier registered by the devices feature module, and the MCP
 * availability flag via [mcpAvailableQualifier] registered in this module.
 */
public val presentationFeatureSettingsModule: Module = module {
    providePlatformMcpAvailableFlag()
    providePlatformClaudeAvailableFlag()
    viewModel {
        SettingsViewModel(
            observeApplicationLanguage = get<ObserveApplicationLanguageUseCase>(),
            getThemeUseCase = get<GetThemeUseCase>(),
            setThemeUseCase = get<SetThemeUseCase>(),
            checkBlePermissions = get<CheckBlePermissionsUseCase>(),
            requestBlePermissions = get<RequestBlePermissionsUseCase>(),
            observeConnectionState = get<ObserveConnectionStateUseCase>(),
            forgetDevice = get<ForgetDeviceUseCase>(),
            setOnboardingStatus = get<SetOnboardingStatusUseCase>(),
            systemSettingsOpener = get<SystemSettingsOpener>(),
            isBleSupported = get(named("isBleSupported")),
            exportCommands = get<ExportCommandsUseCase>(),
            importCommands = get<ImportCommandsUseCase>(),
            getMcpEnabled = get<GetMcpEnabledUseCase>(),
            setMcpEnabled = get<SetMcpEnabledUseCase>(),
            isMcpAvailable = get(mcpAvailableQualifier),
            getClaudeHookEnabled = get<GetClaudeHookEnabledUseCase>(),
            setClaudeHookEnabled = get<SetClaudeHookEnabledUseCase>(),
            isClaudeAvailable = get(claudeAvailableQualifier),
        )
    }
    viewModelOf(::LanguagePickerViewModel)
}
