package presentation.feature.devices.source.devices

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import domain.core.source.exception.BluetoothUnavailableException
import domain.core.source.model.ConnectionStateModel
import domain.usecase.api.source.usecase.ble.CheckBlePermissionsUseCase
import domain.usecase.api.source.usecase.ble.ConnectToDeviceUseCase
import domain.usecase.api.source.usecase.ble.ConsumeSkipAutoReconnectUseCase
import domain.usecase.api.source.usecase.ble.DisconnectDeviceUseCase
import domain.usecase.api.source.usecase.ble.GetLastDeviceUseCase
import domain.usecase.api.source.usecase.ble.ObserveConnectionStateUseCase
import domain.usecase.api.source.usecase.ble.ObserveSelectedCommandUseCase
import domain.usecase.api.source.usecase.ble.RequestBlePermissionsUseCase
import domain.usecase.api.source.usecase.ble.ScanForDevicesUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.time.TimeMark
import kotlin.time.TimeSource
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.annotation.OrbitExperimental
import org.orbitmvi.orbit.viewmodel.container

private const val SCAN_TIMEOUT_MS = 15_000L
private const val QUICK_RECONNECT_TIMEOUT_MS = 3_000L
private const val DEVICE_TTL_MS = 8_000L
private const val DEVICE_PRUNE_INTERVAL_MS = 2_000L

/**
 * ViewModel for the Devices (BLE scanning) screen.
 *
 * On entry, [initializeBle] runs the following sequence:
 * 1. Check BLE permissions. If not granted, show the rationale card and stop — the user
 *    triggers the OS dialog by tapping "Grant Permission" (which dispatches [DevicesIntent.StartScan]).
 * 2. Inspect the current connection state by reading the first emission from
 *    [ObserveConnectionStateUseCase].
 * 3a. If the device is **already connected** — emit [DevicesSideEffect.NavigateToDevice]
 *     immediately; no scan is started.
 * 3b. If the device is **not connected** — attempt a quick reconnect to the last known address
 *     ([QUICK_RECONNECT_TIMEOUT_MS]). On success the connection observer navigates forward.
 *     On timeout/failure, fall through to a full BLE scan.
 *
 * The full BLE scan runs for up to [SCAN_TIMEOUT_MS] seconds. If no device is found within
 * that window, [DevicesState.scanTimedOut] is set so the UI can show recovery hints.
 *
 * [observeConnectionStateChanges] runs concurrently and emits [DevicesSideEffect.NavigateToDevice]
 * whenever the connection state transitions to [ConnectionStateModel.Connected].
 *
 * @param isBleSupported Injected platform flag: false on Desktop JVM, true on Android/iOS.
 */
