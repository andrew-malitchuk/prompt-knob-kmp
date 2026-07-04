package presentation.core.ui.source.kit.molecule.ble

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import presentation.core.styling.core.Theme
import presentation.core.ui.source.kit.atom.decoration.CornerFrame
import presentation.core.ui.source.kit.atom.indicator.StatusIndicator
import presentation.core.ui.source.kit.atom.indicator.StatusIndicatorState

/**
 * Full-size empty state shown when BLE is unavailable on the current platform (e.g. Desktop JVM).
 *
 * @param message Localised message to display below the offline indicator.
 */
@Composable
public fun BleUnavailableContent(message: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(Theme.spacing.spacingL),
        contentAlignment = Alignment.Center,
    ) {
        CornerFrame {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Theme.spacing.spacing2XL),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Theme.spacing.spacingL),
            ) {
                StatusIndicator(state = StatusIndicatorState.Offline)
                Text(
                    text = message,
                    style = Theme.typography.label,
                    color = Theme.color.inkSubtle,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}
