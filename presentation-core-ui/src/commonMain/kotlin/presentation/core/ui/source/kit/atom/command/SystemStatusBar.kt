package presentation.core.ui.source.kit.atom.command

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import presentation.core.styling.core.Theme

/** Centered status pill with a green dot and a [label] (e.g. "READY"). */
@Composable
public fun SystemStatusBar(label: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = Theme.spacing.spacingS),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(Theme.cornerToken.badge)
                .background(Theme.color.success),
        )
        Spacer(modifier = Modifier.width(Theme.spacing.spacingS))
        Text(text = label, style = Theme.typography.caption, color = Theme.color.inkSubtle)
    }
}
