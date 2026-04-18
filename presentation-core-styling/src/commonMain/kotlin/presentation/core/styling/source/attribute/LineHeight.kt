package presentation.core.styling.source.attribute

import androidx.compose.ui.unit.sp
import presentation.core.styling.core.ThemeLineHeight

/**
 * Concrete line-height values for the Kinetic Mono typographic scale.
 *
 * @see ThemeLineHeight
 * @see attributeFontSize
 * @see AttributeTypography
 */
internal val attributeLineHeight: ThemeLineHeight =
    ThemeLineHeight(
        display = 56.sp,
        title = 28.sp,
        label = 14.sp,
        body = 20.sp,
        bodyEmphasis = 20.sp,
        caption = 14.sp,
        action = 20.sp,
    )
