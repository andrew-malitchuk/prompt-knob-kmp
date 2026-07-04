package presentation.feature.devices.di

import org.koin.core.module.Module

// macOS uses CoreBluetooth via Kable; BLE is always available on supported Mac hardware.
internal actual fun Module.providePlatformBleSupportFlag() {
    single(bleSupportedQualifier) { true }
}
