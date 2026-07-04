package presentation.feature.settings.core.utils

import androidx.compose.runtime.Composable

/**
 * Platform-agnostic file import launcher.
 *
 * Returns a `() -> Unit` that, when invoked, opens the platform file picker for `.json` files.
 * [onJsonLoaded] is called with the raw JSON string once the user selects a file.
 * On macOS (where FileKit has no macOS variant) this is a no-op.
 */
@Composable
internal expect fun rememberImportJsonLauncher(onJsonLoaded: (String) -> Unit): () -> Unit

/**
 * Platform-agnostic file export launcher.
 *
 * Returns a `(String) -> Unit` that, when invoked with a JSON string, opens the platform
 * file saver so the user can persist the data as `prompt-knob-commands.json`.
 * On macOS (where FileKit has no macOS variant) this is a no-op.
 */
@Composable
internal expect fun rememberExportJsonLauncher(): (String) -> Unit
