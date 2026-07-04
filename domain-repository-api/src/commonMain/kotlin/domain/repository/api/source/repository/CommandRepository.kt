package domain.repository.api.source.repository

import domain.core.source.model.CommandNodeModel
import domain.core.source.monad.Optional
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for local command and folder persistence.
 */
public interface CommandRepository {

    /** Returns all nodes as a hot [Flow] that re-emits on every database change. */
    public fun observeAll(): Flow<List<CommandNodeModel>>

    /** Returns nodes whose [CommandNodeModel.parentId] equals [parentId] as a hot [Flow]. */
    public fun observeByParentId(parentId: Int): Flow<List<CommandNodeModel>>

    /** One-shot fetch of a single node by its [id], or null if not found. */
    public suspend fun getById(id: Int): CommandNodeModel?

    /**
     * Inserts or updates [command] in the local database.
     * A [CommandNodeModel.id] of 0 means "insert new"; any other value performs an update.
     */
    public suspend fun save(command: CommandNodeModel): Result<Int>

    /** Deletes the command with the given [id]. */
    public suspend fun deleteById(id: Int): Optional

    /** Deletes all commands and folders from the local database. */
    public suspend fun deleteAll(): Optional

    /**
     * Recursively deletes the folder identified by [parentId] and all its descendants.
     */
    public suspend fun deleteByParentIdRecursive(parentId: Int): Optional
}
