package domain.usecase.impl.source.usecase.ble

import domain.core.source.monad.Failure
import domain.repository.api.source.repository.BleRepository
import domain.usecase.api.source.usecase.ble.GetLastDeviceUseCase
import domain.usecase.impl.core.resultLauncher

internal class GetLastDeviceUseCaseImpl(
    private val bleRepository: BleRepository,
) : GetLastDeviceUseCase {
    override suspend fun invoke(): Result<String?> =
        resultLauncher(errorMapper = { Failure.Technical.Preference(it) }) {
            bleRepository.getLastConnectedDevice()
        }
}
