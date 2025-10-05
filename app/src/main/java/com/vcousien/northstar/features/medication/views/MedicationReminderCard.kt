package com.vcousien.northstar.features.medication.views

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vcousien.northstar.R
import com.vcousien.northstar.core.designsystem.DesignTokens
import com.vcousien.northstar.core.designsystem.NSSpacing
import com.vcousien.northstar.core.designsystem.NSTypography
import com.vcousien.northstar.core.designsystem.components.NSCard
import com.vcousien.northstar.features.medication.viewmodels.MedicationViewModel
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.text.SimpleDateFormat
import java.util.*
import kotlin.time.Duration.Companion.minutes

/**
 * MedicationReminderCard - A card that displays the next medication reminder
 *
 * Features:
 * - Shows next upcoming medication reminder
 * - Displays medication name, dosage, and time
 * - Shows countdown until reminder ("In X hours" or "In X minutes")
 * - Auto-refreshes every minute
 * - Empty state when no reminders are scheduled
 * - Matches iOS MedicationReminderCard implementation
 *
 * @param viewModel MedicationViewModel for accessing reminder data
 * @param modifier Modifier to be applied to the card
 */
@Composable
fun MedicationReminderCard(
    viewModel: MedicationViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    // State to force recomposition every minute
    var refreshTrigger by remember { mutableStateOf(0) }

    // Auto-refresh every 60 seconds
    LaunchedEffect(Unit) {
        while (true) {
            delay(60_000L) // 60 seconds
            refreshTrigger++
        }
    }

    // Get the next reminder (refreshes when medications change or time passes)
    val medications by viewModel.medications.collectAsState()
    val nextReminder = remember(medications, refreshTrigger) {
        viewModel.getNextReminder()
    }

    NSCard(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(NSSpacing.md)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.home_next_reminder),
                    style = NSTypography.heading3,
                    color = DesignTokens.Colors.textPrimary
                )

                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = null,
                    tint = DesignTokens.Colors.primary
                )
            }

            // Content
            if (nextReminder != null) {
                ReminderContent(nextReminder = nextReminder)
            } else {
                EmptyReminderState()
            }
        }
    }
}

/**
 * Content shown when there is a next reminder
 */
@Composable
private fun ReminderContent(
    nextReminder: com.vcousien.northstar.features.medication.viewmodels.NextReminderInfo
) {
    val timeUntil = remember(nextReminder.time) {
        getTimeUntilString(nextReminder.time)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = NSSpacing.xs),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Medication info
        Column(
            verticalArrangement = Arrangement.spacedBy(NSSpacing.xs)
        ) {
            Text(
                text = nextReminder.medication.name,
                style = NSTypography.bodyBold,
                color = DesignTokens.Colors.textPrimary
            )

            Text(
                text = nextReminder.medication.displayDosage,
                style = NSTypography.caption,
                color = DesignTokens.Colors.textSecondary
            )
        }

        // Time info
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(NSSpacing.xs)
        ) {
            Text(
                text = formatTime(nextReminder.time),
                style = NSTypography.body,
                color = DesignTokens.Colors.textPrimary
            )

            Text(
                text = timeUntil,
                style = NSTypography.caption,
                color = DesignTokens.Colors.info
            )
        }
    }
}

/**
 * Empty state shown when no reminders are scheduled
 */
@Composable
private fun EmptyReminderState() {
    Text(
        text = stringResource(R.string.home_no_reminders_scheduled),
        style = NSTypography.body,
        color = DesignTokens.Colors.textSecondary,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = NSSpacing.md),
        textAlign = androidx.compose.ui.text.style.TextAlign.Center
    )
}

/**
 * Formats an Instant as a time string
 * @param instant The instant to format
 * @return Formatted time string (e.g., "2:30 PM")
 */
private fun formatTime(instant: kotlinx.datetime.Instant): String {
    val timeZone = TimeZone.currentSystemDefault()
    val localDateTime = instant.toLocalDateTime(timeZone)

    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, localDateTime.hour)
        set(Calendar.MINUTE, localDateTime.minute)
    }

    val formatter = SimpleDateFormat("h:mm a", Locale.getDefault())
    return formatter.format(calendar.time)
}

/**
 * Gets a string describing the time until a future instant
 * @param instant The future instant
 * @return A formatted string like "In 2 hours" or "In 15 minutes"
 */
private fun getTimeUntilString(instant: kotlinx.datetime.Instant): String {
    val now = Clock.System.now()
    val timeZone = TimeZone.currentSystemDefault()

    val nowDateTime = now.toLocalDateTime(timeZone)
    val reminderDateTime = instant.toLocalDateTime(timeZone)

    // Calculate difference in minutes
    val nowInMinutes = nowDateTime.hour * 60 + nowDateTime.minute
    val reminderInMinutes = reminderDateTime.hour * 60 + reminderDateTime.minute

    // Handle next day case
    val diffInMinutes = if (reminderDateTime.date > nowDateTime.date) {
        // Next day
        (24 * 60 - nowInMinutes) + reminderInMinutes
    } else {
        // Same day
        reminderInMinutes - nowInMinutes
    }

    return when {
        diffInMinutes >= 60 -> {
            val hours = diffInMinutes / 60
            "In $hours hour${if (hours > 1) "s" else ""}"
        }
        diffInMinutes > 0 -> {
            "In $diffInMinutes minute${if (diffInMinutes > 1) "s" else ""}"
        }
        else -> {
            "Now"
        }
    }
}
