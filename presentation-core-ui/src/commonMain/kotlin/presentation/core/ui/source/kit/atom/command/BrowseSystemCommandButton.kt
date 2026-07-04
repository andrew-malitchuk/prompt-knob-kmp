package presentation.core.ui.source.kit.atom.command

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import presentation.core.styling.core.Theme
import presentation.core.ui.source.kit.atom.icon.Settings

/** Outlined neutral button for opening the system-command browser. */
@Composable
public fun BrowseSystemCommandButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(Theme.cornerToken.button)
            .background(Theme.color.canvas)
            .border(width = Theme.stroke.thin, color = Theme.color.outlineHigh, shape = Theme.cornerToken.button)
            .clickable(onClick = onClick)
            .padding(horizontal = Theme.spacing.spacingXL, vertical = Theme.spacing.spacingM),
        contentAlignment = Alignment.Center,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
            Icon(imageVector = Settings, contentDescription = null, tint = Theme.color.inkSubtle, modifier = Modifier.size(Theme.size.iconM))
            Spacer(modifier = Modifier.width(Theme.spacing.spacingM))
            Text(text = text, style = Theme.typography.action, color = Theme.color.inkSubtle, textAlign = TextAlign.Center)
        }
    }
}
