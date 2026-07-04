package domain.usecase.api.source.usecase.command

import domain.usecase.api.source.monad.Optional

/**
 * Deletes a command or folder by [id].
 *
 * When the target is a folder, all its descendants are deleted recursively.
 */
public interface DeleteCommandUseCase {
    public suspend operator fun invoke(id: Int, isFolder: Boolean = false): Optional
}
