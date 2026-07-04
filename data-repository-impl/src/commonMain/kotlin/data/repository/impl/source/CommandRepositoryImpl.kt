package data.repository.impl.source

import data.database.api.source.datasource.CommandNodeDataSource
import data.repository.impl.core.mapper.CommandNodeMapper
import domain.core.source.model.CommandNodeModel
import domain.core.source.monad.Optional
import domain.repository.api.source.repository.CommandRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class CommandRepositoryImpl(
    private val dataSource: CommandNodeDataSource,
) : CommandRepository {

    override fun observeAll(): Flow<List<CommandNodeModel>> =
        dataSource.observeAll().map { list -> list.map(CommandNodeMapper.toModel::map) }

    override fun observeByParentId(parentId: Int): Flow<List<CommandNodeModel>> =
        dataSource.observeByParentId(parentId).map { list -> list.map(CommandNodeMapper.toModel::map) }

    override suspend fun getById(id: Int): CommandNodeModel? =
        dataSource.getById(id)?.let(CommandNodeMapper.toModel::map)

    override suspend fun save(command: CommandNodeModel): Result<Int> = runCatching {
        dataSource.upsert(CommandNodeMapper.toResource.map(command))
    }

    override suspend fun deleteById(id: Int): Optional = runCatching {
        dataSource.deleteById(id)
    }

    override suspend fun deleteAll(): Optional = runCatching {
        dataSource.deleteAll()
    }

    override suspend fun deleteByParentIdRecursive(parentId: Int): Optional = runCatching {
        dataSource.deleteByParentIdRecursive(parentId)
    }
}
