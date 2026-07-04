package data.ble.api.core.codec

/**
 * Wire protocol opcodes for messages received from the ESP32 via CMD_CHAR.
 *
 * Each constant is a single-byte opcode placed in byte 1 of a [YamkFrame].
 *
 * @see PhoneToDeviceOpcode
 * @see YamkCodec
 */
public object DeviceToPhoneOpcode {
    /** Notifies the phone that the user has rotated the knob to select a command. */
    public const val CMD_SELECTED: UByte = 0x10u

    /**
     * Acknowledges the completion of a sync flow.
     *
     * The payload byte contains a [SyncAckStatus] code indicating success or failure.
     */
    public const val SYNC_ACK: UByte = 0xF0u

    /**
     * Device firmware version pushed immediately after auth completes.
     *
     * Payload: [major][minor][patch] — three uint8 bytes.
     */
    public const val VERSION_INFO: UByte = 0xF1u

    /**
     * Device battery level, pushed immediately after auth completes and on each change.
     *
     * Payload: [pct] — one uint8 byte, value 0–100.
     */
    public const val BATTERY_LEVEL: UByte = 0x20u
}
