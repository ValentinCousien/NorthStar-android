package com.vcousien.northstar.features.mood.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Add
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
import com.vcousien.northstar.features.mood.viewmodels.MoodTrackingViewModel
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.text.SimpleDateFormat
import java.util.*

/**
 * TodayMoodCard - Card component for displaying today's mood entry
 * 
 * Shows the user's mood for today if recorded, or prompts to record one if not.
 * Clicking the card opens the mood entry for editing or creation.
 * 
 * Features:
 * - Displays current mood with icon and color
 * - Shows timestamp when mood was recorded
 * - Displays notes if present
 * - Empty state when no mood recorded
 * - Clickable to open mood entry form
 * - Matches iOS TodayMoodCard implementation
 * 
 * @param viewModel MoodTrackingViewModel for mood data management
 * @param modifier Modifier to be applied to the card
 * @param onMoodEntryClick Callback when card is clicked to add/edit mood
 */
@Composable
fun TodayMoodCard(
    viewModel: MoodTrackingViewModel = viewModel(),
    modifier: Modifier = Modifier,
    onMoodEntryClick: () -> Unit = {}
) {
    val moodEntries by viewModel.moodEntries.collectAsState()
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
                onMoodEntryClick()
            }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(NSSpacing.md)
        ) {
            // Header
            Text(
                text = stringResource(R.string.common_today),
                style = NSTypography.heading3,
                color = DesignTokens.Colors.textPrimary,
                modifier = Modifier.fillMaxWidth()
            )
            
            if (todayEntry != null) {
                // Mood entry exists
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = NSSpacing.sm),
                    horizontalArrangement = Arrangement.spacedBy(NSSpacing.lg),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Mood icon
                    Icon(
                        imageVector = todayEntry.level.getIcon(),
                        contentDescription = stringResource(todayEntry.level.nameResId),
                        modifier = Modifier.size(40.dp),
                        tint = DesignTokens.Colors.moodColor(todayEntry.level.value)
                    )
                    
                    // Mood details
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(NSSpacing.xs)
                    ) {
                        // Mood level name
                        Text(
                            text = stringResource(todayEntry.level.nameResId),
                            style = NSTypography.subtitle,
                            color = DesignTokens.Colors.textPrimary
                        )
                        
                        // Timestamp
                        Text(
                            text = "Recorded at ${formatTime(todayEntry.dateAsInstant)}",
                            style = NSTypography.caption,
                            color = DesignTokens.Colors.textSecondary
                        )
                    }
                    
                    // Detail indicator
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = DesignTokens.Colors.primary,
                        modifier = Modifier.size(20.dp)
                    )
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
                    title = stringResource(R.string.mood_no_mood_today),
                    message = stringResource(R.string.mood_record),
                    icon = Icons.Default.Add,
                    action = {
                        viewModel.prepareNewEntry()
                        onMoodEntryClick()
                    },
                    actionTitle = stringResource(R.string.common_add),
                    modifier = Modifier.padding(vertical = NSSpacing.sm)
                )
            }
        }
    }
}

/**
 * Compact version of TodayMoodCard for smaller spaces
 */
@Composable
fun TodayMoodCardCompact(
    viewModel: MoodTrackingViewModel = viewModel(),
    modifier: Modifier = Modifier,
    onMoodEntryClick: () -> Unit = {}
) {
    val moodEntries by viewModel.moodEntries.collectAsState()
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
                onMoodEntryClick()
            }
    ) {
        if (todayEntry != null) {
            // Compact view with mood
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(NSSpacing.md),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = todayEntry.level.getIcon(),
                    contentDescription = stringResource(todayEntry.level.nameResId),
                    modifier = Modifier.size(24.dp),
                    tint = DesignTokens.Colors.moodColor(todayEntry.level.value)
                )
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.common_today),
                        style = NSTypography.caption,
                        color = DesignTokens.Colors.textSecondary
                    )
                    
                    Text(
                        text = stringResource(todayEntry.level.nameResId),
                        style = NSTypography.bodyBold,
                        color = DesignTokens.Colors.textPrimary
                    )
                }
                
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = DesignTokens.Colors.primary,
                    modifier = Modifier.size(16.dp)
                )
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
                        text = stringResource(R.string.common_today),
                        style = NSTypography.caption,
                        color = DesignTokens.Colors.textSecondary
                    )
                    
                    Text(
                        text = stringResource(R.string.mood_record),
                        style = NSTypography.bodyBold,
                        color = DesignTokens.Colors.textPrimary
                    )
                }
                
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = DesignTokens.Colors.primary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

/**
 * Utility function to format time for display
 * Converts Instant to readable time format
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
