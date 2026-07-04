package domain.core.source.model

import domain.core.source.model.base.Model

/**
 * Discriminates how a [CommandNodeModel]'s [CommandNodeModel.command] string should be executed
 * on the host platform.
 *
 * - [SYSTEM] — Maps to a known system action (volume, media, brightness, mic, etc.).
 * - [PROMPT] — Sends the string as a prompt to Google Assistant (Android only).
 * - [SHELL]  — Executes the string as a bash/shell command (macOS only).
 * - [SHORTCUT] — Opens the Siri Shortcut with the matching name (iOS only).
 */
public enum class CommandTypeModel : Model {
    SYSTEM,
    PROMPT,
    SHELL,
    SHORTCUT,
}
