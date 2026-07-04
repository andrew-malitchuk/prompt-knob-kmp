package presentation.core.ui.source.kit.atom.decoration

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview
import presentation.core.styling.core.Theme
import presentation.core.styling.source.theme.AppTheme

/**
 * Decorative wrapper that draws thin L-shaped bracket marks at each corner of its bounds.
 *
 * The brackets are rendered via [drawWithContent] and do not consume any layout space —
 * [content] fills the full area. Bracket geometry is controlled by [bracketLength] and
 * [strokeWidth]; both default to values that reproduce the targeting-reticle aesthetic
 * from the splash screen design.
 *
 * ```
 * ┌─    ─┐
 * │      │
 * │      │
 * └─    ─┘
 * ```
 *
 * @param modifier Modifier applied to the root [Box].
 * @param bracketLength Length of each bracket arm in dp. Defaults to 24 dp.
 * @param strokeWidth Stroke weight of the bracket lines in dp. Defaults to 1.5 dp.
 * @param color Stroke color. Defaults to [Theme.color.outlineHigh].
 * @param content Inner composable displayed inside the framed area.
 *
 * @see <a href="https://www.figma.com/design/klEUS15vSHsTO53UXCJ7Uo/sketches?node-id=1550-2">Figma</a>
 */
@Composable
public fun CornerFrame(
    modifier: Modifier = Modifier,
    bracketLength: Dp = 24.dp,
    strokeWidth: Dp = 1.5.dp,
    color: Color = Theme.color.outlineHigh,
    content: @Composable BoxScope.() -> Unit = {},
) {
    Box(
        modifier = modifier.drawWithContent {
            drawContent()
            val arm = bracketLength.toPx()
            val stroke = strokeWidth.toPx()
            val w = size.width
            val h = size.height

            // Top-left corner
            drawLine(color, Offset(0f, 0f), Offset(arm, 0f), stroke, StrokeCap.Square)
            drawLine(color, Offset(0f, 0f), Offset(0f, arm), stroke, StrokeCap.Square)

            // Top-right corner
            drawLine(color, Offset(w, 0f), Offset(w - arm, 0f), stroke, StrokeCap.Square)
            drawLine(color, Offset(w, 0f), Offset(w, arm), stroke, StrokeCap.Square)

            // Bottom-left corner
            drawLine(color, Offset(0f, h), Offset(arm, h), stroke, StrokeCap.Square)
            drawLine(color, Offset(0f, h), Offset(0f, h - arm), stroke, StrokeCap.Square)

            // Bottom-right corner
            drawLine(color, Offset(w, h), Offset(w - arm, h), stroke, StrokeCap.Square)
            drawLine(color, Offset(w, h), Offset(w, h - arm), stroke, StrokeCap.Square)
        },
        content = content,
    )
}

@Preview(showBackground = true)
@Composable
private fun CornerFramePreview() {
    AppTheme {
        CornerFrame(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
        )
    }
}