@OrbitExperimental
public class DevicesViewModel(
    private val checkBlePermissions: CheckBlePermissionsUseCase,
    private val requestBlePermissions: RequestBlePermissionsUseCase,
    private val scanForDevices: ScanForDevicesUseCase,
    private val connectToDevice: ConnectToDeviceUseCase,
    private val disconnect: DisconnectDeviceUseCase,
    private val observeConnectionState: ObserveConnectionStateUseCase,
    private val observeSelectedCommand: ObserveSelectedCommandUseCase,
    private val getLastDevice: GetLastDeviceUseCase,
    private val consumeSkipAutoReconnect: ConsumeSkipAutoReconnectUseCase,
    private val isBleSupported: Boolean,
) : ContainerHost<DevicesState, DevicesSideEffect>, ViewModel() {

    private val log = Logger.withTag("DevicesViewModel")

    override val container: Container<DevicesState, DevicesSideEffect> =
        container(DevicesState(isBleAvailable = isBleSupported)) {
            if (isBleSupported) {
                initializeBle()
                observeConnectionStateChanges()
                observeCommandSelections()
            }
        }

    private var scanJob: Job? = null
    private val timeSource = TimeSource.Monotonic
    private val deviceSeenAt = mutableMapOf<String, TimeMark>()

    public fun handleIntent(intent: DevicesIntent) {
        when (intent) {
            DevicesIntent.StartScan -> onStartScan()
            DevicesIntent.StopScan -> onStopScan()
            DevicesIntent.OnRetryScan -> onRetryScan()
            is DevicesIntent.OnDeviceTapped -> onDeviceTapped(intent.address)
            is DevicesIntent.ConnectToDevice -> onConnectToDevice(intent.address)
            DevicesIntent.Disconnect -> onDisconnect()
            is DevicesIntent.OnPermissionsResult -> onPermissionsResult(intent.granted)
            DevicesIntent.OnBackClick -> onBackClick()
            DevicesIntent.OnSettingsClick -> onSettingsClick()
            DevicesIntent.OnOpenBluetoothSettings -> onOpenBluetoothSettings()
            DevicesIntent.OnResume -> onResume()
        }
    }


    // NOTE: Permissions are NOT auto-requested at screen entry. Instead, when permissions are
    // missing, the PermissionRequestContent rationale card is displayed. The user taps
    // "Grant Permission" which dispatches StartScan → onStartScan() → requestBlePermissions().
    // This follows Android/iOS guidelines: show rationale before the system dialog.
    private fun initializeBle() = intent {
        val hasPermissions = checkBlePermissions().getOrDefault(false)
        if (!hasPermissions) {
            // hasPermissions = false state triggers PermissionRequestContent (rationale card).
            return@intent
        }
        reduce { state.copy(hasPermissions = true) }

        val current = try {
            observeConnectionState().first()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            null
        }

        if (current?.state == ConnectionStateModel.Connected) {
            reduce {
                state.copy(
                    connectionState = ConnectionStateModel.Connected,
                    connectedDevice = current.device,
                )
            }
            postSideEffect(DevicesSideEffect.NavigateToDevice)
            return@intent
        }

        // Not connected — try quick reconnect to last known device before falling
        // back to a full BLE scan. This reduces reconnect time from ~5s to ~1-2s.
        // Skip if the user explicitly disconnected (one-shot flag set by disconnect use case).
        val lastAddress = getLastDevice().getOrNull()
        if (lastAddress != null && !consumeSkipAutoReconnect().getOrDefault(false)) {
            reduce { state.copy(isLoading = true) }
            val reconnected = withTimeoutOrNull(QUICK_RECONNECT_TIMEOUT_MS) {
                connectToDevice(lastAddress, null).getOrNull()
            }
            reduce { state.copy(isLoading = false) }
            if (reconnected != null) {
                // observeConnectionStateChanges() will fire NavigateToDevice automatically.
                return@intent
            }
        }

        onStartScan()
    }

    private fun observeConnectionStateChanges() = intent {
        var previousState: ConnectionStateModel? = null
        var previousDevice: domain.core.source.model.BleDeviceModel? = null
        observeConnectionState()
            .onEach { model ->
                val prev = previousState
                val prevDev = previousDevice
                previousState = model.state
                previousDevice = model.device
                if (model.state == ConnectionStateModel.Connected && model.device != null) {
                    scanJob?.cancel()
                    scanJob = null
                    deviceSeenAt.clear()
                    reduce {
                        state.copy(
                            isLoading = false,
                            connectionState = model.state,
                            connectedDevice = model.device,
                            discoveredDevices = emptyList(),
                        )
                    }
                    // Guard against duplicate navigation: only fire on first Connected+device
                    // emission. The `combine(state, device)` source can emit (Connected, null)
                    // before (Connected, BleDeviceModel) — prevDev==null catches that second
                    // emission so navigation fires even though prev is already Connected.
                    if (prev != ConnectionStateModel.Connected || prevDev == null) {
                        postSideEffect(DevicesSideEffect.NavigateToDevice)
                    }
                } else {
                    reduce {
                        state.copy(
                            connectionState = model.state,
                            connectedDevice = model.device,
                        )
                    }
                    val transitionedToDisconnected =
                        model.state == ConnectionStateModel.Disconnected &&
                        prev != null && prev != ConnectionStateModel.Disconnected
                    if (transitionedToDisconnected && state.hasPermissions && state.isBleAvailable) {
                        reduce {
                            state.copy(
                                discoveredDevices = emptyList(),
                                scanTimedOut = false,
                                isBluetoothDisabled = false,
                            )
                        }
                        onStartScan()
                    }
                }
            }
            .catch { t -> log.e { "observeConnectionState error: $t" } }
            .collect {}
    }

    private fun observeCommandSelections() = intent {
        observeSelectedCommand()
            .onEach { command ->
                log.d { "CMD received id=${command.id} label=${command.label}" }
                reduce {
                    val entry = "#${command.id}: ${command.label}"
                    state.copy(
                        lastSelectedCommand = command,
                        commandHistory = (state.commandHistory + entry).takeLast(20),
                    )
                }
            }
            .catch { t ->
                log.e { "observeSelectedCommand error: $t" }
                reduce {
                    val entry = "ERR: ${t.message}"
                    state.copy(commandHistory = (state.commandHistory + entry).takeLast(20))
                }
            }
            .collect {}
    }


    private fun onStartScan() = intent {
        if (!state.hasPermissions) {
            val granted = requestBlePermissions().getOrDefault(false)
            reduce { state.copy(hasPermissions = granted) }
            if (!granted) {
                postSideEffect(DevicesSideEffect.RequestPermissions)
                return@intent
            }
        }

        reduce {
            state.copy(
                isLoading = true,
                discoveredDevices = emptyList(),
                scanTimedOut = false,
                isBluetoothDisabled = false,
            )
        }

        scanJob?.cancel()
        deviceSeenAt.clear()
        scanJob = viewModelScope.launch {
            launch {
                while (true) {
                    delay(DEVICE_PRUNE_INTERVAL_MS)
                    val stale = deviceSeenAt.entries
                        .filter { (_, mark) -> mark.elapsedNow().inWholeMilliseconds > DEVICE_TTL_MS }
                        .map { it.key }
                        .toSet()
                    if (stale.isNotEmpty()) {
                        stale.forEach { deviceSeenAt.remove(it) }
                        intent {
                            reduce {
                                state.copy(
                                    discoveredDevices = state.discoveredDevices
                                        .filterNot { it.address in stale },
                                )
                            }
                        }
                    }
                }
            }
            try {
                withTimeout(SCAN_TIMEOUT_MS) {
                    scanForDevices()
                        .onEach { device ->
                            deviceSeenAt[device.address] = timeSource.markNow()
                            intent {
                                reduce {
                                    val updated = (listOf(device) + state.discoveredDevices)
                                        .distinctBy { it.address }
                                        .sortedByDescending { it.rssi }
                                    state.copy(discoveredDevices = updated)
                                }
                            }
                        }
                        .catch { t ->
                            val isBtOff = t is BluetoothUnavailableException
                            intent {
                                reduce { state.copy(isLoading = false) }
                                if (isBtOff) {
                                    reduce { state.copy(isBluetoothDisabled = true) }
                                } else {
                                    postSideEffect(DevicesSideEffect.ShowError(t.message ?: "Scan failed"))
                                }
                            }
                        }
                        .collect {}
                }
            } catch (_: TimeoutCancellationException) {
                intent {
                    reduce {
                        state.copy(
                            isLoading = false,
                            scanTimedOut = state.discoveredDevices.isEmpty(),
                        )
                    }
                }
                return@launch
            }
            intent { reduce { state.copy(isLoading = false) } }
        }
    }

    private fun onStopScan() = intent {
        scanJob?.cancel()
        scanJob = null
        deviceSeenAt.clear()
        reduce { state.copy(isLoading = false) }
    }

    private fun onRetryScan() {
        onStartScan()
    }

    private fun onConnectToDevice(address: String) = intent {
        reduce { state.copy(isLoading = true) }
        val name = state.discoveredDevices.firstOrNull { it.address == address }?.name
        connectToDevice(address, name)
            .onFailure { t ->
                reduce { state.copy(isLoading = false) }
                postSideEffect(DevicesSideEffect.ShowError(t.message ?: "Connection failed"))
            }
            .onSuccess {
                reduce { state.copy(isLoading = false) }
            }
    }

    private fun onDisconnect() = intent {
        disconnect()
            .onFailure { t ->
                postSideEffect(DevicesSideEffect.ShowError(t.message ?: "Disconnect failed"))
            }
    }

    private fun onPermissionsResult(granted: Boolean) = intent {
        reduce { state.copy(hasPermissions = granted) }
        if (!granted) {
            postSideEffect(DevicesSideEffect.ShowError("BLE permissions are required"))
        }
    }

    private fun onDeviceTapped(address: String) {
        onConnectToDevice(address)
    }

    private fun onResume() = intent {
        if (state.connectionState == ConnectionStateModel.Connected && state.connectedDevice != null) {
            postSideEffect(DevicesSideEffect.NavigateToDevice)
        }
    }

    private fun onBackClick() = intent {
        postSideEffect(DevicesSideEffect.NavigateBack)
    }

    private fun onSettingsClick() = intent {
        postSideEffect(DevicesSideEffect.NavigateToSettings)
    }

    private fun onOpenBluetoothSettings() = intent {
        postSideEffect(DevicesSideEffect.OpenBluetoothSettings)
    }
}
