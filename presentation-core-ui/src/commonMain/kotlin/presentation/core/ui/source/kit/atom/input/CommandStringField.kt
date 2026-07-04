package presentation.core.ui.source.kit.atom.input

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import presentation.core.styling.core.Theme

/** Multiline command string input with a [tagLabel] pinned to the bottom-right corner. */
@Composable
public fun CommandStringField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    tagLabel: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val inkMain = if (enabled) Theme.color.inkMain else Theme.color.inkSubtle
    val inkSubtle = Theme.color.inkSubtle
    val brand = Theme.color.brand
    val body = Theme.typography.body

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(Theme.cornerToken.input)
            .background(Theme.color.surfaceVariant)
            .padding(Theme.spacing.spacingL),
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            textStyle = body.copy(color = inkMain),
            cursorBrush = SolidColor(brand),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Characters,
                imeAction = ImeAction.Done,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 80.dp),
            decorationBox = { innerTextField ->
                Box {
                    if (value.isEmpty()) {
                        Text(text = placeholder, style = body, color = inkSubtle)
                    }
                    innerTextField()
                }
            },
        )
        Text(
            text = tagLabel,
            style = Theme.typography.caption,
            color = Theme.color.inkSubtle,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(top = Theme.spacing.spacingXL),
        )
    }
}
