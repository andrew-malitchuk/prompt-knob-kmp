package presentation.feature.device.core.utils

import androidx.compose.runtime.Composable

/** Desktop stub: BLE OTA is not available on the JVM Desktop target. */
@Composable
internal actual fun rememberFirmwarePickerLauncher(onFileLoaded: (ByteArray) -> Unit): () -> Unit = {}
