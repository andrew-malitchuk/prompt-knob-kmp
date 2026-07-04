package presentation.feature.home.source.home

import androidx.lifecycle.ViewModel
import domain.core.source.model.ConnectionStateModel
import domain.usecase.api.source.usecase.ble.ObserveConnectionStateUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.annotation.OrbitExperimental
import org.orbitmvi.orbit.viewmodel.container

/**
 * ViewModel for the Home screen.
 *
 * Acts as a BLE-aware routing decision point between Splash and the device screens.
 * On initialization it inspects the current BLE connection state and immediately emits
 * the appropriate navigation side effect:
 * - [HomeSideEffect.NavigateToDevice] if a device is already connected.
 * - [HomeSideEffect.NavigateToDevices] if no device is connected (triggers scan screen).
 *
 * @param observeConnectionState Use case for reading the current BLE connection state.
 *
 * @see HomeScreen
 * @see HomeState
 * @see HomeSideEffect
 */
@OrbitExperimental
public class HomeViewModel(
    private val observeConnectionState: ObserveConnectionStateUseCase,
) : ContainerHost<HomeState, HomeSideEffect>, ViewModel() {

    override val container: Container<HomeState, HomeSideEffect> =
        container(HomeState()) {
            routeToDeviceScreen()
        }

    /**
     * Dispatches the given [intent] to the appropriate handler.
     *
     * @param intent User action from the UI layer.
     */
    public fun handleIntent(intent: HomeIntent) {
        when (intent) {
            HomeIntent.OnSettingsClick -> onSettingsClick()
            HomeIntent.OnStyleguideClick -> onStyleguideClick()
            HomeIntent.OnDeviceClick -> onDeviceClick()
            HomeIntent.OnPresetsClick -> onPresetsClick()
        }
    }


    /**
     * Checks the current BLE connection state and emits the appropriate navigation
     * side effect. Called once at container initialisation.
     *
     * - Connected → [HomeSideEffect.NavigateToDevice]
     * - Anything else / unavailable → [HomeSideEffect.NavigateToDevices]
     */
    private fun routeToDeviceScreen() = intent {
        val current = try {
            observeConnectionState().first()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            // BLE stack may be unavailable (Desktop) or the flow completes before emitting.
            // Treat as disconnected → route to scan screen.
            null
        }

        if (current?.state == ConnectionStateModel.Connected) {
            postSideEffect(HomeSideEffect.NavigateToDevice)
        } else {
            postSideEffect(HomeSideEffect.NavigateToDevices)
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

    /** Emits a side effect to navigate to the BLE devices screen. */
    private fun onDeviceClick() = intent {
        postSideEffect(HomeSideEffect.NavigateToDevices)
    }

    /** Emits a side effect to navigate to the preset gallery screen. */
    private fun onPresetsClick() = intent {
        postSideEffect(HomeSideEffect.NavigateToPresets)
    }
}
