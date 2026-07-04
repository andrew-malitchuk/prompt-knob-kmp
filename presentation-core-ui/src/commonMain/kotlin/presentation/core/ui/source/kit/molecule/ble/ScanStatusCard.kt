package presentation.core.ui.source.kit.molecule.ble

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import presentation.core.styling.core.Theme
import presentation.core.ui.source.kit.atom.button.TacticalButton
import presentation.core.ui.source.kit.atom.container.IconContainer
import presentation.core.ui.source.kit.atom.decoration.CornerFrame
import presentation.core.ui.source.kit.atom.icon.Wifi
import presentation.core.ui.source.kit.atom.text.AutoSizeText

/**
 * Card displaying the current BLE scan / connection status with a central icon frame.
 *
 * @param scanningLabel Small label shown at the top of the card (e.g. "SCANNING").
 * @param cardText Primary display text — device name, connection state, or scanning message.
 * @param isLoading True while a scan or connection is in progress.
 * @param isConnected True when a device is currently connected.
 * @param frequencyLabel Secondary caption shown while [isLoading] is true.
 * @param disconnectLabel Label for the disconnect button shown when [isConnected].
 * @param onDisconnect Callback invoked when the disconnect button is tapped.
 */
@Composable
public fun ScanStatusCard(
    scanningLabel: String,
    cardText: String,
    isLoading: Boolean,
    isConnected: Boolean,
    frequencyLabel: String,
    disconnectLabel: String,
    onDisconnect: () -> Unit,
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
            .padding(Theme.spacing.spacingXL),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Theme.spacing.spacingL),
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = scanningLabel,
                style = Theme.typography.label,
                color = Theme.color.inkSubtle,
            )
            if (!isConnected) {
                CornerFrame(modifier = Modifier.size(Theme.size.icon4XL)) {
                    IconContainer(
                        icon = Wifi,
                        backgroundColor = Theme.color.surfaceVariant,
                        foregroundColor = Theme.color.inkMain,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
            AutoSizeText(
                text = cardText,
                style = Theme.typography.display,
                color = Theme.color.inkMain,
                textAlign = TextAlign.Center,
                maxLines = 1,
            )
            if (isLoading) {
                Text(
                    text = frequencyLabel,
                    style = Theme.typography.caption,
                    color = Theme.color.inkSubtle,
                )
            }
            if (isConnected) {
                TacticalButton(
                    text = disconnectLabel,
                    onClick = onDisconnect,
                )
            }
        }
    }
}
