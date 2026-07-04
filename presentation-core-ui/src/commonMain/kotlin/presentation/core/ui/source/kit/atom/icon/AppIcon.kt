package presentation.core.ui.source.kit.atom.icon

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import presentation.core.styling.core.Theme

/**
 * [AppIcon] icon as a Compose [ImageVector].
 */
public val AppIcon: ImageVector
    @Composable
    get() {
        if (_AppIcon != null) {
            return _AppIcon!!
        }
        _AppIcon = ImageVector.Builder(
            name = "AppIcon",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 177f,
            viewportHeight = 177f,
        ).apply {
            group(
                pivotX = 88.5f,
                pivotY = 88.5f,
                scaleX = 0.65f,
                scaleY = 0.65f,
            ) {
                // Sector indicator (3–6 o'clock)
                path(fill = SolidColor(Theme.color.inkMain)) {
                    moveTo(89f, 117.467f)
                    verticalLineTo(147.067f)
                    lineTo(96.7333f, 146.133f)
                    curveTo(114.333f, 144.133f, 132.867f, 130.8f, 140.867f, 114.4f)
                    curveTo(144.867f, 106.4f, 148.333f, 91.6f, 146.867f, 89.2f)
                    curveTo(146.467f, 88.5333f, 133.267f, 88f, 117.533f, 88f)
                    horizontalLineTo(89f)
                    verticalLineTo(117.467f)
                    close()
                }
                // Ring
                path(
                    fill = null,
                    stroke = SolidColor(Theme.color.inkMain),
                    strokeLineWidth = 25f,
                ) {
                    moveTo(12.5f, 88.5f)
                    arcTo(76f, 76f, 0f, true, false, 164.5f, 88.5f)
                    arcTo(76f, 76f, 0f, true, false, 12.5f, 88.5f)
                    close()
                }
            }
        }.build()

        return _AppIcon!!
    }

@Suppress("ObjectPropertyName")
private var _AppIcon: ImageVector? = null
