package presentation.core.ui.source.kit.organism.ble

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import presentation.core.styling.core.Theme
import presentation.core.ui.source.kit.atom.ble.SignalBars
import presentation.core.ui.source.kit.atom.indicator.StatusIndicator
import presentation.core.ui.source.kit.atom.indicator.StatusIndicatorState
import presentation.core.ui.source.kit.molecule.item.DeviceCard
import presentation.core.ui.source.kit.molecule.item.DeviceCardMetric

/**
 * A discovered BLE device entry for display in [BleDeviceContent].
 *
 * @param address Hardware MAC/UUID address used as a stable list key.
 * @param name Advertised device name, or null if unknown.
 * @param rssi Received signal strength in dBm; [Int.MIN_VALUE] if unavailable.
 */
public data class BleDiscoveredDevice(
    val address: String,
    val name: String?,
    val rssi: Int,
)

/**
 * Scrollable screen body shown when BLE is available and permissions are granted.
 *
 * Renders a header with a [StatusIndicator], the [scanStatusCard] slot, an optional
 * [scanTimeoutContent] slot, and the list of [discoveredDevices].
 *
 * @param title Screen title text.
 * @param indicatorState Current indicator state derived from connection/loading state.
 * @param indicatorLabel Label shown next to the status indicator.
 * @param isLoading True while a scan or connection is in progress.
 * @param scanTimedOut True when the scan completed without finding any device.
 * @param discoveredDevices List of nearby BLE devices found during the current scan.
 * @param discoveredUnitsLabel Section header label for the discovered-device list.
 * @param devicesFoundLabel Formatted string showing the device count (e.g. "02 DEVICES FOUND").
 * @param unknownDeviceLabel Fallback name for devices with no advertised name.
 * @param signalLabel Metric label shown on each [DeviceCard] (e.g. "SIGNAL").
 * @param scanStatusCard Slot composable rendering the current scan/connection status card.
 * @param scanTimeoutContent Slot composable shown when [scanTimedOut] is true and not loading.
 * @param onDeviceTapped Callback invoked with the device [BleDiscoveredDevice.address] on tap.
 * @param listState Scroll state forwarded from the parent for divider visibility.
 */
@Composable
public fun BleDeviceContent(
    title: String,
    indicatorState: StatusIndicatorState,
    indicatorLabel: String,
    isLoading: Boolean,
    scanTimedOut: Boolean,
    discoveredDevices: List<BleDiscoveredDevice>,
    discoveredUnitsLabel: String,
    devicesFoundLabel: String,
    unknownDeviceLabel: String,
    signalLabel: String,
    scanStatusCard: @Composable () -> Unit,
    scanTimeoutContent: @Composable () -> Unit,
    onDeviceTapped: (address: String) -> Unit,
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
                    state = indicatorState,
                    label = indicatorLabel,
                )
            }
        }

        item { scanStatusCard() }

        if (scanTimedOut && !isLoading) {
            item { scanTimeoutContent() }
        }

        if (discoveredDevices.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = discoveredUnitsLabel,
                        style = Theme.typography.label,
                        color = Theme.color.inkSubtle,
                    )
                    Text(
                        text = devicesFoundLabel,
                        style = Theme.typography.label,
                        color = Theme.color.inkSubtle,
                    )
                }
            }

            items(discoveredDevices, key = { it.address }) { device ->
                DeviceCard(
                    deviceName = device.name ?: unknownDeviceLabel,
                    deviceId = device.address,
                    status = StatusIndicatorState.Active,
                    metric = DeviceCardMetric(label = signalLabel, value = ""),
                    onClick = { onDeviceTapped(device.address) },
                    trailingMetricContent = { SignalBars(rssi = device.rssi) },
                )
            }
        }
    }
}
