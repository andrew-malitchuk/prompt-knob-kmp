package domain.usecase.api.source.usecase.ble

import domain.core.source.model.DeviceConnectionModel
import kotlinx.coroutines.flow.Flow

/** Observes the current BLE connection state and device info. */
public interface ObserveConnectionStateUseCase {
    public operator fun invoke(): Flow<DeviceConnectionModel>
}
