package presentation.core.ui.source.kit.atom.indicator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview
import presentation.core.styling.core.Theme
import presentation.core.styling.source.theme.AppTheme

/**
 * A compact status indicator composed of a 6 dp filled dot and an uppercase label.
 *
 * The dot color is driven by [state] using semantic design-system palette tokens
 * (success → connected/active, warning → standby, inkSubtle → offline).
 * The [label] defaults to a human-readable name of [state] but can be overridden
 * to display context-specific copy (e.g. "SYSTEM INITIALIZATION").
 *
 * @param state Visual state that controls the dot fill color.
 * @param label Uppercase status copy shown to the right of the dot.
 *   Defaults to the display name of [state].
 * @param modifier Modifier applied to the root [Row].
 *
 * @see StatusIndicatorState
 * @see <a href="https://www.figma.com/design/klEUS15vSHsTO53UXCJ7Uo/sketches?node-id=1550-2">Figma</a>
 */
@Composable
public fun StatusIndicator(
    state: StatusIndicatorState,
    label: String = state.defaultLabel(),
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing.spacingXS),
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(state.dotColor()),
        )
        Text(
            text = label,
            style = Theme.typography.label,
            color = Theme.color.inkSubtle,
        )
    }
}

/**
 * Visual state of a [StatusIndicator], controlling the dot fill color.
 */
public enum class StatusIndicatorState {
    /** Device or system is reachable and fully operational. */
    Connected,

    /** Device or system is unreachable or powered off. */
    Offline,

    /** Device or system is idle and awaiting activation. */
    Standby,

    /** A background process is running (e.g. scanning, searching, initializing). */
    Active,
}

/** Default display label derived from the state name. */
private fun StatusIndicatorState.defaultLabel(): String = when (this) {
    StatusIndicatorState.Connected -> "CONNECTED"
    StatusIndicatorState.Offline -> "OFFLINE"
    StatusIndicatorState.Standby -> "STANDBY"
    StatusIndicatorState.Active -> "ACTIVE"
}

@Composable
private fun StatusIndicatorState.dotColor(): Color = when (this) {
    StatusIndicatorState.Connected -> Theme.color.success
    StatusIndicatorState.Active -> Theme.color.success
    StatusIndicatorState.Standby -> Theme.color.warning
    StatusIndicatorState.Offline -> Theme.color.inkSubtle
}

@Preview(showBackground = true)
@Composable
private fun StatusIndicatorPreview() {
    AppTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            StatusIndicator(state = StatusIndicatorState.Connected)
            StatusIndicator(state = StatusIndicatorState.Offline)
            StatusIndicator(state = StatusIndicatorState.Standby)
            StatusIndicator(state = StatusIndicatorState.Active, label = "SYSTEM INITIALIZATION")
            StatusIndicator(state = StatusIndicatorState.Active, label = "SYSTEM SEARCH ACTIVE")
        }
    }
}
