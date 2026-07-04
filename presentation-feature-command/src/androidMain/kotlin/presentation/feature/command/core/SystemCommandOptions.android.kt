package presentation.feature.command.core

import domain.core.source.model.SystemCommandOption

internal actual fun availableSystemCommandOptions(): List<SystemCommandOption> = listOf(
    // --- Media Screen preset ---
    SystemCommandOption(
        command = "MEDIA_SCREEN",
        label = "MEDIA SCREEN",
        description = "Volume, play/pause, prev and next mapped to the knob",
    ),
    // --- Audio / Volume ---
    SystemCommandOption(
        command = "VOLUME_UP",
        label = "VOLUME UP",
        description = "Raise media volume one step",
    ),
    SystemCommandOption(
        command = "VOLUME_DOWN",
        label = "VOLUME DOWN",
        description = "Lower media volume one step",
    ),
    SystemCommandOption(
        command = "MUTE",
        label = "MUTE",
        description = "Toggle output mute on / off",
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
    // --- Navigation (requires Accessibility Service) ---
    SystemCommandOption(
        command = "BACK",
        label = "BACK",
        description = "Simulate the Back gesture",
    ),
    SystemCommandOption(
        command = "HOME",
        label = "HOME",
        description = "Go to the home screen",
    ),
    SystemCommandOption(
        command = "RECENTS",
        label = "RECENT APPS",
        description = "Open the recent apps switcher",
    ),
    SystemCommandOption(
        command = "NOTIFICATIONS",
        label = "NOTIFICATIONS",
        description = "Expand the notification shade",
    ),
    SystemCommandOption(
        command = "QUICK_SETTINGS",
        label = "QUICK SETTINGS",
        description = "Expand the quick settings panel",
    ),
    SystemCommandOption(
        command = "SCREENSHOT",
        label = "SCREENSHOT",
        description = "Capture the screen (Android 9+)",
    ),
    // --- Device policy (requires Device Admin) ---
    SystemCommandOption(
        command = "LOCK_SCREEN",
        label = "LOCK SCREEN",
        description = "Lock the device immediately",
    ),
)
