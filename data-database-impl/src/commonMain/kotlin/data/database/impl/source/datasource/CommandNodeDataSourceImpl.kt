package data.database.impl.source.datasource

import data.database.api.source.datasource.CommandNodeDataSource
import data.database.api.source.resource.CommandNodeResource
import data.database.impl.core.dao.CommandNodeRoomDao
import data.database.impl.core.mapper.CommandNodeToEntityMapper
import data.database.impl.core.mapper.CommandNodeToResourceMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class CommandNodeDataSourceImpl(
    private val dao: CommandNodeRoomDao,
) : CommandNodeDataSource {

    override fun observeAll(): Flow<List<CommandNodeResource>> =
        dao.observeAll().map { list -> list.map { CommandNodeToResourceMapper.map(it) } }

    override fun observeByParentId(parentId: Int): Flow<List<CommandNodeResource>> =
        dao.observeByParentId(parentId).map { list -> list.map { CommandNodeToResourceMapper.map(it) } }

    override suspend fun getAll(): List<CommandNodeResource> =
        dao.getAll().map { CommandNodeToResourceMapper.map(it) }

    override suspend fun getById(id: Int): CommandNodeResource? =
        dao.getById(id)?.let { CommandNodeToResourceMapper.map(it) }

    override suspend fun getByParentId(parentId: Int): List<CommandNodeResource> =
        dao.getByParentId(parentId).map { CommandNodeToResourceMapper.map(it) }

    override suspend fun upsert(resource: CommandNodeResource): Int =
        dao.upsert(CommandNodeToEntityMapper.map(resource)).toInt()

    override suspend fun deleteById(id: Int) {
        dao.deleteById(id)
    }

    override suspend fun deleteAll() {
        dao.deleteAll()
    }

    override suspend fun deleteByParentIdRecursive(parentId: Int) {
        dao.deleteRecursive(parentId)
    }
}
