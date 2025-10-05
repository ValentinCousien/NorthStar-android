package com.vcousien.northstar.features.sleep.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import com.vcousien.northstar.core.designsystem.components.NSEmptyState
import com.vcousien.northstar.features.sleep.viewmodels.SleepTrackingViewModel
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.text.SimpleDateFormat
import java.util.*

/**
 * TodaySleepCard - Card component for displaying last night's sleep entry
 *
 * Shows the user's sleep for last night if recorded, or prompts to record one if not.
 * Clicking the card opens the sleep entry for editing or creation.
 *
 * Features:
 * - Displays sleep duration, bedtime, and wake time
 * - Shows quality rating with stars
 * - Displays notes if present
 * - Empty state when no sleep recorded
 * - Clickable to open sleep entry form
 * - Matches iOS TodaySleepCard implementation
 *
 * @param viewModel SleepTrackingViewModel for sleep data management
 * @param modifier Modifier to be applied to the card
 * @param onSleepEntryClick Callback when card is clicked to add/edit sleep
 */
@Composable
fun TodaySleepCard(
    viewModel: SleepTrackingViewModel = viewModel(),
    modifier: Modifier = Modifier,
    onSleepEntryClick: () -> Unit = {}
) {
    val sleepEntries by viewModel.sleepEntries.collectAsState()
    val today = Clock.System.now()
    val todayEntry = viewModel.getEntry(today)

    NSCard(
        modifier = modifier
            .clickable {
                if (todayEntry != null) {
                    // If entry exists, prepare for editing
                    viewModel.prepareEntryForEditing(todayEntry)
                } else {
                    // Otherwise, prepare a new entry
                    viewModel.prepareNewEntry()
                }
                onSleepEntryClick()
            }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(NSSpacing.md)
        ) {
            // Header
            Text(
                text = stringResource(R.string.sleep_last_night),
                style = NSTypography.heading3,
                color = DesignTokens.Colors.textPrimary,
                modifier = Modifier.fillMaxWidth()
            )

            if (todayEntry != null) {
                // Sleep entry exists
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = NSSpacing.sm),
                    horizontalArrangement = Arrangement.spacedBy(NSSpacing.lg),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Sleep icon
                    Icon(
                        imageVector = Icons.Default.Star, // Using star as placeholder for moon icon
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = DesignTokens.Colors.primary
                    )

                    // Sleep details
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(NSSpacing.xs)
                    ) {
                        // Sleep duration
                        Text(
                            text = formatDuration(todayEntry.durationSeconds),
                            style = NSTypography.subtitle,
                            color = DesignTokens.Colors.textPrimary
                        )

                        // Bedtime and wake time
                        Text(
                            text = "${formatTime(todayEntry.startTimeAsInstant)} - ${formatTime(todayEntry.endTimeAsInstant)}",
                            style = NSTypography.caption,
                            color = DesignTokens.Colors.textSecondary
                        )
                    }
                }

                // Sleep quality
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(NSSpacing.sm),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${stringResource(R.string.sleep_quality_short)}:",
                        style = NSTypography.bodyBold,
                        color = DesignTokens.Colors.textPrimary
                    )

                    RatingView(rating = todayEntry.quality)
                }

                // Notes (if present)
                if (todayEntry.notes.isNotEmpty()) {
                    Text(
                        text = todayEntry.notes,
                        style = NSTypography.body,
                        color = DesignTokens.Colors.textPrimary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = NSSpacing.xs)
                    )
                }
            } else {
                // No entry for today - show empty state
                NSEmptyState(
                    title = stringResource(R.string.sleep_no_sleep_today),
                    message = stringResource(R.string.sleep_record_last_night_sleep),
                    icon = Icons.Default.Add,
                    action = {
                        viewModel.prepareNewEntry()
                        onSleepEntryClick()
                    },
                    actionTitle = stringResource(R.string.sleep_add_action),
                    modifier = Modifier.padding(vertical = NSSpacing.sm)
                )
            }
        }
    }
}

/**
 * Compact version of TodaySleepCard for smaller spaces
 */
@Composable
fun TodaySleepCardCompact(
    viewModel: SleepTrackingViewModel = viewModel(),
    modifier: Modifier = Modifier,
    onSleepEntryClick: () -> Unit = {}
) {
    val sleepEntries by viewModel.sleepEntries.collectAsState()
    val today = Clock.System.now()
    val todayEntry = viewModel.getEntry(today)

    NSCard(
        modifier = modifier
            .clickable {
                if (todayEntry != null) {
                    viewModel.prepareEntryForEditing(todayEntry)
                } else {
                    viewModel.prepareNewEntry()
                }
                onSleepEntryClick()
            }
    ) {
        if (todayEntry != null) {
            // Compact view with sleep
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(NSSpacing.md),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = DesignTokens.Colors.primary
                )

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.sleep_last_night),
                        style = NSTypography.caption,
                        color = DesignTokens.Colors.textSecondary
                    )

                    Text(
                        text = formatDuration(todayEntry.durationSeconds),
                        style = NSTypography.bodyBold,
                        color = DesignTokens.Colors.textPrimary
                    )
                }

                RatingView(rating = todayEntry.quality, compact = true)
            }
        } else {
            // Compact empty state
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(NSSpacing.md),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = DesignTokens.Colors.primary
                )

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.sleep_last_night),
                        style = NSTypography.caption,
                        color = DesignTokens.Colors.textSecondary
                    )

                    Text(
                        text = stringResource(R.string.sleep_record_last_night),
                        style = NSTypography.bodyBold,
                        color = DesignTokens.Colors.textPrimary
                    )
                }
            }
        }
    }
}

/**
 * Rating view component showing stars for sleep quality
 * Quality is on 0-10 scale, displayed as 0-5 stars
 */
@Composable
fun RatingView(
    rating: Double,
    maxRating: Double = 10.0,
    compact: Boolean = false
) {
    val starCount = 5
    val starSize = if (compact) 16.dp else 20.dp

    Row(
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        repeat(starCount) { index ->
            val position = index * 2.0 // Convert 5-star to 10-point scale
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = if (position + 0.5 < rating) {
                    DesignTokens.Colors.primary
                } else {
                    DesignTokens.Colors.border
                },
                modifier = Modifier.size(starSize)
            )
        }
    }
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

/**
 * Utility function to format time for display
 * Converts Instant to readable time format (e.g., "10:30 PM")
 */
private fun formatTime(instant: kotlinx.datetime.Instant): String {
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, localDateTime.hour)
        set(Calendar.MINUTE, localDateTime.minute)
    }

    val timeFormatter = SimpleDateFormat("h:mm a", Locale.getDefault())
    return timeFormatter.format(calendar.time)
}
