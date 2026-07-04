package presentation.core.ui.source.kit.molecule.command

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import presentation.core.styling.core.Theme
import presentation.core.ui.source.kit.atom.icon.ChevronRight

/**
 * UI model for a single command/folder row.
 *
 * @property icon Pre-resolved icon; caller maps from key or applies folder/rotary override.
 * @property rotaryBadgeLabel Subtitle shown when [isFolder] && [isRotary].
 * @property agentBadgeLabel Subtitle shown when [isAgentScreen] is true.
 */
public data class CommandRowItem(
    val id: Int,
    val label: String,
    val isFolder: Boolean,
    val isRotary: Boolean,
    val isAgentScreen: Boolean = false,
    val icon: ImageVector,
    val command: String,
    val rotaryBadgeLabel: String,
    val agentBadgeLabel: String = "",
)

/** Single row in the command/folder list. */
@Composable
public fun CommandItemRow(
    item: CommandRowItem,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(Theme.cornerToken.card)
            .background(Theme.color.surface)
            .border(
                width = Theme.stroke.thin,
                color = Theme.color.outlineLow,
                shape = Theme.cornerToken.card,
            )
            .clickable(onClick = onClick)
            .padding(Theme.spacing.spacingM),
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing.spacingM),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = null,
            tint = Theme.color.brand,
            modifier = Modifier.size(Theme.size.iconM),
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Theme.spacing.spacingXXS),
        ) {
            Text(text = item.label, style = Theme.typography.title, color = Theme.color.inkMain)
            if (item.isFolder && item.isRotary) {
                Text(
                    text = item.rotaryBadgeLabel,
                    style = Theme.typography.label,
                    color = Theme.color.brand,
                    maxLines = 1,
                )
            } else if (item.isAgentScreen && item.agentBadgeLabel.isNotBlank()) {
                Text(
                    text = item.agentBadgeLabel,
                    style = Theme.typography.label,
                    color = Theme.color.brand,
                    maxLines = 1,
                )
            } else if (!item.isFolder && item.command.isNotBlank()) {
                Text(
                    text = item.command,
                    style = Theme.typography.label,
                    color = Theme.color.inkSubtle,
                    maxLines = 1,
                )
            }
        }
        if (item.isFolder) {
            Icon(
                imageVector = ChevronRight,
                contentDescription = null,
                tint = Theme.color.inkSubtle,
                modifier = Modifier.size(Theme.size.iconM),
            )
        } else {
            Spacer(
                modifier = Modifier
                    .size(Theme.size.iconM)
                    .clickable(onClick = onDeleteClick),
            )
        }
    }
}
