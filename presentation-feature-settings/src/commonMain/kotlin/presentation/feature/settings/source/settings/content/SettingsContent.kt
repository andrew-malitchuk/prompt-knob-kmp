package presentation.feature.settings.source.settings.content

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.ui.tooling.preview.Preview
import presentation.core.styling.core.Theme
import presentation.core.styling.source.theme.AppTheme
import presentation.core.ui.source.kit.atom.snackbar.StackedSnackbarHost
import presentation.core.ui.source.kit.atom.snackbar.StackedSnakbarHostState
import presentation.core.ui.source.kit.atom.snackbar.rememberStackedSnackbarHostState
import presentation.core.ui.source.kit.molecule.header.ScreenHeader
import presentation.feature.settings.source.settings.SettingsIntent
import presentation.feature.settings.source.settings.SettingsState

/**
 * Root content composable for the Settings screen.
 *
 * Renders a custom header (back button, title, connectivity icon), then cross-fades
 * between [SettingsShimmerContent] (loading) and [SettingsSuccessContent] (loaded)
 * based on [SettingsState.isLoading].
 *
 * @param state Current [SettingsState] driving the UI.
 * @param onIntent Callback to dispatch [SettingsIntent] actions to the ViewModel.
 *
 * @see SettingsSuccessContent
 * @see SettingsShimmerContent
 * @see <a href="https://www.figma.com/design/STUB_REPLACE_ME">Figma</a>
 */
@Composable
internal fun SettingsContent(
    state: SettingsState,
    onIntent: (SettingsIntent) -> Unit,
    onImportClick: () -> Unit,
    snackbarHostState: StackedSnakbarHostState,
) {
    val scrollState = rememberScrollState()
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Theme.color.canvas)
                .statusBarsPadding(),
        ) {
            ScreenHeader(
                onBackClick = { onIntent(SettingsIntent.OnBackClick) },
                showDivider = scrollState.canScrollBackward,
            )

            // Cross-fade between shimmer placeholder and actual settings controls
            Crossfade(
                targetState = state.isLoading,
                modifier = Modifier.fillMaxSize(),
            ) { loading ->
                if (loading) {
                    SettingsShimmerContent()
                } else {
                    SettingsSuccessContent(
                        state = state,
                        onIntent = onIntent,
                        onImportClick = onImportClick,
                        scrollState = scrollState,
                    )
                }
            }
        }
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
            StackedSnackbarHost(hostState = snackbarHostState)
        }
    }
}

@Preview
@Composable
private fun SettingsContentPreview() {
    AppTheme {
        SettingsContent(
            state = SettingsState(),
            onIntent = {},
            onImportClick = {},
            snackbarHostState = rememberStackedSnackbarHostState(),
        )
    }
}
