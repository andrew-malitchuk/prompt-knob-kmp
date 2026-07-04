package domain.usecase.api.source.usecase.ble

import domain.core.source.model.FirmwareVersionModel
import kotlinx.coroutines.flow.Flow

/** Observes the connected device's firmware version. Emits null while unknown or disconnected. */
public interface ObserveFirmwareVersionUseCase {
    public operator fun invoke(): Flow<FirmwareVersionModel?>
}
