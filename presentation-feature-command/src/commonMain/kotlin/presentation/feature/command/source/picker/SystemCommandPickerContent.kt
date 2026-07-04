package presentation.feature.command.source.picker

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.compose.ui.tooling.preview.Preview
import presentation.core.styling.core.Theme
import presentation.core.styling.source.theme.AppTheme
import presentation.core.ui.source.kit.molecule.command.SystemCommandRow
import presentation.core.ui.source.kit.molecule.header.ScreenHeader
import domain.core.source.model.SystemCommandOption
import presentation.feature.command.core.availableSystemCommandOptions
import presentation.feature.command.core.iconForSystemCommand

/**
 * Full-screen overlay listing all preset system commands available on the current
 * platform. Tapping a row passes the command string back via [onSelected].
 *
 * Only rendered when [availableSystemCommandOptions] is non-empty (Android only for now).
 */
@Composable
internal fun SystemCommandPickerContent(
    onSelected: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val options = availableSystemCommandOptions()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Theme.color.canvas),
    ) {
        val listState = rememberLazyListState()
        ScreenHeader(onBackClick = onBack, showDivider = listState.canScrollBackward)

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(Theme.spacing.spacingL),
            verticalArrangement = Arrangement.spacedBy(Theme.spacing.spacingM),
        ) {
            items(options, key = { it.command }) { option ->
                SystemCommandRow(
                    icon = iconForSystemCommand(option.command),
                    label = option.label,
                    description = option.description,
                    onClick = { onSelected(option.command) },
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SystemCommandPickerContentPreview() {
    AppTheme {
        SystemCommandPickerContent(
            onSelected = {},
            onBack = {},
        )
    }
}
