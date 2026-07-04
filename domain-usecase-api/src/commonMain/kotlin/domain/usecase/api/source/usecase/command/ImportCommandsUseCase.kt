package domain.usecase.api.source.usecase.command

/**
 * Deserializes a JSON string and replaces the entire command tree.
 *
 * Returns a [Result] wrapping the count of imported nodes on success.
 */
public interface ImportCommandsUseCase {
    public suspend operator fun invoke(json: String): Result<Int>
}
