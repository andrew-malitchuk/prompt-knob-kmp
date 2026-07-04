package domain.usecase.impl.source.usecase.ble

import domain.core.source.model.DeviceConnectionModel
import domain.repository.api.source.repository.BleRepository
import domain.usecase.api.source.usecase.ble.ObserveConnectionStateUseCase
import kotlinx.coroutines.flow.Flow

internal class ObserveConnectionStateUseCaseImpl(
    private val bleRepository: BleRepository,
) : ObserveConnectionStateUseCase {
    override fun invoke(): Flow<DeviceConnectionModel> =
        bleRepository.observeConnectionState()
}
