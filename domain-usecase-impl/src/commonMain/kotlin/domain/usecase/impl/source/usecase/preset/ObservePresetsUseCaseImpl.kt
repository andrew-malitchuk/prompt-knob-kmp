package domain.usecase.impl.source.usecase.preset

import domain.core.source.model.PresetModel
import domain.repository.api.source.repository.PresetRepository
import domain.usecase.api.source.usecase.preset.ObservePresetsUseCase
import kotlinx.coroutines.flow.Flow

internal class ObservePresetsUseCaseImpl(
    private val presetRepository: PresetRepository,
) : ObservePresetsUseCase {
    override fun invoke(): Flow<List<PresetModel>> = presetRepository.observeAll()
}
