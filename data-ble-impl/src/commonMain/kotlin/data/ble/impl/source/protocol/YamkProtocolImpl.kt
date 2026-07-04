package data.ble.impl.source.protocol

import co.touchlab.kermit.Logger
import data.ble.api.core.codec.blePayloadByte
import data.ble.api.core.codec.DeviceToPhoneOpcode
import domain.core.source.model.ClaudeState
import data.ble.api.core.codec.PhoneToDeviceOpcode
import data.ble.api.core.codec.SyncAckStatus
import data.ble.api.core.codec.YamkCodec
import data.ble.api.core.codec.YamkFrame
import data.ble.api.source.datasource.BleConnection
import data.ble.api.core.resource.CommandEntry
import data.ble.api.core.resource.CommandSelectedEvent
import data.ble.api.core.resource.SyncAckResult
import data.ble.api.core.resource.YamkProtocol
import domain.core.source.model.FirmwareVersionModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach

/**
 * Implements the YAMK application-layer protocol on top of [BleConnection].
 *
 * Responsibilities:
 * - Reassemble multi-chunk incoming frames (MORE bit, sequence-number validation).
 * - Produce and send SYNC_BEGIN / SYNC_COMMAND / SYNC_END frames with correct
 *   sequence numbering, chunked to fit within the negotiated MTU.
 * - Parse CMD_SELECTED and SYNC_ACK notifications from the device.
 *
 * @param connection The active BLE connection.
 * @param codec Codec used to encode/decode frames.
 * @param mtu Initial ATT MTU. Update via [updateMtu] after negotiation completes.
 */
