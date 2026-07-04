package domain.core.source.model

import domain.core.source.model.base.Model

/**
 * Domain model for a single command execution event.
 *
 * @property id Database primary key.
 * @property commandId Id of the executed [CommandNodeModel].
 * @property commandLabel Human-readable name of the command at execution time.
 * @property commandType The [CommandTypeModel] of the executed command.
 * @property executedAt Epoch milliseconds when the command was executed.
 * @property isSuccess True if the command executed successfully, false otherwise.
 */
public data class CommandHistoryEntryModel(
    public val id: Int,
    public val commandId: Int,
    public val commandLabel: String,
    public val commandType: CommandTypeModel,
    public val executedAt: Long,
    public val isSuccess: Boolean,
) : Model
