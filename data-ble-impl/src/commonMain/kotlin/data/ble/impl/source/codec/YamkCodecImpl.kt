package data.ble.impl.source.codec

import data.ble.api.core.codec.YamkCodec
import data.ble.api.core.codec.YamkFrame

/**
 * Default implementation of [YamkCodec].
 *
 * Frame layout (both directions):
 *   Byte 0: [MORE:1][SEQ:7]  — framing header
 *   Byte 1: opcode (UByte)
 *   Bytes 2..N: opcode-specific payload (may be empty)
 */
public class YamkCodecImpl : YamkCodec {

    override fun encode(frame: YamkFrame): ByteArray {
        val header = buildHeader(frame.more, frame.sequence)
        val result = ByteArray(2 + frame.payload.size)
        result[0] = header
        result[1] = frame.opcode.toByte()
        frame.payload.copyInto(result, destinationOffset = 2)
        return result
    }

    override fun decode(bytes: ByteArray): YamkFrame {
        require(bytes.size >= 2) {
            "YAMK frame too short: expected at least 2 bytes, got ${bytes.size}"
        }
        val header = bytes[0]
        val more = (header.toInt() and 0x80) != 0
        val sequence = header.toInt() and 0x7F
        val opcode = bytes[1].toUByte()
        val payload = if (bytes.size > 2) bytes.copyOfRange(2, bytes.size) else ByteArray(0)
        return YamkFrame(more = more, sequence = sequence, opcode = opcode, payload = payload)
    }

    private fun buildHeader(more: Boolean, sequence: Int): Byte {
        val moreBit = if (more) 0x80 else 0x00
        return (moreBit or (sequence and 0x7F)).toByte()
    }
}
