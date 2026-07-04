package domain.usecase.impl.source.usecase.ble

import domain.core.source.model.ClaudeState
import domain.repository.api.source.repository.BleRepository
import domain.usecase.api.source.monad.Optional
import domain.usecase.api.source.usecase.ble.SendClaudeStateUseCase

internal class SendClaudeStateUseCaseImpl(
    private val bleRepository: BleRepository,
) : SendClaudeStateUseCase {
    override suspend fun invoke(state: ClaudeState): Optional =
        bleRepository.sendClaudeState(state)
}
