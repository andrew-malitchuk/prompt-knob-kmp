package presentation.core.ui.source.kit.atom.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/**
 * [AlertTriangle] icon as a Compose [ImageVector].
 */
public val AlertTriangle: ImageVector
    get() {
        if (_AlertTriangle != null) {
            return _AlertTriangle!!
        }
        _AlertTriangle =
            ImageVector.Builder(
                name = "AlertTriangle",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 256f,
                viewportHeight = 256f,
            ).apply {
                path(fill = SolidColor(Color(0xFF1F1F1F))) {
                    // M236.8,188.09 L149.35,36.22 h0
                    // a24.76,24.76,0,0,0-42.7,0L19.2,188.09
                    // a23.51,23.51,0,0,0,0,23.72A24.35,24.35,0,0,0,40.55,224
                    // h174.9a24.35,24.35,0,0,0,21.33-12.19A23.51,23.51,0,0,0,236.8,188.09Z
                    moveTo(236.8f, 188.09f)
                    lineTo(149.35f, 36.22f)
                    horizontalLineToRelative(0f)
                    arcToRelative(24.76f, 24.76f, 0f, false, false, -42.7f, 0f)
                    lineTo(19.2f, 188.09f)
                    arcToRelative(23.51f, 23.51f, 0f, false, false, 0f, 23.72f)
                    arcTo(24.35f, 24.35f, 0f, false, false, 40.55f, 224f)
                    horizontalLineToRelative(174.9f)
                    arcToRelative(24.35f, 24.35f, 0f, false, false, 21.33f, -12.19f)
                    arcTo(23.51f, 23.51f, 0f, false, false, 236.8f, 188.09f)
                    close()
                    // M222.93,203.8a8.5,8.5,0,0,1-7.48,4.2H40.55a8.5,8.5,0,0,1-7.48-4.2
                    // a7.59,7.59,0,0,1,0-7.72L120.52,44.21a8.75,8.75,0,0,1,15,0
                    // l87.45,151.87A7.59,7.59,0,0,1,222.93,203.8Z
                    moveTo(222.93f, 203.8f)
                    arcToRelative(8.5f, 8.5f, 0f, false, true, -7.48f, 4.2f)
                    horizontalLineTo(40.55f)
                    arcToRelative(8.5f, 8.5f, 0f, false, true, -7.48f, -4.2f)
                    arcToRelative(7.59f, 7.59f, 0f, false, true, 0f, -7.72f)
                    lineTo(120.52f, 44.21f)
                    arcToRelative(8.75f, 8.75f, 0f, false, true, 15f, 0f)
                    lineToRelative(87.45f, 151.87f)
                    arcTo(7.59f, 7.59f, 0f, false, true, 222.93f, 203.8f)
                    close()
                    // M120,144V104a8,8,0,0,1,16,0v40a8,8,0,0,1-16,0Z
                    moveTo(120f, 144f)
                    verticalLineTo(104f)
                    arcToRelative(8f, 8f, 0f, false, true, 16f, 0f)
                    verticalLineToRelative(40f)
                    arcToRelative(8f, 8f, 0f, false, true, -16f, 0f)
                    close()
                    // M140,180a12,12,0,1,1-12-12A12,12,0,0,1,140,180Z
                    moveTo(140f, 180f)
                    arcTo(12f, 12f, 0f, true, true, 128f, 168f)
                    arcTo(12f, 12f, 0f, false, true, 140f, 180f)
                    close()
                }
            }.build()

        return _AlertTriangle!!
    }

@Suppress("ObjectPropertyName")
private var _AlertTriangle: ImageVector? = null
