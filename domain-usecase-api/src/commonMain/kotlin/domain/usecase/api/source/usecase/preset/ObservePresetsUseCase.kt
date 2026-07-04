package domain.usecase.api.source.usecase.preset

import domain.core.source.model.PresetModel
import kotlinx.coroutines.flow.Flow

/**
 * Observes the list of user-saved presets as a hot [Flow], ordered by last-updated time descending.
 *
 * Re-emits whenever a preset is saved, updated, or deleted.
 *
 * @see SavePresetUseCase
 * @see DeletePresetUseCase
 */
public interface ObservePresetsUseCase {
    /** Returns a [Flow] that emits the current preset list and updates on every change. */
    public operator fun invoke(): Flow<List<PresetModel>>
}
