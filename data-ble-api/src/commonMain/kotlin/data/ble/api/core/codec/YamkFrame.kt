package data.ble.api.core.codec

import data.core.source.resource.Resource

/**
 * A decoded YAMK wire frame carrying one message in either direction.
 *
 * All messages share the same two-byte framing header:
 * - Byte 0: `[MORE:1][SEQ:7]` — `MORE=1` means more chunks follow; SEQ is 0–127.
 * - Byte 1: opcode (see [PhoneToDeviceOpcode] / [DeviceToPhoneOpcode]).
 * - Bytes 2..N: opcode-specific payload.
 *
 * @property more `true` if this frame has more chunks following it in the same logical message.
 * @property sequence Sequence number in the range 0–127, incrementing per chunk.
 * @property opcode Message type identifier; one of [PhoneToDeviceOpcode] or [DeviceToPhoneOpcode].
 * @property payload Opcode-specific payload bytes, excluding the two-byte header.
 * @see YamkCodec
 */
public data class YamkFrame(
    val more: Boolean,
    val sequence: Int,
    val opcode: UByte,
    val payload: ByteArray,
) : Resource {
    // NOTE: equals() and hashCode() are manually implemented because data class auto-generated
    // versions use referential equality for ByteArray — contentEquals() is not called by default.
    // Without this override, two frames with identical byte payloads would compare as unequal.

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is YamkFrame) return false
        return more == other.more &&
            sequence == other.sequence &&
            opcode == other.opcode &&
            payload.contentEquals(other.payload)
    }

    override fun hashCode(): Int {
        var result = more.hashCode()
        result = 31 * result + sequence
        result = 31 * result + opcode.hashCode()
        result = 31 * result + payload.contentHashCode()
        return result
    }
}
