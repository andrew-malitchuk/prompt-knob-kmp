package domain.usecase.impl.source.usecase.ble

import domain.repository.api.source.repository.BleRepository
import domain.usecase.api.source.monad.Optional
import domain.usecase.api.source.usecase.ble.DisconnectDeviceUseCase

internal class DisconnectDeviceUseCaseImpl(
    private val bleRepository: BleRepository,
) : DisconnectDeviceUseCase {
    override suspend fun invoke(): Optional = runCatching {
        bleRepository.markSkipAutoReconnect()
        bleRepository.disconnect().getOrThrow()
    }
}
