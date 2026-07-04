package presentation.feature.device.source.device

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import domain.core.source.model.ConnectionStateModel
import domain.core.source.model.OtaErrorCodeModel
import domain.core.source.model.OtaStateModel
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import presentation.core.styling.core.Theme
import presentation.core.styling.source.theme.AppTheme
import presentation.core.ui.source.icon.resolveIcon
import presentation.core.ui.source.kit.atom.button.ButtonSizeType
import presentation.core.ui.source.kit.atom.button.IconButton
import presentation.core.ui.source.kit.atom.button.TacticalButton
import presentation.core.ui.source.kit.atom.icon.Plus
import presentation.core.ui.source.kit.atom.icon.Settings
import presentation.core.ui.source.kit.atom.indicator.StatusIndicatorState
import presentation.core.ui.source.kit.atom.snackbar.StackedSnackbarHost
import presentation.core.ui.source.kit.atom.snackbar.StackedSnakbarHostState
import presentation.core.ui.source.kit.atom.snackbar.rememberStackedSnackbarHostState
import presentation.core.ui.source.kit.molecule.device.DeviceHeader
import presentation.core.ui.source.kit.molecule.device.ProfileLoadSection
import presentation.core.ui.source.kit.molecule.header.ScreenHeader
import presentation.core.ui.source.kit.organism.device.HistoryEntry
import presentation.core.ui.source.kit.organism.device.HistorySection
import presentation.core.ui.source.kit.organism.device.MacroKeyItem
import presentation.core.ui.source.kit.organism.device.MacroSection
import presentation.core.ui.source.kit.organism.device.OtaOverlay
import presentation.core.ui.source.kit.organism.device.StatusBatteryCard
import prompt_knob_kmp.presentation_core_localisation.generated.resources.Res
import prompt_knob_kmp.presentation_core_localisation.generated.resources.device_connected
import prompt_knob_kmp.presentation_core_localisation.generated.resources.device_connecting
import prompt_knob_kmp.presentation_core_localisation.generated.resources.device_detail_active_macros
import prompt_knob_kmp.presentation_core_localisation.generated.resources.device_detail_add_macro
import prompt_knob_kmp.presentation_core_localisation.generated.resources.device_detail_battery
import prompt_knob_kmp.presentation_core_localisation.generated.resources.device_detail_current_hardware
import prompt_knob_kmp.presentation_core_localisation.generated.resources.device_detail_disconnect
import prompt_knob_kmp.presentation_core_localisation.generated.resources.device_detail_force_sync
import prompt_knob_kmp.presentation_core_localisation.generated.resources.device_detail_forget
import prompt_knob_kmp.presentation_core_localisation.generated.resources.device_detail_firmware
import prompt_knob_kmp.presentation_core_localisation.generated.resources.device_detail_no_macros
import prompt_knob_kmp.presentation_core_localisation.generated.resources.device_detail_profile_load
import prompt_knob_kmp.presentation_core_localisation.generated.resources.device_detail_status
import prompt_knob_kmp.presentation_core_localisation.generated.resources.device_detail_update_firmware
import prompt_knob_kmp.presentation_core_localisation.generated.resources.device_disconnected
import prompt_knob_kmp.presentation_core_localisation.generated.resources.device_disconnecting
import prompt_knob_kmp.presentation_core_localisation.generated.resources.device_unknown_device
import prompt_knob_kmp.presentation_core_localisation.generated.resources.home_action_presets
import prompt_knob_kmp.presentation_core_localisation.generated.resources.history_clear
import prompt_knob_kmp.presentation_core_localisation.generated.resources.history_empty
import prompt_knob_kmp.presentation_core_localisation.generated.resources.history_item_failure
import prompt_knob_kmp.presentation_core_localisation.generated.resources.history_item_success
import prompt_knob_kmp.presentation_core_localisation.generated.resources.history_title
import prompt_knob_kmp.presentation_core_localisation.generated.resources.ota_abort
import prompt_knob_kmp.presentation_core_localisation.generated.resources.ota_done
import prompt_knob_kmp.presentation_core_localisation.generated.resources.ota_done_subtitle
import prompt_knob_kmp.presentation_core_localisation.generated.resources.ota_err_bad_begin
import prompt_knob_kmp.presentation_core_localisation.generated.resources.ota_err_battery
import prompt_knob_kmp.presentation_core_localisation.generated.resources.ota_err_crc
import prompt_knob_kmp.presentation_core_localisation.generated.resources.ota_err_disconnected
import prompt_knob_kmp.presentation_core_localisation.generated.resources.ota_err_flash
import prompt_knob_kmp.presentation_core_localisation.generated.resources.ota_err_subtitle
import prompt_knob_kmp.presentation_core_localisation.generated.resources.ota_err_too_large
import prompt_knob_kmp.presentation_core_localisation.generated.resources.ota_receiving
import prompt_knob_kmp.presentation_core_localisation.generated.resources.ota_verifying
import prompt_knob_kmp.presentation_core_localisation.generated.resources.ota_waiting

