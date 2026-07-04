package domain.usecase.api.source.command

import domain.core.source.model.CommandTypeModel

public actual fun availableCommandTypes(): List<CommandTypeModel> = listOf(
    CommandTypeModel.SYSTEM,
    CommandTypeModel.SHORTCUT,
)
