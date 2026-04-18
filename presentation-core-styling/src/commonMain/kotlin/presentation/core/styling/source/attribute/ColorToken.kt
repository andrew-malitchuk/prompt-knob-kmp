package presentation.core.styling.source.attribute

import presentation.core.styling.core.ThemeColor
import presentation.core.styling.core.ThemeColorToken

/**
 * Maps a raw [ThemeColor] palette to semantic [ThemeColorToken] roles.
 *
 * @param palette The resolved color palette (light or dark).
 * @return [ThemeColorToken] with all semantic fields mapped.
 * @see ThemeColorToken
 */
internal fun getAttributeColorToken(palette: ThemeColor): ThemeColorToken =
    ThemeColorToken(
        // Background
        backgroundPrimary = palette.canvas,
        backgroundSecondary = palette.surface,
        backgroundTertiary = palette.surfaceVariant,
        backgroundInverse = palette.surfaceInverse,
        // Text
        textPrimary = palette.inkMain,
        textSecondary = palette.inkSubtle,
        textOnAccent = palette.inkOnBrand,
        // Accent
        accentPrimary = palette.brand,
        accentSecondary = palette.brandVariant,
        // Border
        borderSubtle = palette.outlineLow,
        borderStrong = palette.outlineHigh,
        // Status
        statusPositive = palette.success,
        statusNegative = palette.error,
        statusCaution = palette.warning,
        // State
        stateDisabled = palette.disabled,
        overlay = palette.scrim,
    )
