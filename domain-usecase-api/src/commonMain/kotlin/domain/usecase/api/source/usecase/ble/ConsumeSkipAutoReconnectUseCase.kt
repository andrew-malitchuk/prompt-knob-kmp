package domain.usecase.api.source.usecase.ble

/**
 * Reads and clears the skip-auto-reconnect flag.
 *
 * Returns true (and resets the flag) if a user-initiated disconnect occurred since
 * the last call, indicating the Devices screen should skip the quick-reconnect attempt.
 */
public interface ConsumeSkipAutoReconnectUseCase {
    public operator fun invoke(): Result<Boolean>
}
