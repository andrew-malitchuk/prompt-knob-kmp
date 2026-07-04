package domain.usecase.api.source.usecase.configuration

/**
 * Use case for retrieving the current Claude Mode hook server enabled state.
 */
public interface GetClaudeHookEnabledUseCase {
    public suspend operator fun invoke(): Result<Boolean>
}
