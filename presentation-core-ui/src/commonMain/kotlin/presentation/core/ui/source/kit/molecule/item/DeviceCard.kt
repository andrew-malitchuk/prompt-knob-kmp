package presentation.core.ui.source.kit.molecule.item

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import org.jetbrains.compose.ui.tooling.preview.Preview
import presentation.core.styling.core.Theme
import presentation.core.styling.source.theme.AppTheme
import presentation.core.ui.source.kit.atom.indicator.StatusIndicator
import presentation.core.ui.source.kit.atom.indicator.StatusIndicatorState
import presentation.core.ui.source.kit.atom.shape.SquircleShape

/**
 * A full-width card representing a discovered or paired BLE device.
 *
 * Tapping anywhere on the card invokes [onClick]. The card shows the device [status]
 * as a colored [StatusIndicator], [deviceName] and [deviceId] as identity, and a
 * [metric] label+value pair at the bottom-right.
 *
 * @param deviceName Primary device label (e.g. "prompt-knob").
 * @param deviceId Hardware identifier string (e.g. "20:6E:F1:A1:41:1D").
 * @param status Connection state controlling the [StatusIndicator] dot color.
 * @param metric Supporting metric displayed at the bottom-right (e.g. signal strength).
 * @param onClick Callback invoked when the card is tapped.
 * @param modifier Modifier applied to the root card.
 * @param statusLabel Custom status label override. Defaults to the display name of [status].
 * @param trailingMetricContent Optional composable replacing the default [metric] text at
 *   the bottom-right, e.g. a signal-strength bar graph. When non-null, [metric.value] is
 *   hidden but [metric.label] is still shown.
 *
 * @see DeviceCardMetric
 * @see StatusIndicatorState
 * @see <a href="https://www.figma.com/design/klEUS15vSHsTO53UXCJ7Uo/sketches?node-id=1550-210">Figma</a>
 */
@Composable
public fun DeviceCard(
    deviceName: String,
    deviceId: String,
    status: StatusIndicatorState,
    metric: DeviceCardMetric,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    statusLabel: String? = null,
    trailingMetricContent: @Composable (ColumnScope.() -> Unit)? = null,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(SquircleShape(Theme.spacing.spacingM))
            .background(Theme.color.surface)
            .clickable(onClick = onClick)
            .padding(Theme.spacing.spacingL),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing.spacingM),
    ) {
        // Status indicator
        StatusIndicator(
            state = status,
            label = statusLabel ?: status.defaultDisplayLabel(),
        )

        // Device identity
        Column(verticalArrangement = Arrangement.spacedBy(Theme.spacing.spacingXXS)) {
            Text(
                text = deviceName,
                style = Theme.typography.title,
                color = Theme.color.inkMain,
            )
            Text(
                text = deviceId,
                style = Theme.typography.caption,
                color = Theme.color.inkSubtle,
            )
        }

        // Metric (bottom-right)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = metric.label,
                    style = Theme.typography.label,
                    color = Theme.color.inkSubtle,
                )
                if (trailingMetricContent != null) {
                    trailingMetricContent()
                } else {
                    Text(
                        text = metric.value,
                        style = Theme.typography.title,
                        color = Theme.color.inkMain,
                    )
                }
            }
        }
    }
}

/**
 * A label + value pair shown in the bottom-right of a [DeviceCard].
 *
 * Examples: `DeviceCardMetric("BATTERY", "94%")`, `DeviceCardMetric("SIGNAL", "-42dB")`.
 *
 * @param label Short uppercase descriptor for the value (e.g. "dBm").
 * @param value Human-readable value string (e.g. "-65").
 */
public data class DeviceCardMetric(
    val label: String,
    val value: String,
)

/** Maps [StatusIndicatorState] to its canonical display label for [DeviceCard]. */
private fun StatusIndicatorState.defaultDisplayLabel(): String = when (this) {
    StatusIndicatorState.Connected -> "CONNECTED"
    StatusIndicatorState.Offline -> "OFFLINE"
    StatusIndicatorState.Standby -> "STANDBY"
    StatusIndicatorState.Active -> "ACTIVE"
}

@Preview(showBackground = true)
@Composable
private fun DeviceCardPreview() {
    AppTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Theme.spacing.spacingL),
            verticalArrangement = Arrangement.spacedBy(Theme.spacing.spacingM),
        ) {
            DeviceCard(
                deviceName = "prompt-knob",
                deviceId = "20:6E:F1:A1:41:1D",
                status = StatusIndicatorState.Active,
                metric = DeviceCardMetric(label = "dBm", value = "-65"),
                onClick = {},
            )
            DeviceCard(
                deviceName = "MiKettle",
                deviceId = "B8:7C:6F:A9:02:13",
                status = StatusIndicatorState.Active,
                metric = DeviceCardMetric(label = "dBm", value = "-85"),
                onClick = {},
            )
        }
    }
}
