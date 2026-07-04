package presentation.core.ui.source.kit.molecule.command

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import presentation.core.styling.core.Theme

/** Standard / Rotary / Agent three-option (or two-option) mode selector with an optional hint below. */
@Composable
public fun CommandModeToggle(
    sectionLabel: String,
    standardLabel: String,
    rotaryLabel: String,
    rotaryHint: String,
    isRotary: Boolean,
    onSelectStandard: () -> Unit,
    onSelectRotary: () -> Unit,
    modifier: Modifier = Modifier,
    agentLabel: String? = null,
    agentHint: String? = null,
    isAgent: Boolean = false,
    onSelectAgent: (() -> Unit)? = null,
) {
    val isStandard = !isRotary && !isAgent
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing.spacingXS),
    ) {
        Text(text = sectionLabel, style = Theme.typography.label, color = Theme.color.inkSubtle)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing.spacingS),
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(Theme.cornerToken.input)
                    .background(if (isStandard) Theme.color.brand else Theme.color.surfaceVariant)
                    .clickable { if (!isStandard) onSelectStandard() }
                    .padding(horizontal = Theme.spacing.spacingM, vertical = Theme.spacing.spacingM),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = standardLabel,
                    style = Theme.typography.caption,
                    color = if (isStandard) Theme.color.inkOnBrand else Theme.color.inkSubtle,
                    textAlign = TextAlign.Center,
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(Theme.cornerToken.input)
                    .background(if (isRotary) Theme.color.brand else Theme.color.surfaceVariant)
                    .clickable { if (!isRotary) onSelectRotary() }
                    .padding(horizontal = Theme.spacing.spacingM, vertical = Theme.spacing.spacingM),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = rotaryLabel,
                    style = Theme.typography.caption,
                    color = if (isRotary) Theme.color.inkOnBrand else Theme.color.inkSubtle,
                    textAlign = TextAlign.Center,
                )
            }
            if (agentLabel != null && onSelectAgent != null) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(Theme.cornerToken.input)
                        .background(if (isAgent) Theme.color.brand else Theme.color.surfaceVariant)
                        .clickable { if (!isAgent) onSelectAgent() }
                        .padding(horizontal = Theme.spacing.spacingM, vertical = Theme.spacing.spacingM),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = agentLabel,
                        style = Theme.typography.caption,
                        color = if (isAgent) Theme.color.inkOnBrand else Theme.color.inkSubtle,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
        when {
            isRotary -> Text(text = rotaryHint, style = Theme.typography.caption, color = Theme.color.inkSubtle)
            isAgent && agentHint != null -> Text(text = agentHint, style = Theme.typography.caption, color = Theme.color.inkSubtle)
        }
    }
}
