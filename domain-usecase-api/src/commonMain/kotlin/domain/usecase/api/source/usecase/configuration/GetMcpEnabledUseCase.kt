package domain.usecase.api.source.usecase.configuration

/**
 * Use case for retrieving the current MCP server enabled state.
 */
public interface GetMcpEnabledUseCase {
    /**
     * @return A [Result] containing the MCP server enabled flag (`true` by default).
     */
    public suspend operator fun invoke(): Result<Boolean>
}
