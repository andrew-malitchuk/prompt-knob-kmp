package presentation.feature.preset.source.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.stringResource
import presentation.core.styling.core.Theme
import presentation.core.ui.source.kit.atom.snackbar.StackedSnackbarHost
import presentation.core.ui.source.kit.atom.snackbar.StackedSnakbarHostState
import presentation.core.ui.source.kit.molecule.header.NavigationHeader
import presentation.core.ui.source.kit.molecule.item.PresetRow
import presentation.core.ui.source.kit.organism.bottomsheet.ConfirmationSheet
import prompt_knob_kmp.presentation_core_localisation.generated.resources.Res
import prompt_knob_kmp.presentation_core_localisation.generated.resources.preset_apply_body
import prompt_knob_kmp.presentation_core_localisation.generated.resources.preset_apply_confirm
import prompt_knob_kmp.presentation_core_localisation.generated.resources.preset_apply_title
import prompt_knob_kmp.presentation_core_localisation.generated.resources.preset_cancel
import prompt_knob_kmp.presentation_core_localisation.generated.resources.preset_list_title

@Composable
public fun PresetListContent(
    state: PresetListState,
    onIntent: (PresetListIntent) -> Unit,
    snackbarHostState: StackedSnakbarHostState,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Theme.color.canvas),
        ) {
            NavigationHeader(
                title = stringResource(Res.string.preset_list_title),
                onNavigationClick = { onIntent(PresetListIntent.OnBackClick) },
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(Theme.spacing.spacingL),
                verticalArrangement = Arrangement.spacedBy(Theme.spacing.spacingM),
            ) {
                items(state.gallery, key = { it.name }) { preset ->
                    PresetRow(
                        title = preset.name,
                        description = preset.description,
                        onClick = { onIntent(PresetListIntent.OnGalleryPresetClick(preset)) },
                    )
                }

                item { Spacer(modifier = Modifier.height(Theme.spacing.spacingL)) }
            }
        }

        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
            StackedSnackbarHost(hostState = snackbarHostState)
        }
    }

    state.pendingGalleryPreset?.let { preset ->
        ConfirmationSheet(
            title = stringResource(Res.string.preset_apply_title),
            body = stringResource(Res.string.preset_apply_body, preset.name),
            confirmText = stringResource(Res.string.preset_apply_confirm),
            dismissText = stringResource(Res.string.preset_cancel),
            onConfirm = { onIntent(PresetListIntent.OnConfirmGalleryPreset) },
            onDismiss = { onIntent(PresetListIntent.OnDismissGalleryPreset) },
        )
    }
}
