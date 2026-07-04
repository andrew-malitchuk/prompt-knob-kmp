package presentation.core.ui.source.kit.organism.device

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import presentation.core.styling.core.Theme
import presentation.core.ui.source.kit.atom.container.LocalWindowSize
import presentation.core.ui.source.kit.atom.container.WindowSize
import presentation.core.ui.source.kit.molecule.item.MacroKeyCard

/**
 * UI model for a single macro key slot rendered inside [MacroSection].
 *
 * @param id Stable identifier used as a list key.
 * @param slotCode Short slot label shown below the icon (e.g. "C01").
 * @param macroName Human-readable macro label (e.g. "MUTE").
 * @param icon Optional icon vector for the macro; null renders the default placeholder.
 */
public data class MacroKeyItem(
    val id: Int,
    val slotCode: String,
    val macroName: String,
    val icon: ImageVector?,
)

/**
 * Adaptive macro key grid with a section label and empty state.
 *
 * Renders a 2-column grid on compact, 3-column on medium, 4-column on expanded windows.
 *
 * @param sectionLabel Small uppercase label above the grid (e.g. "ACTIVE MACROS").
 * @param noMacrosLabel Text shown when [macroKeys] is empty.
 * @param macroKeys Ordered list of macro key slots to display.
 * @param onMacroClick Callback invoked with the macro [MacroKeyItem.id] on tap.
 */
@Composable
public fun MacroSection(
    sectionLabel: String,
    noMacrosLabel: String,
    macroKeys: List<MacroKeyItem>,
    onMacroClick: (id: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing.spacingM),
    ) {
        Text(
            text = sectionLabel,
            style = Theme.typography.label,
            color = Theme.color.inkSubtle,
        )

        if (macroKeys.isEmpty()) {
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
                    .padding(Theme.spacing.spacing2XL),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = noMacrosLabel,
                    style = Theme.typography.label,
                    color = Theme.color.inkSubtle,
                    textAlign = TextAlign.Center,
                )
            }
        } else {
            val columns = when (LocalWindowSize.current) {
                WindowSize.Compact -> 2
                WindowSize.Medium -> 3
                WindowSize.Expanded -> 4
            }
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Theme.spacing.spacingM),
            ) {
                for (row in macroKeys.chunked(columns)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Theme.spacing.spacingM),
                    ) {
                        row.forEach { macro ->
                            MacroKeyCard(
                                slotCode = macro.slotCode,
                                macroName = macro.macroName,
                                icon = macro.icon,
                                onClick = { onMacroClick(macro.id) },
                                modifier = Modifier.weight(1f),
                            )
                        }
                        repeat(columns - row.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}
