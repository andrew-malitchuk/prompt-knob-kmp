package domain.usecase.impl.source.usecase.preset

import domain.usecase.impl.core.mapper.toModel
import domain.core.source.model.CommandExportPayloadModel
import domain.core.source.monad.Failure
import domain.repository.api.source.repository.CommandRepository
import domain.repository.api.source.repository.PresetRepository
import domain.usecase.api.source.monad.Optional
import domain.usecase.api.source.usecase.preset.LoadPresetUseCase
import domain.usecase.impl.core.resultLauncher
import kotlinx.serialization.json.Json

internal class LoadPresetUseCaseImpl(
    private val presetRepository: PresetRepository,
    private val commandRepository: CommandRepository,
) : LoadPresetUseCase {

    override suspend fun invoke(id: Int): Optional = resultLauncher(
        errorMapper = { Failure.Technical.Database(it) }
    ) {
        val preset = presetRepository.getById(id)
            ?: throw Failure.Logic.NotFound
        val payload = Json.decodeFromString(CommandExportPayloadModel.serializer(), preset.configJson)

        val idMap = mutableMapOf<Int, Int>()
        val remaining = payload.commands.sortedBy { it.parentId }.toMutableList()

        while (remaining.isNotEmpty()) {
            val iter = remaining.iterator()
            var progress = false
            while (iter.hasNext()) {
                val dto = iter.next()
                if (dto.parentId == 0 || dto.parentId in idMap) {
                    val newParentId = if (dto.parentId == 0) 0 else idMap[dto.parentId]!!
                    val newId = commandRepository
                        .save(dto.toModel().copy(id = 0, parentId = newParentId))
                        .getOrThrow()
                    idMap[dto.id] = newId
                    iter.remove()
                    progress = true
                }
            }
            if (!progress) break
        }
    }
}
