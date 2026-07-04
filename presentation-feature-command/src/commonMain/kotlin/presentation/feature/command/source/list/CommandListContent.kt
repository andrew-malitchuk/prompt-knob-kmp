package presentation.feature.command.source.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import presentation.core.styling.core.Theme
import presentation.core.styling.source.theme.AppTheme
import presentation.core.ui.source.kit.atom.button.TacticalButton
import presentation.core.ui.source.kit.atom.command.DeleteActionButton
import presentation.core.ui.source.kit.atom.icon.Folder
import presentation.core.ui.source.kit.atom.icon.Plus
import presentation.core.ui.source.kit.atom.icon.RefreshCcw
import presentation.core.ui.source.kit.atom.snackbar.StackedSnackbarHost
import presentation.core.ui.source.kit.atom.snackbar.StackedSnakbarHostState
import presentation.core.ui.source.kit.atom.snackbar.rememberStackedSnackbarHostState
import presentation.core.ui.source.kit.molecule.command.CommandItemRow
import presentation.core.ui.source.kit.molecule.command.CommandRowItem
import presentation.core.ui.source.kit.molecule.header.ScreenHeader
import presentation.core.ui.source.kit.molecule.state.EmptyState
import presentation.feature.command.core.resolveIcon
import prompt_knob_kmp.presentation_core_localisation.generated.resources.Res
import prompt_knob_kmp.presentation_core_localisation.generated.resources.command_delete_confirm_message
import prompt_knob_kmp.presentation_core_localisation.generated.resources.command_delete_confirm_title
import prompt_knob_kmp.presentation_core_localisation.generated.resources.command_form_cancel
import prompt_knob_kmp.presentation_core_localisation.generated.resources.command_form_delete
import prompt_knob_kmp.presentation_core_localisation.generated.resources.command_list_add_command
import prompt_knob_kmp.presentation_core_localisation.generated.resources.command_list_add_folder
import prompt_knob_kmp.presentation_core_localisation.generated.resources.command_list_delete_folder
import prompt_knob_kmp.presentation_core_localisation.generated.resources.command_list_empty
import prompt_knob_kmp.presentation_core_localisation.generated.resources.command_list_agent_badge
import prompt_knob_kmp.presentation_core_localisation.generated.resources.command_list_rotary_badge

@Composable
public fun CommandListContent(
    state: CommandListState,
    onIntent: (CommandListIntent) -> Unit,
    snackbarHostState: StackedSnakbarHostState,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Theme.color.canvas),
        ) {
            val listState = rememberLazyListState()
            val rotaryBadgeLabel = stringResource(Res.string.command_list_rotary_badge)
            val agentBadgeLabel = stringResource(Res.string.command_list_agent_badge)
            ScreenHeader(
                onBackClick = { onIntent(CommandListIntent.OnBackClick) },
                showDivider = listState.canScrollBackward,
            )
            Box(modifier = Modifier.weight(1f)) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(Theme.spacing.spacingL),
                    verticalArrangement = Arrangement.spacedBy(Theme.spacing.spacingM),
                ) {
                    if (state.items.isEmpty() && !state.isLoading) {
                        item {
                            EmptyState(message = stringResource(Res.string.command_list_empty))
                        }
                    } else {
                        items(state.items, key = { it.id }) { item ->
                            CommandItemRow(
                                item = CommandRowItem(
                                    id = item.id,
                                    label = item.label,
                                    isFolder = item.isFolder,
                                    isRotary = item.isRotary,
                                    isAgentScreen = item.isAgentScreen,
                                    icon = when {
                                        item.isFolder && item.isRotary -> RefreshCcw
                                        item.isFolder -> Folder
                                        else -> resolveIcon(item.icon.orEmpty())
                                    },
                                    command = item.command,
                                    rotaryBadgeLabel = rotaryBadgeLabel,
                                    agentBadgeLabel = agentBadgeLabel,
                                ),
                                onClick = { onIntent(CommandListIntent.OnItemClick(item.id, item.isFolder)) },
                                onDeleteClick = { onIntent(CommandListIntent.OnDeleteClick(item.id, item.label, item.isFolder)) },
                            )
                        }
                    }

                    item {
                        TacticalButton(
                            text = stringResource(Res.string.command_list_add_command),
                            onClick = { onIntent(CommandListIntent.OnAddCommandClick) },
                            icon = Plus,
                        )
                    }

                    item {
                        TacticalButton(
                            text = stringResource(Res.string.command_list_add_folder),
                            onClick = { onIntent(CommandListIntent.OnAddFolderClick) },
                            icon = Folder,
                        )
                    }

                    if (state.parentId != 0) {
                        item {
                            DeleteActionButton(
                                text = stringResource(Res.string.command_list_delete_folder),
                                onClick = { onIntent(CommandListIntent.OnDeleteFolderClick) },
                            )
                        }
                    }
                }
            }

            if (state.pendingDeleteId != null) {
                AlertDialog(
                    onDismissRequest = { onIntent(CommandListIntent.OnDismissDelete) },
                    title = {
                        Text(
                            text = stringResource(Res.string.command_delete_confirm_title),
                            style = Theme.typography.title,
                            color = Theme.color.inkMain,
                        )
                    },
                    text = {
                        Text(
                            text = stringResource(
                                Res.string.command_delete_confirm_message,
                                state.pendingDeleteLabel ?: "",
                            ),
                            style = Theme.typography.body,
                            color = Theme.color.inkSubtle,
                        )
                    },
                    confirmButton = {
                        TextButton(onClick = { onIntent(CommandListIntent.OnConfirmDelete) }) {
                            Text(
                                text = stringResource(Res.string.command_form_delete),
                                color = Theme.color.error,
                            )
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { onIntent(CommandListIntent.OnDismissDelete) }) {
                            Text(
                                text = stringResource(Res.string.command_form_cancel),
                                color = Theme.color.inkSubtle,
                            )
                        }
                    },
                    containerColor = Theme.color.surface,
                )
            }
        }
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
            StackedSnackbarHost(hostState = snackbarHostState)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CommandListContentEmptyPreview() {
    AppTheme {
        CommandListContent(
            state = CommandListState(isLoading = false),
            onIntent = {},
            snackbarHostState = rememberStackedSnackbarHostState(),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CommandListContentWithItemsPreview() {
    AppTheme {
        CommandListContent(
            state = CommandListState(
                isLoading = false,
                items = listOf(
                    CommandItemUiModel(id = 1, label = "MUTE", isFolder = false, isRotary = false, icon = "volume", command = "KEY_MUTE"),
                    CommandItemUiModel(id = 2, label = "MEDIA", isFolder = true, isRotary = false, icon = null, command = ""),
                    CommandItemUiModel(id = 3, label = "BRIGHTNESS", isFolder = true, isRotary = true, icon = null, command = ""),
                ),
            ),
            onIntent = {},
            snackbarHostState = rememberStackedSnackbarHostState(),
        )
    }
}
