package presentation.core.ui.source.kit.molecule.command

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import presentation.core.styling.core.Theme

/** Square button preview card with optional tap-to-test behaviour. */
@Composable
public fun ButtonPreviewSection(
    previewLabel: String,
    displayName: String,
    icon: ImageVector,
    isActive: Boolean,
    onTestClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Theme.spacing.spacingL),
    ) {
        Text(
            text = previewLabel,
            style = Theme.typography.label,
            color = Theme.color.inkSubtle,
            textAlign = TextAlign.Center,
        )
        Box(
            modifier = Modifier
                .size(172.dp)
                .clip(Theme.cornerToken.card)
                .background(if (isActive) Theme.color.surface else Theme.color.surfaceVariant)
                .border(
                    width = Theme.stroke.thin,
                    color = if (isActive) Theme.color.brand else Theme.color.outlineLow,
                    shape = Theme.cornerToken.card,
                )
                .then(
                    if (onTestClick != null) {
                        Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onTestClick,
                        )
                    } else Modifier,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Theme.spacing.spacingM),
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isActive) Theme.color.brand else Theme.color.inkMain,
                    modifier = Modifier.size(Theme.size.icon3XL),
                )
                Text(
                    text = displayName,
                    style = Theme.typography.label,
                    color = if (isActive) Theme.color.brand else Theme.color.inkMain,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}
