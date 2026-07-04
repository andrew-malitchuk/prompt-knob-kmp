package domain.usecase.impl.source.usecase.ble

import domain.repository.api.source.repository.BleRepository
import domain.usecase.api.source.monad.Optional
import domain.usecase.api.source.usecase.ble.ConnectToDeviceUseCase

internal class ConnectToDeviceUseCaseImpl(
    private val bleRepository: BleRepository,
) : ConnectToDeviceUseCase {
    override suspend fun invoke(address: String, name: String?): Optional =
        bleRepository.connect(address, name)
}
