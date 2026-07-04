package presentation.core.ui.source.kit.molecule.item

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import org.jetbrains.compose.ui.tooling.preview.Preview
import presentation.core.styling.core.Theme
import presentation.core.styling.source.theme.AppTheme
import presentation.core.ui.source.kit.atom.icon.ChevronRight
import presentation.core.ui.source.kit.atom.shape.SquircleShape

/**
 * A tappable row that displays a preset entry with a [title], an optional [description],
 * and a trailing chevron.
 *
 * @param title Primary label of the preset.
 * @param description Secondary label shown below [title]; hidden when blank.
 * @param onClick Invoked when the row is tapped.
 * @param modifier Modifier applied to the root [Row].
 *
 * @see <a href="https://www.figma.com/design/STUB_REPLACE_ME">Figma</a>
 */
@Composable
public fun PresetRow(
    title: String,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(SquircleShape(Theme.spacing.spacingM))
            .background(Theme.color.surface)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(Theme.spacing.spacingL),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing.spacingM),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = Theme.typography.bodyEmphasis,
                color = Theme.color.inkMain,
            )
            if (description.isNotBlank()) {
                Text(
                    text = description,
                    style = Theme.typography.caption,
                    color = Theme.color.inkSubtle,
                )
            }
        }
        Image(
            imageVector = ChevronRight,
            contentDescription = null,
            colorFilter = ColorFilter.tint(Theme.color.inkSubtle),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PresetRowPreview() {
    AppTheme {
        Column(
            modifier = Modifier.padding(Theme.spacing.spacingL),
            verticalArrangement = Arrangement.spacedBy(Theme.spacing.spacingM),
        ) {
            PresetRow(
                title = "Gaming",
                description = "High-speed macros for competitive play",
                onClick = {},
            )
            PresetRow(
                title = "Default",
                description = "",
                onClick = {},
            )
        }
    }
}
