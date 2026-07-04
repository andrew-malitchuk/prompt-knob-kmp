package presentation.feature.command.source.form

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import domain.core.source.model.CommandTypeModel
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import presentation.core.styling.core.Theme
import presentation.core.styling.source.theme.AppTheme
import presentation.core.ui.source.kit.atom.command.BrowseSystemCommandButton
import presentation.core.ui.source.kit.atom.command.DeleteActionButton
import presentation.core.ui.source.kit.atom.command.SaveMacroButton
import presentation.core.ui.source.kit.atom.command.TestRunButton
import presentation.core.ui.source.kit.atom.input.CommandStringField
import presentation.core.ui.source.kit.atom.input.FilledTextField
import presentation.core.ui.source.kit.atom.snackbar.StackedSnackbarHost
import presentation.core.ui.source.kit.atom.snackbar.StackedSnakbarHostState
import presentation.core.ui.source.kit.atom.snackbar.rememberStackedSnackbarHostState
import presentation.core.ui.source.kit.molecule.command.ButtonPreviewSection
import presentation.core.ui.source.kit.molecule.command.CommandModeToggle
import presentation.core.ui.source.kit.molecule.command.CommandTypePicker
import presentation.core.ui.source.kit.molecule.command.CreatorToolbar
import presentation.core.ui.source.kit.molecule.command.IconPickerSection
import presentation.core.ui.source.kit.molecule.form.FormSection
import presentation.core.ui.source.kit.organism.bottomsheet.ConfirmationSheet
import presentation.feature.command.core.availableSystemCommandOptions
import presentation.feature.command.core.iconCatalogue
import presentation.feature.command.core.resolveIcon
import presentation.feature.command.source.picker.SystemCommandPickerContent
import prompt_knob_kmp.presentation_core_localisation.generated.resources.Res
import prompt_knob_kmp.presentation_core_localisation.generated.resources.command_delete_confirm_message
import prompt_knob_kmp.presentation_core_localisation.generated.resources.command_delete_confirm_title
import prompt_knob_kmp.presentation_core_localisation.generated.resources.command_form_browse_system
import prompt_knob_kmp.presentation_core_localisation.generated.resources.command_form_button_preview
import prompt_knob_kmp.presentation_core_localisation.generated.resources.command_form_cancel
import prompt_knob_kmp.presentation_core_localisation.generated.resources.command_form_command_label
import prompt_knob_kmp.presentation_core_localisation.generated.resources.command_form_command_required
import prompt_knob_kmp.presentation_core_localisation.generated.resources.command_form_default_name
import prompt_knob_kmp.presentation_core_localisation.generated.resources.command_form_delete
import prompt_knob_kmp.presentation_core_localisation.generated.resources.command_form_hint_prompt
import prompt_knob_kmp.presentation_core_localisation.generated.resources.command_form_hint_shell
import prompt_knob_kmp.presentation_core_localisation.generated.resources.command_form_hint_shortcut
import prompt_knob_kmp.presentation_core_localisation.generated.resources.command_form_hint_system
import prompt_knob_kmp.presentation_core_localisation.generated.resources.command_form_icon_count
import prompt_knob_kmp.presentation_core_localisation.generated.resources.command_form_media_hint
import prompt_knob_kmp.presentation_core_localisation.generated.resources.command_form_media_next
import prompt_knob_kmp.presentation_core_localisation.generated.resources.command_form_media_play_pause
import prompt_knob_kmp.presentation_core_localisation.generated.resources.command_form_media_prev
import prompt_knob_kmp.presentation_core_localisation.generated.resources.command_form_media_vol_down
import prompt_knob_kmp.presentation_core_localisation.generated.resources.command_form_media_vol_up
import prompt_knob_kmp.presentation_core_localisation.generated.resources.command_form_name_hint
import prompt_knob_kmp.presentation_core_localisation.generated.resources.command_form_name_label
import prompt_knob_kmp.presentation_core_localisation.generated.resources.command_form_name_required
import prompt_knob_kmp.presentation_core_localisation.generated.resources.command_form_rotary_ccw_label
import prompt_knob_kmp.presentation_core_localisation.generated.resources.command_form_rotary_ccw_required
import prompt_knob_kmp.presentation_core_localisation.generated.resources.command_form_rotary_cw_label
import prompt_knob_kmp.presentation_core_localisation.generated.resources.command_form_rotary_cw_required
import prompt_knob_kmp.presentation_core_localisation.generated.resources.command_form_rotary_hint
import prompt_knob_kmp.presentation_core_localisation.generated.resources.command_form_rotary_hint_leaf
import prompt_knob_kmp.presentation_core_localisation.generated.resources.command_form_rotary_label
import prompt_knob_kmp.presentation_core_localisation.generated.resources.command_form_agent_hint
import prompt_knob_kmp.presentation_core_localisation.generated.resources.command_form_agent_label
import prompt_knob_kmp.presentation_core_localisation.generated.resources.command_form_rotary_rotary
import prompt_knob_kmp.presentation_core_localisation.generated.resources.command_form_rotary_standard
import prompt_knob_kmp.presentation_core_localisation.generated.resources.command_form_save
import prompt_knob_kmp.presentation_core_localisation.generated.resources.command_form_select_icon
import prompt_knob_kmp.presentation_core_localisation.generated.resources.command_form_test_run
import prompt_knob_kmp.presentation_core_localisation.generated.resources.command_form_type_label
import prompt_knob_kmp.presentation_core_localisation.generated.resources.command_form_type_prompt
import prompt_knob_kmp.presentation_core_localisation.generated.resources.command_form_type_shell
import prompt_knob_kmp.presentation_core_localisation.generated.resources.command_form_type_shortcut
import prompt_knob_kmp.presentation_core_localisation.generated.resources.command_form_type_system
import prompt_knob_kmp.presentation_core_localisation.generated.resources.command_form_media_placeholder_next
import prompt_knob_kmp.presentation_core_localisation.generated.resources.command_form_media_placeholder_play_pause
import prompt_knob_kmp.presentation_core_localisation.generated.resources.command_form_media_placeholder_prev
import prompt_knob_kmp.presentation_core_localisation.generated.resources.command_form_media_placeholder_vol_down
import prompt_knob_kmp.presentation_core_localisation.generated.resources.command_form_media_placeholder_vol_up
import prompt_knob_kmp.presentation_core_localisation.generated.resources.command_form_utf8_tag

