package domain.usecase.api.source.usecase.preset

import domain.usecase.api.source.monad.Optional

/**
 * Activates a saved preset by replacing the current command tree with its stored layout.
 *
 * The active command tree is overwritten with the preset's `configJson`. Call
 * `SyncCommandsUseCase` after this to push the new layout to the connected device.
 *
 * @see SavePresetUseCase
 * @see ImportPresetUseCase
 */
public interface LoadPresetUseCase {
    /**
     * @param id Database id of the preset to activate.
     * @return [Result.success] on successful load, [Result.failure] if the preset was not found
     *   or the command tree could not be restored.
     */
    public suspend operator fun invoke(id: Int): Optional
}
