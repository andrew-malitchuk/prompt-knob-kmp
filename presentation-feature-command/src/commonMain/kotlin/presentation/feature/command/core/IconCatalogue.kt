package presentation.feature.command.core

import androidx.compose.ui.graphics.vector.ImageVector
import presentation.core.ui.source.icon.iconCatalogue as coreIconCatalogue
import presentation.core.ui.source.icon.resolveIcon as coreResolveIcon

/** Ordered list of (key → ImageVector) pairs used by the icon picker and resolver. */
internal val iconCatalogue: List<Pair<String, ImageVector>> = coreIconCatalogue

/**
 * Resolves a stored icon key to an [ImageVector].
 * Returns [HardDrive] when the key is blank or unrecognised.
 */
internal fun resolveIcon(key: String): ImageVector = coreResolveIcon(key)
