package com.vcousien.northstar.features.medication.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import com.vcousien.northstar.core.models.getColor
import com.vcousien.northstar.features.medication.viewmodels.MedicationViewModel
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import java.text.SimpleDateFormat
import java.util.*

/**
 * MedicationTrackerView - Main medication tracking screen
 *
 * Features:
 * - Date selector for viewing different days
 * - Adherence rate card with timeframe selection
 * - Medication log showing today's medications and their status
 * - Clickable medication entries to toggle taken status
 * - Side effects summary
 * - Matches iOS MedicationTrackerView implementation
 *
 * @param viewModel MedicationViewModel for medication management
 * @param modifier Modifier to be applied to the view
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationTrackerView(
    viewModel: MedicationViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    var selectedDate by remember { mutableStateOf(Clock.System.now()) }
    var selectedTimeframe by remember { mutableStateOf(7) }
    val timeframeOptions = listOf(7, 14, 30)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.medication_tracking_title),
                        style = NSTypography.heading1,
                        color = DesignTokens.Colors.textPrimary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DesignTokens.Colors.background
                )
            )
        },
        containerColor = DesignTokens.Colors.background
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Date selector
            DateSelectorView(
                selectedDate = selectedDate,
                onDateChange = { selectedDate = it },
                modifier = Modifier.padding(horizontal = NSSpacing.screenEdge, vertical = NSSpacing.md)
            )

            Spacer(modifier = Modifier.height(NSSpacing.lg))

            // Adherence card
            AdherenceCard(
                viewModel = viewModel,
                selectedTimeframe = selectedTimeframe,
                timeframeOptions = timeframeOptions,
                onTimeframeChange = { selectedTimeframe = it },
                modifier = Modifier.padding(horizontal = NSSpacing.screenEdge)
            )

            Spacer(modifier = Modifier.height(NSSpacing.lg))

            // Medication log
            MedicationLogCard(
                viewModel = viewModel,
                selectedDate = selectedDate,
                modifier = Modifier.padding(horizontal = NSSpacing.screenEdge)
            )

            // Side effects (if present)
            val entriesForDate = viewModel.getEntries(selectedDate)
            val hasSideEffects = entriesForDate.any { it.sideEffects.isNotEmpty() }

            if (hasSideEffects) {
                Spacer(modifier = Modifier.height(NSSpacing.lg))

                SideEffectsCard(
                    viewModel = viewModel,
                    selectedDate = selectedDate,
                    modifier = Modifier.padding(horizontal = NSSpacing.screenEdge)
                )
            }

            Spacer(modifier = Modifier.height(NSSpacing.xxl))
        }
    }
}

/**
 * Date selector component
 */
@Composable
private fun DateSelectorView(
    selectedDate: Instant,
    onDateChange: (Instant) -> Unit,
    modifier: Modifier = Modifier
) {
    val timeZone = TimeZone.currentSystemDefault()
    val today = Clock.System.now()
    val isToday = selectedDate.toLocalDateTime(timeZone).date == today.toLocalDateTime(timeZone).date
    val isFuture = selectedDate > today

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DesignTokens.Colors.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(NSSpacing.md),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                onDateChange(selectedDate.minus(1, DateTimeUnit.DAY, timeZone))
            }) {
                Text("<", style = NSTypography.heading3, color = DesignTokens.Colors.primary)
            }

            Text(
                text = formatDate(selectedDate),
                style = NSTypography.body,
                color = DesignTokens.Colors.textPrimary
            )

            IconButton(
                onClick = {
                    if (!isToday && !isFuture) {
                        onDateChange(selectedDate.plus(1, DateTimeUnit.DAY, timeZone))
                    }
                },
                enabled = !isToday && !isFuture
            ) {
                Text(
                    ">",
                    style = NSTypography.heading3,
                    color = if (isToday || isFuture) {
                        DesignTokens.Colors.textSecondary.copy(alpha = 0.3f)
                    } else {
                        DesignTokens.Colors.primary
                    }
                )
            }
        }
    }
}

/**
 * Adherence card component
 */
