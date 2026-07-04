package presentation.feature.devices.source.devices

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import domain.core.source.model.ConnectionStateModel
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import presentation.core.styling.core.Theme
import presentation.core.styling.source.theme.AppTheme
import presentation.core.ui.source.kit.atom.button.ButtonSizeType
import presentation.core.ui.source.kit.atom.button.IconButton
import presentation.core.ui.source.kit.atom.icon.Settings
import presentation.core.ui.source.kit.atom.indicator.StatusIndicatorState
import presentation.core.ui.source.kit.atom.snackbar.StackedSnackbarHost
import presentation.core.ui.source.kit.atom.snackbar.StackedSnakbarHostState
import presentation.core.ui.source.kit.atom.snackbar.rememberStackedSnackbarHostState
import presentation.core.ui.source.kit.molecule.ble.BleUnavailableContent
import presentation.core.ui.source.kit.molecule.ble.PermissionRequestContent
import presentation.core.ui.source.kit.molecule.ble.ScanStatusCard
import presentation.core.ui.source.kit.molecule.ble.ScanTimeoutContent
import presentation.core.ui.source.kit.molecule.header.ScreenHeader
import presentation.core.ui.source.kit.organism.ble.BleDeviceContent
import presentation.core.ui.source.kit.organism.ble.BleDiscoveredDevice
import presentation.core.ui.source.kit.organism.ble.BluetoothDisabledSheet
import prompt_knob_kmp.presentation_core_localisation.generated.resources.Res
import prompt_knob_kmp.presentation_core_localisation.generated.resources.device_ble_unavailable
import prompt_knob_kmp.presentation_core_localisation.generated.resources.device_bluetooth_disabled_body
import prompt_knob_kmp.presentation_core_localisation.generated.resources.device_bluetooth_disabled_title
import prompt_knob_kmp.presentation_core_localisation.generated.resources.device_bluetooth_open_settings
import prompt_knob_kmp.presentation_core_localisation.generated.resources.device_connected
import prompt_knob_kmp.presentation_core_localisation.generated.resources.device_connecting
import prompt_knob_kmp.presentation_core_localisation.generated.resources.device_devices_found
import prompt_knob_kmp.presentation_core_localisation.generated.resources.device_disconnect
import prompt_knob_kmp.presentation_core_localisation.generated.resources.device_disconnected
import prompt_knob_kmp.presentation_core_localisation.generated.resources.device_disconnecting
import prompt_knob_kmp.presentation_core_localisation.generated.resources.device_discovered_units
import prompt_knob_kmp.presentation_core_localisation.generated.resources.device_frequency_label
import prompt_knob_kmp.presentation_core_localisation.generated.resources.device_grant_permission
import prompt_knob_kmp.presentation_core_localisation.generated.resources.device_permission_body
import prompt_knob_kmp.presentation_core_localisation.generated.resources.device_permission_title
import prompt_knob_kmp.presentation_core_localisation.generated.resources.device_scan_retry
import prompt_knob_kmp.presentation_core_localisation.generated.resources.device_scan_timeout_hint_power
import prompt_knob_kmp.presentation_core_localisation.generated.resources.device_scan_timeout_hint_range
import prompt_knob_kmp.presentation_core_localisation.generated.resources.device_scan_timeout_hint_restart
import prompt_knob_kmp.presentation_core_localisation.generated.resources.device_scan_timeout_title
import prompt_knob_kmp.presentation_core_localisation.generated.resources.device_scanning
import prompt_knob_kmp.presentation_core_localisation.generated.resources.device_signal_label
import prompt_knob_kmp.presentation_core_localisation.generated.resources.device_status_active
import prompt_knob_kmp.presentation_core_localisation.generated.resources.device_status_connected
import prompt_knob_kmp.presentation_core_localisation.generated.resources.device_status_scanning
import prompt_knob_kmp.presentation_core_localisation.generated.resources.device_status_standby
import prompt_knob_kmp.presentation_core_localisation.generated.resources.device_title
import prompt_knob_kmp.presentation_core_localisation.generated.resources.device_unknown_device

/**
 * Stateless rendering composable for the Devices screen.
 *
 * Branches on three mutually exclusive gate states:
 * - BLE unavailable (Desktop JVM): shows an error card with an [Offline][StatusIndicatorState.Offline] indicator.
 * - Permissions not granted: shows a permission-request prompt with a [TacticalButton] CTA.
 * - Ready: shows the full scan / connect / device-list flow using the Prompt Knob design language.
 *
 * All strings are sourced from `presentation-core-localisation`; all colors, typography,
 * and spacing from `presentation-core-styling`. No raw Material3 tokens are used.
 *
 * @param state Current UI state produced by [DevicesViewModel].
 * @param onIntent Callback for dispatching [DevicesIntent] user actions back to the ViewModel.
 * @param modifier Modifier to be applied to the root [Box] container.
 *
 * @see DevicesViewModel
 * @see DevicesScreen
 */
