package presentation.core.ui.source.kit.molecule.item

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import presentation.core.styling.core.Theme

/** Single label + value row used in info/about cards. */
@Composable
public fun InfoRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = Theme.typography.label,
            color = Theme.color.inkSubtle,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = value,
            style = Theme.typography.bodyEmphasis,
            color = Theme.color.inkMain,
        )
    }
}
