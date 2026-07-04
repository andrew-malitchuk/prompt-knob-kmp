package presentation.feature.device.source.device

import domain.core.source.model.CommandHistoryEntryModel
import domain.core.source.model.CommandNodeModel
import domain.core.source.model.ConnectionStateModel
import domain.core.source.model.FirmwareVersionModel
import domain.core.source.model.OtaStateModel

/**
 * UI model for a single macro key slot on the device dashboard.
 *
 * @property slotCode Short slot identifier shown below the icon (e.g. "C01").
 * @property macroName Human-readable macro label (e.g. "MUTE").
 * @property commandId Underlying [CommandNodeModel] identifier.
 */
public data class MacroKeyUiModel(
    val slotCode: String,
    val macroName: String,
    val commandId: Int,
    val icon: String = "",
)

/**
 * Immutable UI state for the Device detail / dashboard screen.
 *
 * @property isLoading True while initial device data is loading.
 * @property deviceName Advertised BLE name of the connected device.
 * @property deviceAddress Hardware address (MAC on Android, UUID on iOS).
 * @property connectionState Current BLE connection state.
 * @property batteryPercentage Device battery level, or null if not yet supported by the firmware.
 * @property macroKeys Ordered list of macro key slots to display in the grid.
 * @property profileLoadPercentage Profile load percentage, or null if not supported.
 * @property lastSelectedCommand The most recently selected command from the device, or null.
 * @property commandHistory Ordered list of recent command execution events (newest first).
 */
public data class DeviceDetailState(
    val isLoading: Boolean = true,
    val deviceName: String = "",
    val deviceAddress: String = "",
    val connectionState: ConnectionStateModel = ConnectionStateModel.Connected,
    val batteryPercentage: Int? = null,
    val firmwareVersion: String? = null,
    val macroKeys: List<MacroKeyUiModel> = emptyList(),
    val profileLoadPercentage: Int? = null,
    val lastSelectedCommand: CommandNodeModel? = null,
    val commandHistory: List<CommandHistoryEntryModel> = emptyList(),
    /** Current OTA state, or null when no OTA session is active. */
    val otaState: OtaStateModel? = null,
)

/**
 * One-shot side effects emitted by [DeviceDetailViewModel].
 */
public sealed class DeviceDetailSideEffect {
    /** Navigate back to the devices list screen. */
    public data object NavigateBack : DeviceDetailSideEffect()

    /** Navigate to the Devices scan screen after a deliberate disconnect. */
    public data object NavigateToDevices : DeviceDetailSideEffect()

    /** Navigate to the Settings screen. */
    public data object NavigateToSettings : DeviceDetailSideEffect()

    /** Navigate to the command list (ADD NEW MACRO flow). */
    public data class NavigateToCommandList(val parentId: Int = 0) : DeviceDetailSideEffect()

    /** Navigate to the command form to edit an existing command. */
    public data class NavigateToCommandForm(val commandId: Int) : DeviceDetailSideEffect()

    /** Display an error message. */
    public data class ShowError(val message: String) : DeviceDetailSideEffect()

    /** Open the platform firmware file picker. */
    public data object ShowFirmwarePicker : DeviceDetailSideEffect()

    /** Navigate to the preset gallery screen. */
    public data object NavigateToPresets : DeviceDetailSideEffect()
}

/**
 * User intents dispatched from the Device detail UI layer.
 */
public sealed class DeviceDetailIntent {
    /** User tapped the back / close button. */
    public data object OnBackClick : DeviceDetailIntent()

    /** User triggered a disconnect from this screen. */
    public data object OnDisconnect : DeviceDetailIntent()

    /** User tapped a macro key tile. */
    public data class OnMacroKeyClick(val commandId: Int) : DeviceDetailIntent()

    /** User tapped the "+ ADD NEW MACRO" CTA. */
    public data object OnAddMacroClick : DeviceDetailIntent()

    /** User tapped the settings icon in the screen header. */
    public data object OnSettingsClick : DeviceDetailIntent()

    /** User confirmed "Forget device" — disconnect, clear prefs, delete all commands. */
    public data object OnForgetDevice : DeviceDetailIntent()

    /** User tapped "Force Sync" — re-sends the full command list to the device immediately. */
    public data object OnForceSync : DeviceDetailIntent()

    /** User tapped the "Clear history" button. */
    public data object OnClearHistory : DeviceDetailIntent()

    /** User tapped "Update Firmware" — triggers the file picker side effect. */
    public data object OnUpdateFirmwareClick : DeviceDetailIntent()

    /** User selected a firmware binary file; carries raw bytes and target version. */
    public data class OnFirmwareFileSelected(
        val firmware: ByteArray,
        val version: FirmwareVersionModel,
    ) : DeviceDetailIntent()

    /** User tapped "Cancel" during an active OTA session. */
    public data object OnAbortOta : DeviceDetailIntent()

    /** User tapped the presets button. */
    public data object OnPresetsClick : DeviceDetailIntent()
}
