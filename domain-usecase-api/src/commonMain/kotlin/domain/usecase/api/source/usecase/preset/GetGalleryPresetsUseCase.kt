package domain.usecase.api.source.usecase.preset

import domain.core.source.model.GalleryPreset

/**
 * Returns the built-in read-only gallery of preset configurations.
 *
 * Gallery presets are hard-coded in the app and never mutated. Calling [invoke] is
 * synchronous and always succeeds. To persist a gallery entry the user must explicitly
 * import it via [ImportPresetUseCase].
 *
 * @see GalleryPreset
 * @see ImportPresetUseCase
 */
public interface GetGalleryPresetsUseCase {
    /** Returns the full list of bundled [GalleryPreset] entries. */
    public operator fun invoke(): Result<List<GalleryPreset>>
}
