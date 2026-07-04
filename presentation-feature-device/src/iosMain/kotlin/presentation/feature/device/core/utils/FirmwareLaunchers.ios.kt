package presentation.feature.device.core.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.readBytes
import kotlinx.coroutines.launch

@Composable
internal actual fun rememberFirmwarePickerLauncher(onFileLoaded: (ByteArray) -> Unit): () -> Unit {
    val scope = rememberCoroutineScope()
    val launcher = rememberFilePickerLauncher(
        type = FileKitType.File(extensions = listOf("bin")),
    ) { file ->
        scope.launch {
            val bytes = file?.readBytes() ?: return@launch
            onFileLoaded(bytes)
        }
    }
    return { launcher.launch() }
}
