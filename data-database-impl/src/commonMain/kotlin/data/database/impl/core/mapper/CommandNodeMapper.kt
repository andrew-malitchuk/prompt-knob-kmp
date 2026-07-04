package data.database.impl.core.mapper

import common.core.source.mapper.Mapper
import data.database.api.source.resource.CommandNodeResource
import data.database.impl.core.entity.CommandNodeEntity

internal object CommandNodeToResourceMapper : Mapper<CommandNodeEntity, CommandNodeResource> {
    override fun map(input: CommandNodeEntity): CommandNodeResource = CommandNodeResource(
        id = input.id,
        parentId = input.parentId,
        label = input.label,
        command = input.command,
        icon = input.icon,
        isFolder = input.isFolder,
        isRotary = input.isRotary,
        isMediaScreen = input.isMediaScreen,
        isAgentScreen = input.isAgentScreen,
        sortOrder = input.sortOrder,
        commandType = input.commandType,
        cwCmdId = input.cwCmdId,
        ccwCmdId = input.ccwCmdId,
        playPauseCmdId = input.playPauseCmdId,
        prevCmdId = input.prevCmdId,
        nextCmdId = input.nextCmdId,
    )
}

internal object CommandNodeToEntityMapper : Mapper<CommandNodeResource, CommandNodeEntity> {
    override fun map(input: CommandNodeResource): CommandNodeEntity = CommandNodeEntity(
        id = input.id,
        parentId = input.parentId,
        label = input.label,
        command = input.command,
        icon = input.icon,
        isFolder = input.isFolder,
        isRotary = input.isRotary,
        isMediaScreen = input.isMediaScreen,
        isAgentScreen = input.isAgentScreen,
        sortOrder = input.sortOrder,
        commandType = input.commandType,
        cwCmdId = input.cwCmdId,
        ccwCmdId = input.ccwCmdId,
        playPauseCmdId = input.playPauseCmdId,
        prevCmdId = input.prevCmdId,
        nextCmdId = input.nextCmdId,
    )
}
