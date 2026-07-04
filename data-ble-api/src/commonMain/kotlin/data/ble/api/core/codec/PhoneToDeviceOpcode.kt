package data.ble.api.core.codec

/**
 * Wire protocol opcodes for messages sent from the phone to the ESP32 via SYNC_CHAR.
 *
 * Each constant is a single-byte opcode placed in byte 1 of a [YamkFrame].
 *
 * @see DeviceToPhoneOpcode
 * @see YamkCodec
 */
public object PhoneToDeviceOpcode {
    /** Begins a sync flow — sent as the first frame before command payloads. */
    public const val SYNC_BEGIN: UByte = 0x01u

    /** Carries one command node payload within an active sync flow. */
    public const val SYNC_COMMAND: UByte = 0x02u

    /** Ends a sync flow — sent after all SYNC_COMMAND frames have been transmitted. */
    public const val SYNC_END: UByte = 0x03u

    /**
     * Instructs the firmware to atomically erase all stored commands from NVS.
     *
     * Simpler than the full SYNC flow (no SYNC_COMMAND frames needed) and used
     * exclusively during the forget-device sequence. The device responds with a
     * SYNC_ACK (0xF0) frame; status byte 0x00 = OK.
     *
     * Wire format: [0x00, 0x04] — YAMK header (MORE=0, SEQ=0) + opcode, no payload.
     */
    public const val CLEAR_COMMANDS: UByte = 0x04u

    /**
     * Signals the firmware to treat the device as user-disconnected.
     *
     * Firmware sets s_user_disconnected=true so ble_server_is_connected() returns
     * false and the UI transitions to CONNECTION LOST within ≤3 s, regardless of
     * whether the physical BLE link remains alive (autoConnect on Android keeps it).
     * The latch is cleared only when the user taps "Reconnect" on the device.
     *
     * Wire format: [0x00, 0xF2] — YAMK header (MORE=0, SEQ=0) + opcode, no payload.
     */
    public const val BLE_OP_DISCONNECT: UByte = 0xF2u

    /**
     * Requests the device to re-emit VERSION_INFO (0xF1).
     *
     * Useful after a reconnect-without-reauth scenario where the device has not
     * pushed VERSION_INFO automatically. The device responds with a VERSION_INFO
     * notification on CMD_CHAR.
     *
     * Wire format: [0x00, 0xF3] — YAMK header (MORE=0, SEQ=0) + opcode, no payload.
     */
    public const val SESSION_HELLO: UByte = 0xF3u

    /**
     * Instructs the firmware to display the approval screen on the device UI.
     *
     * Used by the MCP integration: after sending this opcode, the app waits for a
     * [DeviceToPhoneOpcode.CMD_SELECTED] (0x10) notification. A tap (cmd_id > 0) means
     * approved; a swipe-right sends cmd_id = 0 (rejected); timeout (30 s) = auto-reject.
     *
     * Firmware support: Iteration 2 only. In Iteration 1 the device shows no special UI
     * and any physical tap on the current screen is treated as approval.
     *
     * Wire format: [0x00, 0x05] — YAMK header (MORE=0, SEQ=0) + opcode, no payload.
     */
    public const val BLE_OP_SHOW_APPROVAL: UByte = 0x05u

    /**
     * Instructs the firmware to display a scrollable choice screen on the device UI.
     *
     * Used by the MCP `request_choice` tool: after sending this opcode with a pipe-separated
     * list of options as the payload, the app waits for a [DeviceToPhoneOpcode.CMD_SELECTED]
     * (0x10) notification. The returned cmd_id is 1-based: cmd_id N means option[N-1] was
     * selected; cmd_id == 0 means the user cancelled (swipe-right).
     *
     * Payload encoding: UTF-8 string with options joined by `|`, e.g. `"staging|prod|cancel"`.
     * Maximum total payload length is limited by the negotiated ATT MTU minus 2 header bytes.
     *
     * Firmware support: Iteration 2 only.
     *
     * Wire format: [0x00, 0x06, <options_bytes>…]
     */
    public const val BLE_OP_SHOW_CHOICE: UByte = 0x06u

    /**
     * Fire-and-forget notification displayed on the device UI.
     *
     * Used by the MCP `notify` tool. The device shows the message briefly and optionally
     * plays a haptic or LED pulse. No response is expected — the app does not wait for
     * any [DeviceToPhoneOpcode.CMD_SELECTED] after sending this opcode.
     *
     * Payload encoding: UTF-8 bytes of the message string, optionally prefixed by a
     * single level byte: 0x00 = info, 0x01 = success, 0x02 = warning, 0x03 = error.
     * Format: [level_byte, <message_bytes>…]
     *
     * Firmware support: Iteration 2 only.
     *
     * Wire format: [0x00, 0x07, <level>, <message_bytes>…]
     */
    public const val BLE_OP_NOTIFY: UByte = 0x07u

    /**
     * Sends the current Claude Code session state to the device UI.
     *
     * Used by the Claude Mode hook integration: the macOS app receives HTTP POST requests
     * from Claude Code hooks and translates them into this BLE write.
     *
     * Payload encoding: single state byte — see [data.ble.api.core.codec.blePayloadByte] for the mapping.
     *
     * Wire format: [0x00, 0x08, <state_byte>]
     */
    public const val BLE_OP_CLAUDE_STATE: UByte = 0x08u
}
