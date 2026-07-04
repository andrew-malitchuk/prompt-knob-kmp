package domain.core.source.model

import domain.core.source.model.base.Model
import kotlinx.serialization.Serializable

/**
 * Serializable model for a single command node used in JSON import/export.
 *
 * @property id Original database id of the command node.
 * @property parentId Parent folder id, or 0 for root-level items.
 * @property label Human-readable name.
 * @property isFolder True if this node is a folder (container).
 * @property isRotary True if this is a rotary leaf node.
 * @property isMediaScreen True if this is a media screen leaf node.
 * @property sortOrder Display position within the same parent.
 * @property command The command string. Empty for folders, rotary, and media screen leaves.
 * @property icon Optional icon identifier. Null if no icon is assigned.
 * @property commandType Serialised [CommandTypeModel] name (e.g. `"SYSTEM"`).
 * @property cwCmdId cmd_id fired on clockwise rotation.
 * @property ccwCmdId cmd_id fired on counter-clockwise rotation.
 * @property playPauseCmdId cmd_id fired on centre-tap. Non-zero when [isMediaScreen] = true.
 * @property prevCmdId cmd_id fired on left-half tap. Non-zero when [isMediaScreen] = true.
 * @property nextCmdId cmd_id fired on right-half tap. Non-zero when [isMediaScreen] = true.
 */
@Serializable
public data class CommandNodeExportModel(
    public val id: Int,
    public val parentId: Int,
    public val label: String,
    public val isFolder: Boolean,
    public val isRotary: Boolean,
    public val isMediaScreen: Boolean = false,
    public val sortOrder: Int,
    public val command: String,
    public val icon: String?,
    public val commandType: String,
    public val cwCmdId: Int,
    public val ccwCmdId: Int,
    public val playPauseCmdId: Int = 0,
    public val prevCmdId: Int = 0,
    public val nextCmdId: Int = 0,
) : Model
