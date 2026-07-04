package domain.usecase.impl.source.usecase.ble

import domain.core.source.model.BleDeviceModel
import domain.repository.api.source.repository.BleRepository
import domain.usecase.api.source.usecase.ble.ScanForDevicesUseCase
import kotlinx.coroutines.flow.Flow

internal class ScanForDevicesUseCaseImpl(
    private val bleRepository: BleRepository,
) : ScanForDevicesUseCase {
    override fun invoke(): Flow<BleDeviceModel> = bleRepository.scanForDevices()
}
