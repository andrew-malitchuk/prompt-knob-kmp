package domain.usecase.api.source.usecase.ble

import domain.usecase.api.source.monad.Optional

/**
 * Reads all commands from the local database and synchronises them to the
 * connected knob device.
 *
 * Safe to call when no device is connected — the failure is returned as
 * [Result.failure] and can be silently ignored by callers.
 */
public interface SyncCommandsUseCase {
    public suspend operator fun invoke(): Optional
}