/**
 * Stateless rendering composable for the Device detail / dashboard screen.
 *
 * @param state Current UI state produced by [DeviceDetailViewModel].
 * @param onIntent Callback for dispatching [DeviceDetailIntent] user actions to the ViewModel.
 * @param modifier Modifier applied to the root container.
 *
 * @see DeviceDetailViewModel
 * @see DeviceDetailScreen
 */
@Composable
public fun DeviceDetailContent(
    state: DeviceDetailState,
    onIntent: (DeviceDetailIntent) -> Unit,
    snackbarHostState: StackedSnakbarHostState,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Theme.color.canvas),
        ) {
            val listState = rememberLazyListState()
            ScreenHeader(
                onBackClick = { onIntent(DeviceDetailIntent.OnBackClick) },
                showDivider = listState.canScrollBackward,
                trailingContent = {
                    IconButton(
                        icon = Settings,
                        onClick = { onIntent(DeviceDetailIntent.OnSettingsClick) },
                        size = ButtonSizeType.Medium,
                    )
                },
            )
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(Theme.spacing.spacingL),
                verticalArrangement = Arrangement.spacedBy(Theme.spacing.spacingL),
            ) {
                item {
                    DeviceHeader(
                        hardwareLabel = stringResource(Res.string.device_detail_current_hardware),
                        deviceName = state.deviceName.ifBlank {
                            stringResource(Res.string.device_unknown_device)
                        },
                    )
                }

                item {
                    val indicatorState = when (state.connectionState) {
                        ConnectionStateModel.Connected -> StatusIndicatorState.Connected
                        ConnectionStateModel.Connecting -> StatusIndicatorState.Active
                        else -> StatusIndicatorState.Offline
                    }
                    val statusLabel = when (state.connectionState) {
                        ConnectionStateModel.Connected -> stringResource(Res.string.device_connected)
                        ConnectionStateModel.Connecting -> stringResource(Res.string.device_connecting)
                        ConnectionStateModel.Disconnecting -> stringResource(Res.string.device_disconnecting)
                        ConnectionStateModel.Disconnected -> stringResource(Res.string.device_disconnected)
                    }
                    StatusBatteryCard(
                        indicatorState = indicatorState,
                        statusSectionLabel = stringResource(Res.string.device_detail_status),
                        statusLabel = statusLabel,
                        batteryLabel = state.batteryPercentage?.let {
                            stringResource(Res.string.device_detail_battery)
                        },
                        batteryValue = state.batteryPercentage?.let { "$it%" },
                        firmwareLabel = stringResource(Res.string.device_detail_firmware),
                        firmwareVersion = state.firmwareVersion ?: "—",
                        disconnectLabel = stringResource(Res.string.device_detail_disconnect),
                        forgetLabel = stringResource(Res.string.device_detail_forget),
                        forceSyncLabel = stringResource(Res.string.device_detail_force_sync),
                        updateFirmwareLabel = stringResource(Res.string.device_detail_update_firmware),
                        onDisconnect = { onIntent(DeviceDetailIntent.OnDisconnect) },
                        onForget = { onIntent(DeviceDetailIntent.OnForgetDevice) },
                        onForceSync = { onIntent(DeviceDetailIntent.OnForceSync) },
                        onUpdateFirmware = { onIntent(DeviceDetailIntent.OnUpdateFirmwareClick) },
                    )
                }

                item {
                    MacroSection(
                        sectionLabel = stringResource(Res.string.device_detail_active_macros),
                        noMacrosLabel = stringResource(Res.string.device_detail_no_macros),
                        macroKeys = state.macroKeys.map { macro ->
                            MacroKeyItem(
                                id = macro.commandId,
                                slotCode = macro.slotCode,
                                macroName = macro.macroName,
                                icon = resolveIcon(macro.icon),
                            )
                        },
                        onMacroClick = { id -> onIntent(DeviceDetailIntent.OnMacroKeyClick(id)) },
                    )
                }

                if (state.profileLoadPercentage != null) {
                    item {
                        ProfileLoadSection(
                            label = stringResource(Res.string.device_detail_profile_load),
                            valueLabel = "${state.profileLoadPercentage}%",
                            percentage = state.profileLoadPercentage,
                        )
                    }
                }

                item {
                    TacticalButton(
                        text = stringResource(Res.string.device_detail_add_macro),
                        onClick = { onIntent(DeviceDetailIntent.OnAddMacroClick) },
                        icon = Plus,
                    )
                }

                item {
                    TacticalButton(
                        text = stringResource(Res.string.home_action_presets),
                        onClick = { onIntent(DeviceDetailIntent.OnPresetsClick) },
                    )
                }

                item {
                    HistorySection(
                        titleLabel = stringResource(Res.string.history_title),
                        clearLabel = stringResource(Res.string.history_clear),
                        emptyLabel = stringResource(Res.string.history_empty),
                        successLabel = stringResource(Res.string.history_item_success),
                        failureLabel = stringResource(Res.string.history_item_failure),
                        entries = state.commandHistory.map { entry ->
                            HistoryEntry(
                                commandLabel = entry.commandLabel,
                                isSuccess = entry.isSuccess,
                            )
                        },
                        onClear = { onIntent(DeviceDetailIntent.OnClearHistory) },
                    )
                }
            }
        }

        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
            StackedSnackbarHost(hostState = snackbarHostState)
        }

        AnimatedVisibility(
            visible = state.otaState != null,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            state.otaState?.let { otaState ->
                val title: String
                val subtitle: String?
                val showProgress: Boolean
                val progressValue: Float
                val showCancel: Boolean

                when (otaState) {
                    is OtaStateModel.Waiting -> {
                        title = stringResource(Res.string.ota_waiting)
                        subtitle = null
                        showProgress = false
                        progressValue = 0f
                        showCancel = true
                    }
                    is OtaStateModel.Receiving -> {
                        title = stringResource(Res.string.ota_receiving)
                        subtitle = "${otaState.percentage}%"
                        showProgress = true
                        progressValue = otaState.percentage / 100f
                        showCancel = true
                    }
                    is OtaStateModel.Verifying -> {
                        title = stringResource(Res.string.ota_verifying)
                        subtitle = null
                        showProgress = false
                        progressValue = 1f
                        showCancel = false
                    }
                    is OtaStateModel.Done -> {
                        title = stringResource(Res.string.ota_done)
                        subtitle = stringResource(Res.string.ota_done_subtitle)
                        showProgress = false
                        progressValue = 1f
                        showCancel = false
                    }
                    is OtaStateModel.Error -> {
                        title = stringResource(otaState.code.toStringRes())
                        subtitle = stringResource(Res.string.ota_err_subtitle)
                        showProgress = false
                        progressValue = 0f
                        showCancel = true
                    }
                }

                OtaOverlay(
                    title = title,
                    subtitle = subtitle,
                    showProgress = showProgress,
                    progressValue = progressValue,
                    showCancel = showCancel,
                    abortLabel = stringResource(Res.string.ota_abort),
                    onAbort = { onIntent(DeviceDetailIntent.OnAbortOta) },
                )
            }
        }
    }
}

