package presentation.core.ui.source.showcase

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import presentation.core.styling.core.Theme
import presentation.core.ui.source.kit.atom.button.TacticalButton
import presentation.core.ui.source.kit.atom.icon.ChevronRight

/**
 * Scrollable design-system showcase for the Kinetic Mono style guide.
 *
 * Displays all token groups in a single scrollable list:
 * Colors, Typography, Spacing, Radius, Stroke, Sizes, and live component demos.
 * Intended as a dev utility reachable from the Home screen via [TacticalButton].
 *
 * @param onBack Callback invoked when the user taps the back control.
 */
@Composable
public fun StyleguideScreen(onBack: () -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.color.canvas),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing.spacingXXS),
    ) {

        // ── Header ──────────────────────────────────────────────────────────
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Theme.color.surface)
                    .padding(
                        horizontal = Theme.spacing.spacingL,
                        vertical = Theme.spacing.spacingL,
                    ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(Theme.radius.s))
                        .background(Theme.color.surfaceVariant)
                        .clickable(
                            role = Role.Button,
                            onClickLabel = "Back",
                            onClick = onBack,
                        )
                        .padding(
                            horizontal = Theme.spacing.spacingM,
                            vertical = Theme.spacing.spacingS,
                        ),
                ) {
                    TechnicalLabel(text = "← BACK")
                }
                Spacer(Modifier.width(Theme.spacing.spacingM))
                Text(
                    text = "KINETIC MONO",
                    style = Theme.typography.title,
                    color = Theme.color.inkMain,
                )
                Spacer(Modifier.weight(1f))
                TechnicalLabel(text = "v1.0")
            }
        }

        // ── Colors ──────────────────────────────────────────────────────────
        item { SectionHeader(title = "01 / COLORS") }
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Theme.color.surface)
                    .padding(Theme.spacing.spacingL),
                verticalArrangement = Arrangement.spacedBy(Theme.spacing.spacingS),
            ) {
                ColorRow(label = "BRAND", color = Theme.color.brand)
                ColorRow(label = "BRAND VARIANT", color = Theme.color.brandVariant)
                ColorRow(label = "CANVAS", color = Theme.color.canvas, outlined = true)
                ColorRow(label = "SURFACE", color = Theme.color.surface, outlined = true)
                ColorRow(label = "SURFACE VARIANT", color = Theme.color.surfaceVariant, outlined = true)
                ColorRow(label = "SURFACE INVERSE", color = Theme.color.surfaceInverse)
                ColorRow(label = "INK MAIN", color = Theme.color.inkMain)
                ColorRow(label = "INK SUBTLE", color = Theme.color.inkSubtle)
                ColorRow(label = "INK ON BRAND", color = Theme.color.inkOnBrand, outlined = true)
                ColorRow(label = "OUTLINE LOW", color = Theme.color.outlineLow)
                ColorRow(label = "OUTLINE HIGH", color = Theme.color.outlineHigh)
                ColorRow(label = "SUCCESS", color = Theme.color.success)
                ColorRow(label = "ERROR", color = Theme.color.error)
                ColorRow(label = "WARNING", color = Theme.color.warning)
                ColorRow(label = "DISABLED", color = Theme.color.disabled)
                ColorRow(label = "SCRIM", color = Theme.color.scrim)
            }
        }

        // ── Typography ──────────────────────────────────────────────────────
        item { SectionHeader(title = "02 / TYPOGRAPHY") }
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Theme.color.surface)
                    .padding(Theme.spacing.spacingL),
                verticalArrangement = Arrangement.spacedBy(Theme.spacing.spacingL),
            ) {
                TypographyRow(
                    role = "DISPLAY  48sp",
                    sample = "Aa",
                    style = Theme.typography.display,
                )
                ShowDivider()
                TypographyRow(
                    role = "TITLE  20sp",
                    sample = "SECTION HEADER",
                    style = Theme.typography.title,
                )
                ShowDivider()
                TypographyRow(
                    role = "BODY  14sp",
                    sample = "Functional description copy",
                    style = Theme.typography.body,
                )
                ShowDivider()
                TypographyRow(
                    role = "BODY EMPHASIS  14sp",
                    sample = "Emphasized body copy",
                    style = Theme.typography.bodyEmphasis,
                )
                ShowDivider()
                TypographyRow(
                    role = "ACTION  14sp",
                    sample = "INITIALIZE SYSTEM",
                    style = Theme.typography.action,
                )
                ShowDivider()
                TypographyRow(
                    role = "LABEL  10sp",
                    sample = "FW 1.2.0 // STATUS: ONLINE",
                    style = Theme.typography.label,
                )
                ShowDivider()
                TypographyRow(
                    role = "CAPTION  10sp",
                    sample = "SYS // AUX DATA STREAM",
                    style = Theme.typography.caption,
                )
            }
        }

        // ── Spacing ─────────────────────────────────────────────────────────
        item { SectionHeader(title = "03 / SPACING") }
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Theme.color.surface)
                    .padding(Theme.spacing.spacingL),
                verticalArrangement = Arrangement.spacedBy(Theme.spacing.spacingS),
            ) {
                SpacingRow(label = "XXS", value = Theme.spacing.spacingXXS)
                SpacingRow(label = "XS", value = Theme.spacing.spacingXS)
                SpacingRow(label = "S", value = Theme.spacing.spacingS)
                SpacingRow(label = "M", value = Theme.spacing.spacingM)
                SpacingRow(label = "L", value = Theme.spacing.spacingL)
                SpacingRow(label = "XL", value = Theme.spacing.spacingXL)
                SpacingRow(label = "2XL", value = Theme.spacing.spacing2XL)
                SpacingRow(label = "3XL", value = Theme.spacing.spacing3XL)
                SpacingRow(label = "4XL", value = Theme.spacing.spacing4XL)
                SpacingRow(label = "5XL", value = Theme.spacing.spacing5XL)
            }
        }

        // ── Radius ──────────────────────────────────────────────────────────
        item { SectionHeader(title = "04 / RADIUS") }
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Theme.color.surface)
                    .padding(Theme.spacing.spacingL),
                horizontalArrangement = Arrangement.spacedBy(Theme.spacing.spacingM),
            ) {
                CornerBox(label = "NONE\n0dp", radius = Theme.radius.none)
                CornerBox(label = "XS\n1dp", radius = Theme.radius.xs)
                CornerBox(label = "S\n2dp", radius = Theme.radius.s)
                CornerBox(label = "M\n4dp", radius = Theme.radius.m)
                CornerBox(label = "FULL\n999dp", radius = Theme.radius.full)
            }
        }

        // ── Stroke ──────────────────────────────────────────────────────────
        item { SectionHeader(title = "05 / STROKE") }
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Theme.color.surface)
                    .padding(Theme.spacing.spacingL),
                verticalArrangement = Arrangement.spacedBy(Theme.spacing.spacingM),
            ) {
                StrokeRow(label = "HAIRLINE  0.5dp", width = Theme.stroke.hairline)
                StrokeRow(label = "THIN  1dp", width = Theme.stroke.thin)
                StrokeRow(label = "LIGHT  1.5dp", width = Theme.stroke.light)
                StrokeRow(label = "REGULAR  2dp  ← BRUTALIST ACCENTS", width = Theme.stroke.regular)
                StrokeRow(label = "MEDIUM  3dp", width = Theme.stroke.medium)
                StrokeRow(label = "THICK  4dp  ← PROGRESS BARS", width = Theme.stroke.thick)
                StrokeRow(label = "HEAVY  6dp", width = Theme.stroke.heavy)
                StrokeRow(label = "EXTRA HEAVY  8dp", width = Theme.stroke.extraHeavy)
                StrokeRow(label = "ULTRA  12dp", width = Theme.stroke.ultra)
            }
        }

        // ── Icon Sizes ───────────────────────────────────────────────────────
        item { SectionHeader(title = "06 / ICON SIZES") }
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Theme.color.surface)
                    .padding(Theme.spacing.spacingL),
                horizontalArrangement = Arrangement.spacedBy(Theme.spacing.spacingM),
                verticalAlignment = Alignment.Bottom,
            ) {
                listOf(
                    "XXS\n12dp" to Theme.size.iconXXS,
                    "XS\n16dp" to Theme.size.iconXS,
                    "S\n20dp" to Theme.size.iconS,
                    "M\n24dp" to Theme.size.iconM,
                    "L\n28dp" to Theme.size.iconL,
                    "XL\n32dp" to Theme.size.iconXL,
                ).forEach { (label, size) ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(size)
                                .background(
                                    color = Theme.color.brand,
                                    shape = RoundedCornerShape(Theme.radius.s),
                                ),
                        )
                        TechnicalLabel(text = label, textAlign = TextAlign.Center)
                    }
                }
            }
        }

        // ── Components ───────────────────────────────────────────────────────
        item { SectionHeader(title = "07 / COMPONENTS") }
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Theme.color.surface)
                    .padding(Theme.spacing.spacingL),
                verticalArrangement = Arrangement.spacedBy(Theme.spacing.spacingM),
            ) {
                TechnicalLabel(text = "TACTICAL BUTTON — PRIMARY CTA")
                Spacer(Modifier.height(Theme.spacing.spacingS))
                TacticalButton(
                    text = "INITIALIZE SYSTEM",
                    icon = ChevronRight,
                    onClick = {},
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(Theme.spacing.spacingS))
                TechnicalLabel(text = "TACTICAL BUTTON — DISABLED STATE")
                Spacer(Modifier.height(Theme.spacing.spacingS))
                TacticalButton(
                    text = "SYSTEM OFFLINE",
                    onClick = {},
                    enabled = false,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        item { Spacer(Modifier.height(Theme.spacing.spacing3XL)) }
    }
}

