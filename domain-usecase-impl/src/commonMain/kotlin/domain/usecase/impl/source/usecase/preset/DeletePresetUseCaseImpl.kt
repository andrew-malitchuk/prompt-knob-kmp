package domain.usecase.impl.source.usecase.preset

import domain.repository.api.source.repository.PresetRepository
import domain.usecase.api.source.monad.Optional
import domain.usecase.api.source.usecase.preset.DeletePresetUseCase

internal class DeletePresetUseCaseImpl(
    private val presetRepository: PresetRepository,
) : DeletePresetUseCase {
    override suspend fun invoke(id: Int): Optional = presetRepository.deleteById(id)
}
