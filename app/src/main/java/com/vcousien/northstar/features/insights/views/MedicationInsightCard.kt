package com.vcousien.northstar.features.insights.views

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowCircleDown
import androidx.compose.material.icons.filled.ArrowCircleUp
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vcousien.northstar.R
import com.vcousien.northstar.core.designsystem.DesignTokens
import com.vcousien.northstar.core.designsystem.NSSpacing
import com.vcousien.northstar.core.designsystem.NSTypography
import com.vcousien.northstar.core.designsystem.components.NSCard
import com.vcousien.northstar.core.models.Medication
import com.vcousien.northstar.core.models.MedicationFrequency
import com.vcousien.northstar.features.medication.viewmodels.MedicationViewModel
import com.vcousien.northstar.features.mood.viewmodels.MoodTrackingViewModel
import com.vcousien.northstar.features.sleep.viewmodels.SleepTrackingViewModel
import kotlinx.datetime.*
import kotlin.math.abs

/**
 * MedicationInsightCard - Card showing medication correlations with mood and sleep
 *
 * Features:
 * - Timeframe selector (14, 30, 90 days)
 * - Correlation analysis between medications and mood/sleep
 * - Adherence rate display
 * - Personalized advice based on correlations
 * - Empty states for no medications or insufficient data
 * - Matches iOS MedicationInsightCard implementation
 *
 * @param medicationViewModel ViewModel for medication data
 * @param moodViewModel ViewModel for mood data
 * @param sleepViewModel ViewModel for sleep data
 * @param modifier Modifier to be applied to the card
 */
@Composable
fun MedicationInsightCard(
    medicationViewModel: MedicationViewModel = viewModel(),
    moodViewModel: MoodTrackingViewModel = viewModel(),
    sleepViewModel: SleepTrackingViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    var selectedTimeframe by remember { mutableStateOf(30) }
    val timeframeOptions = listOf(14, 30, 90)

    val medications by medicationViewModel.medications.collectAsState()
    val correlations = remember(medications, selectedTimeframe) {
        calculateCorrelations(
            medications = medications,
            timeframe = selectedTimeframe,
            medicationViewModel = medicationViewModel,
            moodViewModel = moodViewModel,
            sleepViewModel = sleepViewModel
        )
    }

    NSCard(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(NSSpacing.md)
        ) {
            // Header with timeframe selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.insights_medication_wellness_correlation),
                    style = NSTypography.heading3,
                    color = DesignTokens.Colors.textPrimary,
                    modifier = Modifier.weight(1f)
                )

                // Timeframe selector
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    timeframeOptions.forEach { days ->
                        OutlinedButton(
                            onClick = { selectedTimeframe = days },
                            modifier = Modifier.width(60.dp).height(36.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (selectedTimeframe == days) {
                                    DesignTokens.Colors.primary
                                } else {
                                    androidx.compose.ui.graphics.Color.Transparent
                                },
                                contentColor = if (selectedTimeframe == days) {
                                    androidx.compose.ui.graphics.Color.White
                                } else {
                                    DesignTokens.Colors.textPrimary
                                }
                            )
                        ) {
                            Text(
                                text = "${days}d",
                                style = NSTypography.caption
                            )
                        }
                    }
                }
            }

            // Content
            when {
                medications.isEmpty() -> {
                    EmptyMedicationsState()
                }
                correlations.isEmpty() -> {
                    InsufficientDataState()
                }
                else -> {
                    CorrelationsList(correlations = correlations.take(3))

                    HorizontalDivider(color = DesignTokens.Colors.border)

                    AdviceSection(correlations = correlations)
                }
            }
        }
    }
}

/**
 * Empty state when no medications exist
 */
@Composable
private fun EmptyMedicationsState() {
    Text(
        text = stringResource(R.string.insights_no_medications_for_analysis),
        style = NSTypography.body,
        color = DesignTokens.Colors.textSecondary,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = NSSpacing.md),
        textAlign = androidx.compose.ui.text.style.TextAlign.Center
    )
}

/**
 * State when there's insufficient data for analysis
 */
@Composable
private fun InsufficientDataState() {
    Text(
        text = stringResource(R.string.insights_insufficient_data),
        style = NSTypography.body,
        color = DesignTokens.Colors.textSecondary,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = NSSpacing.md),
        textAlign = androidx.compose.ui.text.style.TextAlign.Center
    )
}

/**
 * List of medication correlations
 */
@Composable
private fun CorrelationsList(correlations: List<MedicationCorrelation>) {
    Column(verticalArrangement = Arrangement.spacedBy(NSSpacing.md)) {
        correlations.forEachIndexed { index, correlation ->
            MedicationCorrelationRow(correlation = correlation)

            if (index < correlations.size - 1) {
                HorizontalDivider(color = DesignTokens.Colors.border)
            }
        }
    }
}

