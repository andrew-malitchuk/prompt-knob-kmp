package presentation.core.styling.source.attribute.color

import androidx.compose.ui.graphics.Color
import presentation.core.styling.core.ThemeColor

/**
 * Light-mode color palette — an inverted variant of the Kinetic Mono dark palette.
 *
 * Warm off-white canvas with darkened accent tones, maintaining the desaturated
 * warm-neutral character of the design system.
 *
 * @see attributeDarkColorPalette
 * @see ThemeColor
 */
internal val attributeLightColorPalette: ThemeColor =
    ThemeColor(
        // region 1. Brand
        brand = Color(0xFF8A8974),
        brandVariant = Color(0xFF7A7966),
        // endregion
        // region 2. Surface
        canvas = Color(0xFFF0EFE6),
        surface = Color(0xFFE8E7DE),
        surfaceVariant = Color(0xFFDDDCD2),
        surfaceInverse = Color(0xFF0E0E0E),
        // endregion
        // region 3. Ink
        inkMain = Color(0xFF1A1A16),
        inkSubtle = Color(0xFF6A695E),
        inkOnBrand = Color(0xFFF0EFE6),
        // endregion
        // region 4. Outline
        outlineLow = Color(0xFFCCCBC2),
        outlineHigh = Color(0xFF8A8978),
        // endregion
        // region 5. Status
        success = Color(0xFF5A7A3B),
        error = Color(0xFFA44A3E),
        warning = Color(0xFFA8942E),
        // endregion
        // region 6. Interaction
        disabled = Color(0xFFB0AFA6),
        scrim = Color(0x4D1A1A16),
        // endregion
    )
