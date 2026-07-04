# YAMK BLE Protocol

The app talks to the PromptKnob hardware (an ESP32-based rotary knob) over Bluetooth Low Energy using a small custom wire protocol called **YAMK**. This page documents the protocol as it is implemented in the app's `data-ble-api` and `data-ble-impl` modules. The firmware side lives in a separate repository, [prompt-knob-firmware](https://github.com/andrew-malitchuk/prompt-knob-firmware).

For the app-side transport (scanning, GATT, characteristics, the Android foreground service) see [Firmware BLE implementation](firmware-ble-impl.md).

## Where the code lives

| Concern | Type | Location |
|---------|------|----------|
| Frame model | `YamkFrame` | `data-ble-api/.../core/codec/YamkFrame.kt` |
| Frame encode/decode | `YamkCodec` / `YamkCodecImpl` | `data-ble-api` / `data-ble-impl/.../source/codec` |
| Protocol operations | `YamkProtocol` / `YamkProtocolImpl` | `data-ble-api` / `data-ble-impl/.../source/protocol` |
| Constants (UUIDs, names) | `PromptKnobBle` | `data-ble-impl/.../source/protocol/PromptKnobBle.kt` |
| Phone → device opcodes | `PhoneToDeviceOpcode` | `data-ble-api/.../core/codec` |
| Device → phone opcodes | `DeviceToPhoneOpcode` | `data-ble-api/.../core/codec` |
| OTA opcodes | `OtaOpcode` | `data-ble-api/.../core/codec` |
| Sync ACK status codes | `SyncAckStatus` | `data-ble-api/.../core/codec` |

## Frame layout

A YAMK frame (`YamkFrame`) is a byte array with a one-byte header, a one-byte opcode, and an opcode-specific payload:

```
byte 0 : [ MORE (1 bit) | SEQ (7 bits) ]
byte 1 : opcode (UByte)
byte 2.. : payload (opcode-specific, may be empty)
```

`YamkCodecImpl` builds the header by packing the `MORE` flag into the high bit and the 7-bit sequence into the low bits:

```kotlin
val moreBit = if (more) 0x80 else 0x00
val header = (moreBit or (sequence and 0x7F)).toByte()
```

Decoding reverses this:

```kotlin
val more = (header.toInt() and 0x80) != 0
val sequence = header.toInt() and 0x7F
```

### Sequence numbers (SEQ)

`SEQ` is a 7-bit counter (`0`–`127`) that is **per logical message**, not global. A multi-chunk message must start at `SEQ = 0` and increment by one for each continuation chunk. The device resets its reassembly state whenever it sees `SEQ = 0`.

### The MORE flag (chunking / reassembly)

Because a single BLE write is bounded by the negotiated MTU, large payloads are split into multiple frames. Each non-final chunk sets `MORE = 1`; the final chunk sets `MORE = 0`.

`YamkProtocolImpl` reassembles incoming frames as follows:

- A chunk with `MORE = 1` is buffered; nothing is emitted yet.
  - The first buffered chunk must have `SEQ = 0`; otherwise the fragment is dropped.
  - Continuation chunks must match the expected next sequence, or the buffer is cleared and the fragment is discarded.
- A chunk with `MORE = 0` completes the message: the buffered payload plus this final chunk are combined into one `YamkFrame` and emitted. A `MORE = 0` frame with an empty buffer is a single-chunk message and is emitted as-is.

## Opcodes

### Phone → device (`PhoneToDeviceOpcode`)

| Constant | Value | Meaning |
|----------|-------|---------|
| `SYNC_BEGIN` | `0x01` | Begin a command-sync transaction |
| `SYNC_COMMAND` | `0x02` | Carry one command node payload |
| `SYNC_END` | `0x03` | End the sync transaction (carries checksum) |
| `CLEAR_COMMANDS` | `0x04` | Atomically erase all stored commands |
| `BLE_OP_SHOW_APPROVAL` | `0x05` | Show an approval screen on the device |
| `BLE_OP_SHOW_CHOICE` | `0x06` | Show a choice screen (pipe-separated options) |
| `BLE_OP_NOTIFY` | `0x07` | Fire-and-forget notification |
| `BLE_OP_CLAUDE_STATE` | `0x08` | Push Claude Code session state |
| `BLE_OP_DISCONNECT` | `0xF2` | Signal a user-initiated disconnect |
| `SESSION_HELLO` | `0xF3` | Ask the device to re-emit `VERSION_INFO` |

### Device → phone (`DeviceToPhoneOpcode`)

