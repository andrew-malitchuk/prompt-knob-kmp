package presentation.core.ui.source.kit.molecule.device

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import presentation.core.styling.core.Theme
import presentation.core.ui.source.kit.atom.text.AutoSizeText

/**
 * Screen title block for a connected device detail screen.
 *
 * @param hardwareLabel Small uppercase caption above the device name (e.g. "CURRENT HARDWARE").
 * @param deviceName Primary heading — the advertised device name.
 */
@Composable
public fun DeviceHeader(
    hardwareLabel: String,
    deviceName: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing.spacingXS),
    ) {
        Text(
            text = hardwareLabel,
            style = Theme.typography.label,
            color = Theme.color.inkSubtle,
        )
        AutoSizeText(
            text = deviceName,
            style = Theme.typography.display,
            color = Theme.color.inkMain,
            maxLines = 1,
        )
    }
}
