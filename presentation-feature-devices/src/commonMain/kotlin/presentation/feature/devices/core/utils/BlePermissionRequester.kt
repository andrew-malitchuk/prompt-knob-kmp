package presentation.feature.devices.core.utils

import androidx.compose.runtime.Composable

/**
 * Returns a lambda that, when invoked, triggers the platform-native BLE permission request.
 * [onResult] is called with `true` if all required permissions were granted.
 *
 * On Android this launches [ActivityResultContracts.RequestMultiplePermissions].
 * On iOS / macOS the data layer handles dialog presentation via [CBCentralManager];
 * the returned lambda is a no-op on those platforms.
 */
@Composable
internal expect fun rememberBlePermissionRequester(
    onResult: (granted: Boolean) -> Unit,
): () -> Unit
