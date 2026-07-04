package domain.usecase.api.source.usecase.ble

/**
 * Suspends until the device sends a CMD_SELECTED notification or the timeout elapses.
 *
 * Returns the raw cmd_id (> 0 = approved, 0 = rejected by user) or null on timeout.
 * Subscribe before calling [SendShowApprovalUseCase] to avoid missing the event.
 */
public interface AwaitCommandSelectedUseCase {
    public suspend operator fun invoke(timeoutMs: Long = DEFAULT_TIMEOUT_MS): Result<Int?>

    public companion object {
        public const val DEFAULT_TIMEOUT_MS: Long = 30_000L
    }
}
