package domain.core.source.model

import domain.core.source.model.base.Model

/**
 * A single item in the knob device's command tree.
 *
 * Commands are stored as a flat list with [parentId] references forming a tree.
 *
 * - Root items have [parentId] = 0.
 * - Folder children have [parentId] = the folder's [id].
 * - Folders: [isFolder] = true. Never trigger CMD_SELECTED.
 * - Leaves: [isFolder] = false. Trigger CMD_SELECTED when selected.
 * - Rotary leaves: [isFolder] = false, [isRotary] = true. Wire flags = 0x02.
 *   The device fires CMD_SELECTED([cwCmdId]) on CW rotation and
 *   CMD_SELECTED([ccwCmdId]) on CCW rotation. [cwCmdId]/[ccwCmdId] are IDs
 *   of "virtual" leaf nodes that live in the local DB/cache but are not
 *   sent as standalone BLE entries.
 * - Media screen leaves: [isFolder] = false, [isMediaScreen] = true. Wire flags = 0x04.
 *   Selecting this command on the wheel opens the device's media control screen.
 *   The device fires CMD_SELECTED with one of 5 helper cmd_ids depending on
 *   the gesture (rotation CW/CCW, tap centre/left/right). All 5 IDs are virtual
 *   leaf nodes that live in the local DB/cache but are never sent as standalone
 *   BLE entries. Firmware rejects 0x05 (media+folder combined).
 * - Agent screen leaves: [isFolder] = false, [isAgentScreen] = true. Wire flags = 0x08.
 *   Selecting this command on the wheel opens the device's Claude/Agent status screen.
 *   No virtual children — the screen displays Claude Code hook states (idle/working/waiting/done).
 *   Mutually exclusive with [isRotary] and [isMediaScreen].
 *
 * @property id Unique command identifier (1–255).
 * @property parentId Parent folder id, or 0 for root-level items.
 * @property label Human-readable label shown on the device wheel.
 * @property isFolder True if this is a folder entry (not selectable).
 * @property isRotary True if this is a rotary leaf. [isFolder] must be false.
 *   Wire flags byte = 0x02. Firmware rejects 0x03 (rotary+folder combined).
 * @property isMediaScreen True if this is a media screen leaf. [isFolder] must be false.
 *   Wire flags byte = 0x04. Firmware rejects 0x05 (media+folder combined).
 *   Mutually exclusive with [isRotary] and [isAgentScreen].
 * @property isAgentScreen True if this is an agent/Claude screen leaf. [isFolder] must be false.
 *   Wire flags byte = 0x08. No virtual children needed.
 *   Mutually exclusive with [isRotary] and [isMediaScreen].
 * @property sortOrder Display position within the same parent.
 * @property command The command string executed on the host. Empty for folders, rotary, and media screen leaves.
 * @property icon Optional icon identifier. Null if no icon is assigned.
 * @property commandType How [command] should be executed on the host platform.
 * @property cwCmdId cmd_id the device fires on clockwise rotation. Non-zero when [isRotary]=true or [isMediaScreen]=true.
 * @property ccwCmdId cmd_id the device fires on counter-clockwise rotation. Non-zero when [isRotary]=true or [isMediaScreen]=true.
 * @property playPauseCmdId cmd_id fired on centre-tap (play/pause). Non-zero when [isMediaScreen]=true.
 * @property prevCmdId cmd_id fired on left-half tap (previous track). Non-zero when [isMediaScreen]=true.
 * @property nextCmdId cmd_id fired on right-half tap (next track). Non-zero when [isMediaScreen]=true.
 */
public data class CommandNodeModel(
    public val id: Int,
    public val parentId: Int,
    public val label: String,
    public val isFolder: Boolean,
    public val isRotary: Boolean = false,
    public val isMediaScreen: Boolean = false,
    public val isAgentScreen: Boolean = false,
    public val sortOrder: Int,
    public val command: String = "",
    public val icon: String? = null,
    public val commandType: CommandTypeModel = CommandTypeModel.SYSTEM,
    public val cwCmdId: Int = 0,
    public val ccwCmdId: Int = 0,
    public val playPauseCmdId: Int = 0,
    public val prevCmdId: Int = 0,
    public val nextCmdId: Int = 0,
) : Model
