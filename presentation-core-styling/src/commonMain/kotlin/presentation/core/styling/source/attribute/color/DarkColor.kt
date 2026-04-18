package presentation.core.styling.source.attribute.color

import androidx.compose.ui.graphics.Color
import presentation.core.styling.core.ThemeColor

/**
 * Dark-mode color palette — the **primary** Kinetic Mono experience.
 *
 * Rooted in the "Technical Neutral" base: deep charcoals (`#0e0e0e`) and warm off-whites
 * (`#c8c7ba`) mimicking aged aluminum and industrial plastic.
 *
 * @see attributeLightColorPalette
 * @see ThemeColor
 */
internal val attributeDarkColorPalette: ThemeColor =
    ThemeColor(
        // region 1. Brand
        brand = Color(0xFFC8C7BA),
        brandVariant = Color(0xFFBBB9AD),
        // endregion
        // region 2. Surface
        canvas = Color(0xFF0E0E0E),
        surface = Color(0xFF131313),
        surfaceVariant = Color(0xFF1A1A1A),
        surfaceInverse = Color(0xFFE8E7DD),
        // endregion
        // region 3. Ink
        inkMain = Color(0xFFC8C7BA),
        inkSubtle = Color(0xFF7A796E),
        inkOnBrand = Color(0xFF0E0E0E),
        // endregion
        // region 4. Outline
        outlineLow = Color(0xFF2A2A26),
        outlineHigh = Color(0xFF4A4940),
        // endregion
        // region 5. Status
        success = Color(0xFF8A9A6B),
        error = Color(0xFFC47A6E),
        warning = Color(0xFFC8B46E),
        // endregion
        // region 6. Interaction
        disabled = Color(0xFF3A3A36),
        scrim = Color(0x4D000000),
        // endregion
    )
