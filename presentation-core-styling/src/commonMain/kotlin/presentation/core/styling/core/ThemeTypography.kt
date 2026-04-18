package presentation.core.styling.core

import androidx.compose.ui.text.TextStyle

/**
 * Design-token holder for fully composed [TextStyle] instances across the Kinetic Mono
 * typographic scale.
 *
 * All roles use **Space Grotesk** exclusively to maintain the cohesive, technical rhythm
 * of the Industrial Monolith design language.
 *
 * @param display Monolithic hero text (48 sp, Light weight, tight tracking).
 * @param title Section headline — always uppercase with wide tracking (20 sp, Medium).
 * @param label Technical / firmware metadata — extremely small, heavy tracking (10 sp, Bold).
 * @param body Functional description copy (14 sp, Regular).
 * @param bodyEmphasis Emphasized body copy (14 sp, Bold).
 * @param caption Auxiliary descriptions at technical scale (10 sp, Regular).
 * @param action Button / interactive-element labels (14 sp, Bold).
 * @see Theme.typography
 * @see ThemeFontSize
 * @see ThemeLineHeight
 */
public data class ThemeTypography(
    val display: TextStyle,
    val title: TextStyle,
    val label: TextStyle,
    val body: TextStyle,
    val bodyEmphasis: TextStyle,
    val caption: TextStyle,
    val action: TextStyle,
)
