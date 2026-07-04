package domain.usecase.impl.source.usecase.ble

import domain.repository.api.source.repository.BleRepository
import domain.usecase.api.source.monad.Optional
import domain.usecase.api.source.usecase.ble.SendNotifyUseCase

internal class SendNotifyUseCaseImpl(
    private val bleRepository: BleRepository,
) : SendNotifyUseCase {
    override suspend fun invoke(message: String, level: Int): Optional =
        bleRepository.sendNotify(message, level)
}
