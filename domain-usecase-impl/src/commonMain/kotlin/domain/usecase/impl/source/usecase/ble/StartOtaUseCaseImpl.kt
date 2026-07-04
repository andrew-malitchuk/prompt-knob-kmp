package domain.usecase.impl.source.usecase.ble

import domain.core.source.model.FirmwareVersionModel
import domain.core.source.model.OtaStateModel
import domain.repository.api.source.repository.BleRepository
import domain.usecase.api.source.usecase.ble.StartOtaUseCase
import kotlinx.coroutines.flow.Flow

internal class StartOtaUseCaseImpl(
    private val bleRepository: BleRepository,
) : StartOtaUseCase {
    override operator fun invoke(firmware: ByteArray, version: FirmwareVersionModel): Flow<OtaStateModel> =
        bleRepository.startOta(firmware, version)
}
