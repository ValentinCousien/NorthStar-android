package com.vcousien.northstar.features.sleep.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vcousien.northstar.R
import com.vcousien.northstar.core.designsystem.NSSpacing
import com.vcousien.northstar.core.designsystem.NSTypography
import com.vcousien.northstar.core.designsystem.DesignTokens
import com.vcousien.northstar.core.designsystem.components.NSCard
import com.vcousien.northstar.core.models.SleepEntry
import com.vcousien.northstar.features.sleep.viewmodels.SleepTrackingViewModel
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.text.SimpleDateFormat
import java.util.*

/**
 * RecentSleepHistoryCard - Card component displaying recent sleep entries
 *
 * Shows a list of the 5 most recent sleep entries with dates, durations,
 * and quality ratings. Entries are not clickable (view-only).
 *
 * Features:
 * - Displays up to 5 most recent sleep entries
 * - Shows date and duration for each entry
 * - Quality rating with stars
 * - Empty state when no entries exist
 * - Dividers between entries
 * - Matches iOS RecentSleepHistoryCard implementation
 *
 * @param viewModel SleepTrackingViewModel for sleep data management
 * @param modifier Modifier to be applied to the card
 */
@Composable
fun RecentSleepHistoryCard(
    viewModel: SleepTrackingViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val sleepEntries by viewModel.sleepEntries.collectAsState()

    // Get the 5 most recent entries
    val recentEntries = remember(sleepEntries) {
        sleepEntries
            .sortedByDescending { it.date }
            .take(5)
    }

    NSCard(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(NSSpacing.md)
        ) {
            // Header
            Text(
                text = stringResource(R.string.sleep_history),
                style = NSTypography.heading3,
                color = DesignTokens.Colors.textPrimary,
                modifier = Modifier.fillMaxWidth()
            )

            if (recentEntries.isNotEmpty()) {
                // Recent entries list
                recentEntries.forEachIndexed { index, entry ->
                    SleepHistoryRow(entry = entry)

                    // Add divider between entries (but not after the last one)
                    if (index < recentEntries.size - 1) {
                        HorizontalDivider(
                            color = DesignTokens.Colors.border,
                            thickness = 1.dp
                        )
                    }
                }
            } else {
                // Empty state
                Text(
                    text = stringResource(R.string.sleep_no_entries),
                    style = NSTypography.body,
                    color = DesignTokens.Colors.textSecondary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = NSSpacing.md)
                )
            }
        }
    }
}

/**
 * Row displaying a single sleep history entry
 */
@Composable
private fun SleepHistoryRow(entry: SleepEntry) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = NSSpacing.sm),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Date
        Text(
            text = formatDate(entry.dateAsInstant),
            style = NSTypography.body,
            color = DesignTokens.Colors.textPrimary
        )

        Spacer(modifier = Modifier.weight(1f))

        // Duration and quality
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = formatDuration(entry.durationSeconds),
                style = NSTypography.bodyBold,
                color = DesignTokens.Colors.primary
            )

            // Quality with stars (0-10 scale shown as 0-5 stars)
            Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                repeat(5) { index ->
                    val position = index * 2.0 // Convert 5-star to 10-point scale
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = if (position < entry.quality) {
                            DesignTokens.Colors.primary
                        } else {
                            DesignTokens.Colors.border
                        },
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
    }
}

/**
 * Utility function to format date for display
 * Converts Instant to readable date format (e.g., "Jan 5, 2025")
 */
private fun formatDate(instant: kotlinx.datetime.Instant): String {
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    val calendar = Calendar.getInstance().apply {
        set(Calendar.YEAR, localDateTime.year)
        set(Calendar.MONTH, localDateTime.monthNumber - 1)  // Calendar months are 0-based
        set(Calendar.DAY_OF_MONTH, localDateTime.dayOfMonth)
    }

    val dateFormatter = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
    return dateFormatter.format(calendar.time)
}

/**
 * Utility function to format sleep duration for display
 * Converts seconds to "Xh Ymin" format
 */
private fun formatDuration(durationSeconds: Long): String {
    val hours = durationSeconds / 3600
    val minutes = (durationSeconds % 3600) / 60

    return if (minutes > 0) {
        "${hours}h ${minutes}min"
    } else {
        "${hours}h"
    }
}
