package domain.core.source.model

import domain.core.source.model.base.Model

/**
 * Describes a single preset system command available on the current platform.
 *
 * @property command The command string passed to the executor.
 * @property label Human-readable display name shown in the picker.
 * @property description One-line explanation of what the command does.
 */
public data class SystemCommandOption(
    val command: String,
    val label: String,
    val description: String,
) : Model
