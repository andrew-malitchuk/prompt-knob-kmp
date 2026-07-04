package domain.usecase.impl.core

import domain.core.source.model.CommandExportPayloadModel
import domain.core.source.model.CommandNodeExportModel
import domain.core.source.model.GalleryPreset
import kotlinx.serialization.json.Json

internal actual fun buildGalleryPresets(): List<GalleryPreset> = listOf(
    buildPreset("Media", mediaNodes()),
    buildPreset("Focus", focusNodes()),
    buildPreset("Display", displayNodes()),
    buildPreset("Terminal", terminalNodes()),
)

private fun mediaNodes() = listOf(
    folder(id = 1, label = "Media", sortOrder = 0),
    system(id = 2, parentId = 1, label = "Play / Pause", command = "PLAY_PAUSE", sortOrder = 0),
    system(id = 3, parentId = 1, label = "Next Track", command = "NEXT_TRACK", sortOrder = 1),
    system(id = 4, parentId = 1, label = "Prev Track", command = "PREV_TRACK", sortOrder = 2),
    system(id = 5, parentId = 1, label = "Volume Up", command = "VOLUME_UP", sortOrder = 3),
    system(id = 6, parentId = 1, label = "Volume Down", command = "VOLUME_DOWN", sortOrder = 4),
)

private fun focusNodes() = listOf(
    folder(id = 1, label = "Focus", sortOrder = 0),
    system(id = 2, parentId = 1, label = "Mic Toggle", command = "MIC_TOGGLE", sortOrder = 0),
    system(id = 3, parentId = 1, label = "Mute", command = "MUTE", sortOrder = 1),
    system(id = 4, parentId = 1, label = "Lock Screen", command = "LOCK_SCREEN", sortOrder = 2),
    system(id = 5, parentId = 1, label = "Stay Awake", command = "STAY_AWAKE", sortOrder = 3),
)

private fun displayNodes() = listOf(
    folder(id = 1, label = "Display", sortOrder = 0),
    system(id = 2, parentId = 1, label = "Brightness Up", command = "BRIGHTNESS_UP", sortOrder = 0),
    system(id = 3, parentId = 1, label = "Brightness Down", command = "BRIGHTNESS_DOWN", sortOrder = 1),
    system(id = 4, parentId = 1, label = "Mission Control", command = "MISSION_CONTROL", sortOrder = 2),
    system(id = 5, parentId = 1, label = "Show Desktop", command = "SHOW_DESKTOP", sortOrder = 3),
)

private fun terminalNodes() = listOf(
    folder(id = 1, label = "Terminal", sortOrder = 0),
    shell(id = 2, parentId = 1, label = "Open Terminal", command = "open -a Terminal", sortOrder = 0),
    shell(id = 3, parentId = 1, label = "Open VS Code", command = "open -a 'Visual Studio Code'", sortOrder = 1),
    shell(id = 4, parentId = 1, label = "Say Done", command = "say 'Build complete'", sortOrder = 2),
    system(id = 5, parentId = 1, label = "Screenshot", command = "SCREENSHOT", sortOrder = 3),
)

private fun folder(id: Int, label: String, sortOrder: Int) = CommandNodeExportModel(
    id = id, parentId = 0, label = label, isFolder = true,
    isRotary = false, sortOrder = sortOrder, command = "", icon = null, commandType = "SYSTEM",
    cwCmdId = 0, ccwCmdId = 0,
)

private fun system(id: Int, parentId: Int, label: String, command: String, sortOrder: Int) = CommandNodeExportModel(
    id = id, parentId = parentId, label = label, isFolder = false,
    isRotary = false, sortOrder = sortOrder, command = command, icon = null, commandType = "SYSTEM",
    cwCmdId = 0, ccwCmdId = 0,
)

private fun shell(id: Int, parentId: Int, label: String, command: String, sortOrder: Int) = CommandNodeExportModel(
    id = id, parentId = parentId, label = label, isFolder = false,
    isRotary = false, sortOrder = sortOrder, command = command, icon = null, commandType = "SHELL",
    cwCmdId = 0, ccwCmdId = 0,
)

private fun buildPreset(name: String, nodes: List<CommandNodeExportModel>): GalleryPreset {
    val description = nodes.filter { !it.isFolder }.joinToString(" · ") { it.label }
    val payload = CommandExportPayloadModel(version = 1, exportedAt = "", commands = nodes)
    return GalleryPreset(name = name, description = description, configJson = Json.encodeToString(CommandExportPayloadModel.serializer(), payload))
}
