package domain.usecase.impl.source.usecase.configuration

import domain.core.source.monad.Failure
import domain.repository.api.source.repository.ConfigureRepository
import domain.usecase.api.source.usecase.configuration.GetMcpEnabledUseCase
import domain.usecase.impl.core.resultLauncher

/**
 * Default implementation of [GetMcpEnabledUseCase].
 *
 * Retrieves the current MCP server enabled state from [ConfigureRepository].
 * Defaults to `true` when no value has been persisted yet.
 *
 * @property configureRepository Repository used for reading MCP configuration.
 */
internal class GetMcpEnabledUseCaseImpl(
    private val configureRepository: ConfigureRepository,
) : GetMcpEnabledUseCase {

    /**
     * @return A [Result] containing the MCP server enabled flag.
     * @see GetMcpEnabledUseCase.invoke
     */
    override suspend fun invoke(): Result<Boolean> = resultLauncher(
        errorMapper = Failure.Technical::Preference,
    ) {
        configureRepository.getMcpEnabled() ?: true
    }
}
