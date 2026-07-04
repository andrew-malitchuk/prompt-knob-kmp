package data.database.api.source.resource

import data.core.source.resource.Resource

/**
 * Data-layer representation of a command or folder node stored in the local database.
 *
 * @property id Auto-generated database primary key. 0 means "not yet persisted".
 * @property parentId Parent folder id, or 0 for root-level items.
 * @property label Human-readable name shown in the UI.
 * @property command The command string sent to the device. Empty string for folders.
 * @property icon Optional icon identifier string. Null if no icon is set.
 * @property isFolder True if this node is a folder (container); false if it is a leaf command.
 * @property isRotary True if this folder maps to knob rotation. Only meaningful when [isFolder] = true.
 * @property sortOrder Display order within the same parent level.
 * @property commandType Serialised [domain.core.source.model.CommandTypeModel] name. Defaults to `"SYSTEM"`.
 * @property cwCmdId cmd_id the device fires on clockwise rotation.
 * @property ccwCmdId cmd_id the device fires on counter-clockwise rotation.
 * @property isMediaScreen True if this is a media screen leaf. Wire flags = 0x04.
 * @property isAgentScreen True if this is an agent/Claude screen leaf. Wire flags = 0x08.
 * @property playPauseCmdId cmd_id fired on centre-tap. Non-zero when [isMediaScreen] = true.
 * @property prevCmdId cmd_id fired on left-half tap. Non-zero when [isMediaScreen] = true.
 * @property nextCmdId cmd_id fired on right-half tap. Non-zero when [isMediaScreen] = true.
 */
public data class CommandNodeResource(
    val id: Int,
    val parentId: Int,
    val label: String,
    val command: String,
    val icon: String?,
    val isFolder: Boolean,
    val isRotary: Boolean = false,
    val isMediaScreen: Boolean = false,
    val isAgentScreen: Boolean = false,
    val sortOrder: Int,
    val commandType: String = "SYSTEM",
    val cwCmdId: Int = 0,
    val ccwCmdId: Int = 0,
    val playPauseCmdId: Int = 0,
    val prevCmdId: Int = 0,
    val nextCmdId: Int = 0,
) : Resource
