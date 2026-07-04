package presentation.feature.home.source.home.content

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import presentation.core.styling.core.Theme
import presentation.core.styling.source.theme.AppTheme
import presentation.core.ui.source.kit.atom.button.TacticalButton
import presentation.core.ui.source.kit.atom.icon.Settings
import presentation.feature.home.source.home.HomeIntent
import presentation.feature.home.source.home.HomeState
import prompt_knob_kmp.presentation_core_localisation.generated.resources.Res
import prompt_knob_kmp.presentation_core_localisation.generated.resources.home_action_device
import prompt_knob_kmp.presentation_core_localisation.generated.resources.home_action_presets
import prompt_knob_kmp.presentation_core_localisation.generated.resources.home_action_styleguide
import prompt_knob_kmp.presentation_core_localisation.generated.resources.settings_title

/**
 * Main content composable for the Home screen in its loaded state.
 *
 * Renders a placeholder content area with a settings shortcut and a
 * [TacticalButton] that opens the design-system styleguide showcase.
 *
 * @param state Current immutable UI state snapshot from [presentation.feature.home.source.home.HomeViewModel].
 * @param onIntent Callback that forwards user intents to the ViewModel.
 *
 * @see HomeContent
 * @see <a href="https://www.figma.com/design/STUB_REPLACE_ME">Figma</a>
 */
@Composable
internal fun HomeSuccessContent(
    state: HomeState,
    onIntent: (HomeIntent) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.color.canvas),
    ) {
        TacticalButton(
            text = stringResource(Res.string.home_action_device),
            onClick = { onIntent(HomeIntent.OnDeviceClick) },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(
                    start = Theme.spacing.spacingL,
                    end = Theme.spacing.spacingL,
                    bottom = Theme.spacing.spacing2XL + Theme.spacing.spacingL * 2 + 56.dp * 2,
                ),
        )

        TacticalButton(
            text = stringResource(Res.string.home_action_presets),
            onClick = { onIntent(HomeIntent.OnPresetsClick) },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(
                    start = Theme.spacing.spacingL,
                    end = Theme.spacing.spacingL,
                    bottom = Theme.spacing.spacing2XL + Theme.spacing.spacingL + 56.dp,
                ),
        )

        TacticalButton(
            text = stringResource(Res.string.home_action_styleguide),
            onClick = { onIntent(HomeIntent.OnStyleguideClick) },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(
                    start = Theme.spacing.spacingL,
                    end = Theme.spacing.spacingL,
                    bottom = Theme.spacing.spacing2XL,
                ),
        )

        // Settings button in the bottom-end corner
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(
                    end = Theme.spacing.spacingL,
                    bottom = Theme.spacing.spacingL + Theme.spacing.spacing3XL + Theme.spacing.spacingL,
                )
                .size(56.dp)
                .clip(CircleShape)
                .background(Theme.color.surfaceInverse)
                .clickable { onIntent(HomeIntent.OnSettingsClick) },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Settings,
                contentDescription = stringResource(Res.string.settings_title),
                tint = Theme.color.canvas,
                modifier = Modifier.size(24.dp),
            )
        }
    }
}

@Preview
@Composable
private fun HomeSuccessContentPreview() {
    AppTheme {
        HomeSuccessContent(
            state = HomeState(),
            onIntent = {},
        )
    }
}
