package presentation.feature.device.core.utils

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.readBytes
import kotlinx.coroutines.launch

@Composable
internal actual fun rememberFirmwarePickerLauncher(onFileLoaded: (ByteArray) -> Unit): () -> Unit {
    val scope = rememberCoroutineScope()
    // rememberUpdatedState ensures the callback references the latest lambda even if the
    // composable recomposes between picker launch and file selection (e.g. BLE state update).
    val currentOnFileLoaded by rememberUpdatedState(onFileLoaded)
    val launcher = rememberFilePickerLauncher(
        type = FileKitType.File(extensions = listOf("bin")),
    ) { file ->
        if (file == null) {
            Log.d("FirmwareLauncher", "picker cancelled — no file selected")
            return@rememberFilePickerLauncher
        }
        scope.launch {
            val bytes = runCatching { file.readBytes() }.getOrElse { e ->
                Log.e("FirmwareLauncher", "readBytes failed: ${e.message}", e)
                return@launch
            }
            Log.d("FirmwareLauncher", "loaded ${bytes.size}B")
            currentOnFileLoaded(bytes)
        }
    }
    return { launcher.launch() }
}
