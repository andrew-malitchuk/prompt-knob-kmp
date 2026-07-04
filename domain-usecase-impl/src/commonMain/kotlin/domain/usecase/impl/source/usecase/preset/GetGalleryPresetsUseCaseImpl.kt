package domain.usecase.impl.source.usecase.preset

import domain.core.source.model.GalleryPreset
import domain.usecase.api.source.usecase.preset.GetGalleryPresetsUseCase
import domain.usecase.impl.core.buildGalleryPresets

internal class GetGalleryPresetsUseCaseImpl : GetGalleryPresetsUseCase {
    override fun invoke(): Result<List<GalleryPreset>> = Result.success(buildGalleryPresets())
}