private fun OtaErrorCodeModel.toStringRes() = when (this) {
    OtaErrorCodeModel.CRC -> Res.string.ota_err_crc
    OtaErrorCodeModel.FLASH -> Res.string.ota_err_flash
    OtaErrorCodeModel.TOO_LARGE -> Res.string.ota_err_too_large
    OtaErrorCodeModel.BATTERY -> Res.string.ota_err_battery
    OtaErrorCodeModel.BAD_BEGIN -> Res.string.ota_err_bad_begin
    OtaErrorCodeModel.DISCONNECTED -> Res.string.ota_err_disconnected
}

@Preview(showBackground = true)
@Composable
private fun DeviceDetailContentPreview() {
    AppTheme {
        DeviceDetailContent(
            state = DeviceDetailState(
                isLoading = false,
                deviceName = "PROMPT-KNOB",
                deviceAddress = "20:6E:F1:A1:41:1D",
                connectionState = ConnectionStateModel.Connected,
                batteryPercentage = 94,
                macroKeys = listOf(
                    MacroKeyUiModel(slotCode = "C01", macroName = "MUTE", commandId = 1),
                    MacroKeyUiModel(slotCode = "C02", macroName = "MEDIA", commandId = 2),
                    MacroKeyUiModel(slotCode = "C03", macroName = "BRIGHT", commandId = 3),
                    MacroKeyUiModel(slotCode = "C04", macroName = "SETTINGS", commandId = 4),
                ),
            ),
            onIntent = {},
            snackbarHostState = rememberStackedSnackbarHostState(),
        )
    }
}
