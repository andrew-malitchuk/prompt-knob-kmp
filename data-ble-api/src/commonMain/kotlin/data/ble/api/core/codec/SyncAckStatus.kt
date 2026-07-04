package data.ble.api.core.codec

/**
 * Status codes carried in the payload of a [DeviceToPhoneOpcode.SYNC_ACK] frame.
 *
 * The device sends exactly one `SYNC_ACK` after it receives a [PhoneToDeviceOpcode.SYNC_END].
 *
 * @see DeviceToPhoneOpcode.SYNC_ACK
 */
public object SyncAckStatus {
    /** All commands were written to NVS successfully. */
    public const val OK: UByte = 0x00u

    /** One or more frames contained a malformed header or payload. */
    public const val MALFORMED: UByte = 0x01u

    /** NVS write operation failed — the device could not persist the command set. */
    public const val NVS_WRITE_FAILED: UByte = 0x02u

    /** The sync payload exceeded the device's maximum command slot count. */
    public const val TOO_MANY_COMMANDS: UByte = 0x03u

    /** Some commands arrived without a matching SYNC_BEGIN/SYNC_END envelope. */
    public const val ORPHAN_COMMANDS: UByte = 0x04u
}
