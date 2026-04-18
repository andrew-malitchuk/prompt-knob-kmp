package presentation.feature.home.source.home

/**
 * Immutable UI state for the Home screen.
 *
 * @property isLoading Whether the home screen is in a loading state.
 *
 * @see HomeViewModel
 */
public data class HomeState(
    val isLoading: Boolean = false,
)

/**
 * One-shot side effects emitted by [HomeViewModel].
 *
 * @see HomeScreen
 */
public sealed class HomeSideEffect {
    /** Navigate to the Settings screen. */
    public data object NavigateToSettings : HomeSideEffect()

    /** Navigate to the design-system styleguide showcase. */
    public data object NavigateToStyleguide : HomeSideEffect()

    /**
     * Display an error snackbar.
     *
     * @property messageId String resource identifier for the error message.
     */
    public data class ShowError(val messageId: Int) : HomeSideEffect()
}

/**
 * User intents dispatched from the Home UI layer.
 *
 * @see HomeViewModel.handleIntent
 */
public sealed class HomeIntent {
    /** The user tapped the settings icon. */
    public data object OnSettingsClick : HomeIntent()

    /** The user tapped the styleguide button. */
    public data object OnStyleguideClick : HomeIntent()
}
