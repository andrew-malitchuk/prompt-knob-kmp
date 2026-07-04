package domain.usecase.api.source.usecase.command

import domain.core.source.model.CommandNodeModel

/** Fetches a single [CommandNodeModel] by its [id]. Returns failure if not found. */
public interface GetCommandByIdUseCase {
    public suspend operator fun invoke(id: Int): Result<CommandNodeModel>
}
