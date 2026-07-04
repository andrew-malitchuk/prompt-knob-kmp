package data.executor.impl.core.system

import data.executor.impl.core.shell.ShellCommandExecutor
import domain.core.source.monad.Optional

/**
 * Maps known system command strings to shell/AppleScript invocations via [ShellCommandExecutor].
 *
 * Supported strings (case-insensitive):
 *
 * **Media / Audio:**
 * - `PLAY_PAUSE`, `NEXT_TRACK`, `PREV_TRACK`
 * - `VOLUME_UP`, `VOLUME_DOWN`, `MUTE`
 * - `MIC_TOGGLE`
 *
 * **Display / Brightness:**
 * - `BRIGHTNESS_UP`, `BRIGHTNESS_DOWN`
 *
 * **Window management:**
 * - `MISSION_CONTROL` — opens Mission Control overview
 * - `SHOW_DESKTOP` — shows the desktop (hides all windows)
 *
 * **System:**
 * - `LOCK_SCREEN` — locks the screen (Cmd+Ctrl+Q shortcut via AppleScript)
 * - `SLEEP` — puts the Mac to sleep immediately (`pmset sleepnow`)
 * - `SCREENSHOT` — captures the full screen to ~/Desktop/screenshot.png (no sound)
 * - `STAY_AWAKE` — runs `caffeinate` for 1 h, preventing sleep
 *
 * Unrecognised strings fall through to [ShellCommandExecutor.execute] directly,
 * so `SYSTEM` type can also be used for arbitrary shell commands on macOS.
 */
internal class MacOsSystemCommandExecutor(private val shell: ShellCommandExecutor) {

    suspend fun execute(command: String, sortOrder: Int = -1): Optional = when (command.uppercase().trim()) {
        // --- Media ---
        "PLAY_PAUSE" -> shell.execute(
            "osascript -e 'tell application \"Music\" to playpause'"
        )
        "NEXT_TRACK" -> shell.execute(
            "osascript -e 'tell application \"Music\" to next track'"
        )
        "PREV_TRACK" -> shell.execute(
            "osascript -e 'tell application \"Music\" to previous track'"
        )

        // --- Volume ---
        // Directional base name: sortOrder 0 = CW = up, 1 = CCW = down
        "VOLUME" -> if (sortOrder == 1) {
            shell.execute("osascript -e 'set volume output volume ((output volume of (get volume settings)) - 10)'")
        } else {
            shell.execute("osascript -e 'set volume output volume ((output volume of (get volume settings)) + 10)'")
        }
        "VOLUME_UP" -> shell.execute(
            "osascript -e 'set volume output volume ((output volume of (get volume settings)) + 10)'"
        )
        "VOLUME_DOWN" -> shell.execute(
            "osascript -e 'set volume output volume ((output volume of (get volume settings)) - 10)'"
        )
        "MUTE" -> shell.execute(
            "osascript -e 'set volume with output muted'"
        )
        "MIC_TOGGLE" -> shell.execute(
            "osascript -e 'if (input volume of (get volume settings)) > 0 then set volume input volume 0 else set volume input volume 100'"
        )

        // --- Brightness ---
        // Directional base name: sortOrder 0 = CW = up (key 144), 1 = CCW = down (key 145)
        "BRIGHTNESS" -> if (sortOrder == 1) {
            shell.execute("osascript -e 'tell application \"System Events\" to key code 145'")
        } else {
            shell.execute("osascript -e 'tell application \"System Events\" to key code 144'")
        }
        "BRIGHTNESS_UP" -> shell.execute(
            "osascript -e 'tell application \"System Events\" to key code 144'"
        )
        "BRIGHTNESS_DOWN" -> shell.execute(
            "osascript -e 'tell application \"System Events\" to key code 145'"
        )

        // --- Window management ---
        "MISSION_CONTROL" -> shell.execute(
            "open -b com.apple.exposelauncher"
        )
        "SHOW_DESKTOP" -> shell.execute(
            "osascript -e 'tell application \"System Events\" to key code 103 using {command down}'"
        )

        // --- System ---
        "LOCK_SCREEN" -> shell.execute(
            // Cmd+Ctrl+Q — standard lock screen shortcut since macOS Sierra
            "osascript -e 'tell application \"System Events\" to key code 12 using {control down, command down}'"
        )
        "SLEEP" -> shell.execute("pmset sleepnow")
        "SCREENSHOT" -> shell.execute("screencapture -x ~/Desktop/screenshot.png")
        "STAY_AWAKE" -> shell.execute("caffeinate -u -t 3600 &")

        // Unrecognised: treat the whole string as a raw shell command
        else -> shell.execute(command)
    }
}
