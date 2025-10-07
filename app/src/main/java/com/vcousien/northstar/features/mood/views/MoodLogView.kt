package com.vcousien.northstar.features.mood.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vcousien.northstar.R
import com.vcousien.northstar.core.designsystem.NSSpacing
import com.vcousien.northstar.core.designsystem.NSTypography
import com.vcousien.northstar.core.designsystem.DesignTokens
import com.vcousien.northstar.features.mood.viewmodels.MoodTrackingViewModel

/**
 * MoodLogView - Main mood tracking screen
 *
 * Features:
 * - Today's mood tracking card
 * - Mood trend visualization
 * - Recent mood history
 * - Floating action button to add new entries
 * - Modal sheet for adding/editing entries
 * - Matches iOS MoodLogView iPhone layout
 *
 * @param modifier Modifier to be applied to the view
 * @param viewModel MoodTrackingViewModel for mood data
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodLogView(
    modifier: Modifier = Modifier,
    viewModel: MoodTrackingViewModel = viewModel()
) {
    val shouldShowAddSheet = viewModel.shouldShowAddSheet

    Scaffold(
        topBar = {
            MoodLogTopBar()
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.prepareNewEntry() },
                containerColor = DesignTokens.Colors.primary,
                contentColor = DesignTokens.Colors.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.mood_add_entry)
                )
            }
        },
        containerColor = DesignTokens.Colors.background
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Today's Mood Card
            TodayMoodCard(
                viewModel = viewModel,
                onMoodEntryClick = { viewModel.prepareNewEntry() },
                modifier = Modifier.padding(horizontal = NSSpacing.screenEdge)
            )

            Spacer(modifier = Modifier.height(NSSpacing.lg))

            // Mood Trend Card
            MoodTrendCard(
                viewModel = viewModel,
                modifier = Modifier.padding(horizontal = NSSpacing.screenEdge)
            )

            Spacer(modifier = Modifier.height(NSSpacing.lg))

            // Recent Mood History Card
            RecentMoodHistoryCard(
                viewModel = viewModel,
                modifier = Modifier.padding(horizontal = NSSpacing.screenEdge)
            )

            // Bottom padding for FAB clearance
            Spacer(modifier = Modifier.height(NSSpacing.xl))
        }
    }

    // Add/Edit Mood Entry Sheet
    if (shouldShowAddSheet) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.cancelEditing() },
            containerColor = DesignTokens.Colors.background
        ) {
            AddMoodEntryView(
                viewModel = viewModel,
                onDismiss = { viewModel.cancelEditing() }
            )
        }
    }
}

/**
 * Top bar for the mood log view
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MoodLogTopBar() {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.mood_title),
                style = NSTypography.heading1,
                color = DesignTokens.Colors.textPrimary
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = DesignTokens.Colors.background
        )
    )
}
