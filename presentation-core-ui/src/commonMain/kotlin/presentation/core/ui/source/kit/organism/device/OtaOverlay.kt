package presentation.core.ui.source.kit.organism.device

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import presentation.core.styling.core.Theme
import presentation.core.ui.source.kit.atom.button.TacticalButton

/**
 * Full-screen blocking overlay shown while an OTA firmware update is in progress.
 *
 * The caller is responsible for mapping the domain OTA state to these display-ready parameters
 * before invoking this composable.
 *
 * @param title Primary heading text (e.g. "RECEIVING", "VERIFYING", "UPDATE COMPLETE").
 * @param subtitle Secondary label beneath the title; null hides the subtitle row.
 * @param showProgress Whether to show the [LinearProgressIndicator] below the subtitle.
 * @param progressValue Progress in [0f, 1f] for the linear indicator.
 * @param showCancel Whether the [abortLabel] cancel button is visible.
 * @param abortLabel Label for the cancel/abort button.
 * @param onAbort Callback invoked when the abort button is tapped.
 */
@Composable
public fun OtaOverlay(
    title: String,
    subtitle: String?,
    showProgress: Boolean,
    progressValue: Float,
    showCancel: Boolean,
    abortLabel: String,
    onAbort: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Theme.color.canvas.copy(alpha = 0.95f)),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Theme.spacing.spacing2XL),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Theme.spacing.spacingL),
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = Theme.color.brand,
                trackColor = Theme.color.outlineLow,
            )
            Text(
                text = title,
                style = Theme.typography.display,
                color = Theme.color.inkMain,
                textAlign = TextAlign.Center,
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = Theme.typography.label,
                    color = Theme.color.inkSubtle,
                    textAlign = TextAlign.Center,
                )
            }
            if (showProgress) {
                LinearProgressIndicator(
                    progress = { progressValue },
                    modifier = Modifier.fillMaxWidth(),
                    color = Theme.color.brand,
                    trackColor = Theme.color.outlineLow,
                )
            }
            if (showCancel) {
                TacticalButton(
                    text = abortLabel,
                    onClick = onAbort,
                )
            }
        }
    }
}
