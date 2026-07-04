package domain.usecase.api.source.usecase.ble

import domain.core.source.model.FirmwareVersionModel
import domain.core.source.model.OtaStateModel
import kotlinx.coroutines.flow.Flow

/**
 * Executes an OTA firmware update session against the connected device.
 *
 * Returns a cold [Flow] that emits [OtaStateModel] transitions for the duration
 * of the update. Cancelling the collecting coroutine sends OTA_ABORT to the
 * device and discards the partial image.
 *
 * @see domain.repository.api.source.repository.BleRepository.startOta
 */
public interface StartOtaUseCase {
    public operator fun invoke(firmware: ByteArray, version: FirmwareVersionModel): Flow<OtaStateModel>
}
