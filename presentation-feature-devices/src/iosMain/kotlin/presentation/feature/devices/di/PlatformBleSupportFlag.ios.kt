package presentation.feature.devices.di

import org.koin.core.module.Module

// iOS uses CoreBluetooth via Kable; BLE is always available on device.
internal actual fun Module.providePlatformBleSupportFlag() {
    single(bleSupportedQualifier) { true }
}