| Constant | Value | Payload | Meaning |
|----------|-------|---------|---------|
| `CMD_SELECTED` | `0x10` | `[cmd_id]` | User rotated the knob and selected a command |
| `BATTERY_LEVEL` | `0x20` | `[pct]` (0–100) | Battery level report |
| `SYNC_ACK` | `0xF0` | `[for_opcode][status]` | Acknowledges a sync transaction |
| `VERSION_INFO` | `0xF1` | `[major][minor][patch]` | Firmware version |

### OTA (`OtaOpcode`)

Sent over the dedicated OTA characteristics (see [Firmware BLE implementation](firmware-ble-impl.md)):

| Direction | Constant | Value | Notes |
|-----------|----------|-------|-------|
| Phone → device | `OTA_BEGIN` | `0x50` | `[total_size u32 LE][crc32 u32 LE][major][minor][patch]` |
| Phone → device | `OTA_END` | `0x51` | Request CRC verify + flash commit |
| Phone → device | `OTA_ABORT` | `0x52` | Abort an in-progress OTA |
| Device → phone | `OTA_READY` | `0x60` | Ready to receive firmware data |
| Device → phone | `OTA_PROGRESS` | `0x61` | `[bytes_received u32 LE]` |
| Device → phone | `OTA_DONE` | `0x62` | OTA succeeded |
| Device → phone | `OTA_ERROR` | `0x63` | `[error_code]` |

## Command sync transaction

Pushing the user's command tree to the device is a three-message handshake, implemented in `YamkProtocolImpl.syncCommands`:

1. **`SYNC_BEGIN`** — begins the transaction, payload `[0x01, commands.size]`.
2. **`SYNC_COMMAND` × N** — one frame per command, each carrying the command's encoded payload.
3. **`SYNC_END`** — ends the transaction, payload `[checksum]`.

The device replies with a **`SYNC_ACK`** (`0xF0`) whose payload is `[for_opcode][status]`:

- `for_opcode` is the operation being acknowledged — `0x03` for `SYNC_END`, `0x04` for `CLEAR_COMMANDS`.
- `status` is one of `SyncAckStatus`.

> The ACK flow is subscribed **before** `SYNC_END` is sent (the code launches the collector with `async { … first() }` first). If you subscribed afterwards, the device could reply before the collector started and the call would hang.

### Checksum

The checksum sent in `SYNC_END` is a single-byte **XOR** over the `SYNC_COMMAND` payload bytes only (the header and opcode bytes are not included):

```kotlin
var acc = 0
for (command in commands) {
    val payload = encodeCommandPayload(command)
    acc = payload.fold(acc) { a, b -> a xor (b.toInt() and 0xFF) }
}
val checksum = (acc and 0xFF).toByte()
```

One checksum value covers the entire batch.

### Sync ACK status codes (`SyncAckStatus`)

| Constant | Value | Meaning |
|----------|-------|---------|
| `OK` | `0x00` | All commands persisted successfully |
| `MALFORMED` | `0x01` | A frame had a malformed header/payload |
| `NVS_WRITE_FAILED` | `0x02` | The device could not persist the command set |
| `TOO_MANY_COMMANDS` | `0x03` | Batch exceeded the device's command-slot count |
| `ORPHAN_COMMANDS` | `0x04` | Commands arrived without a matching begin/end |

## Command payload encoding

Each `CommandEntry` is serialized by `YamkProtocolImpl.encodeCommandPayload` into a fixed prefix followed by a UTF-8 label and optional type-specific bytes:

```
byte 0     : cmd_id
byte 1     : parent_id
byte 2     : flags
byte 3     : sort_order
byte 4     : label_len
byte 5..   : label bytes (UTF-8, truncated to the device's max label length)
byte ...   : extra bytes, depending on type:
               Rotary       -> [cw_id][ccw_id]
               Media screen -> [cw_id][ccw_id][play_pause_id][prev_id][next_id]
               Agent screen -> (none)
               Normal leaf  -> (none)
```

The `flags` byte selects the node type (values are mutually exclusive with the folder flag):

| Flag | Meaning |
|------|---------|
| `0x00` | Normal leaf command |
| `0x01` | Folder |
| `0x02` | Rotary leaf |
| `0x04` | Media-screen leaf |
| `0x08` | Agent / Claude-screen leaf |

Labels are truncated to the firmware's maximum label length on UTF-8 boundaries (never splitting a multi-byte codepoint); commands whose labels exceed the limit are rejected by the device.

## Related pages

- [Firmware BLE implementation](firmware-ble-impl.md) — the transport layer (scanning, GATT, characteristics, foreground service).
- [Architecture overview](../architecture/overview.md)
