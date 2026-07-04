package domain.usecase.impl.source.usecase.ble

import domain.repository.api.source.repository.BleRepository
import domain.usecase.api.source.usecase.ble.ConsumeSkipAutoReconnectUseCase

internal class ConsumeSkipAutoReconnectUseCaseImpl(
    private val bleRepository: BleRepository,
) : ConsumeSkipAutoReconnectUseCase {
    override fun invoke(): Result<Boolean> = Result.success(bleRepository.consumeSkipAutoReconnect())
}
