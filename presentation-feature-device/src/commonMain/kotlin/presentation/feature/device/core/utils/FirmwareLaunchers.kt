package presentation.feature.device.core.utils

import androidx.compose.runtime.Composable

/**
 * Platform-agnostic firmware file picker.
 *
 * Returns a `() -> Unit` that, when invoked, opens the platform file picker filtered
 * to binary files. [onFileLoaded] is called with the raw firmware bytes once the user
 * selects a `.bin` file.
 *
 * On macOS (where FileKit has no variant) this is a deliberate no-op.
 * On Desktop JVM this is also a no-op — BLE is not available on Desktop.
 */
@Composable
internal expect fun rememberFirmwarePickerLauncher(onFileLoaded: (ByteArray) -> Unit): () -> Unit
