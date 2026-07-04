package domain.usecase.impl.di

import domain.usecase.api.source.usecase.ble.AwaitCommandSelectedUseCase
import domain.usecase.api.source.usecase.ble.CheckBlePermissionsUseCase
import domain.usecase.api.source.usecase.ble.ConnectToDeviceUseCase
import domain.usecase.api.source.usecase.ble.ConsumeSkipAutoReconnectUseCase
import domain.usecase.api.source.usecase.ble.DisconnectDeviceUseCase
import domain.usecase.api.source.usecase.ble.ForgetDeviceUseCase
import domain.usecase.api.source.usecase.ble.GetLastDeviceUseCase
import domain.usecase.api.source.usecase.ble.ObserveBatteryLevelUseCase
import domain.usecase.api.source.usecase.ble.ObserveConnectionStateUseCase
import domain.usecase.api.source.usecase.ble.ObserveFirmwareVersionUseCase
import domain.usecase.api.source.usecase.ble.ObserveSelectedCommandUseCase
import domain.usecase.api.source.usecase.ble.RequestBlePermissionsUseCase
import domain.usecase.api.source.usecase.ble.ScanForDevicesUseCase
import domain.usecase.api.source.usecase.ble.SendNotifyUseCase
import domain.usecase.api.source.usecase.ble.SendShowApprovalUseCase
import domain.usecase.api.source.usecase.ble.SendShowChoiceUseCase
import domain.usecase.api.source.usecase.ble.StartOtaUseCase
import domain.usecase.api.source.usecase.ble.SyncCommandsUseCase
import domain.usecase.api.source.usecase.command.DeleteCommandUseCase
import domain.usecase.api.source.usecase.command.ExportCommandsUseCase
import domain.usecase.api.source.usecase.command.GetCommandByIdUseCase
import domain.usecase.api.source.usecase.command.ImportCommandsUseCase
import domain.usecase.api.source.usecase.command.ObserveCommandsUseCase
import domain.usecase.api.source.usecase.command.SaveCommandUseCase
import domain.usecase.api.source.usecase.executor.ExecuteCommandUseCase
import domain.usecase.api.source.usecase.history.ClearCommandHistoryUseCase
import domain.usecase.api.source.usecase.history.ObserveCommandHistoryUseCase
import domain.usecase.api.source.usecase.preset.DeletePresetUseCase
import domain.usecase.api.source.usecase.preset.ExportPresetUseCase
import domain.usecase.api.source.usecase.preset.GetGalleryPresetsUseCase
import domain.usecase.api.source.usecase.preset.ImportPresetUseCase
import domain.usecase.api.source.usecase.preset.LoadPresetUseCase
import domain.usecase.api.source.usecase.preset.ObservePresetsUseCase
import domain.usecase.api.source.usecase.preset.SavePresetUseCase
import domain.usecase.api.source.usecase.configuration.GetApplicationLanguageUseCase
import domain.usecase.api.source.usecase.ble.SendClaudeStateUseCase
import domain.usecase.api.source.usecase.configuration.GetClaudeHookEnabledUseCase
import domain.usecase.api.source.usecase.configuration.GetMcpEnabledUseCase
import domain.usecase.api.source.usecase.configuration.GetOnboardingStatusUseCase
import domain.usecase.api.source.usecase.configuration.GetThemeUseCase
import domain.usecase.api.source.usecase.configuration.ObserveApplicationLanguageUseCase
import domain.usecase.api.source.usecase.configuration.ObserveThemeUseCase
import domain.usecase.api.source.usecase.configuration.SetApplicationLanguageUseCase
import domain.usecase.api.source.usecase.configuration.SetClaudeHookEnabledUseCase
import domain.usecase.api.source.usecase.configuration.SetMcpEnabledUseCase
import domain.usecase.api.source.usecase.configuration.SetOnboardingStatusUseCase
import domain.usecase.api.source.usecase.configuration.SetThemeUseCase
import domain.usecase.impl.source.usecase.ble.AwaitCommandSelectedUseCaseImpl
import domain.usecase.impl.source.usecase.ble.CheckBlePermissionsUseCaseImpl
import domain.usecase.impl.source.usecase.ble.ConnectToDeviceUseCaseImpl
import domain.usecase.impl.source.usecase.ble.ConsumeSkipAutoReconnectUseCaseImpl
import domain.usecase.impl.source.usecase.ble.DisconnectDeviceUseCaseImpl
import domain.usecase.impl.source.usecase.ble.ForgetDeviceUseCaseImpl
import domain.usecase.impl.source.usecase.ble.GetLastDeviceUseCaseImpl
import domain.usecase.impl.source.usecase.ble.ObserveBatteryLevelUseCaseImpl
import domain.usecase.impl.source.usecase.ble.ObserveConnectionStateUseCaseImpl
import domain.usecase.impl.source.usecase.ble.ObserveFirmwareVersionUseCaseImpl
import domain.usecase.impl.source.usecase.ble.ObserveSelectedCommandUseCaseImpl
import domain.usecase.impl.source.usecase.ble.RequestBlePermissionsUseCaseImpl
import domain.usecase.impl.source.usecase.ble.ScanForDevicesUseCaseImpl
import domain.usecase.impl.source.usecase.ble.SendClaudeStateUseCaseImpl
import domain.usecase.impl.source.usecase.ble.SendNotifyUseCaseImpl
import domain.usecase.impl.source.usecase.ble.SendShowApprovalUseCaseImpl
import domain.usecase.impl.source.usecase.ble.SendShowChoiceUseCaseImpl
import domain.usecase.impl.source.usecase.ble.StartOtaUseCaseImpl
import domain.usecase.impl.source.usecase.ble.SyncCommandsUseCaseImpl
import domain.usecase.impl.source.usecase.command.DeleteCommandUseCaseImpl
import domain.usecase.impl.source.usecase.command.ExportCommandsUseCaseImpl
import domain.usecase.impl.source.usecase.command.GetCommandByIdUseCaseImpl
import domain.usecase.impl.source.usecase.command.ImportCommandsUseCaseImpl
import domain.usecase.impl.source.usecase.command.ObserveCommandsUseCaseImpl
import domain.usecase.impl.source.usecase.command.SaveCommandUseCaseImpl
import domain.usecase.impl.source.usecase.executor.ExecuteCommandUseCaseImpl
import domain.usecase.impl.source.usecase.history.ClearCommandHistoryUseCaseImpl
import domain.usecase.impl.source.usecase.history.ObserveCommandHistoryUseCaseImpl
import domain.usecase.impl.source.usecase.preset.DeletePresetUseCaseImpl
import domain.usecase.impl.source.usecase.preset.ExportPresetUseCaseImpl
import domain.usecase.impl.source.usecase.preset.GetGalleryPresetsUseCaseImpl
import domain.usecase.impl.source.usecase.preset.ImportPresetUseCaseImpl
import domain.usecase.impl.source.usecase.preset.LoadPresetUseCaseImpl
import domain.usecase.impl.source.usecase.preset.ObservePresetsUseCaseImpl
import domain.usecase.impl.source.usecase.preset.SavePresetUseCaseImpl
import domain.usecase.impl.source.usecase.configuration.GetApplicationLanguageUseCaseImpl
import domain.usecase.impl.source.usecase.configuration.GetClaudeHookEnabledUseCaseImpl
import domain.usecase.impl.source.usecase.configuration.GetMcpEnabledUseCaseImpl
import domain.usecase.impl.source.usecase.configuration.GetOnboardingStatusUseCaseImpl
import domain.usecase.impl.source.usecase.configuration.GetThemeUseCaseImpl
import domain.usecase.impl.source.usecase.configuration.ObserveApplicationLanguageUseCaseImpl
import domain.usecase.impl.source.usecase.configuration.ObserveThemeUseCaseImpl
import domain.usecase.impl.source.usecase.configuration.SetApplicationLanguageUseCaseImpl
import domain.usecase.impl.source.usecase.configuration.SetClaudeHookEnabledUseCaseImpl
import domain.usecase.impl.source.usecase.configuration.SetMcpEnabledUseCaseImpl
import domain.usecase.impl.source.usecase.configuration.SetOnboardingStatusUseCaseImpl
import domain.usecase.impl.source.usecase.configuration.SetThemeUseCaseImpl
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Koin module that provides domain-usecase-impl bindings.
 *
 * Registers all use case implementations as singletons
 * bound to their corresponding API interfaces.
 */
