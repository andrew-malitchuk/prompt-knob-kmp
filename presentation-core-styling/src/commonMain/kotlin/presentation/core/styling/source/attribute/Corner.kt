package presentation.core.styling.source.attribute

import androidx.compose.foundation.shape.RoundedCornerShape
import presentation.core.styling.core.ThemeCorner

/**
 * Concrete corner shapes built from [attributeRadius] values.
 *
 * @see ThemeCorner
 * @see attributeRadius
 */
internal val attributeCorner: ThemeCorner =
    ThemeCorner(
        none = RoundedCornerShape(attributeRadius.none),
        xs = RoundedCornerShape(attributeRadius.xs),
        s = RoundedCornerShape(attributeRadius.s),
        m = RoundedCornerShape(attributeRadius.m),
        full = RoundedCornerShape(attributeRadius.full),
    )
