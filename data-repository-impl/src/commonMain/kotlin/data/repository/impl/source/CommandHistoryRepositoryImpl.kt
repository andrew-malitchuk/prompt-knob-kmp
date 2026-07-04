package data.repository.impl.source

import data.database.api.source.datasource.CommandHistoryDataSource
import data.database.api.source.resource.CommandHistoryResource
import data.repository.impl.core.mapper.CommandHistoryMapper.STATUS_FAILURE
import data.repository.impl.core.mapper.CommandHistoryMapper.STATUS_SUCCESS
import data.repository.impl.core.mapper.CommandHistoryMapper.toEntry
import domain.core.source.model.CommandHistoryEntryModel
import domain.core.source.model.CommandNodeModel
import domain.core.source.monad.Optional
import domain.repository.api.source.repository.CommandHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.Clock

internal class CommandHistoryRepositoryImpl(
    private val dataSource: CommandHistoryDataSource,
) : CommandHistoryRepository {

    override fun observeAll(): Flow<List<CommandHistoryEntryModel>> =
        dataSource.observeAll().map { list -> list.map { it.toEntry() } }

    override suspend fun logExecution(node: CommandNodeModel, success: Boolean) {
        val resource = CommandHistoryResource(
            id = 0,
            commandId = node.id,
            commandLabel = node.label,
            commandType = node.commandType.name,
            executedAt = Clock.System.now().toEpochMilliseconds(),
            status = if (success) STATUS_SUCCESS else STATUS_FAILURE,
        )
        runCatching { dataSource.insert(resource) }
    }

    override suspend fun clearAll(): Optional = runCatching {
        dataSource.clearAll()
    }
}
