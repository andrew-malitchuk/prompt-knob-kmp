package presentation.feature.onboarding.source.onboarding.content

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import presentation.core.styling.core.Theme
import presentation.core.styling.source.theme.AppTheme
import presentation.core.ui.source.kit.atom.button.TacticalButton
import presentation.core.ui.source.kit.atom.decoration.CornerFrame
import presentation.core.ui.source.kit.atom.icon.ChevronRight
import presentation.core.ui.source.kit.organism.animatedsequence.AnimatedItem
import presentation.core.ui.source.kit.organism.animatedsequence.AnimationSequenceHost
import presentation.feature.onboarding.source.onboarding.OnboardingIntent
import presentation.feature.onboarding.source.onboarding.OnboardingState
import prompt_knob_kmp.presentation_core_localisation.generated.resources.Res
import prompt_knob_kmp.presentation_core_localisation.generated.resources.onboarding_description
import prompt_knob_kmp.presentation_core_localisation.generated.resources.onboarding_get_started
import prompt_knob_kmp.presentation_core_localisation.generated.resources.onboarding_title

/** Duration in milliseconds for individual enter/exit animations. */
private const val ANIM_DURATION_MS = 400

/** Delay in milliseconds between sequenced animation items. */
private const val ANIM_DELAY_MS = 200L

/**
 * Success-state content for the Onboarding screen.
 *
 * Renders the full-screen onboarding layout:
 * - A targeting-reticle [CornerFrame] framing the entire dark canvas.
 * - The product title "PROMPT KNOB" in large [Theme.typography.display] type.
 * - A tagline in [Theme.typography.title] with a brutalist 2 dp accent underline below it.
 * - A [TacticalButton] "GET STARTED →" at the bottom to complete onboarding.
 *
 * All three sections animate in sequentially via [AnimationSequenceHost]: the title
 * fades in first, then the tagline and its divider, and finally the CTA button
 * slides up from the bottom.
 *
 * @param state Current [OnboardingState]; [OnboardingState.isLoading] disables the CTA button.
 * @param onIntent Callback to dispatch [presentation.feature.onboarding.source.onboarding.OnboardingIntent] actions to the ViewModel.
 *
 * @see OnboardingContent
 * @see <a href="https://www.figma.com/design/STUB_REPLACE_ME">Figma</a>
 */
@Composable
internal fun OnboardingSuccessContent(
    state: OnboardingState,
    onIntent: (OnboardingIntent) -> Unit,
) {
    CornerFrame(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.color.canvas),
    ) {
        AnimationSequenceHost(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(
                        horizontal = Theme.spacing.spacingL,
                        vertical = Theme.spacing.spacingL,
                    ),
            ) {
                Spacer(modifier = Modifier.weight(1f))

                // Product title — fades in at the vertical centre
                AnimatedItem(
                    index = 0,
                    delayAfterAnimation = ANIM_DELAY_MS,
                    enter = fadeIn(tween(ANIM_DURATION_MS)),
                    exit = fadeOut(tween(ANIM_DURATION_MS)),
                ) {
                    Text(
                        text = stringResource(Res.string.onboarding_title),
                        style = Theme.typography.display,
                        color = Theme.color.inkMain,
                    )
                }

                Spacer(modifier = Modifier.height(Theme.spacing.spacingL))

                // Tagline + brutalist 2 dp accent underline — fades in
                AnimatedItem(
                    index = 1,
                    delayAfterAnimation = ANIM_DELAY_MS,
                    enter = fadeIn(tween(ANIM_DURATION_MS)),
                    exit = fadeOut(tween(ANIM_DURATION_MS)),
                ) {
                    Column {
                        Text(
                            text = stringResource(Res.string.onboarding_description),
                            style = Theme.typography.title,
                            color = Theme.color.inkSubtle,
                        )
                        Spacer(modifier = Modifier.height(Theme.spacing.spacingS))
                        Box(
                            modifier = Modifier
                                .width(Theme.spacing.spacing2XL)
                                .height(Theme.stroke.regular)
                                .background(Theme.color.outlineHigh),
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // CTA button — slides up from the bottom
                AnimatedItem(
                    index = 2,
                    delayAfterAnimation = ANIM_DELAY_MS,
                    enter = slideInVertically(
                        animationSpec = tween(ANIM_DURATION_MS),
                        initialOffsetY = { it },
                    ) + fadeIn(tween(ANIM_DURATION_MS)),
                    exit = slideOutVertically(
                        animationSpec = tween(ANIM_DURATION_MS),
                        targetOffsetY = { it },
                    ) + fadeOut(tween(ANIM_DURATION_MS)),
                ) {
                    TacticalButton(
                        text = stringResource(Res.string.onboarding_get_started),
                        onClick = { onIntent(OnboardingIntent.OnGetStartedClick) },
                        icon = ChevronRight,
                        enabled = !state.isLoading,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun OnboardingSuccessContentPreview() {
    AppTheme {
        OnboardingSuccessContent(
            state = OnboardingState(),
            onIntent = {},
        )
    }
}
