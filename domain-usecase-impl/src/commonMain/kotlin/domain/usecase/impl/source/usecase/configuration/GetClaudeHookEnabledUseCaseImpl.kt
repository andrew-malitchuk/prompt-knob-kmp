package domain.usecase.impl.source.usecase.configuration

import domain.core.source.monad.Failure
import domain.repository.api.source.repository.ConfigureRepository
import domain.usecase.api.source.usecase.configuration.GetClaudeHookEnabledUseCase
import domain.usecase.impl.core.resultLauncher

internal class GetClaudeHookEnabledUseCaseImpl(
    private val configureRepository: ConfigureRepository,
) : GetClaudeHookEnabledUseCase {
    override suspend fun invoke(): Result<Boolean> = resultLauncher(
        errorMapper = Failure.Technical::Preference,
    ) {
        configureRepository.getClaudeHookEnabled() ?: false
    }
}
