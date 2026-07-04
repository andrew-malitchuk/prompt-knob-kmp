package data.database.impl.core.mapper

import common.core.source.mapper.Mapper
import data.database.api.source.resource.CommandHistoryResource
import data.database.impl.core.entity.CommandHistoryEntity

internal object CommandHistoryToResourceMapper : Mapper<CommandHistoryEntity, CommandHistoryResource> {
    override fun map(input: CommandHistoryEntity): CommandHistoryResource = CommandHistoryResource(
        id = input.id,
        commandId = input.commandId,
        commandLabel = input.commandLabel,
        commandType = input.commandType,
        executedAt = input.executedAt,
        status = input.status,
    )
}

internal object CommandHistoryToEntityMapper : Mapper<CommandHistoryResource, CommandHistoryEntity> {
    override fun map(input: CommandHistoryResource): CommandHistoryEntity = CommandHistoryEntity(
        id = input.id,
        commandId = input.commandId,
        commandLabel = input.commandLabel,
        commandType = input.commandType,
        executedAt = input.executedAt,
        status = input.status,
    )
}
