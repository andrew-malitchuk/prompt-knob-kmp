package presentation.feature.onboarding.source.onboarding

/**
 * UI state for the Onboarding screen.
 *
 * @property isLoading Whether a blocking operation (e.g. persisting onboarding status) is in progress.
 */
public data class OnboardingState(
    val isLoading: Boolean = false,
)

/**
 * One-shot side-effects emitted by [OnboardingViewModel].
 */
public sealed class OnboardingSideEffect {

    /** Navigate to the Home screen after onboarding is completed. */
    public data object NavigateToHome : OnboardingSideEffect()

    /**
     * Display an error message to the user.
     *
     * @property messageId String resource identifier for the error message.
     */
    public data class ShowError(val messageId: Int) : OnboardingSideEffect()
}

/**
 * User-initiated actions on the Onboarding screen.
 */
public sealed class OnboardingIntent {

    /** User tapped the "Get Started" button to complete onboarding. */
    public data object OnGetStartedClick : OnboardingIntent()
}
