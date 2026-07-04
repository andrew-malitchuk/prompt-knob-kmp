package domain.usecase.impl.source.usecase.command

import domain.repository.api.source.repository.CommandRepository
import domain.usecase.api.source.monad.Optional
import domain.usecase.api.source.usecase.command.DeleteCommandUseCase

internal class DeleteCommandUseCaseImpl(
    private val repository: CommandRepository,
) : DeleteCommandUseCase {

    override suspend fun invoke(id: Int, isFolder: Boolean): Optional = runCatching {
        if (isFolder) {
            // Delete all descendants first, then the folder record itself.
            repository.deleteByParentIdRecursive(id).getOrThrow()
        }
        repository.deleteById(id).getOrThrow()
    }
}
