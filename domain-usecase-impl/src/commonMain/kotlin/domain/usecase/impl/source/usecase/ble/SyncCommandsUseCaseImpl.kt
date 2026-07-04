package domain.usecase.impl.source.usecase.ble

import domain.repository.api.source.repository.BleRepository
import domain.repository.api.source.repository.CommandRepository
import domain.usecase.api.source.monad.Optional
import domain.usecase.api.source.usecase.ble.SyncCommandsUseCase
import kotlinx.coroutines.flow.first

internal class SyncCommandsUseCaseImpl(
    private val bleRepository: BleRepository,
    private val commandRepository: CommandRepository,
) : SyncCommandsUseCase {

    override suspend fun invoke(): Optional = runCatching {
        val commands = commandRepository.observeAll().first()
        bleRepository.syncCommands(commands).getOrThrow()
    }
}