@Composable
public fun CommandFormContent(
    state: CommandFormState,
    onIntent: (CommandFormIntent) -> Unit,
    snackbarHostState: StackedSnakbarHostState,
    modifier: Modifier = Modifier,
) {
    if (state.showSystemCommandPicker) {
        SystemCommandPickerContent(
            onSelected = { command -> onIntent(CommandFormIntent.OnSystemCommandSelected(command)) },
            onBack = { onIntent(CommandFormIntent.OnSystemCommandPickerDismiss) },
            modifier = modifier,
        )
        return
    }

    val utf8Tag = stringResource(Res.string.command_form_utf8_tag)

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Theme.color.canvas),
        ) {
            val listState = rememberLazyListState()
            CreatorToolbar(
                onBackClick = { onIntent(CommandFormIntent.OnBackClick) },
                cancelLabel = stringResource(Res.string.command_form_cancel),
                onCancelClick = { onIntent(CommandFormIntent.OnBackClick) },
                showDivider = listState.canScrollBackward,
            )

            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = Theme.spacing.spacingL,
                    end = Theme.spacing.spacingL,
                    top = Theme.spacing.spacingXL,
                    bottom = Theme.spacing.spacingXL,
                ),
                verticalArrangement = Arrangement.spacedBy(Theme.spacing.spacingXL),
            ) {
                item {
                    ButtonPreviewSection(
                        previewLabel = stringResource(Res.string.command_form_button_preview),
                        displayName = state.name.ifBlank { stringResource(Res.string.command_form_default_name) },
                        icon = resolveIcon(state.icon),
                        isActive = !state.isFolder && state.command.isNotBlank(),
                        onTestClick = if (!state.isFolder && state.command.isNotBlank()) {
                            { onIntent(CommandFormIntent.OnTestRunClick) }
                        } else null,
                    )
                }

                item {
                    FormSection(
                        label = stringResource(Res.string.command_form_name_label),
                        errorMessage = if (state.hasNameError) stringResource(Res.string.command_form_name_required) else null,
                    ) {
                        FilledTextField(
                            value = state.name,
                            onValueChange = { onIntent(CommandFormIntent.OnNameChanged(it)) },
                            placeholder = stringResource(Res.string.command_form_name_hint),
                            imeAction = if (state.isFolder) ImeAction.Done else ImeAction.Next,
                        )
                    }
                }

                if (!state.isFolder) {
                    item {
                        CommandModeToggle(
                            sectionLabel = stringResource(Res.string.command_form_rotary_label),
                            standardLabel = stringResource(Res.string.command_form_rotary_standard),
                            rotaryLabel = stringResource(Res.string.command_form_rotary_rotary),
                            rotaryHint = stringResource(Res.string.command_form_rotary_hint),
                            isRotary = state.isRotary,
                            onSelectStandard = {
                                onIntent(CommandFormIntent.OnIsRotaryChanged(false))
                                onIntent(CommandFormIntent.OnIsMediaScreenChanged(false))
                                onIntent(CommandFormIntent.OnIsAgentScreenChanged(false))
                            },
                            onSelectRotary = { onIntent(CommandFormIntent.OnIsRotaryChanged(true)) },
                            agentLabel = stringResource(Res.string.command_form_agent_label),
                            agentHint = stringResource(Res.string.command_form_agent_hint),
                            isAgent = state.isAgentScreen,
                            onSelectAgent = { onIntent(CommandFormIntent.OnIsAgentScreenChanged(true)) },
                        )
                    }
                }

                if (!state.isFolder) {
                    if (state.isMediaScreen) {
                        item {
                            Text(
                                text = stringResource(Res.string.command_form_media_hint),
                                style = Theme.typography.caption,
                                color = Theme.color.inkSubtle,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                        item {
                            FormSection(label = stringResource(Res.string.command_form_media_vol_up), errorMessage = null) {
                                CommandStringField(
                                    value = state.cwCommand,
                                    onValueChange = { onIntent(CommandFormIntent.OnCwCommandChanged(it)) },
                                    placeholder = stringResource(Res.string.command_form_media_placeholder_vol_up),
                                    tagLabel = utf8Tag,
                                    enabled = false,
                                )
                            }
                        }
                        if (availableSystemCommandOptions().isNotEmpty()) {
                            item {
                                BrowseSystemCommandButton(
                                    text = stringResource(Res.string.command_form_browse_system),
                                    onClick = { onIntent(CommandFormIntent.OnBrowseSystemCommandClick(SystemCommandTarget.CW)) },
                                )
                            }
                        }
                        item {
                            FormSection(label = stringResource(Res.string.command_form_media_vol_down), errorMessage = null) {
                                CommandStringField(
                                    value = state.ccwCommand,
                                    onValueChange = { onIntent(CommandFormIntent.OnCcwCommandChanged(it)) },
                                    placeholder = stringResource(Res.string.command_form_media_placeholder_vol_down),
                                    tagLabel = utf8Tag,
                                    enabled = false,
                                )
                            }
                        }
                        if (availableSystemCommandOptions().isNotEmpty()) {
                            item {
                                BrowseSystemCommandButton(
                                    text = stringResource(Res.string.command_form_browse_system),
                                    onClick = { onIntent(CommandFormIntent.OnBrowseSystemCommandClick(SystemCommandTarget.CCW)) },
                                )
                            }
                        }
                        item {
                            FormSection(label = stringResource(Res.string.command_form_media_play_pause), errorMessage = null) {
                                CommandStringField(
                                    value = state.playPauseCommand,
                                    onValueChange = { onIntent(CommandFormIntent.OnPlayPauseCommandChanged(it)) },
                                    placeholder = stringResource(Res.string.command_form_media_placeholder_play_pause),
                                    tagLabel = utf8Tag,
                                    enabled = false,
                                )
                            }
                        }
                        if (availableSystemCommandOptions().isNotEmpty()) {
                            item {
                                BrowseSystemCommandButton(
                                    text = stringResource(Res.string.command_form_browse_system),
                                    onClick = { onIntent(CommandFormIntent.OnBrowseSystemCommandClick(SystemCommandTarget.PLAY_PAUSE)) },
                                )
                            }
                        }
                        item {
                            FormSection(label = stringResource(Res.string.command_form_media_prev), errorMessage = null) {
                                CommandStringField(
                                    value = state.prevCommand,
                                    onValueChange = { onIntent(CommandFormIntent.OnPrevCommandChanged(it)) },
                                    placeholder = stringResource(Res.string.command_form_media_placeholder_prev),
                                    tagLabel = utf8Tag,
                                    enabled = false,
                                )
                            }
                        }
                        if (availableSystemCommandOptions().isNotEmpty()) {
                            item {
                                BrowseSystemCommandButton(
                                    text = stringResource(Res.string.command_form_browse_system),
                                    onClick = { onIntent(CommandFormIntent.OnBrowseSystemCommandClick(SystemCommandTarget.PREV)) },
                                )
                            }
                        }
                        item {
                            FormSection(label = stringResource(Res.string.command_form_media_next), errorMessage = null) {
                                CommandStringField(
                                    value = state.nextCommand,
                                    onValueChange = { onIntent(CommandFormIntent.OnNextCommandChanged(it)) },
                                    placeholder = stringResource(Res.string.command_form_media_placeholder_next),
                                    tagLabel = utf8Tag,
                                    enabled = false,
                                )
                            }
                        }
                        if (availableSystemCommandOptions().isNotEmpty()) {
                            item {
                                BrowseSystemCommandButton(
                                    text = stringResource(Res.string.command_form_browse_system),
                                    onClick = { onIntent(CommandFormIntent.OnBrowseSystemCommandClick(SystemCommandTarget.NEXT)) },
                                )
                            }
                        }
                        item {
                            IconPickerSection(
                                sectionLabel = stringResource(Res.string.command_form_select_icon),
                                countLabel = stringResource(Res.string.command_form_icon_count, iconCatalogue.size),
                                catalogue = iconCatalogue,
                                selectedIconKey = state.icon,
                                onIconSelected = { onIntent(CommandFormIntent.OnIconChanged(it)) },
                            )
                        }
                    } else if (state.isRotary) {
                        item {
                            Text(
                                text = stringResource(Res.string.command_form_rotary_hint_leaf),
                                style = Theme.typography.caption,
                                color = Theme.color.inkSubtle,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }

                        if (state.availableCommandTypeModels.size > 1) {
                            item {
                                val typeOptions = state.availableCommandTypeModels.map { type ->
                                    stringResource(
                                        when (type) {
                                            CommandTypeModel.SYSTEM -> Res.string.command_form_type_system
                                            CommandTypeModel.PROMPT -> Res.string.command_form_type_prompt
                                            CommandTypeModel.SHELL -> Res.string.command_form_type_shell
                                            CommandTypeModel.SHORTCUT -> Res.string.command_form_type_shortcut
                                        }
                                    )
                                }
                                CommandTypePicker(
                                    sectionLabel = stringResource(Res.string.command_form_type_label),
                                    options = typeOptions,
                                    selectedIndex = state.availableCommandTypeModels.indexOf(state.commandType),
                                    onSelected = { idx ->
                                        onIntent(CommandFormIntent.OnCommandTypeModelChanged(state.availableCommandTypeModels[idx]))
                                    },
                                )
                            }
                        }

                        item {
                            FormSection(
                                label = stringResource(Res.string.command_form_rotary_cw_label),
                                errorMessage = if (state.hasCwCommandError) stringResource(Res.string.command_form_rotary_cw_required) else null,
                            ) {
                                CommandStringField(
                                    value = state.cwCommand,
                                    onValueChange = { onIntent(CommandFormIntent.OnCwCommandChanged(it)) },
                                    placeholder = commandHintFor(state.commandType),
                                    tagLabel = utf8Tag,
                                    enabled = state.commandType != CommandTypeModel.SYSTEM,
                                )
                            }
                        }

                        if (state.commandType == CommandTypeModel.SYSTEM && availableSystemCommandOptions().isNotEmpty()) {
                            item {
                                BrowseSystemCommandButton(
                                    text = stringResource(Res.string.command_form_browse_system),
                                    onClick = { onIntent(CommandFormIntent.OnBrowseSystemCommandClick(SystemCommandTarget.CW)) },
                                )
                            }
                        }

                        item {
                            FormSection(
                                label = stringResource(Res.string.command_form_rotary_ccw_label),
                                errorMessage = if (state.hasCcwCommandError) stringResource(Res.string.command_form_rotary_ccw_required) else null,
                            ) {
                                CommandStringField(
                                    value = state.ccwCommand,
                                    onValueChange = { onIntent(CommandFormIntent.OnCcwCommandChanged(it)) },
                                    placeholder = commandHintFor(state.commandType),
                                    tagLabel = utf8Tag,
                                    enabled = state.commandType != CommandTypeModel.SYSTEM,
                                )
                            }
                        }

                        if (state.commandType == CommandTypeModel.SYSTEM && availableSystemCommandOptions().isNotEmpty()) {
                            item {
                                BrowseSystemCommandButton(
                                    text = stringResource(Res.string.command_form_browse_system),
                                    onClick = { onIntent(CommandFormIntent.OnBrowseSystemCommandClick(SystemCommandTarget.CCW)) },
                                )
                            }
                        }
                    } else if (state.isAgentScreen) {
                        item {
                            IconPickerSection(
                                sectionLabel = stringResource(Res.string.command_form_select_icon),
                                countLabel = stringResource(Res.string.command_form_icon_count, iconCatalogue.size),
                                catalogue = iconCatalogue,
                                selectedIconKey = state.icon,
                                onIconSelected = { onIntent(CommandFormIntent.OnIconChanged(it)) },
                            )
                        }
                    } else {
                        if (state.availableCommandTypeModels.size > 1) {
                            item {
                                val typeOptions = state.availableCommandTypeModels.map { type ->
                                    stringResource(
                                        when (type) {
                                            CommandTypeModel.SYSTEM -> Res.string.command_form_type_system
                                            CommandTypeModel.PROMPT -> Res.string.command_form_type_prompt
                                            CommandTypeModel.SHELL -> Res.string.command_form_type_shell
                                            CommandTypeModel.SHORTCUT -> Res.string.command_form_type_shortcut
                                        }
                                    )
                                }
                                CommandTypePicker(
                                    sectionLabel = stringResource(Res.string.command_form_type_label),
                                    options = typeOptions,
                                    selectedIndex = state.availableCommandTypeModels.indexOf(state.commandType),
                                    onSelected = { idx ->
                                        onIntent(CommandFormIntent.OnCommandTypeModelChanged(state.availableCommandTypeModels[idx]))
                                    },
                                )
                            }
                        }

                        item {
                            FormSection(
                                label = stringResource(Res.string.command_form_command_label),
                                errorMessage = if (state.hasCommandError) stringResource(Res.string.command_form_command_required) else null,
                            ) {
                                CommandStringField(
                                    value = state.command,
                                    onValueChange = { onIntent(CommandFormIntent.OnCommandChanged(it)) },
                                    placeholder = commandHintFor(state.commandType),
                                    tagLabel = utf8Tag,
                                    enabled = state.commandType != CommandTypeModel.SYSTEM,
                                )
                            }
                        }

                        if (state.commandType == CommandTypeModel.SYSTEM && availableSystemCommandOptions().isNotEmpty()) {
                            item {
                                BrowseSystemCommandButton(
                                    text = stringResource(Res.string.command_form_browse_system),
                                    onClick = { onIntent(CommandFormIntent.OnBrowseSystemCommandClick(SystemCommandTarget.MAIN)) },
                                )
                            }
                        }
                    }

                    if (!state.isAgentScreen) {
                        item {
                            IconPickerSection(
                                sectionLabel = stringResource(Res.string.command_form_select_icon),
                                countLabel = stringResource(Res.string.command_form_icon_count, iconCatalogue.size),
                                catalogue = iconCatalogue,
                                selectedIconKey = state.icon,
                                onIconSelected = { onIntent(CommandFormIntent.OnIconChanged(it)) },
                            )
                        }
                    }
                }

                if (!state.isFolder && !state.isRotary && !state.isMediaScreen && !state.isAgentScreen && state.command.isNotBlank()) {
                    item {
                        TestRunButton(
                            text = stringResource(Res.string.command_form_test_run),
                            onClick = { onIntent(CommandFormIntent.OnTestRunClick) },
                        )
                    }
                }

                item {
                    SaveMacroButton(
                        text = stringResource(Res.string.command_form_save),
                        onClick = { onIntent(CommandFormIntent.OnSaveClick) },
                    )
                }

                if (state.isEditMode) {
                    item {
                        DeleteActionButton(
                            text = stringResource(Res.string.command_form_delete),
                            onClick = { onIntent(CommandFormIntent.OnDeleteClick) },
                        )
                    }
                }
            }

            if (state.showDeleteConfirm) {
                ConfirmationSheet(
                    title = stringResource(Res.string.command_delete_confirm_title),
                    body = stringResource(Res.string.command_delete_confirm_message, state.name.ifBlank { "?" }),
                    confirmText = stringResource(Res.string.command_form_delete),
                    dismissText = stringResource(Res.string.command_form_cancel),
                    onConfirm = { onIntent(CommandFormIntent.OnDeleteConfirm) },
                    onDismiss = { onIntent(CommandFormIntent.OnDeleteDismiss) },
                    isDestructive = true,
                )
            }
        }
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
            StackedSnackbarHost(hostState = snackbarHostState)
        }
    }
}

