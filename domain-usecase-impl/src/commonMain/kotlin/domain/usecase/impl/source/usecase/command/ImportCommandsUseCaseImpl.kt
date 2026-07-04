package domain.usecase.impl.source.usecase.command

import domain.core.source.model.CommandExportPayloadModel
import domain.usecase.impl.core.mapper.toModel
import domain.repository.api.source.repository.CommandRepository
import domain.usecase.api.source.usecase.command.ImportCommandsUseCase
import kotlinx.serialization.json.Json

internal class ImportCommandsUseCaseImpl(
    private val commandRepository: CommandRepository,
) : ImportCommandsUseCase {

    override suspend fun invoke(json: String): Result<Int> = runCatching {
        val payload = Json.decodeFromString(CommandExportPayloadModel.serializer(), json)
        commandRepository.deleteAll().getOrThrow()

        // Map from original exported id → newly assigned DB id.
        val idMap = mutableMapOf<Int, Int>()

        // Process nodes level-by-level (root first, then children) so that by the time
        // we insert a child its parent's new id is already in idMap.
        val remaining = payload.commands.sortedBy { it.parentId }.toMutableList()
        var inserted = 0

        while (remaining.isNotEmpty()) {
            val iter = remaining.iterator()
            var progress = false
            while (iter.hasNext()) {
                val dto = iter.next()
                // Root nodes (parentId == 0) or nodes whose parent was already inserted.
                if (dto.parentId == 0 || dto.parentId in idMap) {
                    val newParentId = if (dto.parentId == 0) 0 else idMap[dto.parentId]!!
                    val newId = commandRepository
                        .save(dto.toModel().copy(id = 0, parentId = newParentId))
                        .getOrThrow()
                    idMap[dto.id] = newId
                    iter.remove()
                    inserted++
                    progress = true
                }
            }
            // Guard against cycles or broken references in the payload.
            if (!progress) break
        }

        inserted
    }
}
