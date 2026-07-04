package domain.usecase.impl.source.usecase.ble

import domain.core.source.model.FirmwareVersionModel
import domain.repository.api.source.repository.BleRepository
import domain.usecase.api.source.usecase.ble.ObserveFirmwareVersionUseCase
import kotlinx.coroutines.flow.Flow

internal class ObserveFirmwareVersionUseCaseImpl(
    private val bleRepository: BleRepository,
) : ObserveFirmwareVersionUseCase {
    override fun invoke(): Flow<FirmwareVersionModel?> =
        bleRepository.observeFirmwareVersion()
}
