package data.ble.api.core.codec

/**
 * Encodes and decodes YAMK wire frames for BLE transport.
 *
 * Frame layout (bytes):
 * ```
 * [0]: MORE:1 bit | SEQ:7 bits
 * [1]: opcode (UByte)
 * [2..N]: payload
 * ```
 *
 * @see YamkFrame
 * @see PhoneToDeviceOpcode
 * @see DeviceToPhoneOpcode
 */
public interface YamkCodec {

    /**
     * Encodes a [YamkFrame] into the raw bytes to be written over BLE.
     *
     * Returned byte array layout:
     * - byte 0: framing header (`MORE` flag OR-ed with `SEQ` in bits 0–6)
     * - byte 1: opcode
     * - bytes 2..N: payload
     *
     * @param frame The frame to encode.
     * @return A `ByteArray` ready to write to SYNC_CHAR.
     * @see decode
     */
    public fun encode(frame: YamkFrame): ByteArray

    /**
     * Decodes raw BLE notification bytes into a [YamkFrame].
     *
     * @param bytes Raw bytes received from CMD_CHAR or SYNC_CHAR. Must be at least 2 bytes.
     * @return The decoded [YamkFrame].
     * @throws IllegalArgumentException if [bytes] is shorter than 2 bytes (missing header or opcode).
     * @see encode
     */
    public fun decode(bytes: ByteArray): YamkFrame
}
