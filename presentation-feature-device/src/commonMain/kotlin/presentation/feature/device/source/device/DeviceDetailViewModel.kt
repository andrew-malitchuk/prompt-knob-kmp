package presentation.feature.device.source.device

import androidx.lifecycle.ViewModel
import domain.core.source.model.ConnectionStateModel
import domain.core.source.model.FirmwareVersionModel
import domain.core.source.model.OtaErrorCodeModel
import domain.core.source.model.OtaStateModel
import domain.usecase.api.source.usecase.ble.DisconnectDeviceUseCase
import domain.usecase.api.source.usecase.ble.ForgetDeviceUseCase
import domain.usecase.api.source.usecase.ble.ObserveBatteryLevelUseCase
import domain.usecase.api.source.usecase.ble.ObserveConnectionStateUseCase
import domain.usecase.api.source.usecase.ble.ObserveFirmwareVersionUseCase
import domain.usecase.api.source.usecase.ble.ObserveSelectedCommandUseCase
import domain.usecase.api.source.usecase.ble.StartOtaUseCase
import domain.usecase.api.source.usecase.ble.SyncCommandsUseCase
import domain.usecase.api.source.usecase.command.ObserveCommandsUseCase
import domain.usecase.api.source.usecase.history.ClearCommandHistoryUseCase
import domain.usecase.api.source.usecase.history.ObserveCommandHistoryUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.annotation.OrbitExperimental
import org.orbitmvi.orbit.viewmodel.container

/**
 * ViewModel for the Device detail / dashboard screen.
 *
 * On entry, observes the current BLE connection state and the local command list.
 * If the BLE connection drops, [DeviceDetailSideEffect.NavigateBack] is emitted.
 *
 * @param observeConnectionState Use case for observing the active BLE connection.
 * @param observeSelectedCommand Use case for observing commands selected on the physical knob.
 * @param disconnect Use case for disconnecting from the current device.
 * @param observeCommands Use case for observing persisted commands from the local database.
 */
