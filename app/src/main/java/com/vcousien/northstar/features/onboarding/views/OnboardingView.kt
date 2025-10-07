package com.vcousien.northstar.features.onboarding.views

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.vcousien.northstar.R
import com.vcousien.northstar.core.designsystem.DesignTokens
import com.vcousien.northstar.core.designsystem.NSSpacing
import com.vcousien.northstar.core.designsystem.NSTypography
import kotlinx.coroutines.launch

/**
 * OnboardingView - Feature introduction flow for first-time users
 *
 * Features:
 * - Multi-page swipeable onboarding
 * - Feature highlights (mood, sleep, medication, privacy)
 * - Page indicators
 * - Navigation buttons (Previous/Next/Get Started)
 * - Animated transitions
 * - Persists onboarding completion status
 * - Matches iOS OnboardingView implementation
 *
 * @param onComplete Callback when onboarding is completed
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingView(
    onComplete: () -> Unit
) {
    // Define colors as state to avoid calling @Composable functions outside composition
    val primaryColor = DesignTokens.Colors.primary
    val moodGoodColor = DesignTokens.Colors.moodColor(6) // Good mood level
    val successColor = DesignTokens.Colors.success
    val infoColor = DesignTokens.Colors.info

    val pages = remember(primaryColor, moodGoodColor, successColor, infoColor) {
        listOf(
            OnboardingPage(
                titleRes = R.string.onboarding_welcome_title,
                descriptionRes = R.string.onboarding_welcome_description,
                icon = Icons.Default.Star,
                backgroundColor = primaryColor
            ),
            OnboardingPage(
                titleRes = R.string.onboarding_mood_title,
                descriptionRes = R.string.onboarding_mood_description,
                icon = Icons.Default.Face,
                backgroundColor = moodGoodColor
            ),
            OnboardingPage(
                titleRes = R.string.onboarding_sleep_title,
                descriptionRes = R.string.onboarding_sleep_description,
                icon = Icons.Default.Bedtime,
                backgroundColor = primaryColor.copy(alpha = 0.8f)
            ),
            OnboardingPage(
                titleRes = R.string.onboarding_medication_title,
                descriptionRes = R.string.onboarding_medication_description,
                icon = Icons.Default.MedicalServices,
                backgroundColor = successColor
            ),
            OnboardingPage(
                titleRes = R.string.onboarding_privacy_title,
                descriptionRes = R.string.onboarding_privacy_description,
                icon = Icons.Default.Lock,
                backgroundColor = infoColor
            )
        )
    }

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()
    val currentPage = pagerState.currentPage

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(pages[currentPage].backgroundColor)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            OnboardingPageContent(page = pages[page])
        }

        // Page indicators and navigation buttons
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = NSSpacing.xl)
        ) {
            // Page indicators
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = NSSpacing.xl),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(pages.size) { index ->
                    val size by animateDpAsState(
                        targetValue = if (currentPage == index) 12.dp else 10.dp
                    )
                    Box(
                        modifier = Modifier
                            .padding(horizontal = NSSpacing.xs)
                            .size(size)
                            .clip(CircleShape)
                            .background(
                                if (currentPage == index)
                                    Color.White
                                else
                                    Color.White.copy(alpha = 0.5f)
                            )
                    )
                }
            }

            // Navigation buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = NSSpacing.xl),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Previous button
                AnimatedVisibility(
                    visible = currentPage > 0,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    TextButton(
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(currentPage - 1)
                            }
                        }
                    ) {
                        Text(
                            text = stringResource(R.string.onboarding_previous),
                            style = NSTypography.bodyBold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Next / Get Started button
                Button(
                    onClick = {
                        if (currentPage < pages.size - 1) {
                            scope.launch {
                                pagerState.animateScrollToPage(currentPage + 1)
                            }
                        } else {
                            onComplete()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = pages[currentPage].backgroundColor
                    ),
                    shape = MaterialTheme.shapes.large
                ) {
                    Text(
                        text = if (currentPage < pages.size - 1)
                            stringResource(R.string.onboarding_next)
                        else
                            stringResource(R.string.onboarding_get_started),
                        style = NSTypography.bodyBold,
                        modifier = Modifier.padding(horizontal = NSSpacing.md)
                    )
                }
            }
        }
    }
}

/**
 * Content for a single onboarding page
 */
@Composable
private fun OnboardingPageContent(
    page: OnboardingPage
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = NSSpacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.weight(1f))

        // Title
        Text(
            text = stringResource(page.titleRes),
            style = NSTypography.heading1,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = NSSpacing.md)
        )

        // Icon
        Icon(
            imageVector = page.icon,
            contentDescription = null,
            modifier = Modifier
                .size(120.dp)
                .padding(bottom = NSSpacing.xxl),
            tint = Color.White
        )

        // Description
        Text(
            text = stringResource(page.descriptionRes),
            style = NSTypography.body,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = NSSpacing.xxl)
        )

        Spacer(modifier = Modifier.weight(1f))

        // Extra space at bottom for indicators and buttons
        Spacer(modifier = Modifier.height(120.dp))
    }
}

/**
 * Data class representing a single onboarding page
 */
data class OnboardingPage(
    val titleRes: Int,
    val descriptionRes: Int,
    val icon: ImageVector,
    val backgroundColor: Color
)
