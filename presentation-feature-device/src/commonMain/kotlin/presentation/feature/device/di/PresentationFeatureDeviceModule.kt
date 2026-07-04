package presentation.feature.device.di

import domain.usecase.api.source.usecase.ble.DisconnectDeviceUseCase
import domain.usecase.api.source.usecase.ble.ForgetDeviceUseCase
import domain.usecase.api.source.usecase.ble.ObserveBatteryLevelUseCase
import domain.usecase.api.source.usecase.ble.ObserveConnectionStateUseCase
import domain.usecase.api.source.usecase.ble.ObserveFirmwareVersionUseCase
import domain.usecase.api.source.usecase.ble.ObserveSelectedCommandUseCase
import domain.usecase.api.source.usecase.ble.StartOtaUseCase
import domain.usecase.api.source.usecase.ble.SyncCommandsUseCase
import domain.usecase.api.source.usecase.command.ObserveCommandsUseCase
import domain.usecase.api.source.usecase.history.ClearCommandHistoryUseCase
import domain.usecase.api.source.usecase.history.ObserveCommandHistoryUseCase
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import presentation.feature.device.source.device.DeviceDetailViewModel

/**
 * Koin module for the device detail feature.
 *
 * Registers [DeviceDetailViewModel] with BLE observation and history use cases.
 */
public val presentationFeatureDeviceModule: Module = module {
    viewModel {
        DeviceDetailViewModel(
            observeConnectionState = get<ObserveConnectionStateUseCase>(),
            observeSelectedCommand = get<ObserveSelectedCommandUseCase>(),
            disconnect = get<DisconnectDeviceUseCase>(),
            forgetDevice = get<ForgetDeviceUseCase>(),
            observeCommands = get<ObserveCommandsUseCase>(),
            syncCommands = get<SyncCommandsUseCase>(),
            observeCommandHistory = get<ObserveCommandHistoryUseCase>(),
            clearCommandHistory = get<ClearCommandHistoryUseCase>(),
            observeFirmwareVersion = get<ObserveFirmwareVersionUseCase>(),
            observeBatteryLevel = get<ObserveBatteryLevelUseCase>(),
            startOta = get<StartOtaUseCase>(),
        )
    }
}
