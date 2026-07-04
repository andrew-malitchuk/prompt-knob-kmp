package domain.usecase.impl.source.usecase.command

import domain.core.source.model.CommandExportPayloadModel
import domain.usecase.impl.core.mapper.toExportModel
import domain.repository.api.source.repository.CommandRepository
import domain.usecase.api.source.usecase.command.ExportCommandsUseCase
import kotlinx.coroutines.flow.first
import kotlin.time.Clock
import kotlinx.serialization.json.Json

internal class ExportCommandsUseCaseImpl(
    private val commandRepository: CommandRepository,
) : ExportCommandsUseCase {

    override suspend fun invoke(): Result<String> = runCatching {
        val commands = commandRepository.observeAll().first()
        val payload = CommandExportPayloadModel(
            version = 1,
            exportedAt = Clock.System.now().toString(),
            commands = commands.map { it.toExportModel() },
        )
        Json.encodeToString(CommandExportPayloadModel.serializer(), payload)
    }
}
