package presentation.feature.settings.core.utils

import androidx.compose.runtime.Composable

/**
 * macOS actual for [rememberImportJsonLauncher].
 *
 * FileKit has no macOS variant, so this is a deliberate no-op.
 * Import functionality is not available on macOS.
 */
@Composable
internal actual fun rememberImportJsonLauncher(onJsonLoaded: (String) -> Unit): () -> Unit = {}

/**
 * macOS actual for [rememberExportJsonLauncher].
 *
 * FileKit has no macOS variant, so this is a deliberate no-op.
 * Export functionality is not available on macOS.
 */
@Composable
internal actual fun rememberExportJsonLauncher(): (String) -> Unit = { _ -> }
