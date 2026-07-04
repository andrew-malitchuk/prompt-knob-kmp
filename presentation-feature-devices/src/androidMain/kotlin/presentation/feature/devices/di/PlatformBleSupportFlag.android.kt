package presentation.feature.devices.di

import org.koin.core.module.Module

// Android supports BLE from API 18; minSdk is 27 — always true.
internal actual fun Module.providePlatformBleSupportFlag() {
    single(bleSupportedQualifier) { true }
}
