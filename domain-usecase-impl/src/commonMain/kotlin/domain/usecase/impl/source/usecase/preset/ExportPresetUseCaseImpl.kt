package domain.usecase.impl.source.usecase.preset

import domain.core.source.monad.Failure
import domain.repository.api.source.repository.PresetRepository
import domain.usecase.api.source.usecase.preset.ExportPresetUseCase
import domain.usecase.impl.core.resultLauncher

internal class ExportPresetUseCaseImpl(
    private val presetRepository: PresetRepository,
) : ExportPresetUseCase {

    override suspend fun invoke(id: Int): Result<String> = resultLauncher(
        errorMapper = { Failure.Technical.Database(it) }
    ) {
        val preset = presetRepository.getById(id)
            ?: throw Failure.Logic.NotFound
        preset.configJson
    }
}
