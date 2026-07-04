package presentation.feature.devices.di

import domain.usecase.api.source.usecase.ble.CheckBlePermissionsUseCase
import domain.usecase.api.source.usecase.ble.ConnectToDeviceUseCase
import domain.usecase.api.source.usecase.ble.ConsumeSkipAutoReconnectUseCase
import domain.usecase.api.source.usecase.ble.DisconnectDeviceUseCase
import domain.usecase.api.source.usecase.ble.GetLastDeviceUseCase
import domain.usecase.api.source.usecase.ble.ObserveConnectionStateUseCase
import domain.usecase.api.source.usecase.ble.ObserveSelectedCommandUseCase
import domain.usecase.api.source.usecase.ble.RequestBlePermissionsUseCase
import domain.usecase.api.source.usecase.ble.ScanForDevicesUseCase
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.named
import org.koin.dsl.module
import presentation.feature.devices.source.devices.DevicesViewModel

/** Named qualifier for the BLE platform support flag. */
public val bleSupportedQualifier: Qualifier = named("isBleSupported")

/**
 * Koin module for the devices feature.
 *
 * Registers [DevicesViewModel] with the platform-specific BLE support flag
 * resolved via [bleSupportedQualifier].
 */
public val presentationFeatureDevicesModule: Module = module {
    providePlatformBleSupportFlag()
    viewModel {
        DevicesViewModel(
            checkBlePermissions = get<CheckBlePermissionsUseCase>(),
            requestBlePermissions = get<RequestBlePermissionsUseCase>(),
            scanForDevices = get<ScanForDevicesUseCase>(),
            connectToDevice = get<ConnectToDeviceUseCase>(),
            disconnect = get<DisconnectDeviceUseCase>(),
            observeConnectionState = get<ObserveConnectionStateUseCase>(),
            observeSelectedCommand = get<ObserveSelectedCommandUseCase>(),
            getLastDevice = get<GetLastDeviceUseCase>(),
            consumeSkipAutoReconnect = get<ConsumeSkipAutoReconnectUseCase>(),
            isBleSupported = get(bleSupportedQualifier),
        )
    }
}

internal expect fun Module.providePlatformBleSupportFlag()
