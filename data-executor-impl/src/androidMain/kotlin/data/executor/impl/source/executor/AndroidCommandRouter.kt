package data.executor.impl.source.executor

import data.executor.api.source.executor.CommandExecutor
import data.executor.impl.core.assistant.AssistantPromptExecutor
import domain.core.source.monad.Optional
import data.executor.impl.core.system.AndroidSystemCommandExecutor
import domain.core.source.model.CommandNodeModel
import domain.core.source.model.CommandTypeModel

internal class AndroidCommandRouter(
    private val assistantExecutor: AssistantPromptExecutor,
    private val systemExecutor: AndroidSystemCommandExecutor,
) : CommandExecutor {

    override suspend fun execute(command: CommandNodeModel): Optional =
        when (command.commandType) {
            CommandTypeModel.PROMPT -> assistantExecutor.execute(command.command)
            CommandTypeModel.SYSTEM -> systemExecutor.execute(command.command, command.sortOrder)
            else -> Result.success(Unit)
        }
}
