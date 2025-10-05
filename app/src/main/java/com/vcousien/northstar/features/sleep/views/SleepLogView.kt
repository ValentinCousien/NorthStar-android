package com.vcousien.northstar.features.sleep.views

import androidx.compose.foundation.background
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
import com.vcousien.northstar.features.sleep.viewmodels.SleepTrackingViewModel

/**
 * SleepLogView - Main sleep tracking screen
 *
 * Displays the main sleep tracking interface with today's sleep,
 * statistics, and recent history. Allows users to add new sleep entries
 * via a floating action button.
 *
 * Features:
 * - Today's sleep card
 * - Sleep statistics card (7-day averages)
 * - Recent sleep history card
 * - Floating action button to add new entries
 * - Modal sheet for adding/editing entries
 * - Matches iOS SleepLogView (iPhone layout)
 *
 * @param viewModel SleepTrackingViewModel for sleep data management
 * @param modifier Modifier to be applied to the view
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SleepLogView(
    viewModel: SleepTrackingViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val shouldShowAddSheet = viewModel.shouldShowAddSheet

    Scaffold(
        topBar = {
            SleepLogTopBar()
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.prepareNewEntry() },
                containerColor = DesignTokens.Colors.primary,
                contentColor = DesignTokens.Colors.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.sleep_add_entry)
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
            // Today's sleep card
            TodaySleepCard(
                viewModel = viewModel,
                onSleepEntryClick = { viewModel.prepareNewEntry() },
                modifier = Modifier.padding(horizontal = NSSpacing.screenEdge)
            )

            Spacer(modifier = Modifier.height(NSSpacing.lg))

            // Sleep statistics
            SleepStatsCard(
                viewModel = viewModel,
                modifier = Modifier.padding(horizontal = NSSpacing.screenEdge)
            )

            Spacer(modifier = Modifier.height(NSSpacing.lg))

            // Recent history
            RecentSleepHistoryCard(
                viewModel = viewModel,
                modifier = Modifier.padding(horizontal = NSSpacing.screenEdge)
            )

            // Bottom padding for FAB clearance
            Spacer(modifier = Modifier.height(NSSpacing.xl))
        }
    }

    // Add/Edit Sleep Entry Sheet
    if (shouldShowAddSheet) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.cancelEditing() },
            containerColor = DesignTokens.Colors.background
        ) {
            AddSleepEntryView(
                viewModel = viewModel,
                onDismiss = { viewModel.cancelEditing() }
            )
        }
    }
}

/**
 * Top bar for the sleep log view
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SleepLogTopBar() {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.sleep_title),
                style = NSTypography.heading1,
                color = DesignTokens.Colors.textPrimary
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = DesignTokens.Colors.background
        )
    )
}
