package com.vcousien.northstar.features.mood.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vcousien.northstar.core.designsystem.NSSpacing
import com.vcousien.northstar.features.mood.viewmodels.MoodTrackingViewModel

/**
 * MoodLogView - Main mood tracking screen
 *
 * Features:
 * - Today's mood tracking card
 * - Mood trend visualization
 * - Recent mood history
 * - Matches iOS MoodLogView iPhone layout
 *
 * @param modifier Modifier to be applied to the view
 * @param viewModel MoodTrackingViewModel for mood data
 */
@Composable
fun MoodLogView(
    modifier: Modifier = Modifier,
    viewModel: MoodTrackingViewModel = viewModel()
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = NSSpacing.screenEdge)
    ) {
        Spacer(modifier = Modifier.height(NSSpacing.lg))

        // Today's Mood Card
        TodayMoodCard(
            viewModel = viewModel
        )

        Spacer(modifier = Modifier.height(NSSpacing.lg))

        // Mood Trend Card
        MoodTrendCard(
            viewModel = viewModel
        )

        Spacer(modifier = Modifier.height(NSSpacing.lg))

        // Recent Mood History Card
        RecentMoodHistoryCard(
            viewModel = viewModel
        )

        Spacer(modifier = Modifier.height(NSSpacing.xxl))
    }
}