@Composable
private fun commandHintFor(type: CommandTypeModel): String = when (type) {
    CommandTypeModel.SYSTEM -> stringResource(Res.string.command_form_hint_system)
    CommandTypeModel.PROMPT -> stringResource(Res.string.command_form_hint_prompt)
    CommandTypeModel.SHELL -> stringResource(Res.string.command_form_hint_shell)
    CommandTypeModel.SHORTCUT -> stringResource(Res.string.command_form_hint_shortcut)
}

@Preview(showBackground = true)
@Composable
private fun CommandFormContentPreview() {
    AppTheme {
        CommandFormContent(
            state = CommandFormState(
                isLoading = false,
                isEditMode = false,
                isFolder = false,
                name = "MUTE",
                command = "KEY_MUTE",
                icon = "volume",
                commandType = CommandTypeModel.SYSTEM,
                availableCommandTypeModels = listOf(CommandTypeModel.SYSTEM, CommandTypeModel.SHELL),
            ),
            onIntent = {},
            snackbarHostState = rememberStackedSnackbarHostState(),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CommandFormFolderContentPreview() {
    AppTheme {
        CommandFormContent(
            state = CommandFormState(
                isLoading = false,
                isEditMode = false,
                isFolder = true,
                name = "MEDIA",
            ),
            onIntent = {},
            snackbarHostState = rememberStackedSnackbarHostState(),
        )
    }
}
