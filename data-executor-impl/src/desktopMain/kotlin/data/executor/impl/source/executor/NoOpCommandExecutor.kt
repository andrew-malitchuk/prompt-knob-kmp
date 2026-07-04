package data.executor.impl.source.executor

import co.touchlab.kermit.Logger
import data.executor.api.source.executor.CommandExecutor
import domain.core.source.monad.Optional
import domain.core.source.model.CommandNodeModel

internal class NoOpCommandExecutor : CommandExecutor {
    private val log = Logger.withTag("NoOpCommandExecutor")

    override suspend fun execute(command: CommandNodeModel): Optional {
        log.d { "${command.label} (${command.commandType}) → ${command.command}" }
        return Result.success(Unit)
    }
}
