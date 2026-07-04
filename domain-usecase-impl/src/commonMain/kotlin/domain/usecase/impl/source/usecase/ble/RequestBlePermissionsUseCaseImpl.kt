package domain.usecase.impl.source.usecase.ble

import domain.core.source.monad.Failure
import domain.repository.api.source.repository.BleRepository
import domain.usecase.api.source.usecase.ble.RequestBlePermissionsUseCase
import domain.usecase.impl.core.resultLauncher

internal class RequestBlePermissionsUseCaseImpl(
    private val bleRepository: BleRepository,
) : RequestBlePermissionsUseCase {
    override suspend fun invoke(): Result<Boolean> =
        resultLauncher(errorMapper = { Failure.Technical.Platform(it) }) {
            bleRepository.requestPermissions()
        }
}
