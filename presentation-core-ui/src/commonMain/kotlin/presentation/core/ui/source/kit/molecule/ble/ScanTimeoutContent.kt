package presentation.core.ui.source.kit.molecule.ble

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import presentation.core.styling.core.Theme
import presentation.core.ui.source.kit.atom.button.TacticalButton
import presentation.core.ui.source.kit.atom.decoration.CornerFrame

/**
 * Card shown when a BLE scan times out without finding any devices.
 *
 * Displays a [title], a list of troubleshooting [hints], and a retry [TacticalButton].
 *
 * @param title Heading shown at the top of the card.
 * @param hints Ordered list of troubleshooting hint strings displayed as bullet lines.
 * @param retryLabel Label for the retry button.
 * @param onRetry Callback invoked when the user taps the retry button.
 */
@Composable
public fun ScanTimeoutContent(
    title: String,
    hints: List<String>,
    retryLabel: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CornerFrame(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Theme.spacing.spacing2XL),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Theme.spacing.spacingL),
        ) {
            Text(
                text = title,
                style = Theme.typography.title,
                color = Theme.color.inkMain,
                textAlign = TextAlign.Center,
            )
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Theme.spacing.spacingS),
            ) {
                hints.forEach { hint ->
                    Text(
                        text = "— $hint",
                        style = Theme.typography.body,
                        color = Theme.color.inkSubtle,
                    )
                }
            }
            TacticalButton(
                text = retryLabel,
                onClick = onRetry,
            )
        }
    }
}
