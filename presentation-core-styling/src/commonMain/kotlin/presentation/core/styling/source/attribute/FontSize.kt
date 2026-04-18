package presentation.core.styling.source.attribute

import androidx.compose.ui.unit.sp
import presentation.core.styling.core.ThemeFontSize

/**
 * Concrete font-size values for the Kinetic Mono typographic scale.
 *
 * Display is set to 48 sp — an extreme scale for monolithic focal points on mobile.
 * Body is 14 sp for functional descriptions; label / caption drop to 10 sp for
 * the firmware-version / status-indicator aesthetic.
 *
 * @see ThemeFontSize
 * @see AttributeTypography
 */
internal val attributeFontSize: ThemeFontSize =
    ThemeFontSize(
        display = 48.sp,
        title = 20.sp,
        label = 10.sp,
        body = 14.sp,
        bodyEmphasis = 14.sp,
        caption = 10.sp,
        action = 14.sp,
    )
