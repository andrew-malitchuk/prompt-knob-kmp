package domain.core.source.model

import domain.core.source.model.base.Model
import kotlinx.serialization.Serializable

/**
 * Top-level container for an exported command tree.
 *
 * @property version Schema version for forward/backward compatibility. Current version: 1.
 * @property exportedAt ISO-8601 timestamp of when the export was created.
 * @property commands Flat list of all command nodes in the tree.
 */
@Serializable
public data class CommandExportPayloadModel(
    public val version: Int = 1,
    public val exportedAt: String,
    public val commands: List<CommandNodeExportModel>,
) : Model
