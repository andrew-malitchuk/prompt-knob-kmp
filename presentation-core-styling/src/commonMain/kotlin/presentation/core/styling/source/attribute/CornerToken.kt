package presentation.core.styling.source.attribute

import presentation.core.styling.core.ThemeCorner
import presentation.core.styling.core.ThemeCornerToken

/**
 * Maps [ThemeCorner] shapes to semantic [ThemeCornerToken] component roles.
 *
 * @param corner The resolved corner shape set.
 * @return [ThemeCornerToken] with all semantic fields mapped.
 * @see ThemeCornerToken
 */
internal fun getAttributeCornerToken(corner: ThemeCorner): ThemeCornerToken =
    ThemeCornerToken(
        card = corner.m,
        button = corner.s,
        chip = corner.m,
        badge = corner.full,
        input = corner.s,
    )