/**
 * Row showing a single medication's correlations
 */
@Composable
private fun MedicationCorrelationRow(correlation: MedicationCorrelation) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = NSSpacing.xs),
        verticalArrangement = Arrangement.spacedBy(NSSpacing.sm)
    ) {
        Text(
            text = correlation.medication.name,
            style = NSTypography.bodyBold,
            color = DesignTokens.Colors.textPrimary
        )

        // Mood correlation
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.insights_mood_label),
                style = NSTypography.caption,
                color = DesignTokens.Colors.textSecondary
            )

            CorrelationIndicator(
                value = correlation.moodCorrelation,
                positiveLabel = stringResource(R.string.insights_improves),
                negativeLabel = stringResource(R.string.insights_worsens),
                neutralLabel = stringResource(R.string.insights_neutral)
            )
        }

        // Sleep correlation
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.insights_sleep_label),
                style = NSTypography.caption,
                color = DesignTokens.Colors.textSecondary
            )

            CorrelationIndicator(
                value = correlation.sleepCorrelation,
                positiveLabel = stringResource(R.string.insights_improves),
                negativeLabel = stringResource(R.string.insights_disrupts),
                neutralLabel = stringResource(R.string.insights_neutral)
            )
        }

        // Adherence
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.insights_adherence_label),
                style = NSTypography.caption,
                color = DesignTokens.Colors.textSecondary
            )

            Text(
                text = "${correlation.adherenceRate.toInt()}%",
                style = NSTypography.caption,
                color = getAdherenceColor(correlation.adherenceRate)
            )
        }
    }
}

/**
 * Correlation indicator showing direction and strength
 */
@Composable
private fun CorrelationIndicator(
    value: Double,
    positiveLabel: String,
    negativeLabel: String,
    neutralLabel: String
) {
    val (label, color, icon) = getCorrelationDisplay(
        value = value,
        positiveLabel = positiveLabel,
        negativeLabel = negativeLabel,
        neutralLabel = neutralLabel
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(NSSpacing.xs),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = NSTypography.caption,
            color = color
        )

        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(16.dp)
        )
    }
}

/**
 * Advice section based on correlations
 */
@Composable
private fun AdviceSection(correlations: List<MedicationCorrelation>) {
    Column(verticalArrangement = Arrangement.spacedBy(NSSpacing.xs)) {
        Text(
            text = stringResource(R.string.insights_advice),
            style = NSTypography.bodyBold,
            color = DesignTokens.Colors.primary
        )

        Text(
            text = getAdviceText(correlations),
            style = NSTypography.body,
            color = DesignTokens.Colors.textSecondary
        )
    }
}

// Data Classes and Helper Functions

/**
 * Data class representing medication correlation with mood and sleep
 */
data class MedicationCorrelation(
    val medication: Medication,
    val moodCorrelation: Double,
    val sleepCorrelation: Double,
    val adherenceRate: Double
)

/**
 * Calculates correlations between medications and mood/sleep
 */
private fun calculateCorrelations(
    medications: List<Medication>,
    timeframe: Int,
    medicationViewModel: MedicationViewModel,
    moodViewModel: MoodTrackingViewModel,
    sleepViewModel: SleepTrackingViewModel
): List<MedicationCorrelation> {
    val timeZone = TimeZone.currentSystemDefault()
    val today = Clock.System.now()
    val startDate = today.minus(timeframe - 1, DateTimeUnit.DAY, timeZone)

    // Get data for timeframe
    val medicationEntries = medicationViewModel.getEntries(startDate, today)
    val moodEntries = moodViewModel.getEntries(startDate, today)
    val sleepEntries = sleepViewModel.getEntries(startDate, today)

    val correlations = mutableListOf<MedicationCorrelation>()

    for (medication in medications) {
        // Filter entries for this medication
        val medEntries = medicationEntries.filter { it.medicationId == medication.id && it.taken }

        // Need at least 5 entries for meaningful correlation
        if (medEntries.size < 5) continue

        // Calculate adherence
        var scheduledDays = 0
        var takenDays = 0

        for (dayOffset in 0 until timeframe) {
            val date = today.minus(dayOffset, DateTimeUnit.DAY, timeZone)

            if (shouldTakeMedication(medication, date, timeZone)) {
                scheduledDays++

                val takenOnDay = medEntries.any { entry ->
                    areSameDay(entry.dateAsInstant, date, timeZone)
                }

                if (takenOnDay) takenDays++
            }
        }

        val adherenceRate = if (scheduledDays > 0) {
            (takenDays.toDouble() / scheduledDays.toDouble()) * 100.0
        } else {
            0.0
        }

        // Calculate correlations
        var moodCorrelationSum = 0.0
        var sleepCorrelationSum = 0.0
        var correlationCount = 0

        for (medEntry in medEntries) {
            val nextDay = medEntry.dateAsInstant.plus(1, DateTimeUnit.DAY, timeZone)

            val moodEntry = moodEntries.firstOrNull { areSameDay(it.dateAsInstant, nextDay, timeZone) }
            val sleepEntry = sleepEntries.firstOrNull { areSameDay(it.dateAsInstant, nextDay, timeZone) }

            if (moodEntry != null && sleepEntry != null) {
                // Mood correlation (normalize to -1 to 1)
                val moodValue = (moodEntry.level.ordinal.toDouble() / 8.0) * 2.0 - 1.0
                moodCorrelationSum += moodValue

                // Sleep correlation (normalize to -1 to 1)
                val sleepValue = (sleepEntry.quality / 10.0) * 2.0 - 1.0
                sleepCorrelationSum += sleepValue

                correlationCount++
            }
        }

        if (correlationCount > 0) {
            val moodCorrelation = moodCorrelationSum / correlationCount
            val sleepCorrelation = sleepCorrelationSum / correlationCount

            correlations.add(
                MedicationCorrelation(
                    medication = medication,
                    moodCorrelation = moodCorrelation,
                    sleepCorrelation = sleepCorrelation,
                    adherenceRate = adherenceRate
                )
            )
        }
    }

    // Sort by strongest correlation
    return correlations.sortedByDescending {
        abs(it.moodCorrelation) + abs(it.sleepCorrelation)
    }
}