public class YamkProtocolImpl(
    private val connection: BleConnection,
    private val codec: YamkCodec,
    mtu: Int = 23,
) : YamkProtocol {

    private val log = Logger.withTag("YamkProtocolImpl")

    private var _mtu: Int = mtu
    override val mtu: Int get() = _mtu

    override fun updateMtu(negotiatedMtu: Int) {
        _mtu = negotiatedMtu
    }

    /** Maximum usable bytes per BLE write (ATT header takes 3 bytes). */
    private val maxPayloadPerChunk: Int get() = (_mtu - 3).coerceAtLeast(1)

    /**
     * Executes the full transactional SYNC flow:
     * 1. Send SYNC_BEGIN
     * 2. Send one SYNC_COMMAND per entry
     * 3. Send SYNC_END with XOR checksum
     * 4. Await SYNC_ACK from device
     *
     * @return [SyncAckResult.Ok] on success, [SyncAckResult.Error] on device error.
     */
    override suspend fun syncCommands(commands: List<CommandEntry>): SyncAckResult = coroutineScope {
        sendFrame(PhoneToDeviceOpcode.SYNC_BEGIN, byteArrayOf(0x01, commands.size.toByte()))

        // XOR only over SYNC_COMMAND payloads — framing byte and opcode are excluded.
        var acc = 0

        for (command in commands) {
            val payload = encodeCommandPayload(command)
            val hex = payload.joinToString(" ") { b -> (b.toInt() and 0xFF).toString(16).padStart(2, '0') }
            acc = payload.fold(acc) { a, b -> a xor (b.toInt() and 0xFF) }
            log.d { "CHECKSUM_BYTES cmd_id=0x${(command.id and 0xFF).toString(16).padStart(2, '0')}: $hex  acc=0x${acc.toString(16).padStart(2, '0')}" }
            sendFrame(PhoneToDeviceOpcode.SYNC_COMMAND, payload)
        }

        val checksum = (acc and 0xFF).toByte()
        log.d { "CHECKSUM_FINAL: 0x${(checksum.toInt() and 0xFF).toString(16).padStart(2, '0')} (${commands.size} commands)" }

        // Subscribe to the ACK flow BEFORE sending SYNC_END. If we subscribed after,
        // the device could respond before our collector starts, causing the ACK to be
        // missed and awaitSyncAck() to hang indefinitely.
        // payload[0] = for_opcode (0x03 = SYNC_END) disambiguates from CLEAR_COMMANDS ACK.
        val ackDeferred = async {
            observeReassembledFrames()
                .filter { frame ->
                    frame.opcode == DeviceToPhoneOpcode.SYNC_ACK &&
                        frame.payload.isNotEmpty() &&
                        frame.payload[0].toUByte() == PhoneToDeviceOpcode.SYNC_END
                }
                .first()
        }

        sendFrame(PhoneToDeviceOpcode.SYNC_END, byteArrayOf(checksum))

        val ackFrame = ackDeferred.await()
        val status = if (ackFrame.payload.size >= 2) ackFrame.payload[1].toUByte() else SyncAckStatus.OK
        if (status == SyncAckStatus.OK) SyncAckResult.Ok else SyncAckResult.Error(status)
    }

    /**
     * Returns a [Flow] that emits a [CommandSelectedEvent] each time the device
     * notifies a CMD_SELECTED (opcode 0x10).
     *
     * Handles frame reassembly for multi-chunk notifications.
     */
    override fun observeCommandSelected(): Flow<CommandSelectedEvent> =
        observeReassembledFrames()
            .onEach { frame ->
                log.d { "frame opcode=0x${frame.opcode.toString(16)} payloadLen=${frame.payload.size}" }
            }
            .filter { it.opcode == DeviceToPhoneOpcode.CMD_SELECTED }
            .mapNotNull { frame ->
                if (frame.payload.isEmpty()) null
                else {
                    val cmdId = frame.payload[0].toInt() and 0xFF
                    log.d { "CMD_SELECTED cmdId=$cmdId" }
                    CommandSelectedEvent(cmdId = cmdId)
                }
            }

    override suspend fun clearCommands(): SyncAckResult = coroutineScope {
        // payload[0] = for_opcode (0x04 = CLEAR_COMMANDS) disambiguates from SYNC_END ACK.
        val ackDeferred = async {
            observeReassembledFrames()
                .filter { frame ->
                    frame.opcode == DeviceToPhoneOpcode.SYNC_ACK &&
                        frame.payload.isNotEmpty() &&
                        frame.payload[0].toUByte() == PhoneToDeviceOpcode.CLEAR_COMMANDS
                }
                .first()
        }
        sendFrame(PhoneToDeviceOpcode.CLEAR_COMMANDS, byteArrayOf())
        val ackFrame = ackDeferred.await()
        val status = if (ackFrame.payload.size >= 2) ackFrame.payload[1].toUByte() else SyncAckStatus.OK
        if (status == SyncAckStatus.OK) SyncAckResult.Ok else SyncAckResult.Error(status)
    }

    override suspend fun sendDisconnectCommand() {
        runCatching { sendFrame(PhoneToDeviceOpcode.BLE_OP_DISCONNECT, byteArrayOf()) }
            .onFailure { log.w { "BLE_OP_DISCONNECT write failed (device may be gone): ${it.message}" } }
    }

    override suspend fun sendSessionHello() {
        runCatching { sendFrame(PhoneToDeviceOpcode.SESSION_HELLO, byteArrayOf()) }
            .onFailure { log.w { "SESSION_HELLO write failed: ${it.message}" } }
    }

    override suspend fun sendShowApproval(payload: ByteArray) {
        runCatching { sendFrame(PhoneToDeviceOpcode.BLE_OP_SHOW_APPROVAL, payload) }
            .onFailure { log.w { "BLE_OP_SHOW_APPROVAL write failed (firmware may not support it yet): ${it.message}" } }
    }

    override suspend fun sendShowChoice(options: List<String>) {
        val payload = options.joinToString("|").encodeToByteArray()
        runCatching { sendFrame(PhoneToDeviceOpcode.BLE_OP_SHOW_CHOICE, payload) }
            .onFailure { log.w { "BLE_OP_SHOW_CHOICE write failed (firmware may not support it yet): ${it.message}" } }
    }

    override suspend fun sendNotify(message: String, level: Int) {
        val messageBytes = message.encodeToByteArray()
        val payload = byteArrayOf(level.coerceIn(0, 3).toByte()) + messageBytes
        runCatching { sendFrame(PhoneToDeviceOpcode.BLE_OP_NOTIFY, payload) }
            .onFailure { log.w { "BLE_OP_NOTIFY write failed (firmware may not support it yet): ${it.message}" } }
    }

    override suspend fun sendClaudeState(state: ClaudeState) {
        runCatching { sendFrame(PhoneToDeviceOpcode.BLE_OP_CLAUDE_STATE, byteArrayOf(state.blePayloadByte)) }
            .onFailure { log.w { "BLE_OP_CLAUDE_STATE write failed (firmware may not support it yet): ${it.message}" } }
    }

    override fun observeBatteryLevel(): Flow<Int> =
        observeReassembledFrames()
            .filter { it.opcode == DeviceToPhoneOpcode.BATTERY_LEVEL }
            .mapNotNull { frame ->
                if (frame.payload.isEmpty()) {
                    log.w { "BATTERY_LEVEL payload empty" }
                    null
                } else {
                    val pct = (frame.payload[0].toInt() and 0xFF).coerceIn(0, 100)
                    log.d { "BATTERY_LEVEL $pct%" }
                    pct
                }
            }

    override fun observeVersionInfo(): Flow<FirmwareVersionModel> =
        observeReassembledFrames()
            .filter { it.opcode == DeviceToPhoneOpcode.VERSION_INFO }
            .mapNotNull { frame ->
                if (frame.payload.size < 3) {
                    log.w { "VERSION_INFO payload too short: ${frame.payload.size}B" }
                    null
                } else {
                    FirmwareVersionModel(
                        major = frame.payload[0].toInt() and 0xFF,
                        minor = frame.payload[1].toInt() and 0xFF,
                        patch = frame.payload[2].toInt() and 0xFF,
                    ).also { log.d { "VERSION_INFO $it" } }
                }
            }


    // SEQ is per-message: resets to 0 at the start of every new logical message and
    // increments only for subsequent chunks of the same message. The firmware resets
    // its reassembly state whenever it sees SEQ=0, so each new message must start at 0.
    private suspend fun sendFrame(opcode: UByte, payload: ByteArray) {
        var chunkSeq = 0
        val chunks = payload.chunked(maxPayloadPerChunk)
        if (chunks.isEmpty()) {
            connection.write(codec.encode(YamkFrame(more = false, sequence = 0, opcode = opcode, payload = ByteArray(0))))
            return
        }
        chunks.forEachIndexed { index, chunk ->
            val isLast = index == chunks.lastIndex
            connection.write(codec.encode(YamkFrame(more = !isLast, sequence = chunkSeq++, opcode = opcode, payload = chunk)))
        }
    }

    /**
     * Reassembles multi-chunk (MORE=1) frames into a single complete frame.
     *
     * Validates the 7-bit sequence counter: each multi-chunk message must start at
     * SEQ=0 and increment by 1 per chunk. A mismatch resets the buffer and drops
     * the incomplete fragment.
     */
    private fun observeReassembledFrames(): Flow<YamkFrame> {
        val buffer = mutableListOf<Byte>()
        var currentOpcode: UByte = 0u
        var expectedNextSeq = 0
        return connection.observeNotifications()
            .onEach { bytes ->
                val hex = bytes.joinToString(" ") { (it.toInt() and 0xFF).toString(16).padStart(2, '0') }
                log.v { "raw notification ${bytes.size}B: $hex" }
            }
            .map { bytes -> codec.decode(bytes) }
            .mapNotNull { frame ->
                if (frame.more) {
                    if (buffer.isEmpty()) {
                        // First chunk of a new multi-part message — SEQ must be 0.
                        if (frame.sequence != 0) {
                            log.w { "reassembly: mid-stream chunk without start (seq=${frame.sequence}), dropping" }
                            return@mapNotNull null
                        }
                        currentOpcode = frame.opcode
                        expectedNextSeq = 1
                    } else {
                        // Continuation chunk — sequence number must match expected.
                        if (frame.sequence != expectedNextSeq) {
                            log.w { "reassembly: seq mismatch expected=$expectedNextSeq got=${frame.sequence}, resetting buffer" }
                            buffer.clear()
                            expectedNextSeq = 0
                            return@mapNotNull null
                        }
                        expectedNextSeq++
                    }
                    buffer.addAll(frame.payload.toList())
                    null
                } else {
                    if (buffer.isNotEmpty()) {
                        buffer.addAll(frame.payload.toList())
                        val complete = YamkFrame(
                            more = false,
                            sequence = frame.sequence,
                            opcode = currentOpcode,
                            payload = buffer.toByteArray(),
                        )
                        buffer.clear()
                        expectedNextSeq = 0
                        complete
                    } else {
                        frame
                    }
                }
            }
    }

    private fun encodeCommandPayload(command: CommandEntry): ByteArray {
        // The device stores labels in a fixed 15-byte buffer (firmware BLE_MAX_LABEL_LEN)
        // and DROPS any SYNC_COMMAND whose label exceeds it — which then breaks the batch
        // count and XOR checksum, causing the whole sync to be rejected with
        // SYNC_ACK status=0x01 (MALFORMED). The label is display-only; the full command
        // text lives in a separate field and is never sent here, so truncating the synced
        // label is lossless for execution.
        val labelBytes = command.label.truncateToUtf8Bytes(MAX_LABEL_BYTES)
        // Layout: [cmd_id][parent_id][flags][sort_order][label_len][label bytes][extra bytes]
        // Rotary:      +2 bytes [cw_id][ccw_id]
        // MediaScreen:  +5 bytes [cw_id][ccw_id][play_pause_id][prev_id][next_id]
        // AgentScreen:  +0 bytes (flag only, no extra payload)
        // All extra bytes are included in the XOR checksum automatically.
        // No null terminator — ESP32 reads exactly label_len bytes at offset 5.
        val extraBytes = when {
            command.isMediaScreen -> 5
            command.isRotary -> 2
            else -> 0
        }
        val payload = ByteArray(5 + labelBytes.size + extraBytes)
        payload[0] = command.id.toByte()
        payload[1] = command.parentId.toByte()
        // 0x00 = normal leaf | 0x01 = folder | 0x02 = rotary leaf | 0x04 = media screen leaf | 0x08 = agent screen leaf
        // AgentScreen/MediaScreen/Rotary MUST NOT be combined with folder.
        val flags = when {
            command.isAgentScreen -> 0x08
            command.isMediaScreen -> 0x04
            command.isRotary -> 0x02
            command.isFolder -> 0x01
            else -> 0x00
        }
        payload[2] = flags.toByte()
        payload[3] = command.sortOrder.toByte()
        payload[4] = labelBytes.size.toByte()
        labelBytes.copyInto(payload, destinationOffset = 5)
        val afterLabel = 5 + labelBytes.size
        if (command.isMediaScreen) {
            payload[afterLabel]     = command.cwCmdId.toByte()
            payload[afterLabel + 1] = command.ccwCmdId.toByte()
            payload[afterLabel + 2] = command.playPauseCmdId.toByte()
            payload[afterLabel + 3] = command.prevCmdId.toByte()
            payload[afterLabel + 4] = command.nextCmdId.toByte()
        } else if (command.isRotary) {
            payload[afterLabel]     = command.cwCmdId.toByte()
            payload[afterLabel + 1] = command.ccwCmdId.toByte()
        }
        return payload
    }
}

// Must match firmware BLE_MAX_LABEL_LEN (commands.h). The device silently rejects
// SYNC_COMMAND frames with a longer label, which corrupts the sync count/checksum.
private const val MAX_LABEL_BYTES = 15

// Encode as UTF-8 and clamp to [maxBytes], backing off to a character boundary so a
// multi-byte codepoint is never split (which would produce an invalid UTF-8 label).
private fun String.truncateToUtf8Bytes(maxBytes: Int): ByteArray {
    val bytes = encodeToByteArray()
    if (bytes.size <= maxBytes) return bytes
    var end = maxBytes
    // UTF-8 continuation bytes match 0b10xxxxxx; step back while the cut lands on one.
    while (end > 0 && (bytes[end].toInt() and 0xC0) == 0x80) end--
    return bytes.copyOf(end)
}

// Kotlin stdlib doesn't have a chunked for ByteArray, add a local extension
private fun ByteArray.chunked(size: Int): List<ByteArray> {
    if (isEmpty()) return emptyList()
    val chunks = mutableListOf<ByteArray>()
    var offset = 0
    while (offset < this.size) {
        val end = minOf(offset + size, this.size)
        chunks.add(copyOfRange(offset, end))
        offset = end
    }
    return chunks
}
