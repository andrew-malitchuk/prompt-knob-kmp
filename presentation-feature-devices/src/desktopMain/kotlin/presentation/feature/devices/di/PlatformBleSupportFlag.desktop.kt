package presentation.feature.devices.di

import org.koin.core.module.Module

// Desktop JVM has no Bluetooth stack in the KMP BLE layer; always disabled.
internal actual fun Module.providePlatformBleSupportFlag() {
    single(bleSupportedQualifier) { false }
}
