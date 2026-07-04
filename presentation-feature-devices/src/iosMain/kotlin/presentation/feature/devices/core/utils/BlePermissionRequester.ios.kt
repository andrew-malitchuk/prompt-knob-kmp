package presentation.feature.devices.core.utils

import androidx.compose.runtime.Composable

/**
 * iOS actual: BLE permission is handled by CoreBluetooth when the data layer initialises
 * [CBCentralManager]; returns a no-op lambda.
 */
@Composable
internal actual fun rememberBlePermissionRequester(
    onResult: (Boolean) -> Unit,
): () -> Unit = {}
