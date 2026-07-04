package data.ble.api.core.resource

import data.core.source.resource.Resource

/**
 * A single command entry passed to the device during the SYNC flow.
 *
 * @property id Unique command identifier. Folders: 128–255, leaves: 1–127.
 * @property parentId Parent folder id, or 0 for root-level items.
 * @property isFolder True if this entry is a folder (does not trigger CMD_SELECTED).
 * @property isRotary True if this is a rotary leaf — flags=0x02 on wire.
 *   Must NOT be combined with [isFolder] (firmware rejects flags=0x03).
 *   When true, [cwCmdId] and [ccwCmdId] are appended after the label bytes.
 * @property isMediaScreen True if this is a media screen leaf — flags=0x04 on wire.
 *   Must NOT be combined with [isFolder] (firmware rejects flags=0x05).
 *   When true, 5 bytes are appended after the label: [cwCmdId][ccwCmdId][playPauseCmdId][prevCmdId][nextCmdId].
 * @property isAgentScreen True if this is an agent/Claude screen leaf — flags=0x08 on wire.
 *   Must NOT be combined with [isFolder]. No extra payload bytes beyond the label.
 * @property sortOrder Display order within the same parent level.
 * @property label Human-readable label shown on device.
 * @property cwCmdId cmd_id fired on clockwise rotation.
 * @property ccwCmdId cmd_id fired on counter-clockwise rotation.
 * @property playPauseCmdId cmd_id fired on centre-tap (non-zero when [isMediaScreen]).
 * @property prevCmdId cmd_id fired on left-half tap (non-zero when [isMediaScreen]).
 * @property nextCmdId cmd_id fired on right-half tap (non-zero when [isMediaScreen]).
 */
public data class CommandEntry(
    val id: Int,
    val parentId: Int,
    val isFolder: Boolean,
    val isRotary: Boolean = false,
    val isMediaScreen: Boolean = false,
    val isAgentScreen: Boolean = false,
    val sortOrder: Int,
    val label: String,
    val cwCmdId: Int = 0,
    val ccwCmdId: Int = 0,
    val playPauseCmdId: Int = 0,
    val prevCmdId: Int = 0,
    val nextCmdId: Int = 0,
) : Resource
