package presentation.core.ui.source.kit.organism.device

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import presentation.core.styling.core.Theme

/**
 * UI model for a single command execution history entry.
 *
 * @param commandLabel Human-readable command name at execution time.
 * @param isSuccess True if the command executed successfully.
 */
public data class HistoryEntry(
    val commandLabel: String,
    val isSuccess: Boolean,
)

/**
 * Section showing the most recent command execution history (capped at 20 entries).
 *
 * Shows an empty-state card when [entries] is empty; otherwise renders a bordered list
 * with per-row success/failure badges and a "Clear" tap target in the header.
 *
 * @param titleLabel Section header label (e.g. "HISTORY").
 * @param clearLabel Tappable label shown when entries exist (e.g. "CLEAR").
 * @param emptyLabel Text shown in the empty-state card.
 * @param successLabel Badge text for successful entries.
 * @param failureLabel Badge text for failed entries.
 * @param entries Ordered list of history entries (newest first).
 * @param onClear Callback invoked when the clear label is tapped.
 */
@Composable
public fun HistorySection(
    titleLabel: String,
    clearLabel: String,
    emptyLabel: String,
    successLabel: String,
    failureLabel: String,
    entries: List<HistoryEntry>,
    onClear: () -> Unit,
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
            Text(
                text = titleLabel,
                style = Theme.typography.label,
                color = Theme.color.inkSubtle,
            )
            if (entries.isNotEmpty()) {
                Text(
                    text = clearLabel,
                    style = Theme.typography.caption,
                    color = Theme.color.brand,
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onClear,
                    ),
                )
            }
        }

        if (entries.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(Theme.cornerToken.card)
                    .background(Theme.color.surface)
                    .border(
                        width = Theme.stroke.thin,
                        color = Theme.color.outlineLow,
                        shape = Theme.cornerToken.card,
                    )
                    .padding(Theme.spacing.spacingL),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = emptyLabel,
                    style = Theme.typography.label,
                    color = Theme.color.inkSubtle,
                )
            }
        } else {
            val visible = entries.take(20)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(Theme.cornerToken.card)
                    .background(Theme.color.surface)
                    .border(
                        width = Theme.stroke.thin,
                        color = Theme.color.outlineLow,
                        shape = Theme.cornerToken.card,
                    ),
            ) {
                visible.forEachIndexed { index, entry ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = Theme.spacing.spacingL,
                                vertical = Theme.spacing.spacingM,
                            ),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = entry.commandLabel,
                            style = Theme.typography.body,
                            color = Theme.color.inkMain,
                            modifier = Modifier.weight(1f),
                        )
                        Text(
                            text = if (entry.isSuccess) successLabel else failureLabel,
                            style = Theme.typography.caption,
                            color = if (entry.isSuccess) Theme.color.success else Theme.color.error,
                        )
                    }
                    if (index < visible.lastIndex) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = Theme.spacing.spacingL)
                                .height(Theme.stroke.thin)
                                .background(Theme.color.outlineLow),
                        )
                    }
                }
            }
        }
    }
}
