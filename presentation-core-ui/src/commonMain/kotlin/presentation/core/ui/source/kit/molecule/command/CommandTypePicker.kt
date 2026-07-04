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

/** Horizontal multi-option type picker. [options] are pre-resolved display strings. */
@Composable
public fun CommandTypePicker(
    sectionLabel: String,
    options: List<String>,
    selectedIndex: Int,
    onSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing.spacingXS),
    ) {
        Text(text = sectionLabel, style = Theme.typography.label, color = Theme.color.inkSubtle)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing.spacingS),
        ) {
            options.forEachIndexed { index, label ->
                val isSelected = index == selectedIndex
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(Theme.cornerToken.input)
                        .background(if (isSelected) Theme.color.brand else Theme.color.surfaceVariant)
                        .clickable { onSelected(index) }
                        .padding(horizontal = Theme.spacing.spacingM, vertical = Theme.spacing.spacingM),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = label,
                        style = Theme.typography.caption,
                        color = if (isSelected) Theme.color.inkOnBrand else Theme.color.inkSubtle,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}
