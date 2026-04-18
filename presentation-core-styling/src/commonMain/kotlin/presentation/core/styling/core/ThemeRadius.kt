package presentation.core.styling.core

import androidx.compose.ui.unit.Dp

/**
 * Design-token holder for corner radius values.
 *
 * Kinetic Mono enforces a brutalist constraint: **no rounded corners larger than 4 dp**
 * except for status indicator dots ([full] = 999 dp).
 *
 * @param none No rounding (0 dp).
 * @param xs Minimal rounding (1 dp).
 * @param s Small rounding — tactical button radius (2 dp).
 * @param m Medium rounding — maximum for most components (4 dp).
 * @param full Fully rounded — reserved for status dots only (999 dp).
 * @see Theme.radius
 */
public data class ThemeRadius(
    val none: Dp,
    val xs: Dp,
    val s: Dp,
    val m: Dp,
    val full: Dp,
)
