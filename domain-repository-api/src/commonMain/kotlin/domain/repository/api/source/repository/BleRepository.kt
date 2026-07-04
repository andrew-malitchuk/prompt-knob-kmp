package domain.repository.api.source.repository

import domain.core.source.model.BleDeviceModel
import domain.core.source.model.ClaudeState
import domain.core.source.model.CommandNodeModel
import domain.core.source.model.DeviceConnectionModel
import domain.core.source.model.FirmwareVersionModel
import domain.core.source.model.OtaStateModel
import domain.core.source.monad.Optional
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for BLE device operations.
 *
 * Bridges the data layer (BleScanner, BleConnection, YamkProtocolImpl)
 * to the domain layer use cases.
 */
public interface BleRepository {
    /** Emits the current [DeviceConnectionModel] and updates on state changes. */
    public fun observeConnectionState(): Flow<DeviceConnectionModel>

    /** Returns a [Flow] of [BleDeviceModel] entries as BLE devices are discovered. */
    public fun scanForDevices(): Flow<BleDeviceModel>

    /**
     * Initiates a connection to the device at [address].
     * Returns [Result.success] when connected, [Result.failure] on error.
     */
    public suspend fun connect(address: String, name: String? = null): Optional

    /** Disconnects from the currently connected device. */
    public suspend fun disconnect(): Optional

    /**
     * Synchronises the [commands] list to the device using the YAMK SYNC flow.
     * Returns [Result.success] on SYNC_ACK OK, [Result.failure] on device error.
     */
    public suspend fun syncCommands(commands: List<CommandNodeModel>): Optional

    /**
     * Sends `CLEAR_COMMANDS` (0x04) to the device and awaits `SYNC_ACK`.
     *
     * The firmware erases all stored commands from NVS atomically. Used during the
     * forget-device flow instead of an empty SYNC sequence.
     *
     * Returns [Result.success] on SYNC_ACK OK, [Result.failure] on device error or timeout.
     */
    public suspend fun clearCommands(): Optional

    /**
     * Emits a [CommandNodeModel] each time the device sends a CMD_SELECTED notification.
     * The emitted node is looked up by its id from the last synced command list.
     * Emits nothing if the command id is unknown.
     */
    public fun observeSelectedCommand(): Flow<CommandNodeModel>

    /** Returns true if all required BLE permissions are currently granted. */
    public suspend fun checkPermissions(): Boolean

    /**
     * Requests BLE permissions from the user.
     * Returns true if granted after the request.
     */
    public suspend fun requestPermissions(): Boolean

    /**
     * Returns the address of the last successfully connected device, or null if none.
     * On Android this is a MAC address; on iOS/macOS it is a CoreBluetooth UUID string.
     */
    public suspend fun getLastConnectedDevice(): String?

    /** Clears the persisted last-connected device address. */
    public suspend fun clearLastConnectedDevice(): Optional

    /**
     * Sets a one-shot flag so the next call to [consumeSkipAutoReconnect] returns true.
     *
     * Call this before a user-initiated disconnect to prevent [DevicesViewModel] from
     * auto-reconnecting to the last device when the user lands on the Devices screen.
     */
    public fun markSkipAutoReconnect()

    /**
     * Reads and clears the skip-auto-reconnect flag. Returns true (and resets to false)
     * if [markSkipAutoReconnect] was called since the last consume; false otherwise.
     */
    public fun consumeSkipAutoReconnect(): Boolean

    /**
     * Emits the device firmware version once it is received via VERSION_INFO notify
     * (opcode 0xF1) or the DIS Firmware Revision String fallback (0x180A / 0x2A26).
     * Emits null while unknown or disconnected.
     */
    public fun observeFirmwareVersion(): Flow<FirmwareVersionModel?>

    /**
     * Emits the device battery level (0–100) when received via BATTERY_LEVEL notify
     * (opcode 0x20). Emits null while unknown or disconnected.
     */
    public fun observeBatteryLevel(): Flow<Int?>

    /**
     * Sends `BLE_OP_SHOW_APPROVAL` (0x05) to the device.
     *
     * In Iteration 2+ firmware the device switches its UI to the approval screen.
     * In Iteration 1 this is a best-effort write with no visible effect on the device.
     *
     * Returns [Result.success] if the write completed; [Result.failure] on BLE error.
     */
    public suspend fun sendShowApproval(): Optional

    /**
     * Sends `BLE_OP_SHOW_CHOICE` (0x06) with the given options to the device.
     *
     * The firmware displays a scrollable choice screen. The user rotates the knob to
     * navigate and taps to confirm. The device responds via [observeCommandSelectedRaw]:
     * cmd_id > 0 means option[cmd_id - 1] was selected; cmd_id == 0 means cancelled.
     *
     * Returns [Result.success] if the write completed; [Result.failure] on BLE error.
     *
     * @param options Non-empty list of option strings to display on the device.
     */
    public suspend fun sendShowChoice(options: List<String>): Optional

    /**
     * Sends `BLE_OP_NOTIFY` (0x07) — a fire-and-forget notification to the device.
     *
     * The device briefly displays [message] with optional haptic/LED feedback based on
     * [level]. No response is expected.
     *
     * Returns [Result.success] if the write completed; [Result.failure] on BLE error.
     *
     * @param message Text to display on the device screen.
     * @param level Severity hint: 0=info, 1=success, 2=warning, 3=error.
     */
    public suspend fun sendNotify(message: String, level: Int = 0): Optional

    /**
     * Sends `BLE_OP_CLAUDE_STATE` (0x08) — updates the Claude Code session state on the device.
     *
     * The device shows the Claude screen with a corresponding state indicator.
     * No response is expected.
     *
     * Returns [Result.success] if the write completed; [Result.failure] on BLE error.
     *
     * @param state Current Claude Code session state to display.
     */
    public suspend fun sendClaudeState(state: ClaudeState): Optional

    /**
     * Returns a [Flow] that emits a raw command id (int) each time the device sends
     * a CMD_SELECTED notification, without resolving the id against the command cache.
     *
     * Used by the MCP approval flow: any emission after [sendShowApproval] is treated
     * as a user response (cmd_id > 0 = approved, cmd_id == 0 = rejected).
     */
    public fun observeCommandSelectedRaw(): Flow<Int>

    /**
     * Executes the full OTA firmware update sequence.
     *
     * Returns a cold [Flow] that emits [OtaStateModel] transitions: [OtaStateModel.Waiting] →
     * [OtaStateModel.Receiving] (repeated with progress) → [OtaStateModel.Verifying] → [OtaStateModel.Done].
     * Emits [OtaStateModel.Error] on any failure.
     *
     * The flow is cancellation-aware: cancelling the collecting coroutine sends OTA_ABORT
     * to the device and discards the partial image.
     *
     * @param firmware Raw binary content of the firmware `.bin` file.
     * @param version Semantic version declared to the device in the OTA_BEGIN handshake.
     */
    public fun startOta(firmware: ByteArray, version: FirmwareVersionModel): Flow<OtaStateModel>
}
