package data.executor.impl.source.executor

import data.executor.api.source.executor.CommandExecutor
import data.executor.impl.core.shell.ShellCommandExecutor
import domain.core.source.monad.Optional
import data.executor.impl.core.system.MacOsSystemCommandExecutor
import domain.core.source.model.CommandNodeModel
import domain.core.source.model.CommandTypeModel

internal class MacOsCommandRouter(
    private val shellExecutor: ShellCommandExecutor,
    private val systemExecutor: MacOsSystemCommandExecutor,
) : CommandExecutor {

    override suspend fun execute(command: CommandNodeModel): Optional =
        when (command.commandType) {
            CommandTypeModel.SYSTEM -> systemExecutor.execute(command.command, command.sortOrder)
            CommandTypeModel.SHELL -> shellExecutor.execute(command.command)
            else -> Result.success(Unit)
        }
}
