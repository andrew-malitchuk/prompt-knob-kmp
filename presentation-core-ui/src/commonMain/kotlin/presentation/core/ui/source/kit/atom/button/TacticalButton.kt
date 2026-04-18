package presentation.core.ui.source.kit.atom.button

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import presentation.core.styling.core.Theme

/**
 * Primary CTA button for the Kinetic Mono design system — the "Tactical Button".
 *
 * Fulfills screen-level transitions (e.g. main screen → next step). Renders a
 * full-width rectangular button with:
 *
 * - **Tactile gradient** background (`brand` → `brandVariant`, 135° diagonal).
 * - **2 dp corners** — brutalist constraint from the design system.
 * - **Shadow-xl** — diffused black shadow for physical presence on dark surfaces.
 * - **Icon translate** — the optional trailing icon shifts +4 dp on press to simulate
 *   mechanical readiness.
 * - **Scale press feedback** — button shrinks to 97 % on press for a physical click feel.
 *
 * ```kotlin
 * TacticalButton(
 *     text = "INITIALIZE",
 *     icon = ChevronRight,
 *     onClick = { navController.navigate(Home) },
 * )
 * ```
 *
 * @param text Label displayed in the button — uppercase at call site for the engineering aesthetic.
 * @param onClick Callback invoked on tap.
 * @param modifier Modifier applied to the root composable.
 * @param icon Optional trailing icon that translates on press (mechanical readiness).
 * @param enabled Whether the button accepts input.
 *
 * @see <a href="https://www.figma.com/design/STUB_REPLACE_ME">Figma</a>
 */
@Composable
public fun TacticalButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Scale down slightly on press — physical click feel
    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.97f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium,
        ),
        label = "TacticalButton: scale",
    )

    // Trailing icon slides right on press — mechanical readiness
    val iconOffsetX by animateDpAsState(
        targetValue = if (isPressed && enabled) 4.dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow,
        ),
        label = "TacticalButton: iconOffsetX",
    )

    val brand = Theme.color.brand
    val brandVariant = Theme.color.brandVariant
    val inkOnBrand = Theme.color.inkOnBrand
    val disabled = Theme.color.disabled
    val corner = Theme.cornerToken.button

    val foreground = if (enabled) inkOnBrand else disabled
    val shadowElevation = if (enabled) 12.dp else 0.dp

    Box(
        modifier = modifier
            .scale(scale)
            .shadow(
                elevation = shadowElevation,
                shape = corner,
                ambientColor = androidx.compose.ui.graphics.Color.Black,
                spotColor = androidx.compose.ui.graphics.Color.Black,
            )
            .clip(corner)
            .drawBehind {
                if (enabled) {
                    drawRect(
                        brush = Brush.linearGradient(
                            colors = listOf(brand, brandVariant),
                            start = Offset(size.width, 0f),
                            end = Offset(0f, size.height),
                        ),
                    )
                } else {
                    drawRect(color = disabled.copy(alpha = 0.3f))
                }
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick,
                role = Role.Button,
            )
            .padding(
                horizontal = Theme.spacing.spacingXL,
                vertical = Theme.spacing.spacingL,
            )
            .fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                text = text,
                style = Theme.typography.action,
                color = foreground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (icon != null) {
                Spacer(modifier = Modifier.width(Theme.spacing.spacingS))
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = foreground,
                    modifier = Modifier
                        .size(Theme.size.iconM)
                        .offset(x = iconOffsetX),
                )
            }
        }
    }
}
