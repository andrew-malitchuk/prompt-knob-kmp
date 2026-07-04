package domain.usecase.impl.source.usecase.ble

import domain.core.source.monad.Failure
import domain.repository.api.source.repository.BleRepository
import domain.usecase.api.source.usecase.ble.CheckBlePermissionsUseCase
import domain.usecase.impl.core.resultLauncher

internal class CheckBlePermissionsUseCaseImpl(
    private val bleRepository: BleRepository,
) : CheckBlePermissionsUseCase {
    override suspend fun invoke(): Result<Boolean> =
        resultLauncher(errorMapper = { Failure.Technical.Platform(it) }) {
            bleRepository.checkPermissions()
        }
}
