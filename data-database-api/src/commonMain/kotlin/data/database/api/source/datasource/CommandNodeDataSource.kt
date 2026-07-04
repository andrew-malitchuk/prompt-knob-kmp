package data.database.api.source.datasource

import data.database.api.source.resource.CommandNodeResource
import kotlinx.coroutines.flow.Flow

/**
 * Data source contract for local command/folder persistence.
 *
 * Implementations use Room (or another local DB) as the backing store.
 */
public interface CommandNodeDataSource :
    ObservableDataSource<CommandNodeResource>,
    ReadableDataSource<CommandNodeResource>,
    UpsertableDataSource<CommandNodeResource>,
    DeletableDataSource {

    /** Returns nodes whose [CommandNodeResource.parentId] equals [parentId] as a hot [Flow]. */
    public fun observeByParentId(parentId: Int): Flow<List<CommandNodeResource>>

    /** One-shot fetch of nodes belonging to [parentId]. */
    public suspend fun getByParentId(parentId: Int): List<CommandNodeResource>

    /**
     * Recursively deletes the folder identified by [parentId] and all its descendant nodes.
     *
     * The entire deletion is performed inside a single database transaction.
     */
    public suspend fun deleteByParentIdRecursive(parentId: Int)
}
