package data.database.api.source.resource

import data.core.source.resource.Resource

/**
 * Data-layer representation of a single command execution event.
 *
 * @property id Auto-generated primary key. 0 means not yet persisted.
 * @property commandId The id of the executed [CommandNodeResource].
 * @property commandLabel Human-readable name of the command at execution time.
 * @property commandType Serialised [domain.core.source.model.CommandTypeModel] name.
 * @property executedAt Epoch milliseconds when the command was executed.
 * @property status Execution outcome: "SUCCESS" or "FAILURE".
 */
public data class CommandHistoryResource(
    val id: Int,
    val commandId: Int,
    val commandLabel: String,
    val commandType: String,
    val executedAt: Long,
    val status: String,
) : Resource
