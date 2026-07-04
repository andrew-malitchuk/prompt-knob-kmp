package data.ble.impl.di

import org.koin.core.module.Module

/**
 * Registers platform-specific BLE components in the Koin module.
 *
 * - **Android:** Kable-backed [BleScanner] and [BleConnection], Android [BlePermissionChecker].
 * - **iOS:** Kable-backed [BleScanner] and [BleConnection], CoreBluetooth [BlePermissionChecker].
 * - **macOS:** Kable-backed [BleScanner] and [BleConnection], CoreBluetooth [BlePermissionChecker].
 * - **Desktop:** No-op stubs — BLE is not available on the JVM desktop target.
 */
internal expect fun Module.provideBleAdapter()
