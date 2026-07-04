package presentation.core.ui.source.kit.molecule.ble

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import presentation.core.styling.core.Theme
import presentation.core.ui.source.kit.atom.button.TacticalButton
import presentation.core.ui.source.kit.atom.container.IconContainer
import presentation.core.ui.source.kit.atom.decoration.CornerFrame
import presentation.core.ui.source.kit.atom.icon.Wifi
import presentation.core.ui.source.kit.atom.indicator.StatusIndicator
import presentation.core.ui.source.kit.atom.indicator.StatusIndicatorState

/**
 * Scrollable screen content shown when BLE permissions have not been granted.
 *
 * @param title Screen title text.
 * @param statusLabel Label shown next to the offline [StatusIndicator].
 * @param body Explanatory body text shown in the permission rationale card.
 * @param ctaLabel Label for the permission-grant button.
 * @param onGrantPermission Callback invoked when the user taps the CTA.
 * @param listState Scroll state forwarded from the parent for divider visibility.
 */
@Composable
public fun PermissionRequestContent(
    title: String,
    statusLabel: String,
    body: String,
    ctaLabel: String,
    onGrantPermission: () -> Unit,
    listState: LazyListState = rememberLazyListState(),
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(Theme.spacing.spacingL),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing.spacingL),
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(Theme.spacing.spacingXS)) {
                Text(
                    text = title,
                    style = Theme.typography.display,
                    color = Theme.color.inkMain,
                )
                StatusIndicator(
                    state = StatusIndicatorState.Offline,
                    label = statusLabel,
                )
            }
        }

        item {
            CornerFrame(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Theme.spacing.spacing2XL),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(Theme.spacing.spacingL),
                ) {
                    IconContainer(
                        icon = Wifi,
                        backgroundColor = Theme.color.surfaceVariant,
                        foregroundColor = Theme.color.inkSubtle,
                        modifier = Modifier.size(Theme.size.icon4XL),
                    )
                    Text(
                        text = body,
                        style = Theme.typography.body,
                        color = Theme.color.inkSubtle,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }

        item {
            TacticalButton(
                text = ctaLabel,
                onClick = onGrantPermission,
            )
        }
    }
}
