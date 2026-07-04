package presentation.feature.settings.source.settings

import androidx.compose.runtime.Composable
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import org.koin.compose.viewmodel.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import presentation.core.navigation.api.core.composition.LocalAppNavigator
import presentation.core.navigation.api.source.destination.AppNavigator
import presentation.core.navigation.api.source.destination.Destination
import org.jetbrains.compose.resources.stringResource
import presentation.core.ui.source.kit.atom.snackbar.StackedSnackbarDuration
import presentation.core.ui.source.kit.atom.snackbar.rememberStackedSnackbarHostState
import presentation.feature.settings.core.utils.rememberExportJsonLauncher
import prompt_knob_kmp.presentation_core_localisation.generated.resources.Res
import prompt_knob_kmp.presentation_core_localisation.generated.resources.settings_import_success
import prompt_knob_kmp.presentation_core_localisation.generated.resources.snackbar_error
import presentation.feature.settings.core.utils.rememberImportJsonLauncher
import presentation.feature.settings.source.settings.content.SettingsContent

/**
 * Entry-point composable for the Settings feature.
 *
 * Collects Orbit MVI state and side-effects from [SettingsViewModel],
 * delegates rendering to [SettingsContent], and handles navigation
 * side-effects via [LocalAppNavigator].
 *
 * @param viewModel Koin-provided [SettingsViewModel] instance.
 *
 * @see SettingsContent
 * @see <a href="https://www.figma.com/design/STUB_REPLACE_ME">Figma</a>
 */
@Composable
public fun SettingsScreen(viewModel: SettingsViewModel = koinViewModel()) {
    val appNavigator = LocalAppNavigator.current
    val state = viewModel.collectAsState()
    val snackbarHostState = rememberStackedSnackbarHostState()
    val errorTitle = stringResource(Res.string.snackbar_error)
    val importedFormat = stringResource(Res.string.settings_import_success)

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.handleIntent(SettingsIntent.RefreshPermissions)
    }

    val importLauncher = rememberImportJsonLauncher { json ->
        viewModel.handleIntent(SettingsIntent.OnImportCommands(json))
    }
    val exportLauncher = rememberExportJsonLauncher()

    viewModel.collectSideEffect { effect ->
        when (effect) {
            SettingsSideEffect.GoBackEffect -> appNavigator?.backAction()
            SettingsSideEffect.NavigateToLanguagePicker -> appNavigator?.navigate(Destination.LanguagePicker)
            SettingsSideEffect.NavigateToDevice -> appNavigator?.navigate(Destination.Device)
            SettingsSideEffect.NavigateToAbout -> appNavigator?.navigate(Destination.About)
            SettingsSideEffect.NavigateToSplash -> appNavigator?.navigate(Destination.Splash, AppNavigator.NavOptions.ClearTask)
            is SettingsSideEffect.ShowError -> snackbarHostState.showSnackbar(
                title = errorTitle,
                duration = StackedSnackbarDuration.Short,
            )
            is SettingsSideEffect.ExportReady -> exportLauncher(effect.json)
            is SettingsSideEffect.ShowImportSuccess -> snackbarHostState.showSnackbar(
                title = importedFormat.replace("%1\$d", effect.count.toString()),
                duration = StackedSnackbarDuration.Short,
            )
            is SettingsSideEffect.ShowErrorMessage -> snackbarHostState.showSnackbar(
                title = effect.message,
                duration = StackedSnackbarDuration.Short,
            )
        }
    }

    SettingsContent(
        state = state.value,
        onIntent = viewModel::handleIntent,
        onImportClick = importLauncher,
        snackbarHostState = snackbarHostState,
    )
}
