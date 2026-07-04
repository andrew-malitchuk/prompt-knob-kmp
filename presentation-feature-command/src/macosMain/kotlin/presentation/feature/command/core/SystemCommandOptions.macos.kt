package presentation.feature.command.core

import domain.core.source.model.SystemCommandOption

internal actual fun availableSystemCommandOptions(): List<SystemCommandOption> = listOf(
    // --- Volume ---
    SystemCommandOption(
        command = "VOLUME_UP",
        label = "VOLUME UP",
        description = "Raise output volume by 10%",
    ),
    SystemCommandOption(
        command = "VOLUME_DOWN",
        label = "VOLUME DOWN",
        description = "Lower output volume by 10%",
    ),
    SystemCommandOption(
        command = "MUTE",
        label = "MUTE",
        description = "Toggle output mute on / off",
    ),
    SystemCommandOption(
        command = "MIC_TOGGLE",
        label = "MIC TOGGLE",
        description = "Toggle microphone input mute",
    ),
    // --- Brightness ---
    SystemCommandOption(
        command = "BRIGHTNESS_UP",
        label = "BRIGHTNESS UP",
        description = "Increase display brightness one step",
    ),
    SystemCommandOption(
        command = "BRIGHTNESS_DOWN",
        label = "BRIGHTNESS DOWN",
        description = "Decrease display brightness one step",
    ),
    // --- Media playback ---
    SystemCommandOption(
        command = "PLAY_PAUSE",
        label = "PLAY / PAUSE",
        description = "Toggle current media playback",
    ),
    SystemCommandOption(
        command = "NEXT_TRACK",
        label = "NEXT TRACK",
        description = "Skip to the next media track",
    ),
    SystemCommandOption(
        command = "PREV_TRACK",
        label = "PREV TRACK",
        description = "Go back to the previous track",
    ),
    // --- Window management ---
    SystemCommandOption(
        command = "MISSION_CONTROL",
        label = "MISSION CONTROL",
        description = "Open Mission Control overview",
    ),
    SystemCommandOption(
        command = "SHOW_DESKTOP",
        label = "SHOW DESKTOP",
        description = "Hide all windows and show the desktop",
    ),
    // --- System ---
    SystemCommandOption(
        command = "LOCK_SCREEN",
        label = "LOCK SCREEN",
        description = "Lock the screen immediately",
    ),
    SystemCommandOption(
        command = "SLEEP",
        label = "SLEEP",
        description = "Put the Mac to sleep",
    ),
    SystemCommandOption(
        command = "SCREENSHOT",
        label = "SCREENSHOT",
        description = "Capture full screen to ~/Desktop",
    ),
    SystemCommandOption(
        command = "STAY_AWAKE",
        label = "STAY AWAKE",
        description = "Prevent sleep for 1 hour",
    ),
)
