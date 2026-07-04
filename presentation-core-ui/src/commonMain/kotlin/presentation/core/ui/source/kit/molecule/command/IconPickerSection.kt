package presentation.core.ui.source.kit.molecule.command

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import presentation.core.styling.core.Theme

/** 4-column icon picker grid. [catalogue] is a list of (key → ImageVector) pairs. */
@Composable
public fun IconPickerSection(
    sectionLabel: String,
    countLabel: String,
    catalogue: List<Pair<String, ImageVector>>,
    selectedIconKey: String,
    onIconSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing.spacingM),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = sectionLabel, style = Theme.typography.label, color = Theme.color.inkSubtle)
            Text(text = countLabel, style = Theme.typography.caption, color = Theme.color.inkSubtle)
        }

        val rows = catalogue.chunked(4)
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(Theme.spacing.spacingS),
        ) {
            for (row in rows) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Theme.spacing.spacingS),
                ) {
                    for ((key, icon) in row) {
                        val isSelected = key == selectedIconKey
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .clip(Theme.cornerToken.card)
                                .background(if (isSelected) Theme.color.brand else Theme.color.surfaceVariant)
                                .clickable { onIconSelected(key) },
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = if (isSelected) Theme.color.inkOnBrand else Theme.color.inkSubtle,
                                modifier = Modifier.size(Theme.size.iconM),
                            )
                        }
                    }
                    repeat(4 - row.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}
