package domain.usecase.api.source.usecase.ble

import domain.core.source.model.BleDeviceModel
import kotlinx.coroutines.flow.Flow

/** Returns a [Flow] of nearby [BleDeviceModel] entries from a BLE scan. */
public interface ScanForDevicesUseCase {
    public operator fun invoke(): Flow<BleDeviceModel>
}
