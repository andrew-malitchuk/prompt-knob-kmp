package data.database.impl.source.datasource

import data.database.api.source.datasource.CommandHistoryDataSource
import data.database.api.source.resource.CommandHistoryResource
import data.database.impl.core.dao.CommandHistoryRoomDao
import data.database.impl.core.mapper.CommandHistoryToEntityMapper
import data.database.impl.core.mapper.CommandHistoryToResourceMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class CommandHistoryDataSourceImpl(
    private val dao: CommandHistoryRoomDao,
) : CommandHistoryDataSource {

    override fun observeAll(): Flow<List<CommandHistoryResource>> =
        dao.observeAll().map { list -> list.map { CommandHistoryToResourceMapper.map(it) } }

    override suspend fun insert(resource: CommandHistoryResource): Long =
        dao.insert(CommandHistoryToEntityMapper.map(resource))

    override suspend fun clearAll() {
        dao.deleteAll()
    }
}
