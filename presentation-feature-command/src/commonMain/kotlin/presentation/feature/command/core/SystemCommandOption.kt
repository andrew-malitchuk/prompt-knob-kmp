package presentation.feature.command.core

import domain.core.source.model.SystemCommandOption

/**
 * Returns the ordered list of preset [SystemCommandOption]s available on the
 * current platform. Empty on platforms that do not support a system command picker.
 */
internal expect fun availableSystemCommandOptions(): List<SystemCommandOption>
