package presentation.feature.home.source.home

import androidx.lifecycle.ViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.annotation.OrbitExperimental
import org.orbitmvi.orbit.viewmodel.container

/**
 * ViewModel for the Home screen.
 *
 * Follows the MVI pattern via Orbit: UI dispatches [HomeIntent] actions
 * through [handleIntent], the ViewModel reduces them into [HomeState]
 * updates, and emits [HomeSideEffect] for one-shot navigation events.
 *
 * @see HomeScreen
 * @see HomeState
 * @see HomeSideEffect
 */
@OrbitExperimental
public class HomeViewModel(
    // inject use cases here
) : ContainerHost<HomeState, HomeSideEffect>, ViewModel() {

    override val container: Container<HomeState, HomeSideEffect> =
        container<HomeState, HomeSideEffect>(HomeState())

    /**
     * Dispatches the given [intent] to the appropriate handler.
     *
     * @param intent User action from the UI layer.
     */
    public fun handleIntent(intent: HomeIntent) {
        when (intent) {
            HomeIntent.OnSettingsClick -> onSettingsClick()
            HomeIntent.OnStyleguideClick -> onStyleguideClick()
        }
    }

    /** Emits a side effect to navigate to the Settings screen. */
    private fun onSettingsClick() = intent {
        postSideEffect(HomeSideEffect.NavigateToSettings)
    }

    /** Emits a side effect to navigate to the styleguide showcase. */
    private fun onStyleguideClick() = intent {
        postSideEffect(HomeSideEffect.NavigateToStyleguide)
    }
}
