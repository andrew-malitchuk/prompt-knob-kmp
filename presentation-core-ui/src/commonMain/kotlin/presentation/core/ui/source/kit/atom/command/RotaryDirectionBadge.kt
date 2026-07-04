package presentation.core.ui.source.kit.atom.command

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import presentation.core.styling.core.Theme
import presentation.core.ui.source.kit.atom.icon.RefreshCcw

/** Pill badge showing a rotary direction label (CW or CCW). */
@Composable
public fun RotaryDirectionBadge(label: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(Theme.cornerToken.input)
            .background(Theme.color.brand)
            .padding(horizontal = Theme.spacing.spacingM, vertical = Theme.spacing.spacingS),
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing.spacingS),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = RefreshCcw,
            contentDescription = null,
            tint = Theme.color.inkOnBrand,
            modifier = Modifier.size(Theme.size.iconS),
        )
        Text(
            text = label,
            style = Theme.typography.caption,
            color = Theme.color.inkOnBrand,
        )
    }
}
