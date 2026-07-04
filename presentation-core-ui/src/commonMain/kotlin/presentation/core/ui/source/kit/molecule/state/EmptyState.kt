package presentation.core.ui.source.kit.molecule.state

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import presentation.core.styling.core.Theme

/** Bordered card with centered [message] for empty-list placeholders. */
@Composable
public fun EmptyState(
    message: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(Theme.cornerToken.card)
            .background(Theme.color.surface)
            .border(
                width = Theme.stroke.thin,
                color = Theme.color.outlineLow,
                shape = Theme.cornerToken.card,
            )
            .padding(Theme.spacing.spacing2XL),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = message,
            style = Theme.typography.label,
            color = Theme.color.inkSubtle,
            textAlign = TextAlign.Center,
        )
    }
}
