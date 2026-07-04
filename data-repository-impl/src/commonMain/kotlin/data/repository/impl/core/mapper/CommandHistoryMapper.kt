package data.repository.impl.core.mapper

import data.database.api.source.resource.CommandHistoryResource
import domain.core.source.model.CommandHistoryEntryModel
import domain.core.source.model.CommandTypeModel

internal object CommandHistoryMapper {

    const val STATUS_SUCCESS = "SUCCESS"
    const val STATUS_FAILURE = "FAILURE"

    fun CommandHistoryResource.toEntry(): CommandHistoryEntryModel = CommandHistoryEntryModel(
        id = id,
        commandId = commandId,
        commandLabel = commandLabel,
        commandType = runCatching { CommandTypeModel.valueOf(commandType) }.getOrDefault(CommandTypeModel.SYSTEM),
        executedAt = executedAt,
        isSuccess = status == STATUS_SUCCESS,
    )
}
