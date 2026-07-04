package domain.repository.api.source.repository

import domain.core.source.model.PresetModel
import domain.core.source.monad.Optional
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for preset persistence.
 *
 * Abstracts the local database backing store. Implementation lives in `data-repository-impl`.
 *
 * @see PresetModel
 */
public interface PresetRepository {

    /** Returns a hot [Flow] of all saved presets, ordered by last-updated descending. */
    public fun observeAll(): Flow<List<PresetModel>>

    /**
     * Returns a single preset by its [id], or null if not found.
     *
     * @param id Database primary key.
     */
    public suspend fun getById(id: Int): PresetModel?

    /**
     * Inserts or updates a [preset]. Returns the persisted row id.
     *
     * @param preset Preset to save. A zero [PresetModel.id] triggers an insert.
     */
    public suspend fun save(preset: PresetModel): Result<Int>

    /**
     * Deletes the preset with the given [id].
     *
     * @param id Database primary key of the preset to delete.
     */
    public suspend fun deleteById(id: Int): Optional
}