// ── Private helpers ──────────────────────────────────────────────────────────

@Composable
private fun SectionHeader(title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Theme.color.canvas)
            .padding(
                horizontal = Theme.spacing.spacingL,
                vertical = Theme.spacing.spacingM,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing.spacingM),
    ) {
        Box(
            modifier = Modifier
                .width(Theme.stroke.regular)
                .height(Theme.spacing.spacingL)
                .background(Theme.color.brand),
        )
        TechnicalLabel(text = title)
    }
}

@Composable
private fun ColorRow(label: String, color: Color, outlined: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing.spacingM),
    ) {
        val shape = RoundedCornerShape(Theme.radius.s)
        Box(
            modifier = Modifier
                .size(width = 48.dp, height = 24.dp)
                .clip(shape)
                .background(color)
                .let { m ->
                    if (outlined) m.border(Theme.stroke.thin, Theme.color.outlineLow, shape) else m
                },
        )
        TechnicalLabel(text = label, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun TypographyRow(role: String, sample: String, style: TextStyle) {
    Column(modifier = Modifier.fillMaxWidth()) {
        TechnicalLabel(text = role)
        Spacer(Modifier.height(4.dp))
        Text(
            text = sample,
            style = style,
            color = Theme.color.inkMain,
            maxLines = 1,
        )
    }
}

@Composable
private fun SpacingRow(label: String, value: Dp) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing.spacingM),
    ) {
        TechnicalLabel(text = label, modifier = Modifier.width(32.dp))
        TechnicalLabel(
            text = "${value.value.toInt()}dp",
            modifier = Modifier.width(36.dp),
        )
        Box(
            modifier = Modifier
                .height(Theme.stroke.thick)
                .width(value)
                .background(Theme.color.brand),
        )
    }
}

@Composable
private fun CornerBox(label: String, radius: Dp) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Theme.spacing.spacingXS),
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .border(
                    width = Theme.stroke.regular,
                    color = Theme.color.brand,
                    shape = RoundedCornerShape(radius.coerceAtMost(20.dp)),
                ),
        )
        TechnicalLabel(text = label, textAlign = TextAlign.Center)
    }
}

@Composable
private fun StrokeRow(label: String, width: Dp) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing.spacingXS),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(width)
                .background(Theme.color.brand),
        )
        TechnicalLabel(text = label)
    }
}

@Composable
private fun ShowDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(Theme.stroke.regular)
            .background(Theme.color.outlineLow),
    )
}

@Composable
private fun TechnicalLabel(
    text: String,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Start,
) {
    Text(
        text = text,
        style = Theme.typography.label,
        color = Theme.color.inkSubtle,
        modifier = modifier,
        textAlign = textAlign,
    )
}