@Composable
private fun AdherenceCard(
    viewModel: MedicationViewModel,
    selectedTimeframe: Int,
    timeframeOptions: List<Int>,
    onTimeframeChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val adherenceRate = remember(selectedTimeframe) {
        viewModel.calculateAdherence(days = selectedTimeframe)
    }

    NSCard(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(NSSpacing.md)
        ) {
            Text(
                text = stringResource(R.string.medication_adherence_rate),
                style = NSTypography.heading3,
                color = DesignTokens.Colors.textPrimary
            )

            // Timeframe selector
            Row(
                horizontalArrangement = Arrangement.spacedBy(NSSpacing.sm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.medication_period_label),
                    style = NSTypography.caption,
                    color = DesignTokens.Colors.textSecondary
                )

                timeframeOptions.forEach { days ->
                    Button(
                        onClick = { onTimeframeChange(days) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedTimeframe == days) {
                                DesignTokens.Colors.primary
                            } else {
                                DesignTokens.Colors.surface
                            },
                            contentColor = if (selectedTimeframe == days) {
                                androidx.compose.ui.graphics.Color.White
                            } else {
                                DesignTokens.Colors.textPrimary
                            }
                        ),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text(
                            text = "$days ${stringResource(R.string.medication_days_suffix)}",
                            style = NSTypography.caption
                        )
                    }
                }
            }

            // Adherence display
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = String.format("%.0f%%", adherenceRate),
                    style = NSTypography.heading1,
                    color = getAdherenceColor(adherenceRate)
                )

                Icon(
                    imageVector = when {
                        adherenceRate >= 90 -> Icons.Default.Add // Use checkmark in real app
                        adherenceRate >= 70 -> Icons.Default.Add // Use warning in real app
                        else -> Icons.Default.Add // Use x in real app
                    },
                    contentDescription = null,
                    tint = getAdherenceColor(adherenceRate),
                    modifier = Modifier.size(40.dp)
                )
            }

            // Progress bar
            LinearProgressIndicator(
                progress = { (adherenceRate / 100.0).toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = getAdherenceColor(adherenceRate),
                trackColor = DesignTokens.Colors.primaryLight
            )

            Text(
                text = getAdherenceFeedback(adherenceRate),
                style = NSTypography.caption,
                color = DesignTokens.Colors.textSecondary
            )
        }
    }
}

/**
 * Medication log card showing medications for the selected date
 */
@Composable
private fun MedicationLogCard(
    viewModel: MedicationViewModel,
    selectedDate: Instant,
    modifier: Modifier = Modifier
) {
    val medicationsForDate by viewModel.medications.collectAsState()
    val entriesForDate by remember(selectedDate) {
        derivedStateOf { viewModel.getEntries(selectedDate) }
    }

    NSCard(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(NSSpacing.md)
        ) {
            Text(
                text = "${stringResource(R.string.medication_tracking_for_date)} ${formatDate(selectedDate)}",
                style = NSTypography.heading3,
                color = DesignTokens.Colors.textPrimary
            )

            val scheduledMedications = viewModel.getMedicationsForToday()
            val asNeededMedications = medicationsForDate.filter { it.frequency == com.vcousien.northstar.core.models.MedicationFrequency.AS_NEEDED }

            if (scheduledMedications.isNotEmpty() || asNeededMedications.isNotEmpty()) {
                // Scheduled medications
                scheduledMedications.forEach { medication ->
                    Column {
                        Text(
                            text = medication.name,
                            style = NSTypography.bodyBold,
                            color = DesignTokens.Colors.textPrimary
                        )

                        if (!medication.hasDifferentDosages) {
                            Text(
                                text = medication.dosage,
                                style = NSTypography.caption,
                                color = DesignTokens.Colors.textSecondary
                            )
                        }

                        medication.timeOfDay.forEach { timeOfDay ->
                            val entry = entriesForDate.firstOrNull {
                                it.medicationId == medication.id && it.timeOfDay == timeOfDay
                            }

                            MedicationDoseRow(
                                medication = medication,
                                timeOfDay = timeOfDay,
                                entry = entry,
                                onClick = {
                                    toggleMedicationStatus(viewModel, medication, timeOfDay, selectedDate, entry)
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(NSSpacing.sm))
                    }
                }
            } else {
                Text(
                    text = stringResource(R.string.medication_no_medications_date),
                    style = NSTypography.body,
                    color = DesignTokens.Colors.textSecondary,
                    modifier = Modifier.padding(vertical = NSSpacing.md)
                )
            }
        }
    }
}

/**
 * Row for a single medication dose
 */
@Composable
private fun MedicationDoseRow(
    medication: com.vcousien.northstar.core.models.Medication,
    timeOfDay: com.vcousien.northstar.core.models.TimeOfDay,
    entry: com.vcousien.northstar.core.models.MedicationEntry?,
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
        Row(
            horizontalArrangement = Arrangement.spacedBy(NSSpacing.sm)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .padding(top = 6.dp)
                    .aspectRatio(1f)
                    .background(
                        color = timeOfDay.getColor(),
                        shape = androidx.compose.foundation.shape.CircleShape
                    )
            )

            Column {
                Text(
                    text = timeOfDay.displayName,
                    style = NSTypography.body,
                    color = DesignTokens.Colors.textPrimary
                )

                if (medication.hasDifferentDosages) {
                    Text(
                        text = medication.getDosage(timeOfDay),
                        style = NSTypography.small,
                        color = DesignTokens.Colors.textSecondary
                    )
                }
            }
        }

        if (entry != null && entry.taken) {
            Text(
                text = "${stringResource(R.string.medication_taken_at)} ${entry.actualTimeAsInstant?.let { formatTime(it) } ?: formatTime(entry.dateAsInstant)}",
                style = NSTypography.small,
                color = DesignTokens.Colors.success
            )
        } else {
            Text(
                text = stringResource(R.string.medication_not_taken_status),
                style = NSTypography.small,
                color = DesignTokens.Colors.error
            )
        }
    }
}

