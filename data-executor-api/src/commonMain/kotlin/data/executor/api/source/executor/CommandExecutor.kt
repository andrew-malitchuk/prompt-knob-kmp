package data.executor.api.source.executor

import domain.core.source.model.CommandNodeModel
import domain.core.source.monad.Optional

/**
 * Platform-specific executor that turns a [CommandNodeModel] into a concrete action.
 *
 * Each platform provides its own implementation registered in the Koin graph
 * by `data-executor-impl`.
 */
public interface CommandExecutor {
    public suspend fun execute(command: CommandNodeModel): Optional
}
