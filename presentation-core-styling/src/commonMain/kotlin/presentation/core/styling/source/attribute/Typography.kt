package presentation.core.styling.source.attribute

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import org.jetbrains.compose.resources.Font
import presentation.core.styling.core.ThemeTypography
import prompt_knob_kmp.presentation_core_styling.generated.resources.Res
import prompt_knob_kmp.presentation_core_styling.generated.resources.spacegrotesk_bold
import prompt_knob_kmp.presentation_core_styling.generated.resources.spacegrotesk_light
import prompt_knob_kmp.presentation_core_styling.generated.resources.spacegrotesk_medium
import prompt_knob_kmp.presentation_core_styling.generated.resources.spacegrotesk_regular

/**
 * Builds the Space Grotesk [FontFamily] from bundled Compose Resources font files.
 *
 * Space Grotesk is the exclusive typeface of the Kinetic Mono design system,
 * maintaining a cohesive technical rhythm across all roles.
 *
 * @return [FontFamily] containing Light, Regular, Medium, and Bold weights.
 */
@Composable
internal fun SpaceGroteskFontFamily(): FontFamily =
    FontFamily(
        Font(Res.font.spacegrotesk_light, FontWeight.Light),
        Font(Res.font.spacegrotesk_regular, FontWeight.Normal),
        Font(Res.font.spacegrotesk_medium, FontWeight.Medium),
        Font(Res.font.spacegrotesk_bold, FontWeight.Bold),
    )

/**
 * Assembles the complete [ThemeTypography] by combining [attributeFontSize],
 * [attributeLineHeight], and [SpaceGroteskFontFamily] into ready-to-use [TextStyle] tokens.
 *
 * - Display: Light weight, tight tracking — the "Monolith" hero scale.
 * - Title: Medium weight, wide tracking (0.25 em) — always uppercase at call site.
 * - Label: Bold weight, heavy tracking (0.4 em) — firmware / technical metadata.
 * - Body / action: Regular / Bold weight for functional UI text.
 *
 * @return [ThemeTypography] containing a [TextStyle] for every typographic scale level.
 * @see ThemeTypography
 * @see attributeFontSize
 * @see attributeLineHeight
 */
@Composable
internal fun AttributeTypography(): ThemeTypography {
    val spaceGrotesk = SpaceGroteskFontFamily()

    return ThemeTypography(
        display = TextStyle(
            fontSize = attributeFontSize.display,
            lineHeight = attributeLineHeight.display,
            fontWeight = FontWeight.Light,
            fontFamily = spaceGrotesk,
            letterSpacing = (-0.02).em,
        ),
        title = TextStyle(
            fontSize = attributeFontSize.title,
            lineHeight = attributeLineHeight.title,
            fontWeight = FontWeight.Medium,
            fontFamily = spaceGrotesk,
            letterSpacing = 0.25.em,
        ),
        label = TextStyle(
            fontSize = attributeFontSize.label,
            lineHeight = attributeLineHeight.label,
            fontWeight = FontWeight.Bold,
            fontFamily = spaceGrotesk,
            letterSpacing = 0.4.em,
        ),
        body = TextStyle(
            fontSize = attributeFontSize.body,
            lineHeight = attributeLineHeight.body,
            fontWeight = FontWeight.Normal,
            fontFamily = spaceGrotesk,
        ),
        bodyEmphasis = TextStyle(
            fontSize = attributeFontSize.bodyEmphasis,
            lineHeight = attributeLineHeight.bodyEmphasis,
            fontWeight = FontWeight.Bold,
            fontFamily = spaceGrotesk,
        ),
        caption = TextStyle(
            fontSize = attributeFontSize.caption,
            lineHeight = attributeLineHeight.caption,
            fontWeight = FontWeight.Normal,
            fontFamily = spaceGrotesk,
            letterSpacing = 0.4.em,
        ),
        action = TextStyle(
            fontSize = attributeFontSize.action,
            lineHeight = attributeLineHeight.action,
            fontWeight = FontWeight.Bold,
            fontFamily = spaceGrotesk,
        ),
    )
}
