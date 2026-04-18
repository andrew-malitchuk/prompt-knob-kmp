package presentation.core.styling.source.attribute

import androidx.compose.ui.unit.dp
import presentation.core.styling.core.ThemeStroke

/**
 * Concrete stroke-width values.
 *
 * Key Kinetic Mono values: [ThemeStroke.regular] (2 dp) for brutalist accent separators,
 * [ThemeStroke.thick] (4 dp) for progress visualizer bars.
 *
 * @see ThemeStroke
 */
internal val attributeStroke: ThemeStroke =
    ThemeStroke(
        hairline = 0.5.dp,
        thin = 1.dp,
        light = 1.5.dp,
        regular = 2.dp,
        medium = 3.dp,
        thick = 4.dp,
        heavy = 6.dp,
        extraHeavy = 8.dp,
        ultra = 12.dp,
    )
