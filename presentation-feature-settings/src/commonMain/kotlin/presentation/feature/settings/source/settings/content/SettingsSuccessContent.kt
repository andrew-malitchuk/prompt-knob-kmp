package presentation.feature.settings.source.settings.content

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.foundation.border
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import presentation.core.styling.core.Theme
import presentation.core.styling.source.theme.AppTheme
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import presentation.core.ui.source.kit.atom.icon.Settings
import presentation.core.ui.source.kit.atom.toggle.SimpleToggle
import presentation.core.ui.source.kit.atom.indicator.StatusIndicator
import presentation.core.ui.source.kit.atom.indicator.StatusIndicatorState
import presentation.core.ui.source.kit.molecule.setting.SettingRow
import prompt_knob_kmp.presentation_core_localisation.generated.resources.Res
import presentation.core.ui.source.kit.atom.button.TacticalButton
import presentation.core.ui.source.kit.atom.text.SectionHeaderWithIcon
import presentation.core.ui.source.kit.molecule.item.SelectableCard
import presentation.core.ui.source.kit.molecule.item.SelectionRow
import prompt_knob_kmp.presentation_core_localisation.generated.resources.settings_appearance
import prompt_knob_kmp.presentation_core_localisation.generated.resources.settings_title
import prompt_knob_kmp.presentation_core_localisation.generated.resources.settings_accessibility_access
import prompt_knob_kmp.presentation_core_localisation.generated.resources.settings_accessibility_enable
import prompt_knob_kmp.presentation_core_localisation.generated.resources.settings_accessibility_granted
import prompt_knob_kmp.presentation_core_localisation.generated.resources.settings_accessibility_subtitle
import prompt_knob_kmp.presentation_core_localisation.generated.resources.settings_bluetooth_access
import prompt_knob_kmp.presentation_core_localisation.generated.resources.settings_bluetooth_granted
import prompt_knob_kmp.presentation_core_localisation.generated.resources.settings_bluetooth_request
import prompt_knob_kmp.presentation_core_localisation.generated.resources.settings_bluetooth_subtitle
import prompt_knob_kmp.presentation_core_localisation.generated.resources.settings_erase_all_data
import presentation.core.ui.source.kit.organism.bottomsheet.ConfirmationSheet
import prompt_knob_kmp.presentation_core_localisation.generated.resources.settings_erase_cancel_button
import prompt_knob_kmp.presentation_core_localisation.generated.resources.settings_erase_confirm_body
import prompt_knob_kmp.presentation_core_localisation.generated.resources.settings_erase_confirm_button
import prompt_knob_kmp.presentation_core_localisation.generated.resources.settings_erase_confirm_title
import prompt_knob_kmp.presentation_core_localisation.generated.resources.settings_notification_access
import prompt_knob_kmp.presentation_core_localisation.generated.resources.settings_notification_enable
import prompt_knob_kmp.presentation_core_localisation.generated.resources.settings_notification_granted
import prompt_knob_kmp.presentation_core_localisation.generated.resources.settings_notification_subtitle
import prompt_knob_kmp.presentation_core_localisation.generated.resources.settings_permissions
import prompt_knob_kmp.presentation_core_localisation.generated.resources.settings_connected_via_ble
import prompt_knob_kmp.presentation_core_localisation.generated.resources.settings_connecting
import prompt_knob_kmp.presentation_core_localisation.generated.resources.settings_core_version
import prompt_knob_kmp.presentation_core_localisation.generated.resources.settings_disconnected
import prompt_knob_kmp.presentation_core_localisation.generated.resources.settings_hardware_status
import prompt_knob_kmp.presentation_core_localisation.generated.resources.settings_language_selector
import prompt_knob_kmp.presentation_core_localisation.generated.resources.settings_theme_brutalist
import prompt_knob_kmp.presentation_core_localisation.generated.resources.settings_theme_brutalist_subtitle
import prompt_knob_kmp.presentation_core_localisation.generated.resources.settings_theme_monolith
import prompt_knob_kmp.presentation_core_localisation.generated.resources.settings_theme_monolith_subtitle
import prompt_knob_kmp.presentation_core_localisation.generated.resources.settings_data_export
import prompt_knob_kmp.presentation_core_localisation.generated.resources.settings_data_import
import prompt_knob_kmp.presentation_core_localisation.generated.resources.settings_data_section
import prompt_knob_kmp.presentation_core_localisation.generated.resources.settings_mcp_guide_copied
import prompt_knob_kmp.presentation_core_localisation.generated.resources.settings_mcp_guide_copy
import prompt_knob_kmp.presentation_core_localisation.generated.resources.settings_mcp_guide_command_label
import prompt_knob_kmp.presentation_core_localisation.generated.resources.settings_mcp_guide_how_body
import prompt_knob_kmp.presentation_core_localisation.generated.resources.settings_mcp_guide_how_title
import prompt_knob_kmp.presentation_core_localisation.generated.resources.settings_mcp_guide_tool_approval
import prompt_knob_kmp.presentation_core_localisation.generated.resources.settings_mcp_guide_tool_choice
import prompt_knob_kmp.presentation_core_localisation.generated.resources.settings_mcp_guide_tool_notify
import prompt_knob_kmp.presentation_core_localisation.generated.resources.settings_mcp_guide_tools_title
import prompt_knob_kmp.presentation_core_localisation.generated.resources.settings_mcp_guide_what_body
import prompt_knob_kmp.presentation_core_localisation.generated.resources.settings_mcp_guide_what_title
import prompt_knob_kmp.presentation_core_localisation.generated.resources.settings_claude_guide_config_label
import prompt_knob_kmp.presentation_core_localisation.generated.resources.settings_claude_guide_copied
import prompt_knob_kmp.presentation_core_localisation.generated.resources.settings_claude_guide_copy
import prompt_knob_kmp.presentation_core_localisation.generated.resources.settings_claude_guide_how_body
import prompt_knob_kmp.presentation_core_localisation.generated.resources.settings_claude_guide_how_title
import prompt_knob_kmp.presentation_core_localisation.generated.resources.settings_claude_guide_state_done
import prompt_knob_kmp.presentation_core_localisation.generated.resources.settings_claude_guide_state_idle
import prompt_knob_kmp.presentation_core_localisation.generated.resources.settings_claude_guide_state_waiting
import prompt_knob_kmp.presentation_core_localisation.generated.resources.settings_claude_guide_state_working
import prompt_knob_kmp.presentation_core_localisation.generated.resources.settings_claude_guide_states_title
import prompt_knob_kmp.presentation_core_localisation.generated.resources.settings_claude_guide_what_body
import prompt_knob_kmp.presentation_core_localisation.generated.resources.settings_claude_guide_what_title
import prompt_knob_kmp.presentation_core_localisation.generated.resources.settings_claude_section
import prompt_knob_kmp.presentation_core_localisation.generated.resources.settings_claude_server
import prompt_knob_kmp.presentation_core_localisation.generated.resources.settings_claude_subtitle
import prompt_knob_kmp.presentation_core_localisation.generated.resources.settings_mcp_section
import prompt_knob_kmp.presentation_core_localisation.generated.resources.settings_mcp_server
import prompt_knob_kmp.presentation_core_localisation.generated.resources.settings_mcp_subtitle
import prompt_knob_kmp.presentation_core_localisation.generated.resources.settings_system_info
import prompt_knob_kmp.presentation_core_localisation.generated.resources.settings_theme_selector
import presentation.feature.settings.source.settings.SettingsIntent
import presentation.feature.settings.source.settings.SettingsState

