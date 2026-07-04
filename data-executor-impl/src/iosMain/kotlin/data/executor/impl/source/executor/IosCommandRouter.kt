package data.executor.impl.source.executor

import data.executor.api.source.executor.CommandExecutor
import data.executor.impl.core.shortcut.SiriShortcutExecutor
import domain.core.source.monad.Optional
import data.executor.impl.core.system.IosSystemCommandExecutor
import domain.core.source.model.CommandNodeModel
import domain.core.source.model.CommandTypeModel

internal class IosCommandRouter(
    private val shortcutExecutor: SiriShortcutExecutor,
    private val systemExecutor: IosSystemCommandExecutor,
) : CommandExecutor {

    override suspend fun execute(command: CommandNodeModel): Optional =
        when (command.commandType) {
            CommandTypeModel.SHORTCUT -> shortcutExecutor.execute(command.command)
            CommandTypeModel.SYSTEM -> systemExecutor.execute(command.command, command.sortOrder)
            else -> Result.success(Unit)
        }
}
