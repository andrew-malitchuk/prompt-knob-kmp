package domain.usecase.api.source.usecase.executor

import domain.usecase.api.source.monad.Optional
import domain.core.source.model.CommandNodeModel

/**
 * Executes a [CommandNodeModel] on the current platform.
 *
 * Delegates to the platform-specific [domain.usecase.api.source.executor.CommandExecutor].
 */
public interface ExecuteCommandUseCase {
    public suspend operator fun invoke(command: CommandNodeModel): Optional
}
