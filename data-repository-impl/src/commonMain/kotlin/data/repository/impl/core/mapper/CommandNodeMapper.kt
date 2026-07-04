package data.repository.impl.core.mapper

import common.core.source.mapper.Mapper
import data.database.api.source.resource.CommandNodeResource
import domain.core.source.model.CommandNodeModel
import domain.core.source.model.CommandTypeModel

/**
 * Mappers for converting between [CommandNodeResource] (data layer) and [CommandNodeModel] (domain layer).
 */
internal object CommandNodeMapper {

    val toModel: Mapper<CommandNodeResource, CommandNodeModel> =
        Mapper {
            CommandNodeModel(
                id = it.id,
                parentId = it.parentId,
                label = it.label,
                isFolder = it.isFolder,
                isRotary = it.isRotary,
                isMediaScreen = it.isMediaScreen,
                isAgentScreen = it.isAgentScreen,
                sortOrder = it.sortOrder,
                command = it.command,
                icon = it.icon,
                commandType = runCatching { CommandTypeModel.valueOf(it.commandType) }
                    .getOrDefault(CommandTypeModel.SYSTEM),
                cwCmdId = it.cwCmdId,
                ccwCmdId = it.ccwCmdId,
                playPauseCmdId = it.playPauseCmdId,
                prevCmdId = it.prevCmdId,
                nextCmdId = it.nextCmdId,
            )
        }

    val toResource: Mapper<CommandNodeModel, CommandNodeResource> =
        Mapper {
            CommandNodeResource(
                id = it.id,
                parentId = it.parentId,
                label = it.label,
                command = it.command,
                icon = it.icon,
                isFolder = it.isFolder,
                isRotary = it.isRotary,
                isMediaScreen = it.isMediaScreen,
                isAgentScreen = it.isAgentScreen,
                sortOrder = it.sortOrder,
                commandType = it.commandType.name,
                cwCmdId = it.cwCmdId,
                ccwCmdId = it.ccwCmdId,
                playPauseCmdId = it.playPauseCmdId,
                prevCmdId = it.prevCmdId,
                nextCmdId = it.nextCmdId,
            )
        }
}
