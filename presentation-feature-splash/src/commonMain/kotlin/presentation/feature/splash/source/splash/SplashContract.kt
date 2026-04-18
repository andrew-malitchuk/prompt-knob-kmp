package presentation.feature.splash.source.splash

/**
 * UI state for the Splash screen.
 *
 * @property isLoading Whether the splash animation / initial loading is still in progress.
 */
public data class SplashState(
    val isLoading: Boolean = true,
)

/**
 * One-shot side effects emitted by [SplashViewModel].
 */
public sealed class SplashSideEffect {

    /**
     * Navigate to the onboarding flow (first launch).
     */
    public data object NavigateToOnboarding : SplashSideEffect()

    /**
     * Navigate to the home screen (onboarding already completed).
     */
    public data object NavigateToHome : SplashSideEffect()

    /**
     * Display an error message identified by a string resource [messageId].
     */
    public data class ShowError(val messageId: Int) : SplashSideEffect()
}

/**
 * User- or system-initiated actions on the Splash screen.
 */
public sealed class SplashIntent {

    /** The splash animation has completed and the screen is ready to navigate away. */
    public data object OnAnimationFinished : SplashIntent()
}
