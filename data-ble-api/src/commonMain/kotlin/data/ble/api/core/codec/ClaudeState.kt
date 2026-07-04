package data.ble.api.core.codec

import domain.core.source.model.ClaudeState

/** Maps a domain [ClaudeState] to its BLE wire-protocol payload byte. */
public val ClaudeState.blePayloadByte: Byte
    get() = when (this) {
        ClaudeState.IDLE -> 0x00
        ClaudeState.WORKING -> 0x01
        ClaudeState.WAITING -> 0x02
        ClaudeState.DONE -> 0x03
        ClaudeState.ERROR -> 0x04
    }
