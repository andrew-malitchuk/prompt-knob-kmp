package presentation.core.ui.source.kit.molecule.command

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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

/** Single row in the system command picker list. */
@Composable
public fun SystemCommandRow(
    icon: ImageVector,
    label: String,
    description: String,
    onClick: () -> Unit,
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
            imageVector = icon,
            contentDescription = null,
            tint = Theme.color.brand,
            modifier = Modifier.size(Theme.size.iconM),
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Theme.spacing.spacingXXS),
        ) {
            Text(text = label, style = Theme.typography.title, color = Theme.color.inkMain)
            Text(text = description, style = Theme.typography.label, color = Theme.color.inkSubtle)
        }
        Icon(
            imageVector = ChevronRight,
            contentDescription = null,
            tint = Theme.color.inkSubtle,
            modifier = Modifier.size(Theme.size.iconM),
        )
    }
}
