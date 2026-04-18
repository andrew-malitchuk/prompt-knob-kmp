package presentation.core.styling.core

import androidx.compose.ui.unit.TextUnit

/**
 * Design-token holder for line heights across the Kinetic Mono typographic scale.
 *
 * Each value corresponds to its counterpart in [ThemeFontSize] and ensures
 * consistent vertical rhythm throughout the industrial UI.
 *
 * @param display Line height for monolithic display text.
 * @param title Line height for section headlines.
 * @param label Line height for technical metadata labels.
 * @param body Line height for functional body copy.
 * @param bodyEmphasis Line height for emphasized body copy.
 * @param caption Line height for auxiliary descriptions.
 * @param action Line height for button / interactive-element labels.
 * @see ThemeFontSize
 * @see ThemeTypography
 * @see Theme.lineHeight
 */
public data class ThemeLineHeight(
    val display: TextUnit,
    val title: TextUnit,
    val label: TextUnit,
    val body: TextUnit,
    val bodyEmphasis: TextUnit,
    val caption: TextUnit,
    val action: TextUnit,
)
