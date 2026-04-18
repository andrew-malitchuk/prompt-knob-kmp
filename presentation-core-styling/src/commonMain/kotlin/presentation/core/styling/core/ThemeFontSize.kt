package presentation.core.styling.core

import androidx.compose.ui.unit.TextUnit

/**
 * Design-token holder for font sizes across the Kinetic Mono typographic scale.
 *
 * Display uses an extreme 48 sp to create the "Monolith" focal point on mobile.
 * Body copy is set at 14 sp for functional readability; labels / technical metadata
 * drop to 10 sp for the "firmware version" aesthetic.
 *
 * @param display Font size for monolithic hero / display text (48 sp).
 * @param title Font size for section headlines (20 sp).
 * @param label Font size for technical metadata / firmware labels (10 sp).
 * @param body Font size for functional body copy (14 sp).
 * @param bodyEmphasis Font size for emphasized body copy (14 sp, paired with bold weight).
 * @param caption Font size for auxiliary descriptions (10 sp).
 * @param action Font size for button / interactive-element labels (14 sp).
 * @see ThemeTypography
 * @see Theme.fontSize
 */
public data class ThemeFontSize(
    val display: TextUnit,
    val title: TextUnit,
    val label: TextUnit,
    val body: TextUnit,
    val bodyEmphasis: TextUnit,
    val caption: TextUnit,
    val action: TextUnit,
)
