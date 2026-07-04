package domain.usecase.api.source.usecase.command

/**
 * Serializes the full command tree to a JSON string.
 *
 * Returns a [Result] wrapping the JSON payload string on success.
 */
public interface ExportCommandsUseCase {
    public suspend operator fun invoke(): Result<String>
}
