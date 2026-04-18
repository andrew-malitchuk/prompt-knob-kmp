package presentation.core.styling.source.attribute

import androidx.compose.ui.unit.dp
import presentation.core.styling.core.ThemeRadius

/**
 * Concrete radius values for the Kinetic Mono design system.
 *
 * Brutalist constraint: max 4 dp for components, 999 dp reserved for status dots.
 *
 * @see ThemeRadius
 */
internal val attributeRadius: ThemeRadius =
    ThemeRadius(
        none = 0.dp,
        xs = 1.dp,
        s = 2.dp,
        m = 4.dp,
        full = 999.dp,
    )
