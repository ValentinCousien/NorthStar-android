package com.vcousien.northstar.features.mood.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
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
import com.vcousien.northstar.core.models.MoodEntry
import com.vcousien.northstar.features.mood.viewmodels.MoodTrackingViewModel
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.text.SimpleDateFormat
import java.util.*

/**
 * RecentMoodHistoryCard - Card component displaying recent mood entries
 *
 * Shows a list of the 5 most recent mood entries with dates, mood levels,
 * and navigation indicators. Clicking an entry opens it for editing.
 *
 * Features:
 * - Displays up to 5 most recent mood entries
 * - Shows date and mood level for each entry
 * - Clickable entries for editing
 * - Empty state when no entries exist
 * - Dividers between entries
 * - Matches iOS RecentMoodHistoryCard implementation
 *
 * @param viewModel MoodTrackingViewModel for mood data management
 * @param modifier Modifier to be applied to the card
 * @param onEntryClick Callback when an entry is clicked to edit it
 */
@Composable
fun RecentMoodHistoryCard(
    viewModel: MoodTrackingViewModel = viewModel(),
    modifier: Modifier = Modifier,
    onEntryClick: () -> Unit = {}
) {
    val moodEntries by viewModel.moodEntries.collectAsState()

    // Get the 5 most recent entries
    val recentEntries = remember(moodEntries) {
        moodEntries
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
                text = stringResource(R.string.mood_history),
                style = NSTypography.heading3,
                color = DesignTokens.Colors.textPrimary,
                modifier = Modifier.fillMaxWidth()
            )

            if (recentEntries.isNotEmpty()) {
                // Recent entries list
                recentEntries.forEachIndexed { index, entry ->
                    MoodHistoryRow(
                        entry = entry,
                        onClick = {
                            viewModel.prepareEntryForEditing(entry)
                            onEntryClick()
                        }
                    )

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
                    text = stringResource(R.string.mood_no_entries),
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
 * Row displaying a single mood history entry
 */
@Composable
private fun MoodHistoryRow(
    entry: MoodEntry,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
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

        // Mood level icon and name
        Row(
            horizontalArrangement = Arrangement.spacedBy(NSSpacing.xs),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = entry.level.getIcon(),
                contentDescription = null,
                tint = entry.level.getColor(),
                modifier = Modifier.size(20.dp)
            )

            Text(
                text = entry.level.getName(),
                style = NSTypography.caption,
                color = DesignTokens.Colors.textSecondary
            )
        }

        // Chevron indicator
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = DesignTokens.Colors.textSecondary,
            modifier = Modifier
                .size(16.dp)
                .padding(start = NSSpacing.sm)
        )
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
