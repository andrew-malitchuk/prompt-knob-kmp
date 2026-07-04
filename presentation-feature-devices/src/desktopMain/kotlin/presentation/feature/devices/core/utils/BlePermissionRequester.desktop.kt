package presentation.feature.devices.core.utils

import androidx.compose.runtime.Composable

/**
 * Desktop JVM actual: BLE is not available on Desktop; returns a no-op lambda.
 */
@Composable
internal actual fun rememberBlePermissionRequester(
    onResult: (Boolean) -> Unit,
): () -> Unit = {}
