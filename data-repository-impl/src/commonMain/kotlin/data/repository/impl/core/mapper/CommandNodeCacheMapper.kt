package data.repository.impl.core.mapper

import common.core.source.mapper.Mapper
import data.runtime.api.source.resource.CommandNodeCacheResource
import domain.core.source.model.CommandNodeModel
import domain.core.source.model.CommandTypeModel

internal object CommandNodeCacheMapper {

    val toModel: Mapper<CommandNodeCacheResource, CommandNodeModel> = Mapper {
        CommandNodeModel(
            id = it.id,
            parentId = it.parentId,
            label = it.label,
            isFolder = it.isFolder,
            isRotary = it.isRotary,
            isMediaScreen = it.isMediaScreen,
            sortOrder = it.sortOrder,
            command = it.command,
            icon = it.icon,
            commandType = runCatching { CommandTypeModel.valueOf(it.commandType) }.getOrDefault(CommandTypeModel.SYSTEM),
            cwCmdId = it.cwCmdId,
            ccwCmdId = it.ccwCmdId,
            playPauseCmdId = it.playPauseCmdId,
            prevCmdId = it.prevCmdId,
            nextCmdId = it.nextCmdId,
        )
    }

    val toResource: Mapper<CommandNodeModel, CommandNodeCacheResource> = Mapper {
        CommandNodeCacheResource(
            id = it.id,
            parentId = it.parentId,
            label = it.label,
            isFolder = it.isFolder,
            isRotary = it.isRotary,
            isMediaScreen = it.isMediaScreen,
            sortOrder = it.sortOrder,
            command = it.command,
            icon = it.icon,
            commandType = it.commandType.name,
            cwCmdId = it.cwCmdId,
            ccwCmdId = it.ccwCmdId,
            playPauseCmdId = it.playPauseCmdId,
            prevCmdId = it.prevCmdId,
            nextCmdId = it.nextCmdId,
        )
    }
}
