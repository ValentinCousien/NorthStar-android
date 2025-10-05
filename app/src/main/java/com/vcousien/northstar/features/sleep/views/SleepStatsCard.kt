package com.vcousien.northstar.features.sleep.views

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vcousien.northstar.R
import com.vcousien.northstar.core.designsystem.NSSpacing
import com.vcousien.northstar.core.designsystem.NSTypography
import com.vcousien.northstar.core.designsystem.DesignTokens
import com.vcousien.northstar.core.designsystem.components.NSCard
import com.vcousien.northstar.features.sleep.viewmodels.SleepTrackingViewModel
import androidx.compose.material3.Text

/**
 * SleepStatsCard - Card component displaying sleep statistics
 *
 * Shows 7-day average sleep duration and quality statistics.
 *
 * Features:
 * - 7-day average sleep duration
 * - 7-day average sleep quality
 * - Formatted duration display
 * - Quality rating display
 * - Matches iOS SleepStatsCard implementation
 *
 * @param viewModel SleepTrackingViewModel for sleep data management
 * @param modifier Modifier to be applied to the card
 */
@Composable
fun SleepStatsCard(
    viewModel: SleepTrackingViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val sleepEntries by viewModel.sleepEntries.collectAsState()

    // Calculate 7-day averages
    val avgDuration = remember(sleepEntries) {
        viewModel.getAverageSleepDuration()
    }

    val avgQuality = remember(sleepEntries) {
        viewModel.getAverageSleepQuality(days = 7)
    }

    NSCard(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(NSSpacing.md)
        ) {
            // Header
            Text(
                text = stringResource(R.string.sleep_stats),
                style = NSTypography.heading3,
                color = DesignTokens.Colors.textPrimary,
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(NSSpacing.xl)
            ) {
                // Average sleep duration
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(NSSpacing.xs)
                ) {
                    Text(
                        text = "7-Day Average",
                        style = NSTypography.caption,
                        color = DesignTokens.Colors.textSecondary
                    )

                    Text(
                        text = formatDuration(avgDuration),
                        style = NSTypography.heading3,
                        color = DesignTokens.Colors.primary
                    )
                }

                // Average sleep quality
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(NSSpacing.xs)
                ) {
                    Text(
                        text = "Avg Quality",
                        style = NSTypography.caption,
                        color = DesignTokens.Colors.textSecondary
                    )

                    Text(
                        text = formatQuality(avgQuality),
                        style = NSTypography.heading3,
                        color = DesignTokens.Colors.primary
                    )
                }
            }
        }
    }
}

/**
 * Utility function to format sleep duration for display
 * Converts seconds to "Xh Ymin" format
 */
private fun formatDuration(durationSeconds: Long): String {
    if (durationSeconds == 0L) return "—"

    val hours = durationSeconds / 3600
    val minutes = (durationSeconds % 3600) / 60

    return if (minutes > 0) {
        "${hours}h ${minutes}min"
    } else {
        "${hours}h"
    }
}

/**
 * Utility function to format quality rating for display
 * Shows quality on 0-10 scale
 */
private fun formatQuality(quality: Double): String {
    return if (quality > 0) {
        String.format("%.1f/10", quality)
    } else {
        "—"
    }
}
