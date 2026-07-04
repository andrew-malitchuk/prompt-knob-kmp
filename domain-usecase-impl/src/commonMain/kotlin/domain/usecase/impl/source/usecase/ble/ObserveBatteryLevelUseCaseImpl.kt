package domain.usecase.impl.source.usecase.ble

import domain.repository.api.source.repository.BleRepository
import domain.usecase.api.source.usecase.ble.ObserveBatteryLevelUseCase
import kotlinx.coroutines.flow.Flow

internal class ObserveBatteryLevelUseCaseImpl(
    private val bleRepository: BleRepository,
) : ObserveBatteryLevelUseCase {
    override fun invoke(): Flow<Int?> = bleRepository.observeBatteryLevel()
}
