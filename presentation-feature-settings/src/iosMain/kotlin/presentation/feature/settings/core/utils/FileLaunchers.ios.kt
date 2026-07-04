package presentation.feature.settings.core.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.dialogs.compose.rememberFileSaverLauncher
import io.github.vinceglb.filekit.readString
import kotlinx.coroutines.launch

/**
 * iOS actual for [rememberImportJsonLauncher].
 *
 * Uses [rememberFilePickerLauncher] from FileKit to open the system file picker
 * filtered to `.json` files. The file contents are read asynchronously and
 * forwarded to [onJsonLoaded] on the calling coroutine scope.
 */
@Composable
internal actual fun rememberImportJsonLauncher(onJsonLoaded: (String) -> Unit): () -> Unit {
    val scope = rememberCoroutineScope()
    val launcher = rememberFilePickerLauncher(
        type = FileKitType.File(extensions = listOf("json")),
    ) { file ->
        scope.launch {
            val json = file?.readString() ?: return@launch
            onJsonLoaded(json)
        }
    }
    return { launcher.launch() }
}

/**
 * iOS actual for [rememberExportJsonLauncher].
 *
 * Uses [rememberFileSaverLauncher] from FileKit to open the system file saver,
 * writing the provided JSON string as `prompt-knob-commands.json`.
 */
@Composable
internal actual fun rememberExportJsonLauncher(): (String) -> Unit {
    val launcher = rememberFileSaverLauncher { }
    return { json ->
        launcher.launch(
            bytes = json.encodeToByteArray(),
            baseName = "prompt-knob-commands",
            extension = "json",
        )
    }
}
