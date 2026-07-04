package presentation.feature.command.core

import androidx.compose.ui.graphics.vector.ImageVector
import presentation.core.ui.source.kit.atom.icon.AlertTriangle
import presentation.core.ui.source.kit.atom.icon.ChevronLeft
import presentation.core.ui.source.kit.atom.icon.Home
import presentation.core.ui.source.kit.atom.icon.Image
import presentation.core.ui.source.kit.atom.icon.Lock
import presentation.core.ui.source.kit.atom.icon.RefreshCcw
import presentation.core.ui.source.kit.atom.icon.Settings
import presentation.core.ui.source.kit.atom.icon.Smartphone
import presentation.core.ui.source.kit.atom.icon.Sun
import presentation.core.ui.source.kit.atom.icon.Type
import presentation.core.ui.source.kit.atom.icon.Wifi

internal fun iconForSystemCommand(command: String): ImageVector = when (command) {
    "MEDIA_SCREEN" -> Smartphone
    "VOLUME_UP", "VOLUME_DOWN", "BRIGHTNESS_UP", "BRIGHTNESS_DOWN", "STAY_AWAKE" -> Sun
    "MUTE", "MIC_TOGGLE" -> Type
    "PLAY_PAUSE" -> RefreshCcw
    "NEXT_TRACK", "PREV_TRACK" -> Smartphone
    "BACK" -> ChevronLeft
    "HOME" -> Home
    "RECENTS", "MISSION_CONTROL" -> Wifi
    "NOTIFICATIONS" -> AlertTriangle
    "QUICK_SETTINGS", "SHOW_DESKTOP" -> Settings
    "SCREENSHOT" -> Image
    "LOCK_SCREEN", "SLEEP" -> Lock
    else -> Settings
}
