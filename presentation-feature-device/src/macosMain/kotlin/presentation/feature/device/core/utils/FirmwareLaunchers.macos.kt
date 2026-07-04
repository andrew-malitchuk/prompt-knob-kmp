package presentation.feature.device.core.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.launch
import platform.AppKit.NSOpenPanel
import platform.Foundation.NSFileManager
import platform.posix.memcpy

@OptIn(ExperimentalForeignApi::class)
@Composable
internal actual fun rememberFirmwarePickerLauncher(onFileLoaded: (ByteArray) -> Unit): () -> Unit {
    val scope = rememberCoroutineScope()
    val callback by rememberUpdatedState(onFileLoaded)
    return remember(scope) {
        {
            scope.launch {
                val panel = NSOpenPanel()
                panel.canChooseFiles = true
                panel.canChooseDirectories = false
                panel.allowsMultipleSelection = false
                if (panel.runModal() == 1L) { // NSModalResponseOK
                    val path = panel.URL?.path ?: return@launch
                    val data = NSFileManager.defaultManager.contentsAtPath(path) ?: return@launch
                    val length = data.length.toInt()
                    val bytes = ByteArray(length)
                    if (length > 0) {
                        bytes.usePinned { pinned ->
                            memcpy(pinned.addressOf(0), data.bytes, data.length)
                        }
                    }
                    callback(bytes)
                }
            }
        }
    }
}
