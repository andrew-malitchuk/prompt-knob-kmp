package domain.usecase.api.source.usecase.ble

import kotlinx.coroutines.flow.Flow

/** Observes the connected device's battery level (0–100), or null when unknown. */
public interface ObserveBatteryLevelUseCase {
    public operator fun invoke(): Flow<Int?>
}
