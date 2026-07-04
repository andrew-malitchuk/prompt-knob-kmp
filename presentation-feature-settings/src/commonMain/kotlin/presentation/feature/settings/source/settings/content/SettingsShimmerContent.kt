package presentation.feature.settings.source.settings.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview
import presentation.core.styling.core.Theme
import presentation.core.styling.source.theme.AppTheme
import presentation.core.ui.core.modifier.ShimmerProvider
import presentation.core.ui.core.modifier.shimmerable

/**
 * Shimmer loading placeholder for the Settings screen.
 *
 * Mirrors the layout of [SettingsSuccessContent] with shimmering boxes in place
 * of each content element, providing a skeleton preview while preferences are
 * being loaded.
 *
 * @see SettingsSuccessContent
 * @see <a href="https://www.figma.com/design/STUB_REPLACE_ME">Figma</a>
 */
@Composable
internal fun SettingsShimmerContent() {
    ShimmerProvider(isLoading = true) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Theme.spacing.spacingL),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(48.dp)
                    .shimmerable(shape = RoundedCornerShape(4.dp)),
            )

            Spacer(modifier = Modifier.height(Theme.spacing.spacingM))

            // Accent underline shimmer
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.25f)
                    .height(2.dp)
                    .shimmerable(shape = RoundedCornerShape(1.dp)),
            )

            Spacer(modifier = Modifier.height(Theme.spacing.spacingXL))

            // Section header
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .height(12.dp)
                    .shimmerable(shape = RoundedCornerShape(4.dp)),
            )

            Spacer(modifier = Modifier.height(Theme.spacing.spacingS))

            // Toggle row
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shimmerable(shape = RoundedCornerShape(4.dp)),
            )

            Spacer(modifier = Modifier.height(Theme.spacing.spacingXL))

            // Section header
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.35f)
                    .height(12.dp)
                    .shimmerable(shape = RoundedCornerShape(4.dp)),
            )

            Spacer(modifier = Modifier.height(Theme.spacing.spacingS))

            // Language dropdown
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .shimmerable(shape = RoundedCornerShape(4.dp)),
            )

            Spacer(modifier = Modifier.height(Theme.spacing.spacingM))

            // Theme cards - side by side
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Theme.spacing.spacingS),
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(80.dp)
                        .shimmerable(shape = RoundedCornerShape(4.dp)),
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(80.dp)
                        .shimmerable(shape = RoundedCornerShape(4.dp)),
                )
            }

            Spacer(modifier = Modifier.height(Theme.spacing.spacing3XL))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .shimmerable(shape = RoundedCornerShape(1.dp)),
            )

            Spacer(modifier = Modifier.height(Theme.spacing.spacingL))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .height(12.dp)
                            .shimmerable(shape = RoundedCornerShape(4.dp)),
                    )
                    Spacer(modifier = Modifier.height(Theme.spacing.spacingXS))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .height(40.dp)
                            .shimmerable(shape = RoundedCornerShape(4.dp)),
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.4f)
                            .height(12.dp)
                            .shimmerable(shape = RoundedCornerShape(4.dp)),
                    )
                    Spacer(modifier = Modifier.height(Theme.spacing.spacingXS))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .height(16.dp)
                            .shimmerable(shape = RoundedCornerShape(4.dp)),
                    )
                }
            }

            Spacer(modifier = Modifier.height(Theme.spacing.spacingL))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .shimmerable(shape = RoundedCornerShape(4.dp)),
            )

            Spacer(modifier = Modifier.height(Theme.spacing.spacing5XL))
        }
    }
}

@Preview
@Composable
private fun SettingsShimmerContentPreview() {
    AppTheme {
        SettingsShimmerContent()
    }
}