/**
 * Checks if medication should be taken on a specific date
 */
private fun shouldTakeMedication(
    medication: Medication,
    date: Instant,
    timeZone: TimeZone
): Boolean {
    return when (medication.frequency) {
        MedicationFrequency.DAILY -> true
        MedicationFrequency.WEEKLY -> {
            // Assume Sunday (day 0)
            val localDate = date.toLocalDateTime(timeZone).date
            localDate.dayOfWeek.ordinal == 0
        }
        MedicationFrequency.AS_NEEDED -> false
        MedicationFrequency.CUSTOM -> false
    }
}

/**
 * Checks if two instants are on the same day
 */
private fun areSameDay(instant1: Instant, instant2: Instant, timeZone: TimeZone): Boolean {
    val date1 = instant1.toLocalDateTime(timeZone).date
    val date2 = instant2.toLocalDateTime(timeZone).date
    return date1 == date2
}

/**
 * Gets correlation display data (label, color, icon)
 */
@Composable
private fun getCorrelationDisplay(
    value: Double,
    positiveLabel: String,
    negativeLabel: String,
    neutralLabel: String
): Triple<String, androidx.compose.ui.graphics.Color, ImageVector> {
    return when {
        value >= 0.3 -> Triple(positiveLabel, DesignTokens.Colors.success, Icons.Default.ArrowCircleUp)
        value <= -0.3 -> Triple(negativeLabel, DesignTokens.Colors.error, Icons.Default.ArrowCircleDown)
        else -> Triple(neutralLabel, DesignTokens.Colors.textSecondary, Icons.Default.RemoveCircle)
    }
}

/**
 * Gets color for adherence rate
 */
@Composable
private fun getAdherenceColor(rate: Double): androidx.compose.ui.graphics.Color {
    return when {
        rate >= 80 -> DesignTokens.Colors.success
        rate >= 50 -> DesignTokens.Colors.warning
        else -> DesignTokens.Colors.error
    }
}

/**
 * Gets personalized advice based on correlations
 */
private fun getAdviceText(correlations: List<MedicationCorrelation>): String {
    if (correlations.isEmpty()) {
        return "Continue taking your medications regularly and recording your data to get personalized insights."
    }

    val strongest = correlations.first()

    // Check for low adherence
    if (strongest.adherenceRate < 70) {
        return "Try to improve your adherence to ${strongest.medication.name} for better results. Consider enabling reminders."
    }

    // Check for strong mood impact
    if (abs(strongest.moodCorrelation) >= 0.5) {
        return if (strongest.moodCorrelation > 0) {
            "${strongest.medication.name} appears to have a positive effect on your mood. Keep it up!"
        } else {
            "${strongest.medication.name} may be negatively impacting your mood. Discuss this with your doctor at your next appointment."
        }
    }

    // Check for strong sleep impact
    if (abs(strongest.sleepCorrelation) >= 0.5) {
        return if (strongest.sleepCorrelation > 0) {
            "${strongest.medication.name} appears to improve your sleep. Make sure to take it regularly."
        } else {
            "${strongest.medication.name} may be disrupting your sleep. Consider adjusting the timing with your doctor's approval."
        }
    }

    return "Continue tracking your medications and wellness to get more precise insights over time."
}
