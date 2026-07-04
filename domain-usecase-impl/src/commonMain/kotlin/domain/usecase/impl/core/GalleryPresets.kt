package domain.usecase.impl.core

import domain.core.source.model.GalleryPreset

internal expect fun buildGalleryPresets(): List<GalleryPreset>
