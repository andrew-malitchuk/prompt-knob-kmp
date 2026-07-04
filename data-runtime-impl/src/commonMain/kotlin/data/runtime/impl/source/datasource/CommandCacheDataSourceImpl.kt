package data.runtime.impl.source.datasource

import data.runtime.api.source.datasource.CommandCacheDataSource
import data.runtime.api.source.resource.CommandNodeCacheResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

internal class CommandCacheDataSourceImpl : CommandCacheDataSource {

    private val _cache = MutableStateFlow<Map<Int, CommandNodeCacheResource>>(emptyMap())
    private val _selectedCommand = MutableSharedFlow<CommandNodeCacheResource>(extraBufferCapacity = 64)
    private val _rawCommandId = MutableSharedFlow<Int>(extraBufferCapacity = 64)

    override fun observeCache(): Flow<Map<Int, CommandNodeCacheResource>> = _cache.asStateFlow()
    override fun getCacheSnapshot(): Map<Int, CommandNodeCacheResource> = _cache.value
    override suspend fun setCache(commands: Map<Int, CommandNodeCacheResource>) { _cache.value = commands }

    override fun observeSelectedCommand(): Flow<CommandNodeCacheResource> = _selectedCommand.asSharedFlow()
    override fun emitSelectedCommand(command: CommandNodeCacheResource): Boolean = _selectedCommand.tryEmit(command)

    override fun observeRawCommandId(): Flow<Int> = _rawCommandId.asSharedFlow()
    override fun emitRawCommandId(id: Int): Boolean = _rawCommandId.tryEmit(id)
}
