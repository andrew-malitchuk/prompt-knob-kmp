package presentation.core.ui.source.kit.organism.device

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import presentation.core.styling.core.Theme
import presentation.core.ui.source.kit.atom.button.TacticalButton
import presentation.core.ui.source.kit.atom.indicator.StatusIndicator
import presentation.core.ui.source.kit.atom.indicator.StatusIndicatorState

/**
 * Card showing the device connection state, optional battery level, firmware version,
 * and action buttons for disconnect / forget / sync / firmware update.
 *
 * @param indicatorState Current connection state represented as a [StatusIndicatorState].
 * @param statusSectionLabel Label for the status column (e.g. "STATUS").
 * @param statusLabel Human-readable connection state string.
 * @param batteryLabel Label for the battery column; null hides the battery section entirely.
 * @param batteryValue Formatted battery string (e.g. "94%"); null hides the battery section.
 * @param firmwareLabel Label for the firmware row (e.g. "FIRMWARE").
 * @param firmwareVersion Firmware version string, or "—" if unavailable.
 * @param disconnectLabel Label for the disconnect button.
 * @param forgetLabel Label for the forget-device button.
 * @param forceSyncLabel Label for the force-sync button.
 * @param updateFirmwareLabel Label for the update-firmware button.
 * @param onDisconnect Callback for disconnect tap.
 * @param onForget Callback for forget-device tap.
 * @param onForceSync Callback for force-sync tap.
 * @param onUpdateFirmware Callback for update-firmware tap.
 */
@Composable
public fun StatusBatteryCard(
    indicatorState: StatusIndicatorState,
    statusSectionLabel: String,
    statusLabel: String,
    batteryLabel: String?,
    batteryValue: String?,
    firmwareLabel: String,
    firmwareVersion: String,
    disconnectLabel: String,
    forgetLabel: String,
    forceSyncLabel: String,
    updateFirmwareLabel: String,
    onDisconnect: () -> Unit,
    onForget: () -> Unit,
    onForceSync: () -> Unit,
    onUpdateFirmware: () -> Unit,
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
            .padding(Theme.spacing.spacingL),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(Theme.spacing.spacingM),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(Theme.spacing.spacingXXS)) {
                    Text(
                        text = statusSectionLabel,
                        style = Theme.typography.label,
                        color = Theme.color.inkSubtle,
                    )
                    StatusIndicator(
                        state = indicatorState,
                        label = statusLabel,
                    )
                }

                if (batteryLabel != null && batteryValue != null) {
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(Theme.spacing.spacingXXS),
                    ) {
                        Text(
                            text = batteryLabel,
                            style = Theme.typography.label,
                            color = Theme.color.inkSubtle,
                        )
                        Text(
                            text = batteryValue,
                            style = Theme.typography.display,
                            color = Theme.color.inkMain,
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = firmwareLabel,
                    style = Theme.typography.label,
                    color = Theme.color.inkSubtle,
                )
                Text(
                    text = firmwareVersion,
                    style = Theme.typography.body,
                    color = Theme.color.inkMain,
                )
            }

            TacticalButton(text = disconnectLabel, onClick = onDisconnect)
            TacticalButton(text = forgetLabel, onClick = onForget)
            TacticalButton(text = forceSyncLabel, onClick = onForceSync)
            TacticalButton(text = updateFirmwareLabel, onClick = onUpdateFirmware)
        }
    }
}
