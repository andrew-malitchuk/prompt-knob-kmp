package domain.usecase.impl.core.mapper

import domain.core.source.model.CommandNodeExportModel
import domain.core.source.model.CommandNodeModel
import domain.core.source.model.CommandTypeModel

internal fun CommandNodeModel.toExportModel(): CommandNodeExportModel = CommandNodeExportModel(
    id = id,
    parentId = parentId,
    label = label,
    isFolder = isFolder,
    isRotary = isRotary,
    isMediaScreen = isMediaScreen,
    sortOrder = sortOrder,
    command = command,
    icon = icon,
    commandType = commandType.name,
    cwCmdId = cwCmdId,
    ccwCmdId = ccwCmdId,
    playPauseCmdId = playPauseCmdId,
    prevCmdId = prevCmdId,
    nextCmdId = nextCmdId,
)

internal fun CommandNodeExportModel.toModel(): CommandNodeModel = CommandNodeModel(
    id = id,
    parentId = parentId,
    label = label,
    isFolder = isFolder,
    isRotary = isRotary,
    isMediaScreen = isMediaScreen,
    sortOrder = sortOrder,
    command = command,
    icon = icon,
    commandType = runCatching { CommandTypeModel.valueOf(commandType) }.getOrDefault(CommandTypeModel.SYSTEM),
    cwCmdId = cwCmdId,
    ccwCmdId = ccwCmdId,
    playPauseCmdId = playPauseCmdId,
    prevCmdId = prevCmdId,
    nextCmdId = nextCmdId,
)
