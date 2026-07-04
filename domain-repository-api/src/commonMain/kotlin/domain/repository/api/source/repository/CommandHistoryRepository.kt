package domain.repository.api.source.repository

import domain.core.source.model.CommandHistoryEntryModel
import domain.core.source.model.CommandNodeModel
import domain.core.source.monad.Optional
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for persisted command execution history.
 */
public interface CommandHistoryRepository {

    /** Returns a hot [Flow] of all history entries, newest first. */
    public fun observeAll(): Flow<List<CommandHistoryEntryModel>>

    /**
     * Records a command execution event.
     *
     * @param node The command that was executed.
     * @param success True if execution succeeded, false otherwise.
     */
    public suspend fun logExecution(node: CommandNodeModel, success: Boolean)

    /** Deletes all history entries. */
    public suspend fun clearAll(): Optional
}
