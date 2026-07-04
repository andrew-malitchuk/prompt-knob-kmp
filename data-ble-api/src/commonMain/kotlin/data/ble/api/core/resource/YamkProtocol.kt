package data.ble.api.core.resource

import domain.core.source.model.ClaudeState
import domain.core.source.model.FirmwareVersionModel
import kotlinx.coroutines.flow.Flow

/**
 * Application-layer protocol for communicating with the PromptKnob device over BLE.
 *
 * Handles YAMK frame encoding/decoding, multi-chunk reassembly, and the transactional
 * SYNC flow that pushes the current command tree to device NVS.
 *
 * @see data.ble.api.core.codec.YamkCodec for the lower-level frame codec.
 * @see data.ble.api.source.datasource.BleConnection for the transport layer.
 */
public interface YamkProtocol {

    /** Currently active ATT MTU. Updated via [updateMtu] after negotiation completes. */
    public val mtu: Int

    /**
     * Updates the ATT MTU after negotiation with the peripheral.
     *
     * Call this after [data.ble.api.source.datasource.BleConnection.negotiateMtu] to
     * ensure chunk sizes stay within the agreed MTU. Usable payload per chunk = [mtu] − 3.
     *
     * @param negotiatedMtu The MTU value returned by negotiation.
     */
    public fun updateMtu(negotiatedMtu: Int)

    /**
     * Executes the full transactional SYNC flow against the connected device:
     * `SYNC_BEGIN` → N × `SYNC_COMMAND` → `SYNC_END` → awaits `SYNC_ACK`.
     *
     * @param commands The complete command tree to push to the device.
     * @return [SyncAckResult.Ok] on success; [SyncAckResult.Error] if the device
     *   rejected the payload.
     */
    public suspend fun syncCommands(commands: List<CommandEntry>): SyncAckResult

    /**
     * Sends `CLEAR_COMMANDS` (0x04) and awaits `SYNC_ACK` from the device.
     *
     * The firmware erases all stored commands from NVS atomically. Used during the
     * forget-device flow as a lighter alternative to an empty SYNC sequence.
     *
     * @return [SyncAckResult.Ok] on success; [SyncAckResult.Error] on device error.
     */
    public suspend fun clearCommands(): SyncAckResult

    /**
     * Returns a [Flow] that emits a [CommandSelectedEvent] each time the device
     * notifies a `CMD_SELECTED` frame (opcode 0x10).
     *
     * Handles frame reassembly for multi-chunk notifications internally.
     */
    public fun observeCommandSelected(): Flow<CommandSelectedEvent>

    /**
     * Returns a [Flow] that emits a [FirmwareVersionModel] when the device sends a
     * `VERSION_INFO` frame (opcode 0xF1). The device pushes this immediately after
     * authentication completes — before any sync flow starts.
     */
    public fun observeVersionInfo(): Flow<FirmwareVersionModel>

    /**
     * Sends `BLE_OP_DISCONNECT` (0xF2) to the device and waits for the write to complete.
     *
     * The firmware latches `s_user_disconnected=true` so its UI transitions to CONNECTION
     * LOST regardless of whether the Android OS keeps the physical BLE link alive via
     * autoConnect. Call this before [data.ble.api.source.datasource.BleConnection.disconnect].
     *
     * Failures (e.g. device already gone) are swallowed — callers must still proceed
     * with the disconnect even if this write is rejected.
     */
    public suspend fun sendDisconnectCommand()

    /**
     * Sends `SESSION_HELLO` (0xF3) to the device and waits for the write to complete.
     *
     * The device responds with a `VERSION_INFO` (0xF1) notification. Use this after a
     * reconnect-without-reauth scenario to ensure the version observer receives the
     * firmware version.
     *
     * Failures are swallowed — the DIS fallback handles the case where this write fails.
     */
    public suspend fun sendSessionHello()

    /**
     * Returns a [Flow] that emits a battery percentage (0–100) each time the device sends
     * a `BATTERY_LEVEL` (0x20) notification.
     */
    public fun observeBatteryLevel(): Flow<Int>

    /**
     * Sends `BLE_OP_SHOW_APPROVAL` (0x05) to the device and waits for the write to complete.
     *
     * Instructs the firmware to switch its UI to the approval screen. In Iteration 1
     * (before firmware support) this write has no visible effect on the device, but the
     * app will still await the next [DeviceToPhoneOpcode.CMD_SELECTED] notification.
     *
     * Failures are swallowed — callers must still wait for the CMD_SELECTED event.
     *
     * @param payload Optional metadata byte array (empty in Iteration 1).
     */
    public suspend fun sendShowApproval(payload: ByteArray = ByteArray(0))

    /**
     * Sends `BLE_OP_SHOW_CHOICE` (0x06) with a pipe-separated options payload.
     *
     * Instructs the firmware to display a scrollable choice screen. The user rotates
     * the knob to navigate options and taps to confirm. The device responds with a
     * [data.ble.api.core.codec.DeviceToPhoneOpcode.CMD_SELECTED] notification where
     * cmd_id is 1-based (option[cmd_id - 1] was selected) or 0 for cancel.
     *
     * Failures are swallowed — callers must still await the CMD_SELECTED event.
     *
     * @param options Non-empty list of display strings shown on the device screen.
     */
    public suspend fun sendShowChoice(options: List<String>)

    /**
     * Sends `BLE_OP_NOTIFY` (0x07) — a fire-and-forget notification.
     *
     * The device briefly displays [message] and optionally plays a haptic/LED pulse
     * based on [level]. No response is expected; this call returns as soon as the
     * BLE write completes. Failures are swallowed.
     *
     * @param message Text to display on the device screen.
     * @param level Severity hint for LED/haptic feedback (0=info, 1=success, 2=warning, 3=error).
     */
    public suspend fun sendNotify(message: String, level: Int = 0)

    /**
     * Sends `BLE_OP_CLAUDE_STATE` (0x08) — updates the Claude Code session state on the device.
     *
     * Instructs the firmware to show the Claude screen with the given [state] indicator.
     * No response is expected; this call returns as soon as the BLE write completes.
     * Failures are swallowed — a missed state update does not affect device correctness.
     *
     * @param state Current Claude Code session state.
     */
    public suspend fun sendClaudeState(state: ClaudeState)
}
