package domain.usecase.impl.source.usecase.preset

import domain.core.source.model.CommandExportPayloadModel
import domain.core.source.model.PresetModel
import domain.core.source.monad.Failure
import domain.repository.api.source.repository.PresetRepository
import domain.usecase.api.source.usecase.preset.ImportPresetUseCase
import domain.usecase.impl.core.resultLauncher
import kotlin.time.Clock
import kotlinx.serialization.json.Json

internal class ImportPresetUseCaseImpl(
    private val presetRepository: PresetRepository,
) : ImportPresetUseCase {

    override suspend fun invoke(name: String, json: String): Result<Int> = resultLauncher(
        errorMapper = { Failure.Technical.Database(it) }
    ) {
        Json.decodeFromString(CommandExportPayloadModel.serializer(), json)
        val now = Clock.System.now().toEpochMilliseconds()
        presetRepository.save(
            PresetModel(
                id = 0,
                name = name,
                createdAt = now,
                updatedAt = now,
                configJson = json,
            )
        ).getOrThrow()
    }
}
