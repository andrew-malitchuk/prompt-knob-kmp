package data.ble.api.core.codec

/**
 * Wire protocol opcodes for OTA firmware update, exchanged over the dedicated OTA GATT service.
 *
 * Phone-to-device frames (via OTA_CTRL_CHAR, Write-With-Response) use opcodes 0x50–0x52.
 * Device-to-phone notifications (via OTA_STATUS_CHAR) use opcodes 0x60–0x63.
 *
 * Each frame starts with a one-byte header (0x00 = MORE=0, SEQ=0) followed by the opcode byte,
 * matching the same framing as YAMK but over the separate OTA service UUIDs.
 *
 * OTA_DATA chunks are sent as raw firmware bytes (no header) via OTA_DATA_CHAR, Write-Without-Response.
 */
public object OtaOpcode {
    /** Begins the OTA flow. Payload: [total_size: u32 LE][crc32: u32 LE][major][minor][patch]. */
    public const val OTA_BEGIN: UByte = 0x50u

    /** Signals the last chunk was sent and requests CRC verification + flash commit. */
    public const val OTA_END: UByte = 0x51u

    /** Aborts an in-progress OTA; device discards the partial image. */
    public const val OTA_ABORT: UByte = 0x52u

    /** Device is ready to receive firmware data chunks. */
    public const val OTA_READY: UByte = 0x60u

    /** Device progress notification. Payload: [bytes_received: u32 LE]. */
    public const val OTA_PROGRESS: UByte = 0x61u

    /** OTA completed successfully; device will restart momentarily. */
    public const val OTA_DONE: UByte = 0x62u

    /** OTA failed. Payload byte contains the error code. */
    public const val OTA_ERROR: UByte = 0x63u
}