public val domainUseCaseImplModule: Module = module {
    // Configuration
    singleOf(::GetThemeUseCaseImpl) bind GetThemeUseCase::class
    singleOf(::SetThemeUseCaseImpl) bind SetThemeUseCase::class
    singleOf(::ObserveThemeUseCaseImpl) bind ObserveThemeUseCase::class
    singleOf(::GetOnboardingStatusUseCaseImpl) bind GetOnboardingStatusUseCase::class
    singleOf(::SetOnboardingStatusUseCaseImpl) bind SetOnboardingStatusUseCase::class
    singleOf(::SetApplicationLanguageUseCaseImpl) bind SetApplicationLanguageUseCase::class
    singleOf(::GetApplicationLanguageUseCaseImpl) bind GetApplicationLanguageUseCase::class
    singleOf(::ObserveApplicationLanguageUseCaseImpl) bind ObserveApplicationLanguageUseCase::class
    singleOf(::GetMcpEnabledUseCaseImpl) bind GetMcpEnabledUseCase::class
    singleOf(::SetMcpEnabledUseCaseImpl) bind SetMcpEnabledUseCase::class
    singleOf(::GetClaudeHookEnabledUseCaseImpl) bind GetClaudeHookEnabledUseCase::class
    singleOf(::SetClaudeHookEnabledUseCaseImpl) bind SetClaudeHookEnabledUseCase::class

    // BLE
    singleOf(::GetLastDeviceUseCaseImpl) bind GetLastDeviceUseCase::class
    singleOf(::ForgetDeviceUseCaseImpl) bind ForgetDeviceUseCase::class
    singleOf(::ScanForDevicesUseCaseImpl) bind ScanForDevicesUseCase::class
    singleOf(::ConnectToDeviceUseCaseImpl) bind ConnectToDeviceUseCase::class
    singleOf(::DisconnectDeviceUseCaseImpl) bind DisconnectDeviceUseCase::class
    singleOf(::ObserveConnectionStateUseCaseImpl) bind ObserveConnectionStateUseCase::class
    singleOf(::ObserveFirmwareVersionUseCaseImpl) bind ObserveFirmwareVersionUseCase::class
    singleOf(::ObserveBatteryLevelUseCaseImpl) bind ObserveBatteryLevelUseCase::class
    singleOf(::ConsumeSkipAutoReconnectUseCaseImpl) bind ConsumeSkipAutoReconnectUseCase::class
    singleOf(::SyncCommandsUseCaseImpl) bind SyncCommandsUseCase::class
    singleOf(::ObserveSelectedCommandUseCaseImpl) bind ObserveSelectedCommandUseCase::class
    singleOf(::CheckBlePermissionsUseCaseImpl) bind CheckBlePermissionsUseCase::class
    singleOf(::RequestBlePermissionsUseCaseImpl) bind RequestBlePermissionsUseCase::class
    singleOf(::StartOtaUseCaseImpl) bind StartOtaUseCase::class
    singleOf(::SendShowApprovalUseCaseImpl) bind SendShowApprovalUseCase::class
    singleOf(::SendShowChoiceUseCaseImpl) bind SendShowChoiceUseCase::class
    singleOf(::SendNotifyUseCaseImpl) bind SendNotifyUseCase::class
    singleOf(::SendClaudeStateUseCaseImpl) bind SendClaudeStateUseCase::class
    singleOf(::AwaitCommandSelectedUseCaseImpl) bind AwaitCommandSelectedUseCase::class

    // Command
    singleOf(::ObserveCommandsUseCaseImpl) bind ObserveCommandsUseCase::class
    singleOf(::GetCommandByIdUseCaseImpl) bind GetCommandByIdUseCase::class
    singleOf(::SaveCommandUseCaseImpl) bind SaveCommandUseCase::class
    singleOf(::DeleteCommandUseCaseImpl) bind DeleteCommandUseCase::class
    singleOf(::ExportCommandsUseCaseImpl) bind ExportCommandsUseCase::class
    singleOf(::ImportCommandsUseCaseImpl) bind ImportCommandsUseCase::class

    // Executor
    singleOf(::ExecuteCommandUseCaseImpl) bind ExecuteCommandUseCase::class

    // History
    singleOf(::ObserveCommandHistoryUseCaseImpl) bind ObserveCommandHistoryUseCase::class
    singleOf(::ClearCommandHistoryUseCaseImpl) bind ClearCommandHistoryUseCase::class

    // Preset
    singleOf(::ObservePresetsUseCaseImpl) bind ObservePresetsUseCase::class
    singleOf(::SavePresetUseCaseImpl) bind SavePresetUseCase::class
    singleOf(::LoadPresetUseCaseImpl) bind LoadPresetUseCase::class
    singleOf(::DeletePresetUseCaseImpl) bind DeletePresetUseCase::class
    singleOf(::ExportPresetUseCaseImpl) bind ExportPresetUseCase::class
    singleOf(::ImportPresetUseCaseImpl) bind ImportPresetUseCase::class
    singleOf(::GetGalleryPresetsUseCaseImpl) bind GetGalleryPresetsUseCase::class
}
