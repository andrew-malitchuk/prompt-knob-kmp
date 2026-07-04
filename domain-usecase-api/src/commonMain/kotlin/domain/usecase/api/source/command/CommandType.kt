package domain.usecase.api.source.command

import domain.core.source.model.CommandTypeModel

/** Returns the [CommandTypeModel] values available on the current platform. */
public expect fun availableCommandTypes(): List<CommandTypeModel>
