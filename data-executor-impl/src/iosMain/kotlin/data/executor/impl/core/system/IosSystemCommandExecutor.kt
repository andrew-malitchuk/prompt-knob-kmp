package data.executor.impl.core.system

import data.executor.impl.core.shortcut.SiriShortcutExecutor
import domain.core.source.monad.Optional

/**
 * Maps known system command strings to Siri Shortcut invocations on iOS.
 *
 * iOS does not expose direct volume or media control APIs to third-party apps.
 * The recommended approach is for the user to create Shortcuts with the same
 * names as the command strings (e.g., a Shortcut called "VOLUME_UP").
 *
 * If no matching shortcut is found, the user will see the Shortcuts app open
 * and a "not found" error — this is expected behaviour for unconfigured commands.
 */
internal class IosSystemCommandExecutor(private val shortcutExecutor: SiriShortcutExecutor) {

    suspend fun execute(command: String, sortOrder: Int = -1): Optional {
        // Resolve directional base names to Shortcut names the user has configured.
        // sortOrder 0 = CW = up/increase, 1 = CCW = down/decrease.
        val shortcutName = when (command.uppercase().trim()) {
            "VOLUME" -> if (sortOrder == 1) "VOLUME_DOWN" else "VOLUME_UP"
            "BRIGHTNESS" -> if (sortOrder == 1) "BRIGHTNESS_DOWN" else "BRIGHTNESS_UP"
            else -> command
        }
        return shortcutExecutor.execute(shortcutName)
    }
}