/**
 * Success-state content for the Settings screen.
 *
 * Renders the full settings UI in six sections:
 * 1. Screen title with accent underline
 * 2. Permissions — Bluetooth, notification, and accessibility access rows (platform-conditional)
 * 3. Appearance — language row (navigates to dedicated picker) + theme selector cards
 * 4. Data — export and import command JSON buttons
 * 5. Danger — "Erase All Data" button with a confirmation sheet
 * 6. Info — system info link, app version, and BLE hardware connection status
 *
 * All section headers, rows, toggles, and cards reuse existing design-system
 * components from `presentation-core-ui`.
 *
 * @param state Current [SettingsState] containing all displayable values.
 * @param onIntent Callback to dispatch [SettingsIntent] actions to the ViewModel.
 *
 * @see SettingsContent
 * @see <a href="https://www.figma.com/design/STUB_REPLACE_ME">Figma</a>
 */
@Composable
internal fun SettingsSuccessContent(
    state: SettingsState,
    onIntent: (SettingsIntent) -> Unit,
    onImportClick: () -> Unit,
    scrollState: ScrollState = rememberScrollState(),
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
    ) {
        Text(
            text = stringResource(Res.string.settings_title),
            style = Theme.typography.display,
            color = Theme.color.inkMain,
            modifier = Modifier
                .padding(horizontal = Theme.spacing.spacingL)
                .padding(top = Theme.spacing.spacingXL, bottom = Theme.spacing.spacingS),
        )
        // Accent underline under the title
        Box(
            modifier = Modifier
                .padding(horizontal = Theme.spacing.spacingL)
                .width(80.dp)
                .height(Theme.stroke.regular)
                .background(Theme.color.brand),
        )

        Spacer(modifier = Modifier.height(Theme.spacing.spacingXL))

        // The section header and its rows are shown only when at least one
        // permission is relevant on the current platform.
        val hasPermissionRows = state.isBleSupported || !state.isAccessibilityGranted || !state.isNotificationPermissionGranted
        if (hasPermissionRows) {
            SectionHeaderWithIcon(
                icon = Settings,
                title = stringResource(Res.string.settings_permissions),
            )

            Spacer(modifier = Modifier.height(Theme.spacing.spacingS))
        }

        if (state.isBleSupported) {
            Box(
                modifier = Modifier
                    .padding(horizontal = Theme.spacing.spacingL)
                    .fillMaxWidth()
                    .clip(shape = RoundedCornerShape(Theme.spacing.spacingXS))
                    .background(Theme.color.surfaceVariant),
            ) {
                SettingRow(
                    label = stringResource(Res.string.settings_bluetooth_access),
                    subtitle = stringResource(Res.string.settings_bluetooth_subtitle),
                    trailing = {
                        if (state.isBlePermissionGranted) {
                            Text(
                                text = stringResource(Res.string.settings_bluetooth_granted),
                                style = Theme.typography.caption,
                                color = Theme.color.success,
                            )
                        } else {
                            Text(
                                text = stringResource(Res.string.settings_bluetooth_request),
                                style = Theme.typography.caption,
                                color = Theme.color.brand,
                                modifier = Modifier.clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = { onIntent(SettingsIntent.OnRequestBlePermission) },
                                ),
                            )
                        }
                    },
                )
            }
        }

        // Notification permission row — shown on Android 13+ when not yet granted
        if (!state.isNotificationPermissionGranted) {
            Spacer(modifier = Modifier.height(Theme.spacing.spacingS))

            Box(
                modifier = Modifier
                    .padding(horizontal = Theme.spacing.spacingL)
                    .fillMaxWidth()
                    .clip(shape = RoundedCornerShape(Theme.spacing.spacingXS))
                    .background(Theme.color.surfaceVariant),
            ) {
                SettingRow(
                    label = stringResource(Res.string.settings_notification_access),
                    subtitle = stringResource(Res.string.settings_notification_subtitle),
                    trailing = {
                        Text(
                            text = stringResource(Res.string.settings_notification_enable),
                            style = Theme.typography.caption,
                            color = Theme.color.brand,
                            modifier = Modifier.clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = { onIntent(SettingsIntent.OnOpenNotificationSettings) },
                            ),
                        )
                    },
                )
            }
        }

        // Accessibility service row — shown only when not yet enabled
        if (!state.isAccessibilityGranted) {
            Spacer(modifier = Modifier.height(Theme.spacing.spacingS))

            Box(
                modifier = Modifier
                    .padding(horizontal = Theme.spacing.spacingL)
                    .fillMaxWidth()
                    .clip(shape = RoundedCornerShape(Theme.spacing.spacingXS))
                    .background(Theme.color.surfaceVariant),
            ) {
                SettingRow(
                    label = stringResource(Res.string.settings_accessibility_access),
                    subtitle = stringResource(Res.string.settings_accessibility_subtitle),
                    trailing = {
                        Text(
                            text = stringResource(Res.string.settings_accessibility_enable),
                            style = Theme.typography.caption,
                            color = Theme.color.brand,
                            modifier = Modifier.clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = { onIntent(SettingsIntent.OnOpenAccessibilitySettings) },
                            ),
                        )
                    },
                )
            }
        }

        if (hasPermissionRows) {
            Spacer(modifier = Modifier.height(Theme.spacing.spacingXL))
        }

        SectionHeaderWithIcon(
            icon = Settings,
            title = stringResource(Res.string.settings_appearance),
        )

        Spacer(modifier = Modifier.height(Theme.spacing.spacingS))

        // Language selector — tapping navigates to the dedicated Language Picker screen
        Box(
            modifier = Modifier
                .padding(horizontal = Theme.spacing.spacingL)
                .fillMaxWidth()
                .clip(shape = RoundedCornerShape(Theme.spacing.spacingXS))
                .background(Theme.color.surfaceVariant),
        ) {
            Column(modifier = Modifier.padding(Theme.spacing.spacingL)) {
                Text(
                    text = stringResource(Res.string.settings_language_selector),
                    style = Theme.typography.label,
                    color = Theme.color.inkSubtle,
                )
                Spacer(modifier = Modifier.height(Theme.spacing.spacingS))
                SelectionRow(
                    label = state.languageOptions.getOrElse(state.selectedLanguageIndex) { "English" },
                    onClick = { onIntent(SettingsIntent.OnLanguagePickerClick) },
                )
            }
        }

        Spacer(modifier = Modifier.height(Theme.spacing.spacingM))

        // Theme selector cards
        Box(
            modifier = Modifier
                .padding(horizontal = Theme.spacing.spacingL)
                .fillMaxWidth()
                .clip(shape = RoundedCornerShape(Theme.spacing.spacingXS))
                .background(Theme.color.surfaceVariant),
        ) {
            Column(modifier = Modifier.padding(Theme.spacing.spacingL)) {
                Text(
                    text = stringResource(Res.string.settings_theme_selector),
                    style = Theme.typography.label,
                    color = Theme.color.inkSubtle,
                )
                Spacer(modifier = Modifier.height(Theme.spacing.spacingM))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Theme.spacing.spacingS),
                ) {
                    // Index 0 = Dark (Monolith)
                    SelectableCard(
                        title = stringResource(Res.string.settings_theme_monolith),
                        subtitle = stringResource(Res.string.settings_theme_monolith_subtitle),
                        selected = state.selectedThemeIndex == 0,
                        onClick = { onIntent(SettingsIntent.SelectTheme(0)) },
                        modifier = Modifier.weight(1f),
                    )
                    // Index 1 = Light (Brutalist)
                    SelectableCard(
                        title = stringResource(Res.string.settings_theme_brutalist),
                        subtitle = stringResource(Res.string.settings_theme_brutalist_subtitle),
                        selected = state.selectedThemeIndex == 1,
                        onClick = { onIntent(SettingsIntent.SelectTheme(1)) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(Theme.spacing.spacingXL))

        SectionHeaderWithIcon(
            icon = Settings,
            title = stringResource(Res.string.settings_data_section),
        )

        Spacer(modifier = Modifier.height(Theme.spacing.spacingS))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Theme.spacing.spacingL),
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing.spacingS),
        ) {
            TacticalButton(
                text = stringResource(Res.string.settings_data_export),
                onClick = { onIntent(SettingsIntent.OnExportCommands) },
                modifier = Modifier.weight(1f),
            )
            TacticalButton(
                text = stringResource(Res.string.settings_data_import),
                onClick = onImportClick,
                modifier = Modifier.weight(1f),
            )
        }

        if (state.isMcpSectionVisible) {
            Spacer(modifier = Modifier.height(Theme.spacing.spacingXL))

            SectionHeaderWithIcon(
                icon = Settings,
                title = stringResource(Res.string.settings_mcp_section),
            )

            Spacer(modifier = Modifier.height(Theme.spacing.spacingS))

            // Toggle row
            Box(
                modifier = Modifier
                    .padding(horizontal = Theme.spacing.spacingL)
                    .fillMaxWidth()
                    .clip(shape = RoundedCornerShape(Theme.spacing.spacingXS))
                    .background(Theme.color.surfaceVariant),
            ) {
                SettingRow(
                    label = stringResource(Res.string.settings_mcp_server),
                    subtitle = stringResource(Res.string.settings_mcp_subtitle, state.mcpPort.toString()),
                    trailing = {
                        SimpleToggle(
                            checked = state.isMcpEnabled,
                            onCheckedChange = { onIntent(SettingsIntent.OnToggleMcp(it)) },
                        )
                    },
                )
            }

            Spacer(modifier = Modifier.height(Theme.spacing.spacingM))

            // Guide card
            val setupCommand = "claude mcp add prompt-knob --transport sse http://localhost:${state.mcpPort}/sse --scope user"
            var copied by remember { mutableStateOf(false) }
            val clipboard = LocalClipboardManager.current

            Column(
                modifier = Modifier
                    .padding(horizontal = Theme.spacing.spacingL)
                    .fillMaxWidth()
                    .clip(shape = RoundedCornerShape(Theme.spacing.spacingXS))
                    .background(Theme.color.surfaceVariant)
                    .padding(Theme.spacing.spacingL),
                verticalArrangement = Arrangement.spacedBy(Theme.spacing.spacingM),
            ) {
                // What is MCP
                Text(
                    text = stringResource(Res.string.settings_mcp_guide_what_title),
                    style = Theme.typography.label,
                    color = Theme.color.inkSubtle,
                )
                Text(
                    text = stringResource(Res.string.settings_mcp_guide_what_body),
                    style = Theme.typography.caption,
                    color = Theme.color.inkMain,
                )

                // Divider
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(Theme.stroke.thin)
                        .background(Theme.color.outlineLow),
                )

                // How to connect
                Text(
                    text = stringResource(Res.string.settings_mcp_guide_how_title),
                    style = Theme.typography.label,
                    color = Theme.color.inkSubtle,
                )
                Text(
                    text = stringResource(Res.string.settings_mcp_guide_how_body),
                    style = Theme.typography.caption,
                    color = Theme.color.inkMain,
                )

                // Setup command block
                Text(
                    text = stringResource(Res.string.settings_mcp_guide_command_label),
                    style = Theme.typography.label,
                    color = Theme.color.inkSubtle,
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(Theme.spacing.spacingXS))
                        .border(
                            width = Theme.stroke.regular,
                            color = Theme.color.outlineLow,
                            shape = RoundedCornerShape(Theme.spacing.spacingXS),
                        )
                        .background(Theme.color.surface)
                        .padding(Theme.spacing.spacingM),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = setupCommand,
                        style = Theme.typography.caption,
                        color = Theme.color.inkMain,
                        modifier = Modifier.weight(1f),
                    )
                    Spacer(modifier = Modifier.width(Theme.spacing.spacingS))
                    Text(
                        text = if (copied) stringResource(Res.string.settings_mcp_guide_copied)
                               else stringResource(Res.string.settings_mcp_guide_copy),
                        style = Theme.typography.caption,
                        color = if (copied) Theme.color.success else Theme.color.brand,
                        modifier = Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                        ) {
                            clipboard.setText(AnnotatedString(setupCommand))
                            copied = true
                        },
                    )
                }

                // Divider
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(Theme.stroke.thin)
                        .background(Theme.color.outlineLow),
                )

                // Available tools
                Text(
                    text = stringResource(Res.string.settings_mcp_guide_tools_title),
                    style = Theme.typography.label,
                    color = Theme.color.inkSubtle,
                )
                listOf(
                    stringResource(Res.string.settings_mcp_guide_tool_approval),
                    stringResource(Res.string.settings_mcp_guide_tool_choice),
                    stringResource(Res.string.settings_mcp_guide_tool_notify),
                ).forEach { tool ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Theme.spacing.spacingS),
                        verticalAlignment = Alignment.Top,
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(top = 5.dp)
                                .size(4.dp)
                                .clip(CircleShape)
                                .background(Theme.color.brand),
                        )
                        Text(
                            text = tool,
                            style = Theme.typography.caption,
                            color = Theme.color.inkMain,
                        )
                    }
                }
            }
        }

        if (state.isClaudeSectionVisible) {
            Spacer(modifier = Modifier.height(Theme.spacing.spacingXL))

            SectionHeaderWithIcon(
                icon = Settings,
                title = stringResource(Res.string.settings_claude_section),
            )

            Spacer(modifier = Modifier.height(Theme.spacing.spacingS))

            Box(
                modifier = Modifier
                    .padding(horizontal = Theme.spacing.spacingL)
                    .fillMaxWidth()
                    .clip(shape = RoundedCornerShape(Theme.spacing.spacingXS))
                    .background(Theme.color.surfaceVariant),
            ) {
                SettingRow(
                    label = stringResource(Res.string.settings_claude_server),
                    subtitle = stringResource(Res.string.settings_claude_subtitle, state.claudeHookPort.toString()),
                    trailing = {
                        SimpleToggle(
                            checked = state.isClaudeModeEnabled,
                            onCheckedChange = { onIntent(SettingsIntent.OnToggleClaudeMode(it)) },
                        )
                    },
                )
            }

            Spacer(modifier = Modifier.height(Theme.spacing.spacingM))

            val hooksConfig = """{"hooks":{"SessionStart":[{"hooks":[{"type":"command","command":"curl -s -m 2 -X POST http://127.0.0.1:${state.claudeHookPort}/state -d 'idle'"}]}],"UserPromptSubmit":[{"hooks":[{"type":"command","command":"curl -s -m 2 -X POST http://127.0.0.1:${state.claudeHookPort}/state -d 'working'"}]}],"PreToolUse":[{"hooks":[{"type":"command","command":"curl -s -m 2 -X POST http://127.0.0.1:${state.claudeHookPort}/state -d 'working'"}]}],"Notification":[{"hooks":[{"type":"command","command":"curl -s -m 2 -X POST http://127.0.0.1:${state.claudeHookPort}/state -d 'waiting'"}]}],"Stop":[{"hooks":[{"type":"command","command":"curl -s -m 2 -X POST http://127.0.0.1:${state.claudeHookPort}/state -d 'done'"}]}],"SessionEnd":[{"hooks":[{"type":"command","command":"curl -s -m 2 -X POST http://127.0.0.1:${state.claudeHookPort}/state -d 'idle'"}]}]}}"""
            var claudeCopied by remember { mutableStateOf(false) }
            val claudeClipboard = LocalClipboardManager.current

            Column(
                modifier = Modifier
                    .padding(horizontal = Theme.spacing.spacingL)
                    .fillMaxWidth()
                    .clip(shape = RoundedCornerShape(Theme.spacing.spacingXS))
                    .background(Theme.color.surfaceVariant)
                    .padding(Theme.spacing.spacingL),
                verticalArrangement = Arrangement.spacedBy(Theme.spacing.spacingM),
            ) {
                Text(
                    text = stringResource(Res.string.settings_claude_guide_what_title),
                    style = Theme.typography.label,
                    color = Theme.color.inkSubtle,
                )
                Text(
                    text = stringResource(Res.string.settings_claude_guide_what_body),
                    style = Theme.typography.caption,
                    color = Theme.color.inkMain,
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(Theme.stroke.thin)
                        .background(Theme.color.outlineLow),
                )

                Text(
                    text = stringResource(Res.string.settings_claude_guide_how_title),
                    style = Theme.typography.label,
                    color = Theme.color.inkSubtle,
                )
                Text(
                    text = stringResource(Res.string.settings_claude_guide_how_body),
                    style = Theme.typography.caption,
                    color = Theme.color.inkMain,
                )

                Text(
                    text = stringResource(Res.string.settings_claude_guide_config_label),
                    style = Theme.typography.label,
                    color = Theme.color.inkSubtle,
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(Theme.spacing.spacingXS))
                        .border(
                            width = Theme.stroke.regular,
                            color = Theme.color.outlineLow,
                            shape = RoundedCornerShape(Theme.spacing.spacingXS),
                        )
                        .background(Theme.color.surface)
                        .padding(Theme.spacing.spacingM),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = hooksConfig,
                        style = Theme.typography.caption,
                        color = Theme.color.inkMain,
                        modifier = Modifier.weight(1f),
                    )
                    Spacer(modifier = Modifier.width(Theme.spacing.spacingS))
                    Text(
                        text = if (claudeCopied) stringResource(Res.string.settings_claude_guide_copied)
                               else stringResource(Res.string.settings_claude_guide_copy),
                        style = Theme.typography.caption,
                        color = if (claudeCopied) Theme.color.success else Theme.color.brand,
                        modifier = Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                        ) {
                            claudeClipboard.setText(AnnotatedString(hooksConfig))
                            claudeCopied = true
                        },
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(Theme.stroke.thin)
                        .background(Theme.color.outlineLow),
                )

                Text(
                    text = stringResource(Res.string.settings_claude_guide_states_title),
                    style = Theme.typography.label,
                    color = Theme.color.inkSubtle,
                )
                listOf(
                    stringResource(Res.string.settings_claude_guide_state_working),
                    stringResource(Res.string.settings_claude_guide_state_waiting),
                    stringResource(Res.string.settings_claude_guide_state_done),
                    stringResource(Res.string.settings_claude_guide_state_idle),
                ).forEach { stateLabel ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Theme.spacing.spacingS),
                        verticalAlignment = Alignment.Top,
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(top = 5.dp)
                                .size(4.dp)
                                .clip(CircleShape)
                                .background(Theme.color.brand),
                        )
                        Text(
                            text = stateLabel,
                            style = Theme.typography.caption,
                            color = Theme.color.inkMain,
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(Theme.spacing.spacing3XL))

        TacticalButton(
            text = stringResource(Res.string.settings_erase_all_data),
            onClick = { onIntent(SettingsIntent.OnEraseAllDataClick) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Theme.spacing.spacingL),
        )

        if (state.showEraseConfirmation) {
            ConfirmationSheet(
                title = stringResource(Res.string.settings_erase_confirm_title),
                body = stringResource(Res.string.settings_erase_confirm_body),
                confirmText = stringResource(Res.string.settings_erase_confirm_button),
                dismissText = stringResource(Res.string.settings_erase_cancel_button),
                onConfirm = { onIntent(SettingsIntent.OnEraseAllData) },
                onDismiss = { onIntent(SettingsIntent.OnDismissEraseConfirmation) },
                isDestructive = true,
            )
        }

        Spacer(modifier = Modifier.height(Theme.spacing.spacingXL))

        // System info navigation link
        Text(
            text = stringResource(Res.string.settings_system_info),
            style = Theme.typography.label,
            color = Theme.color.brand,
            modifier = Modifier
                .padding(horizontal = Theme.spacing.spacingL)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { onIntent(SettingsIntent.OnSystemInfoClick) },
                ),
        )

        Spacer(modifier = Modifier.height(Theme.spacing.spacingM))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Theme.spacing.spacingL)
                .height(Theme.stroke.thin)
                .background(Theme.color.outlineLow),
        )

        Spacer(modifier = Modifier.height(Theme.spacing.spacingL))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Theme.spacing.spacingL),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
        ) {
            Column {
                Text(
                    text = stringResource(Res.string.settings_core_version),
                    style = Theme.typography.label,
                    color = Theme.color.inkSubtle,
                )
                Spacer(modifier = Modifier.height(Theme.spacing.spacingXS))
                Text(
                    text = state.appVersion,
                    style = Theme.typography.display,
                    color = Theme.color.inkMain,
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = stringResource(Res.string.settings_hardware_status),
                    style = Theme.typography.label,
                    color = Theme.color.inkSubtle,
                )
                Spacer(modifier = Modifier.height(Theme.spacing.spacingXS))
                val connectionLabel = when (state.connectionStateName) {
                    "CONNECTED VIA BLE" -> stringResource(Res.string.settings_connected_via_ble)
                    "CONNECTING" -> stringResource(Res.string.settings_connecting)
                    else -> stringResource(Res.string.settings_disconnected)
                }
                val statusState = when (state.connectionStateName) {
                    "CONNECTED VIA BLE" -> StatusIndicatorState.Connected
                    "CONNECTING" -> StatusIndicatorState.Standby
                    else -> StatusIndicatorState.Offline
                }
                StatusIndicator(
                    state = statusState,
                    label = connectionLabel,
                )
            }
        }

        Spacer(modifier = Modifier.height(Theme.spacing.spacingXL))
    }
}

@Preview
@Composable
private fun SettingsSuccessContentPreview() {
    AppTheme {
        SettingsSuccessContent(
            state = SettingsState(),
            onIntent = {},
            onImportClick = {},
        )
    }
}

@Preview
@Composable
private fun SettingsSuccessContentWithPermissionsPreview() {
    AppTheme {
        SettingsSuccessContent(
            state = SettingsState(
                isBleSupported = true,
                isBlePermissionGranted = false,
                isAccessibilityGranted = false,
                isNotificationPermissionGranted = false,
                connectionStateName = "CONNECTED VIA BLE",
                appVersion = "v0.1.0",
            ),
            onIntent = {},
            onImportClick = {},
        )
    }
}