/**
 * Side effects card
 */
@Composable
private fun SideEffectsCard(
    viewModel: MedicationViewModel,
    selectedDate: Instant,
    modifier: Modifier = Modifier
) {
    val entriesWithSideEffects = remember(selectedDate) {
        viewModel.getEntries(selectedDate).filter { it.sideEffects.isNotEmpty() }
    }

    NSCard(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(NSSpacing.md)
        ) {
            Text(
                text = stringResource(R.string.medication_side_effects_title),
                style = NSTypography.heading3,
                color = DesignTokens.Colors.textPrimary
            )

            entriesWithSideEffects.forEach { entry ->
                Column {
                    Text(
                        text = entry.medicationName,
                        style = NSTypography.bodyBold,
                        color = DesignTokens.Colors.textPrimary
                    )

                    entry.sideEffects.forEach { sideEffect ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = NSSpacing.xxs),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = sideEffect.name,
                                style = NSTypography.body,
                                color = DesignTokens.Colors.textPrimary
                            )

                            Text(
                                text = "${stringResource(R.string.medication_severity_label)} ${sideEffect.severity.toInt()}/10",
                                style = NSTypography.caption,
                                color = getSeverityColor(sideEffect.severity)
                            )
                        }
                    }
                }
            }
        }
    }
}

// Helper functions

private fun toggleMedicationStatus(
    viewModel: MedicationViewModel,
    medication: com.vcousien.northstar.core.models.Medication,
    timeOfDay: com.vcousien.northstar.core.models.TimeOfDay,
    selectedDate: Instant,
    currentEntry: com.vcousien.northstar.core.models.MedicationEntry?
) {
    if (currentEntry != null) {
        // Toggle existing entry
        val updatedEntry = currentEntry.copy(
            taken = !currentEntry.taken,
            actualTime = if (!currentEntry.taken) Clock.System.now().toEpochMilliseconds() else null
        )
        viewModel.updateEntry(updatedEntry)
    } else {
        // Create new entry
        val newEntry = com.vcousien.northstar.core.models.MedicationEntry(
            date = selectedDate,
            medicationId = medication.id,
            medicationName = medication.name,
            timeOfDay = timeOfDay,
            taken = true,
            actualTime = Clock.System.now()
        )
        viewModel.addEntry(newEntry)
    }
}

@Composable
private fun getAdherenceColor(rate: Double): androidx.compose.ui.graphics.Color {
    return when {
        rate >= 90 -> DesignTokens.Colors.success
        rate >= 70 -> DesignTokens.Colors.warning
        else -> DesignTokens.Colors.error
    }
}

private fun getAdherenceFeedback(rate: Double): String {
    return when {
        rate >= 90 -> "Excellent adherence! Keep it up!"
        rate >= 70 -> "Good adherence. Try to improve consistency."
        rate >= 50 -> "Average adherence. Consider using reminders."
        else -> "Low adherence. Talk to your doctor."
    }
}

@Composable
private fun getSeverityColor(severity: Double): androidx.compose.ui.graphics.Color {
    return when {
        severity <= 3 -> DesignTokens.Colors.success
        severity <= 7 -> DesignTokens.Colors.warning
        else -> DesignTokens.Colors.error
    }
}

private fun formatDate(instant: Instant): String {
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    val calendar = Calendar.getInstance().apply {
        set(Calendar.YEAR, localDateTime.year)
        set(Calendar.MONTH, localDateTime.monthNumber - 1)
        set(Calendar.DAY_OF_MONTH, localDateTime.dayOfMonth)
    }

    val formatter = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
    return formatter.format(calendar.time)
}

private fun formatTime(instant: Instant): String {
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, localDateTime.hour)
        set(Calendar.MINUTE, localDateTime.minute)
    }

    val formatter = SimpleDateFormat("h:mm a", Locale.getDefault())
    return formatter.format(calendar.time)
}
