package data.ble.impl.di

import data.ble.api.source.datasource.BleConnection
import data.ble.api.source.datasource.BlePermissionChecker
import data.ble.api.source.datasource.BleScanner
import data.ble.api.source.datasource.BleServiceController
import data.ble.impl.source.datasource.BleConnectionImpl
import data.ble.impl.source.datasource.BleScannerImpl
import data.ble.impl.core.permission.BlePermissionCheckerImpl
import data.ble.impl.source.service.BleServiceControllerImpl
import org.koin.core.module.Module

internal actual fun Module.provideBleAdapter() {
    single<BleScanner> { BleScannerImpl() }
    single<BleConnection> { BleConnectionImpl() }
    single<BlePermissionChecker> { BlePermissionCheckerImpl() }
    single<BleServiceController> { BleServiceControllerImpl() }
}
