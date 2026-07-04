package domain.usecase.api.source.usecase.preset

import domain.usecase.api.source.monad.Optional

/**
 * Saves the current command tree as a new named preset.
 *
 * Snapshots the active command layout at call time. The snapshot is stored independently
 * from the live tree — future edits to commands do not affect previously saved presets.
 *
 * @see LoadPresetUseCase
 * @see ObservePresetsUseCase
 */
public interface SavePresetUseCase {
    /**
     * @param name Display name for the new preset.
     * @return [Result.success] on successful save, [Result.failure] if the current tree
     *   is empty or the database write fails.
     */
    public suspend operator fun invoke(name: String): Optional
}
