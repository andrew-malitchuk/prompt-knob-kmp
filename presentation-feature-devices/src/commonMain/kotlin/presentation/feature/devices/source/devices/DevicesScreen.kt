package presentation.feature.devices.source.devices

import androidx.compose.runtime.Composable
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import org.koin.compose.viewmodel.koinViewModel
import org.orbitmvi.orbit.annotation.OrbitExperimental
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import presentation.core.navigation.api.core.composition.LocalAppNavigator
import presentation.core.navigation.api.source.destination.Destination
import presentation.core.ui.source.kit.atom.snackbar.StackedSnackbarDuration
import presentation.core.ui.source.kit.atom.snackbar.rememberStackedSnackbarHostState
import presentation.feature.devices.core.utils.rememberBlePermissionRequester
import presentation.feature.devices.core.utils.rememberOpenBluetoothSettingsLauncher

/**
 * Entry-point composable for the Devices (BLE scanning) screen.
 *
 * Injects [DevicesViewModel] via Koin, observes MVI state and side effects,
 * and delegates rendering to [DevicesContent].
 *
 * **Auto-connect / auto-scan flow:**
 * - If the BLE device is already connected when the screen opens, [DevicesSideEffect.NavigateToDevice]
 *   is emitted immediately and this screen navigates forward to the device detail screen.
 * - Otherwise, a BLE scan starts automatically and discovered devices are displayed for the user
 *   to tap and connect. Once connection is established [DevicesSideEffect.NavigateToDevice] fires.
 *
 * **Permission flow:**
 * - On Android the system permission dialog is shown via [rememberBlePermissionRequester].
 * - On iOS / macOS the data layer triggers the CoreBluetooth dialog when [BlePermissionChecker]
 *   is called; the presentation-layer requester is a no-op on those platforms.
 *
 * @param viewModel MVI host injected by Koin; override in tests to provide a fake.
 *
 * @see DevicesViewModel
 * @see DevicesContent
 * @see <a href="https://www.figma.com/design/STUB_REPLACE_ME">Figma</a>
 */
@OptIn(OrbitExperimental::class)
@Composable
public fun DevicesScreen(
    viewModel: DevicesViewModel = koinViewModel(),
) {
    val appNavigator = LocalAppNavigator.current
    val state = viewModel.collectAsState()
    val snackbarHostState = rememberStackedSnackbarHostState()

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.handleIntent(DevicesIntent.OnResume)
    }

    val requestPermissions = rememberBlePermissionRequester { granted ->
        viewModel.handleIntent(DevicesIntent.OnPermissionsResult(granted))
    }
    val openBluetoothSettings = rememberOpenBluetoothSettingsLauncher()

    viewModel.collectSideEffect { effect ->
        when (effect) {
            DevicesSideEffect.RequestPermissions -> requestPermissions()
            DevicesSideEffect.NavigateToDevice -> appNavigator?.navigate(Destination.Device)
            DevicesSideEffect.NavigateToSettings -> appNavigator?.navigate(Destination.Settings)
            DevicesSideEffect.NavigateBack -> appNavigator?.popBackStack()
            DevicesSideEffect.OpenBluetoothSettings -> openBluetoothSettings()
            is DevicesSideEffect.ShowError -> snackbarHostState.showSnackbar(
                title = effect.message,
                duration = StackedSnackbarDuration.Short,
            )
        }
    }

    DevicesContent(
        state = state.value,
        onIntent = viewModel::handleIntent,
        snackbarHostState = snackbarHostState,
    )
}
