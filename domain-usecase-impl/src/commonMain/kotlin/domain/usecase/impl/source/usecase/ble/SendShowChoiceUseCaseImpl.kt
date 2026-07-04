package domain.usecase.impl.source.usecase.ble

import domain.repository.api.source.repository.BleRepository
import domain.usecase.api.source.monad.Optional
import domain.usecase.api.source.usecase.ble.SendShowChoiceUseCase

internal class SendShowChoiceUseCaseImpl(
    private val bleRepository: BleRepository,
) : SendShowChoiceUseCase {
    override suspend fun invoke(options: List<String>): Optional =
        bleRepository.sendShowChoice(options)
}
