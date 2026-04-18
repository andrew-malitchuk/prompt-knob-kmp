package presentation.core.styling.core

import androidx.compose.foundation.shape.RoundedCornerShape

/**
 * Design-token holder for named corner shapes built from [ThemeRadius] values.
 *
 * @param none Sharp corners ([ThemeRadius.none]).
 * @param xs Minimal rounding ([ThemeRadius.xs]).
 * @param s Small rounding — tactical buttons ([ThemeRadius.s]).
 * @param m Medium rounding — cards, inputs ([ThemeRadius.m]).
 * @param full Fully rounded — status dots ([ThemeRadius.full]).
 * @see Theme.corner
 * @see ThemeRadius
 */
public data class ThemeCorner(
    val none: RoundedCornerShape,
    val xs: RoundedCornerShape,
    val s: RoundedCornerShape,
    val m: RoundedCornerShape,
    val full: RoundedCornerShape,
)
