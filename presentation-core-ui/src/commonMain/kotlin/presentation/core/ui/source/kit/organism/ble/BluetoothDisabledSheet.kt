package presentation.core.ui.source.kit.organism.ble

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import presentation.core.styling.core.Theme
import presentation.core.ui.source.kit.atom.button.TacticalButton
import presentation.core.ui.source.kit.organism.bottomsheet.AppBottomSheet

/**
 * Non-dismissible bottom sheet shown when the Bluetooth adapter is turned off.
 *
 * The user must tap the [settingsLabel] CTA to open platform Bluetooth settings.
 *
 * @param title Sheet title.
 * @param body Explanatory body text.
 * @param settingsLabel Label for the open-settings button.
 * @param onOpenSettings Callback invoked when the CTA is tapped.
 */
@Composable
public fun BluetoothDisabledSheet(
    title: String,
    body: String,
    settingsLabel: String,
    onOpenSettings: () -> Unit,
) {
    AppBottomSheet(
        onDismiss = { /* non-dismissible — user must act */ },
        title = title,
    ) {
        Text(
            text = body,
            style = Theme.typography.body,
            color = Theme.color.inkSubtle,
        )
        Spacer(modifier = Modifier.height(Theme.spacing.spacingL))
        TacticalButton(
            text = settingsLabel,
            onClick = onOpenSettings,
        )
    }
}
