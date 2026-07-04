package presentation.core.ui.source.kit.molecule.device

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import presentation.core.styling.core.Theme
import presentation.core.ui.source.kit.atom.progress.WavyProgressIndicator

/**
 * Card showing profile sync progress with a [WavyProgressIndicator].
 *
 * @param label Section label shown above the percentage value.
 * @param valueLabel Formatted percentage string shown as display text (e.g. "72%").
 * @param percentage Progress value in [0, 100] used for the indicator.
 */
@Composable
public fun ProfileLoadSection(
    label: String,
    valueLabel: String,
    percentage: Int,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(Theme.cornerToken.card)
            .background(Theme.color.surface)
            .border(
                width = Theme.stroke.thin,
                color = Theme.color.outlineLow,
                shape = Theme.cornerToken.card,
            )
            .padding(Theme.spacing.spacingL),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Theme.spacing.spacingM),
        ) {
            Text(
                text = label,
                style = Theme.typography.label,
                color = Theme.color.inkSubtle,
            )
            Text(
                text = valueLabel,
                style = Theme.typography.display,
                color = Theme.color.inkMain,
            )
            WavyProgressIndicator(
                progress = percentage / 100f,
                modifier = Modifier.fillMaxWidth(),
                activeColor = Theme.color.inkMain,
                inactiveColor = Theme.color.outlineLow,
            )
        }
    }
}