@Composable
public fun DevicesContent(
    state: DevicesState,
    onIntent: (DevicesIntent) -> Unit,
    snackbarHostState: StackedSnakbarHostState,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Theme.color.canvas),
        ) {
            ScreenHeader(
                onBackClick = { onIntent(DevicesIntent.OnBackClick) },
                showDivider = listState.canScrollBackward,
                trailingContent = {
                    IconButton(
                        icon = Settings,
                        onClick = { onIntent(DevicesIntent.OnSettingsClick) },
                        size = ButtonSizeType.Medium,
                    )
                },
            )
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.TopStart,
            ) {
                when {
                    !state.isBleAvailable -> BleUnavailableContent(
                        message = stringResource(Res.string.device_ble_unavailable),
                    )
                    !state.hasPermissions -> PermissionRequestContent(
                        title = stringResource(Res.string.device_title),
                        statusLabel = stringResource(Res.string.device_permission_title),
                        body = stringResource(Res.string.device_permission_body),
                        ctaLabel = stringResource(Res.string.device_grant_permission),
                        onGrantPermission = { onIntent(DevicesIntent.StartScan) },
                        listState = listState,
                    )
                    else -> {
                        val indicatorState = when {
                            state.connectionState == ConnectionStateModel.Connected -> StatusIndicatorState.Connected
                            state.isLoading -> StatusIndicatorState.Active
                            else -> StatusIndicatorState.Standby
                        }
                        val indicatorLabel = when {
                            state.connectionState == ConnectionStateModel.Connected ->
                                stringResource(Res.string.device_status_connected)
                            state.isLoading ->
                                stringResource(Res.string.device_status_active)
                            else ->
                                stringResource(Res.string.device_status_standby)
                        }
                        val cardText = when (state.connectionState) {
                            ConnectionStateModel.Connected ->
                                state.connectedDevice?.name
                                    ?: state.connectedDevice?.address
                                    ?: stringResource(Res.string.device_connected)
                            ConnectionStateModel.Connecting ->
                                stringResource(Res.string.device_connecting)
                            ConnectionStateModel.Disconnecting ->
                                stringResource(Res.string.device_disconnecting)
                            ConnectionStateModel.Disconnected ->
                                if (state.isLoading) stringResource(Res.string.device_scanning)
                                else stringResource(Res.string.device_disconnected)
                        }
                        val cardStatusLabel = when {
                            state.connectionState == ConnectionStateModel.Connected ->
                                stringResource(Res.string.device_status_connected)
                            state.isLoading ->
                                stringResource(Res.string.device_status_scanning)
                            else ->
                                stringResource(Res.string.device_status_standby)
                        }

                        BleDeviceContent(
                            title = stringResource(Res.string.device_title),
                            indicatorState = indicatorState,
                            indicatorLabel = indicatorLabel,
                            isLoading = state.isLoading,
                            scanTimedOut = state.scanTimedOut,
                            discoveredDevices = state.discoveredDevices.map {
                                BleDiscoveredDevice(
                                    address = it.address,
                                    name = it.name,
                                    rssi = it.rssi,
                                )
                            },
                            discoveredUnitsLabel = stringResource(Res.string.device_discovered_units),
                            devicesFoundLabel = stringResource(
                                Res.string.device_devices_found,
                                state.discoveredDevices.size,
                            ),
                            unknownDeviceLabel = stringResource(Res.string.device_unknown_device),
                            signalLabel = stringResource(Res.string.device_signal_label),
                            scanStatusCard = {
                                ScanStatusCard(
                                    scanningLabel = cardStatusLabel,
                                    cardText = cardText,
                                    isLoading = state.isLoading,
                                    isConnected = state.connectionState == ConnectionStateModel.Connected,
                                    frequencyLabel = stringResource(Res.string.device_frequency_label),
                                    disconnectLabel = stringResource(Res.string.device_disconnect),
                                    onDisconnect = { onIntent(DevicesIntent.Disconnect) },
                                )
                            },
                            scanTimeoutContent = {
                                ScanTimeoutContent(
                                    title = stringResource(Res.string.device_scan_timeout_title),
                                    hints = listOf(
                                        stringResource(Res.string.device_scan_timeout_hint_power),
                                        stringResource(Res.string.device_scan_timeout_hint_range),
                                        stringResource(Res.string.device_scan_timeout_hint_restart),
                                    ),
                                    retryLabel = stringResource(Res.string.device_scan_retry),
                                    onRetry = { onIntent(DevicesIntent.OnRetryScan) },
                                )
                            },
                            onDeviceTapped = { address -> onIntent(DevicesIntent.OnDeviceTapped(address)) },
                            listState = listState,
                        )
                    }
                }
            }
        }
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
            StackedSnackbarHost(hostState = snackbarHostState)
        }
    }

    if (state.isBluetoothDisabled) {
        BluetoothDisabledSheet(
            title = stringResource(Res.string.device_bluetooth_disabled_title),
            body = stringResource(Res.string.device_bluetooth_disabled_body),
            settingsLabel = stringResource(Res.string.device_bluetooth_open_settings),
            onOpenSettings = { onIntent(DevicesIntent.OnOpenBluetoothSettings) },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DevicesContentScanningPreview() {
    AppTheme {
        DevicesContent(
            state = DevicesState(
                isLoading = true,
                hasPermissions = true,
                isBleAvailable = true,
                connectionState = ConnectionStateModel.Disconnected,
            ),
            onIntent = {},
            snackbarHostState = rememberStackedSnackbarHostState(),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DevicesContentPermissionPreview() {
    AppTheme {
        DevicesContent(
            state = DevicesState(
                hasPermissions = false,
                isBleAvailable = true,
            ),
            onIntent = {},
            snackbarHostState = rememberStackedSnackbarHostState(),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DevicesContentBleUnavailablePreview() {
    AppTheme {
        DevicesContent(
            state = DevicesState(isBleAvailable = false),
            onIntent = {},
            snackbarHostState = rememberStackedSnackbarHostState(),
        )
    }
}
