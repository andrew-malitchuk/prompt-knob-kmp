package presentation.core.styling.core

import androidx.compose.ui.graphics.Color

/**
 * Design-token holder for the Kinetic Mono color palette.
 *
 * Colors are grouped into semantic roles derived from the "Technical Neutral" palette:
 * deep charcoals, warm off-whites, and desaturated accents that mimic aged aluminum
 * and industrial plastic.
 *
 * @param brand Primary accent — the "backlight on a precision instrument" (`#c8c7ba`).
 * @param brandVariant Secondary accent for tactile gradient end (`#bbb9ad`).
 * @param canvas Root background — deep charcoal surface (`#0e0e0e`).
 * @param surface Structural background for large elements — surface container low (`#131313`).
 * @param surfaceVariant Utility-driven floating elements — surface container high.
 * @param surfaceInverse Inverted surface for high-contrast overlays.
 * @param inkMain Primary text / icon color on dark surfaces.
 * @param inkSubtle Secondary text / icon color for less prominent content.
 * @param inkOnBrand Text / icon color rendered on top of [brand] backgrounds.
 * @param outlineLow Low-emphasis border (30 % opacity bracket accents).
 * @param outlineHigh High-emphasis border / divider.
 * @param success Semantic color indicating a positive state (warm desaturated green).
 * @param error Semantic color indicating an error state (warm desaturated red).
 * @param warning Semantic color indicating a cautionary state (warm desaturated amber).
 * @param disabled Color applied to disabled / inactive UI elements.
 * @param scrim Semi-transparent overlay color used behind modals.
 * @see Theme.color
 */
public data class ThemeColor(
    // region 1. Brand
    val brand: Color,
    val brandVariant: Color,
    // endregion
    // region 2. Surface
    val canvas: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val surfaceInverse: Color,
    // endregion
    // region 3. Ink
    val inkMain: Color,
    val inkSubtle: Color,
    val inkOnBrand: Color,
    // endregion
    // region 4. Outline
    val outlineLow: Color,
    val outlineHigh: Color,
    // endregion
    // region 5. Status
    val success: Color,
    val error: Color,
    val warning: Color,
    // endregion
    // region 6. Interaction
    val disabled: Color,
    val scrim: Color,
    // endregion
)
