package com.vcousien.northstar.app

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vcousien.northstar.R
import com.vcousien.northstar.core.designsystem.DesignTokens
import com.vcousien.northstar.core.designsystem.NSSpacing
import com.vcousien.northstar.core.designsystem.NSTypography
import com.vcousien.northstar.core.designsystem.components.NSCard
import com.vcousien.northstar.features.medication.viewmodels.MedicationViewModel
import com.vcousien.northstar.features.medication.views.MedicationReminderCard
import com.vcousien.northstar.features.medication.views.TodayMedicationCard
import com.vcousien.northstar.features.mood.viewmodels.MoodTrackingViewModel
import com.vcousien.northstar.features.mood.views.TodayMoodCard
import com.vcousien.northstar.features.sleep.viewmodels.SleepTrackingViewModel
import kotlinx.datetime.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * HomeView - Main home screen showing today's summary and key metrics
 *
 * Features:
 * - Personalized greeting with date
 * - Today's summary card (mood, sleep, medications at a glance)
 * - Today's medication tracking
 * - Today's mood tracking
 * - Next medication reminder
 * - Matches iOS HomeView iPhone layout
 *
 * @param modifier Modifier to be applied to the view
 */
@Composable
fun HomeView(
    modifier: Modifier = Modifier,
    moodViewModel: MoodTrackingViewModel = viewModel(),
    sleepViewModel: SleepTrackingViewModel = viewModel(),
    medicationViewModel: MedicationViewModel = viewModel()
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = NSSpacing.screenEdge)
    ) {
        Spacer(modifier = Modifier.height(NSSpacing.lg))

        // Greeting Section
        GreetingSection()

        Spacer(modifier = Modifier.height(NSSpacing.lg))

        // Today's Summary Card
        TodaysSummaryCard(
            moodViewModel = moodViewModel,
            sleepViewModel = sleepViewModel,
            medicationViewModel = medicationViewModel
        )

        Spacer(modifier = Modifier.height(NSSpacing.lg))

        // Today's Medication Card
        TodayMedicationCard(
            viewModel = medicationViewModel
        )

        Spacer(modifier = Modifier.height(NSSpacing.lg))

        // Today's Mood Card
        TodayMoodCard(
            viewModel = moodViewModel
        )

        Spacer(modifier = Modifier.height(NSSpacing.lg))

        // Medication Reminder Card
        MedicationReminderCard(
            viewModel = medicationViewModel
        )

        Spacer(modifier = Modifier.height(NSSpacing.xxl))
    }
}

/**
 * Greeting section with personalized message and date
 */
@Composable
private fun GreetingSection() {
    val greetingMessage = remember { getGreetingMessage() }
    val currentDate = remember { getCurrentDate() }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(NSSpacing.sm)
    ) {
        Text(
            text = greetingMessage,
            style = NSTypography.heading2,
            color = DesignTokens.Colors.textPrimary,
            textAlign = TextAlign.Center
        )

        Text(
            text = currentDate,
            style = NSTypography.subtitle,
            color = DesignTokens.Colors.textSecondary
        )
    }
}

/**
 * Today's summary card showing mood, sleep, and medication overview
 */
@Composable
private fun TodaysSummaryCard(
    moodViewModel: MoodTrackingViewModel,
    sleepViewModel: SleepTrackingViewModel,
    medicationViewModel: MedicationViewModel
) {
    val today = remember { Clock.System.now() }
    val moodEntry = moodViewModel.currentEntry
    val sleepEntries by sleepViewModel.sleepEntries.collectAsState()
    val todaySleep = remember(sleepEntries, today) {
        sleepViewModel.getEntry(today)
    }
    val medications by medicationViewModel.medications.collectAsState()
    val medicationEntries by medicationViewModel.medicationEntries.collectAsState()

    val (takenCount, totalCount) = remember(medications, medicationEntries, today) {
        getTodayMedicationCounts(medications, medicationEntries, today)
    }

    NSCard {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(NSSpacing.md)
        ) {
            Text(
                text = stringResource(R.string.common_today),
                style = NSTypography.heading3,
                color = DesignTokens.Colors.textPrimary
            )

            HorizontalDivider(color = DesignTokens.Colors.border)

            // Summary Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = NSSpacing.sm),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Mood Summary
                SummaryItem(
                    icon = moodEntry?.level?.icon ?: "❓",
                    value = moodEntry?.level?.displayName ?: "---",
                    label = stringResource(R.string.tabs_mood),
                    color = moodEntry?.level?.color ?: DesignTokens.Colors.textSecondary
                )

                // Sleep Summary
                SummaryItem(
                    icon = "😴",
                    value = todaySleep?.let { "${it.durationInHours.toInt()}h" } ?: "---",
                    label = stringResource(R.string.tabs_sleep),
                    color = DesignTokens.Colors.textPrimary
                )

                // Medication Summary
                SummaryItem(
                    icon = "💊",
                    value = "$takenCount/$totalCount",
                    label = stringResource(R.string.tabs_medication),
                    color = DesignTokens.Colors.textPrimary
                )
            }
        }
    }
}

/**
 * Individual summary item in the today's summary card
 */
@Composable
private fun SummaryItem(
    icon: String,
    value: String,
    label: String,
    color: androidx.compose.ui.graphics.Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(NSSpacing.xs)
    ) {
        Text(
            text = icon,
            style = NSTypography.heading2
        )

        Text(
            text = value,
            style = NSTypography.bodyBold,
            color = color
        )

        Text(
            text = label,
            style = NSTypography.caption,
            color = DesignTokens.Colors.textSecondary
        )
    }
}

// Helper Functions

/**
 * Gets personalized greeting based on time of day
 */
private fun getGreetingMessage(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val greeting = when (hour) {
        in 5..11 -> "Good Morning"
        in 12..16 -> "Good Afternoon"
        else -> "Good Evening"
    }
    // TODO: Add user name support when UserSettings is implemented
    return greeting
}

/**
 * Gets current date formatted
 */
private fun getCurrentDate(): String {
    val formatter = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault())
    return formatter.format(Date())
}

/**
 * Gets today's medication counts (taken vs total)
 */
private fun getTodayMedicationCounts(
    medications: List<com.vcousien.northstar.core.models.Medication>,
    entries: List<com.vcousien.northstar.core.models.MedicationEntry>,
    today: Instant
): Pair<Int, Int> {
    val timeZone = TimeZone.currentSystemDefault()
    val todayDate = today.toLocalDateTime(timeZone).date

    // Count taken medications today
    val takenCount = entries.count { entry ->
        val entryDate = entry.dateAsInstant.toLocalDateTime(timeZone).date
        entryDate == todayDate && entry.taken
    }

    // Count total medications scheduled for today
    val totalCount = medications.sumOf { medication ->
        if (medication.frequency == com.vcousien.northstar.core.models.MedicationFrequency.AS_NEEDED) {
            0
        } else {
            medication.timeOfDay.size
        }
    }

    return Pair(takenCount, totalCount)
}
