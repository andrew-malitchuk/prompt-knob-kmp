package presentation.core.styling.core

import androidx.compose.ui.unit.Dp

/**
 * Design-token holder for stroke / border widths.
 *
 * Key values for Kinetic Mono: 2 dp for brutalist accent separators,
 * 4 dp for progress visualizer bars.
 *
 * @param hairline Thinnest stroke (0.5 dp).
 * @param thin Thin stroke (1 dp).
 * @param light Light stroke (1.5 dp).
 * @param regular Standard stroke — brutalist accent lines (2 dp).
 * @param medium Medium stroke (3 dp).
 * @param thick Thick stroke — progress bars (4 dp).
 * @param heavy Heavy stroke (6 dp).
 * @param extraHeavy Extra-heavy stroke (8 dp).
 * @param ultra Thickest stroke (12 dp).
 * @see Theme.stroke
 */
public data class ThemeStroke(
    val hairline: Dp,
    val thin: Dp,
    val light: Dp,
    val regular: Dp,
    val medium: Dp,
    val thick: Dp,
    val heavy: Dp,
    val extraHeavy: Dp,
    val ultra: Dp,
)