@OrbitExperimental
public class DeviceDetailViewModel(
    private val observeConnectionState: ObserveConnectionStateUseCase,
    private val observeSelectedCommand: ObserveSelectedCommandUseCase,
    private val disconnect: DisconnectDeviceUseCase,
    private val forgetDevice: ForgetDeviceUseCase,
    private val observeCommands: ObserveCommandsUseCase,
    private val syncCommands: SyncCommandsUseCase,
    private val observeCommandHistory: ObserveCommandHistoryUseCase,
    private val clearCommandHistory: ClearCommandHistoryUseCase,
    private val observeFirmwareVersion: ObserveFirmwareVersionUseCase,
    private val observeBatteryLevel: ObserveBatteryLevelUseCase,
    private val startOta: StartOtaUseCase,
) : ContainerHost<DeviceDetailState, DeviceDetailSideEffect>, ViewModel() {

    private var otaJob: Job? = null

    override val container: Container<DeviceDetailState, DeviceDetailSideEffect> =
        container(DeviceDetailState()) {
            observeConnection()
            observeSelectedCommands()
            observeMacroKeys()
            observeHistory()
            observeFirmware()
            observeBattery()
        }

    /**
     * Dispatches a [DeviceDetailIntent] to the appropriate internal handler.
     *
     * @param intent User action from the UI layer.
     */
    public fun handleIntent(intent: DeviceDetailIntent) {
        when (intent) {
            DeviceDetailIntent.OnBackClick -> onBackClick()
            DeviceDetailIntent.OnDisconnect -> onDisconnect()
            is DeviceDetailIntent.OnMacroKeyClick -> onMacroKeyClick(intent.commandId)
            DeviceDetailIntent.OnAddMacroClick -> onAddMacroClick()
            DeviceDetailIntent.OnSettingsClick -> onSettingsClick()
            DeviceDetailIntent.OnForgetDevice -> onForgetDevice()
            DeviceDetailIntent.OnForceSync -> onForceSync()
            DeviceDetailIntent.OnClearHistory -> onClearHistory()
            DeviceDetailIntent.OnUpdateFirmwareClick -> onUpdateFirmwareClick()
            is DeviceDetailIntent.OnFirmwareFileSelected -> onFirmwareFileSelected(intent.firmware, intent.version)
            DeviceDetailIntent.OnAbortOta -> onAbortOta()
            DeviceDetailIntent.OnPresetsClick -> onPresetsClick()
        }
    }


    private fun observeConnection() = intent {
        observeConnectionState()
            .onEach { model ->
                reduce {
                    state.copy(
                        isLoading = false,
                        deviceName = model.device?.name ?: "",
                        deviceAddress = model.device?.address ?: "",
                        connectionState = model.state,
                    )
                }
                if (model.state == ConnectionStateModel.Disconnected) {
                    postSideEffect(DeviceDetailSideEffect.NavigateToDevices)
                }
            }
            .catch { t ->
                reduce { state.copy(isLoading = false) }
                postSideEffect(DeviceDetailSideEffect.ShowError(t.message ?: "Connection error"))
            }
            .collect {}
    }

    private fun observeSelectedCommands() = intent {
        observeSelectedCommand()
            .onEach { command ->
                reduce { state.copy(lastSelectedCommand = command) }
            }
            .catch { }
            .collect {}
    }

    private fun observeHistory() = intent {
        observeCommandHistory()
            .onEach { entries ->
                reduce { state.copy(commandHistory = entries) }
            }
            .catch { }
            .collect {}
    }

    private fun observeFirmware() = intent {
        observeFirmwareVersion()
            .onEach { fw -> reduce { state.copy(firmwareVersion = fw?.toString()) } }
            .catch { }
            .collect {}
    }

    private fun observeBattery() = intent {
        observeBatteryLevel()
            .onEach { pct -> reduce { state.copy(batteryPercentage = pct) } }
            .catch { }
            .collect {}
    }

    private fun observeMacroKeys() = intent {
        var isFirstEmission = true
        observeCommands(parentId = 0)
            .onEach { nodes ->
                reduce {
                    state.copy(
                        macroKeys = nodes.mapIndexed { index, node ->
                            MacroKeyUiModel(
                                slotCode = "C${(index + 1).toString().padStart(2, '0')}",
                                macroName = node.label,
                                commandId = node.id,
                                icon = node.icon.orEmpty(),
                            )
                        },
                    )
                }
                // Skip the first emission — LifecycleEventEffect(ON_RESUME) covers the initial sync.
                // On subsequent emissions the command list changed (user returned from editing),
                // so push the updated commands to the device immediately.
                if (!isFirstEmission && state.connectionState == ConnectionStateModel.Connected) {
                    syncCommands()
                }
                isFirstEmission = false
            }
            .catch { }
            .collect {}
    }


    private fun onBackClick() = intent {
        disconnect()
            .onFailure {
                // Already gone — navigate away manually since observeConnection won't fire.
                postSideEffect(DeviceDetailSideEffect.NavigateToDevices)
            }
        // On success: observeConnection() detects Disconnected and posts NavigateToDevices.
    }

    private fun onDisconnect() = intent {
        disconnect()
            .onFailure { t ->
                postSideEffect(DeviceDetailSideEffect.ShowError(t.message ?: "Disconnect failed"))
            }
        // On success: observeConnection() detects Disconnected and posts NavigateToDevices.
    }

    private fun onMacroKeyClick(commandId: Int) = intent {
        postSideEffect(DeviceDetailSideEffect.NavigateToCommandForm(commandId))
    }

    private fun onAddMacroClick() = intent {
        postSideEffect(DeviceDetailSideEffect.NavigateToCommandList())
    }

    private fun onSettingsClick() = intent {
        postSideEffect(DeviceDetailSideEffect.NavigateToSettings)
    }

    private fun onForgetDevice() = intent {
        forgetDevice()
            .onSuccess {
                postSideEffect(DeviceDetailSideEffect.NavigateToDevices)
            }
            .onFailure { t ->
                postSideEffect(DeviceDetailSideEffect.ShowError(t.message ?: "Forget device failed"))
            }
    }

    private fun onForceSync() = intent {
        syncCommands()
            .onFailure { t ->
                postSideEffect(DeviceDetailSideEffect.ShowError(t.message ?: "Sync failed"))
            }
    }

    private fun onClearHistory() = intent {
        clearCommandHistory()
    }

    private fun onUpdateFirmwareClick() = intent {
        postSideEffect(DeviceDetailSideEffect.ShowFirmwarePicker)
    }

    private fun onFirmwareFileSelected(firmware: ByteArray, version: FirmwareVersionModel) {
        otaJob?.cancel()
        otaJob = intent {
            startOta(firmware, version)
                .onEach { otaState -> reduce { state.copy(otaState = otaState) } }
                // Unexpected BLE/IO exception (not an OTA protocol error): surface as Error so the
                // overlay shows "INVALID REQUEST" instead of silently disappearing.
                .catch { reduce { state.copy(otaState = OtaStateModel.Error(OtaErrorCodeModel.BAD_BEGIN)) } }
                .collect {}
            // Flow completed normally. Auto-dismiss Done after 3 s so the user sees success
            // then the screen resumes. Error states stay until the user explicitly taps Cancel.
            if (state.otaState is OtaStateModel.Done) {
                delay(3_000)
                reduce { state.copy(otaState = null) }
            } else {
                reduce {
                    state.copy(
                        otaState = when (state.otaState) {
                            is OtaStateModel.Error -> state.otaState
                            else -> null
                        },
                    )
                }
            }
        }
    }

    private fun onAbortOta() {
        otaJob?.cancel()
        otaJob = null
        intent { reduce { state.copy(otaState = null) } }
    }

    private fun onPresetsClick() = intent {
        postSideEffect(DeviceDetailSideEffect.NavigateToPresets)
    }
}
