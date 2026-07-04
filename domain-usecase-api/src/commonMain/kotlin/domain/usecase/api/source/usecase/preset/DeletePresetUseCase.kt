package domain.usecase.api.source.usecase.preset

import domain.usecase.api.source.monad.Optional

/**
 * Deletes a saved preset by its id.
 *
 * Does not affect the active command tree; call [SyncCommandsUseCase] afterwards if needed.
 *
 * @see SavePresetUseCase
 * @see ObservePresetsUseCase
 */
public interface DeletePresetUseCase {
    /**
     * @param id Database id of the preset to delete.
     * @return [Result.success] on successful deletion, [Result.failure] if the preset was not found.
     */
    public suspend operator fun invoke(id: Int): Optional
}
