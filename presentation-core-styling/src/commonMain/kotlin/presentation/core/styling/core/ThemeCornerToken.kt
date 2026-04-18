package presentation.core.styling.core

import androidx.compose.foundation.shape.RoundedCornerShape

/**
 * Semantic corner tokens mapping UI components to their designated [ThemeCorner] shapes.
 *
 * Kinetic Mono enforces brutalist corners: max 4 dp except for status dots.
 *
 * @param card Card / container shape (maps to [ThemeCorner.m], 4 dp).
 * @param button Tactical button shape (maps to [ThemeCorner.s], 2 dp).
 * @param chip Industrial chip / pod shape (maps to [ThemeCorner.m], 4 dp).
 * @param badge Status dot / glow indicator (maps to [ThemeCorner.full], 999 dp).
 * @param input Text input field shape (maps to [ThemeCorner.s], 2 dp).
 * @see Theme.cornerToken
 * @see ThemeCorner
 */
public data class ThemeCornerToken(
    val card: RoundedCornerShape,
    val button: RoundedCornerShape,
    val chip: RoundedCornerShape,
    val badge: RoundedCornerShape,
    val input: RoundedCornerShape,
)
