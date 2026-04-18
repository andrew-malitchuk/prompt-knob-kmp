package presentation.core.styling.core

import androidx.compose.ui.graphics.Color

/**
 * Semantic color tokens that map raw [ThemeColor] palette values to UI-role names.
 *
 * Consumers reference tokens like `Theme.colorToken.backgroundPrimary` instead of
 * raw palette entries, enabling consistent theming when the underlying palette changes.
 *
 * @param backgroundPrimary Root screen background (maps to [ThemeColor.canvas]).
 * @param backgroundSecondary Structural container background (maps to [ThemeColor.surface]).
 * @param backgroundTertiary Floating / utility element background (maps to [ThemeColor.surfaceVariant]).
 * @param backgroundInverse High-contrast overlay background (maps to [ThemeColor.surfaceInverse]).
 * @param textPrimary Primary text color (maps to [ThemeColor.inkMain]).
 * @param textSecondary Secondary / subtle text color (maps to [ThemeColor.inkSubtle]).
 * @param textOnAccent Text rendered on accent backgrounds (maps to [ThemeColor.inkOnBrand]).
 * @param accentPrimary Primary interactive accent (maps to [ThemeColor.brand]).
 * @param accentSecondary Secondary accent / gradient end (maps to [ThemeColor.brandVariant]).
 * @param borderSubtle Low-emphasis border (maps to [ThemeColor.outlineLow]).
 * @param borderStrong High-emphasis border (maps to [ThemeColor.outlineHigh]).
 * @param statusPositive Positive state indicator (maps to [ThemeColor.success]).
 * @param statusNegative Error state indicator (maps to [ThemeColor.error]).
 * @param statusCaution Warning state indicator (maps to [ThemeColor.warning]).
 * @param stateDisabled Disabled element color (maps to [ThemeColor.disabled]).
 * @param overlay Scrim / overlay color (maps to [ThemeColor.scrim]).
 * @see Theme.colorToken
 * @see ThemeColor
 */
public data class ThemeColorToken(
    // region Background
    val backgroundPrimary: Color,
    val backgroundSecondary: Color,
    val backgroundTertiary: Color,
    val backgroundInverse: Color,
    // endregion
    // region Text
    val textPrimary: Color,
    val textSecondary: Color,
    val textOnAccent: Color,
    // endregion
    // region Accent
    val accentPrimary: Color,
    val accentSecondary: Color,
    // endregion
    // region Border
    val borderSubtle: Color,
    val borderStrong: Color,
    // endregion
    // region Status
    val statusPositive: Color,
    val statusNegative: Color,
    val statusCaution: Color,
    // endregion
    // region State
    val stateDisabled: Color,
    val overlay: Color,
    // endregion
)
