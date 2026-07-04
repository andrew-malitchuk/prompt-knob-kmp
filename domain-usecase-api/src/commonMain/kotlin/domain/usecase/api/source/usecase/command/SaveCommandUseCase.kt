package domain.usecase.api.source.usecase.command

import domain.core.source.model.CommandNodeModel

/**
 * Inserts or updates a [CommandNodeModel] in local storage.
 *
 * A [CommandNodeModel.id] of 0 triggers an insert; any other value performs an update.
 */
public interface SaveCommandUseCase {
    /** Inserts or updates [command]. Returns the persisted row id (auto-generated for inserts). */
    public suspend operator fun invoke(command: CommandNodeModel): Result<Int>
}
