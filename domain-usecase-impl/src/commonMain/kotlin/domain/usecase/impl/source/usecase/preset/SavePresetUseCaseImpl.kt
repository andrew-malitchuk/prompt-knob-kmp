package domain.usecase.impl.source.usecase.preset

import domain.usecase.impl.core.mapper.toExportModel
import domain.core.source.model.CommandExportPayloadModel
import domain.core.source.model.PresetModel
import domain.core.source.monad.Failure
import domain.repository.api.source.repository.CommandRepository
import domain.repository.api.source.repository.PresetRepository
import domain.usecase.api.source.monad.Optional
import domain.usecase.api.source.usecase.preset.SavePresetUseCase
import domain.usecase.impl.core.resultLauncher
import kotlinx.coroutines.flow.first
import kotlin.time.Clock
import kotlinx.serialization.json.Json

internal class SavePresetUseCaseImpl(
    private val commandRepository: CommandRepository,
    private val presetRepository: PresetRepository,
) : SavePresetUseCase {

    override suspend fun invoke(name: String): Optional = resultLauncher(
        errorMapper = { Failure.Technical.Database(it) }
    ) {
        val commands = commandRepository.observeAll().first()
        val payload = CommandExportPayloadModel(
            version = 1,
            exportedAt = Clock.System.now().toString(),
            commands = commands.map { it.toExportModel() },
        )
        val json = Json.encodeToString(CommandExportPayloadModel.serializer(), payload)
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
