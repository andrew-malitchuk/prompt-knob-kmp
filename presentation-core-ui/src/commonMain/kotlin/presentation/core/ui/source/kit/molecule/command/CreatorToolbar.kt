package presentation.core.ui.source.kit.molecule.command

import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import presentation.core.styling.core.Theme
import presentation.core.ui.source.kit.molecule.header.ScreenHeader

/** ScreenHeader with a trailing cancel text button. */
@Composable
public fun CreatorToolbar(
    onBackClick: () -> Unit,
    cancelLabel: String,
    onCancelClick: () -> Unit,
    showDivider: Boolean = false,
    modifier: Modifier = Modifier,
) {
    ScreenHeader(
        onBackClick = onBackClick,
        showDivider = showDivider,
        modifier = modifier,
        trailingContent = {
            Text(
                text = cancelLabel,
                style = Theme.typography.label,
                color = Theme.color.inkSubtle,
                modifier = Modifier.clickable(onClick = onCancelClick),
            )
        },
    )
}
