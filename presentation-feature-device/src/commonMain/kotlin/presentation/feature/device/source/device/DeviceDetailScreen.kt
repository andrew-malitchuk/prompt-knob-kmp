package presentation.feature.device.source.device

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import domain.core.source.model.FirmwareVersionModel
import org.koin.compose.viewmodel.koinViewModel
import org.orbitmvi.orbit.annotation.OrbitExperimental
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import presentation.core.navigation.api.core.composition.LocalAppNavigator
import presentation.core.navigation.api.source.destination.Destination
import presentation.core.navigation.api.source.destination.AppNavigator
import presentation.core.ui.source.kit.atom.snackbar.StackedSnackbarDuration
import presentation.core.ui.source.kit.atom.snackbar.rememberStackedSnackbarHostState
import presentation.feature.device.core.utils.rememberFirmwarePickerLauncher

/**
 * Entry-point composable for the Device detail / dashboard screen.
 *
 * Injects [DeviceDetailViewModel] via Koin, observes MVI state and side effects,
 * and delegates rendering to [DeviceDetailContent].
 *
 * This screen is navigated to after a successful BLE connection on the Devices screen.
 * It shows the connected device's dashboard: status, battery, macro grid, hardware info,
 * profile load, and add-macro CTA.
 *
 * @param viewModel MVI host injected by Koin; override in tests to provide a fake.
 *
 * @see DeviceDetailViewModel
 * @see DeviceDetailContent
 * @see <a href="https://www.figma.com/design/STUB_REPLACE_ME">Figma</a>
 */
@OptIn(OrbitExperimental::class)
@Composable
public fun DeviceDetailScreen(
    viewModel: DeviceDetailViewModel = koinViewModel(),
) {
    val appNavigator = LocalAppNavigator.current
    val state = viewModel.collectAsState()
    val snackbarHostState = rememberStackedSnackbarHostState()

    // Fires every time the screen enters composition — both on first open and on back-pop.
    // LifecycleEventEffect(ON_RESUME) is unreliable with Navigation 3: NavDisplay removes
    // entries from composition when not top, so LocalLifecycleOwner only reflects the
    // app-level lifecycle, not per-entry navigation events.
    LaunchedEffect(Unit) {
        viewModel.handleIntent(DeviceDetailIntent.OnForceSync)
    }

    val firmwareVersion = state.value.firmwareVersion?.let { raw ->
        val parts = raw.split(".").mapNotNull { it.toIntOrNull() }
        if (parts.size >= 3) FirmwareVersionModel(parts[0], parts[1], parts[2]) else FirmwareVersionModel(0, 0, 0)
    } ?: FirmwareVersionModel(0, 0, 0)

    var shouldLaunchPicker by remember { mutableStateOf(false) }

    val launchPicker = rememberFirmwarePickerLauncher { bytes ->
        viewModel.handleIntent(DeviceDetailIntent.OnFirmwareFileSelected(bytes, firmwareVersion))
    }

    viewModel.collectSideEffect { effect ->
        when (effect) {
            DeviceDetailSideEffect.NavigateBack -> appNavigator?.popBackStack()
            DeviceDetailSideEffect.NavigateToDevices ->
                appNavigator?.navigate(Destination.Devices, AppNavigator.NavOptions.ClearTask)
            DeviceDetailSideEffect.NavigateToSettings -> appNavigator?.navigate(Destination.Settings)
            is DeviceDetailSideEffect.NavigateToCommandList ->
                appNavigator?.navigate(Destination.CommandList(parentId = effect.parentId))
            is DeviceDetailSideEffect.NavigateToCommandForm ->
                appNavigator?.navigate(Destination.CommandForm(commandId = effect.commandId))
            is DeviceDetailSideEffect.ShowError -> snackbarHostState.showSnackbar(
                title = effect.message,
                duration = StackedSnackbarDuration.Short,
            )
            DeviceDetailSideEffect.ShowFirmwarePicker -> shouldLaunchPicker = true
            DeviceDetailSideEffect.NavigateToPresets -> appNavigator?.navigate(Destination.Presets)
        }
    }

    if (shouldLaunchPicker) {
        shouldLaunchPicker = false
        launchPicker()
    }

    DeviceDetailContent(
        state = state.value,
        onIntent = viewModel::handleIntent,
        snackbarHostState = snackbarHostState,
    )
}
