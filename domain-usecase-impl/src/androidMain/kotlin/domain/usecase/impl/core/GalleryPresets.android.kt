package domain.usecase.impl.core

import domain.core.source.model.CommandExportPayloadModel
import domain.core.source.model.CommandNodeExportModel
import domain.core.source.model.GalleryPreset
import kotlinx.serialization.json.Json

internal actual fun buildGalleryPresets(): List<GalleryPreset> = listOf(
    buildPreset("Commute", commuteNodes()),
    buildPreset("Music", musicNodes()),
    buildPreset("Daily", dailyNodes()),
)

private fun commuteNodes() = listOf(
    folder(id = 1, label = "Commute", sortOrder = 0),
    prompt(id = 2, parentId = 1, label = "Navigate home", sortOrder = 0),
    prompt(id = 3, parentId = 1, label = "Show traffic to work", sortOrder = 1),
)

private fun musicNodes() = listOf(
    folder(id = 1, label = "Music", sortOrder = 0),
    prompt(id = 2, parentId = 1, label = "Play my liked songs on YouTube Music", sortOrder = 0),
    prompt(id = 3, parentId = 1, label = "Play lo-fi playlist", sortOrder = 1),
)

private fun dailyNodes() = listOf(
    folder(id = 1, label = "Daily", sortOrder = 0),
    prompt(id = 2, parentId = 1, label = "What's on my calendar today?", sortOrder = 0),
    prompt(id = 3, parentId = 1, label = "Turn off the lights", sortOrder = 1),
)

private fun folder(id: Int, label: String, sortOrder: Int) = CommandNodeExportModel(
    id = id, parentId = 0, label = label, isFolder = true,
    isRotary = false, sortOrder = sortOrder, command = "", icon = null, commandType = "SYSTEM",
    cwCmdId = 0, ccwCmdId = 0,
)

private fun prompt(id: Int, parentId: Int, label: String, sortOrder: Int) = CommandNodeExportModel(
    id = id, parentId = parentId, label = label, isFolder = false,
    isRotary = false, sortOrder = sortOrder, command = label, icon = null, commandType = "PROMPT",
    cwCmdId = 0, ccwCmdId = 0,
)

private fun buildPreset(name: String, nodes: List<CommandNodeExportModel>): GalleryPreset {
    val description = nodes.filter { !it.isFolder }.joinToString(" · ") { it.label }
    val payload = CommandExportPayloadModel(version = 1, exportedAt = "", commands = nodes)
    return GalleryPreset(name = name, description = description, configJson = Json.encodeToString(CommandExportPayloadModel.serializer(), payload))
}
