package data.runtime.api.source.datasource

import data.runtime.api.source.resource.CommandNodeCacheResource
import kotlinx.coroutines.flow.Flow

/**
 * In-memory data source for the command tree cache and selection events.
 *
 * The cache is rebuilt from the database on every sync. Selection events are
 * hot streams with no replay — late subscribers only receive events after subscription.
 */
public interface CommandCacheDataSource {

    public fun observeCache(): Flow<Map<Int, CommandNodeCacheResource>>
    public fun getCacheSnapshot(): Map<Int, CommandNodeCacheResource>
    public suspend fun setCache(commands: Map<Int, CommandNodeCacheResource>)

    public fun observeSelectedCommand(): Flow<CommandNodeCacheResource>
    public fun emitSelectedCommand(command: CommandNodeCacheResource): Boolean

    public fun observeRawCommandId(): Flow<Int>
    public fun emitRawCommandId(id: Int): Boolean
}
