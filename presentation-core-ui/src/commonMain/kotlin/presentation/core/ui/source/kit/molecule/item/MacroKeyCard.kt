package presentation.core.ui.source.kit.molecule.item

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview
import presentation.core.styling.core.Theme
import presentation.core.styling.source.theme.AppTheme
import presentation.core.ui.source.kit.atom.icon.Home
import presentation.core.ui.source.kit.atom.icon.RefreshCcw
import presentation.core.ui.source.kit.atom.icon.Settings
import presentation.core.ui.source.kit.atom.icon.Sun
import presentation.core.ui.source.kit.atom.shape.SquircleShape

/**
 * A square tile representing a single programmable macro key.
 *
 * The tile displays the macro [icon] in the top-left area, the [slotCode] (e.g. "C01")
 * below the icon as a muted label, and the [macroName] (e.g. "MUTE") anchored to the
 * bottom. It is designed to be placed inside a two-column lazy grid on the macro
 * dashboard screen.
 *
 * @param slotCode Short slot identifier rendered below the icon (e.g. "C01").
 * @param macroName Human-readable macro label anchored at the bottom (e.g. "MUTE").
 * @param icon Vector icon representing the macro action.
 * @param onClick Callback invoked when the user taps the tile.
 * @param modifier Modifier applied to the root tile composable.
 *
 * @see <a href="https://www.figma.com/design/klEUS15vSHsTO53UXCJ7Uo/sketches?node-id=1550-210">Figma</a>
 */
@Composable
public fun MacroKeyCard(
    slotCode: String,
    macroName: String,
    icon: ImageVector?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .aspectRatio(1f)
            .clip(SquircleShape(Theme.spacing.spacingM))
            .background(Theme.color.surface)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(Theme.spacing.spacingM),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(Theme.spacing.spacingXXS)) {
            if (icon != null) {
                Image(
                    imageVector = icon,
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(Theme.color.inkMain),
                    modifier = Modifier.size(24.dp),
                )
            }
            Text(
                text = slotCode,
                style = Theme.typography.label,
                color = Theme.color.inkSubtle,
            )
        }
        Text(
            text = macroName,
            style = Theme.typography.title,
            color = Theme.color.inkMain,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MacroKeyCardPreview() {
    AppTheme {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Theme.spacing.spacingL),
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing.spacingM),
        ) {
            MacroKeyCard(
                slotCode = "C01",
                macroName = "MUTE",
                icon = Home,
                onClick = {},
                modifier = Modifier.weight(1f),
            )
            MacroKeyCard(
                slotCode = "C02",
                macroName = "MEDIA",
                icon = RefreshCcw,
                onClick = {},
                modifier = Modifier.weight(1f),
            )
            MacroKeyCard(
                slotCode = "C03",
                macroName = "BRIGHT",
                icon = Sun,
                onClick = {},
                modifier = Modifier.weight(1f),
            )
            MacroKeyCard(
                slotCode = "C04",
                macroName = "SETTINGS",
                icon = Settings,
                onClick = {},
                modifier = Modifier.weight(1f),
            )
        }
    }
}
