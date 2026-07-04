package presentation.feature.devices.core.utils

import androidx.compose.runtime.Composable

/** Returns a lambda that opens the platform Bluetooth settings screen. No-op on non-Android. */
@Composable
internal expect fun rememberOpenBluetoothSettingsLauncher(): () -> Unit
