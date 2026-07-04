package presentation.feature.devices.source.devices

import domain.core.source.model.BleDeviceModel
import domain.core.source.model.CommandNodeModel
import domain.core.source.model.ConnectionStateModel

/**
 * Immutable UI state for the Devices screen.
 *
 * @property isLoading True while a connection or scan is in progress.
 * @property hasPermissions True if all required BLE permissions are granted.
 * @property isBleAvailable False on platforms where BLE is not supported (Desktop).
 * @property connectionState Current BLE connection state.
 * @property discoveredDevices List of nearby BLE devices found during a scan.
 * @property connectedDevice Currently connected device, or null if disconnected.
 * @property lastSelectedCommand The most recently selected command from the device, or null.
 * @property commandHistory Ordered list of recent command/debug events (newest last, max 20).
 * @property scanTimedOut True when the scan completed without finding any device.
 * @property isBluetoothDisabled True when the scan failed because the BT adapter is turned off.
 */
public data class DevicesState(
    val isLoading: Boolean = false,
    val hasPermissions: Boolean = false,
    val isBleAvailable: Boolean = true,
    val connectionState: ConnectionStateModel = ConnectionStateModel.Disconnected,
    val discoveredDevices: List<BleDeviceModel> = emptyList(),
    val connectedDevice: BleDeviceModel? = null,
    val lastSelectedCommand: CommandNodeModel? = null,
    val commandHistory: List<String> = emptyList(),
    val scanTimedOut: Boolean = false,
    val isBluetoothDisabled: Boolean = false,
)

/**
 * One-shot side effects emitted by [DevicesViewModel].
 */
public sealed class DevicesSideEffect {
    /** Request BLE permissions from the platform (Android Activity, iOS CBCentralManager). */
    public data object RequestPermissions : DevicesSideEffect()

    /** Display an error message. */
    public data class ShowError(val message: String) : DevicesSideEffect()

    /**
     * Device is connected — navigate forward to the device detail screen.
     *
     * Emitted in two cases:
     * - At screen entry, if the device was already connected by the OS or a previous session.
     * - During scanning, as soon as a connection is established.
     */
    public data object NavigateToDevice : DevicesSideEffect()

    /** Navigate to the Settings screen. */
    public data object NavigateToSettings : DevicesSideEffect()

    /** Navigate back to the previous screen. */
    public data object NavigateBack : DevicesSideEffect()

    /** Open the platform Bluetooth settings screen. */
    public data object OpenBluetoothSettings : DevicesSideEffect()
}

/**
 * User intents dispatched from the Devices UI layer.
 */
public sealed class DevicesIntent {
    /** Start scanning for BLE devices. */
    public data object StartScan : DevicesIntent()

    /** Stop the ongoing scan. */
    public data object StopScan : DevicesIntent()

    /** Retry scan after a timeout or error. */
    public data object OnRetryScan : DevicesIntent()

    /** User tapped a discovered device — connects to the device immediately. */
    public data class OnDeviceTapped(val address: String) : DevicesIntent()

    /** Connect to the selected device. */
    public data class ConnectToDevice(val address: String) : DevicesIntent()

    /** Disconnect from the currently connected device. */
    public data object Disconnect : DevicesIntent()

    /** Called by the UI after a permission request completes. */
    public data class OnPermissionsResult(val granted: Boolean) : DevicesIntent()

    /** User tapped the back button in the screen header. */
    public data object OnBackClick : DevicesIntent()

    /** User tapped the settings icon in the screen header. */
    public data object OnSettingsClick : DevicesIntent()

    /** User tapped "Open Bluetooth Settings" on the BT-disabled sheet. */
    public data object OnOpenBluetoothSettings : DevicesIntent()

    /** Screen resumed (e.g. back navigation from Device detail). */
    public data object OnResume : DevicesIntent()
}
