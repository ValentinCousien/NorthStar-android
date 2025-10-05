package com.vcousien.northstar.features.medication.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vcousien.northstar.R
import com.vcousien.northstar.core.designsystem.NSSpacing
import com.vcousien.northstar.core.designsystem.NSTypography
import com.vcousien.northstar.core.designsystem.DesignTokens
import com.vcousien.northstar.core.designsystem.components.NSCard
import com.vcousien.northstar.core.designsystem.components.NSEmptyState
import com.vcousien.northstar.core.models.*
import com.vcousien.northstar.features.medication.viewmodels.MedicationViewModel
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.text.SimpleDateFormat
import java.util.*

/**
 * TodayMedicationCard - Home screen card showing today's medications
 *
 * Features:
 * - Groups medications by time of day (Morning, Noon, Evening, Bedtime)
 * - Shows overall status (all medications taken or not)
 * - Allows marking medications as taken with checkboxes
 * - Special section for "as needed" medications
 * - Empty state when no medications scheduled
 * - Matches iOS TodayMedicationsCard implementation
 *
 * @param viewModel MedicationViewModel for medication management
 * @param modifier Modifier to be applied to the card
 * @param onAddMedicationClick Callback when add medication is clicked
 */
@Composable
fun TodayMedicationCard(
    viewModel: MedicationViewModel = viewModel(),
    modifier: Modifier = Modifier,
    onAddMedicationClick: () -> Unit = {}
) {
    val medications by viewModel.medications.collectAsState()
    val entries by viewModel.medicationEntries.collectAsState()

    val today = Clock.System.now()
    val todayMedications = remember(medications) {
        viewModel.getMedicationsForToday()
    }

    val asNeededMedications = remember(medications) {
        medications.filter { it.frequency == MedicationFrequency.AS_NEEDED }
    }

    // Group medications by time of day
    val groupedMedications = remember(todayMedications) {
        groupMedicationsByTimeOfDay(todayMedications)
    }

    // Get available time slots in order
    val availableTimeSlots = remember(groupedMedications) {
        TimeOfDay.entries.filter { groupedMedications[it] != null }
    }

    val allMedicationsTaken = remember(entries, todayMedications) {
        viewModel.allMedicationsTaken(today)
    }

    NSCard(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(NSSpacing.md)
        ) {
            // Header
            Text(
                text = stringResource(R.string.common_today),
                style = NSTypography.heading3,
                color = DesignTokens.Colors.textPrimary
            )

            if (todayMedications.isNotEmpty() || asNeededMedications.isNotEmpty()) {
                // Global status (only for scheduled medications)
                if (todayMedications.isNotEmpty()) {
                    Row(
                        modifier = Modifier.padding(vertical = NSSpacing.xs),
                        horizontalArrangement = Arrangement.spacedBy(NSSpacing.sm),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (allMedicationsTaken) Icons.Default.Check else Icons.Default.Add,
                            contentDescription = null,
                            tint = if (allMedicationsTaken) {
                                DesignTokens.Colors.success
                            } else {
                                DesignTokens.Colors.textSecondary
                            },
                            modifier = Modifier.size(20.dp)
                        )

                        Text(
                            text = if (allMedicationsTaken) {
                                stringResource(R.string.medication_all_medications_taken_today)
                            } else {
                                stringResource(R.string.medication_medications_to_take_today)
                            },
                            style = NSTypography.bodyBold,
                            color = if (allMedicationsTaken) {
                                DesignTokens.Colors.success
                            } else {
                                DesignTokens.Colors.textPrimary
                            }
                        )
                    }

                    // Time slots with medications
                    availableTimeSlots.forEachIndexed { index, timeOfDay ->
                        val medicationsForTime = groupedMedications[timeOfDay] ?: emptyList()

                        TimeSlotSection(
                            timeOfDay = timeOfDay,
                            medications = medicationsForTime,
                            viewModel = viewModel,
                            today = today,
                            isLast = index == availableTimeSlots.size - 1 && asNeededMedications.isEmpty()
                        )
                    }
                }

                // As needed medications section
                if (asNeededMedications.isNotEmpty()) {
                    if (todayMedications.isNotEmpty()) {
                        HorizontalDivider(
                            color = DesignTokens.Colors.border,
                            modifier = Modifier.padding(vertical = NSSpacing.sm)
                        )
                    }

                    AsNeededSection(
                        medications = asNeededMedications,
                        viewModel = viewModel,
                        today = today
                    )
                }
            } else {
                // Empty state
                NSEmptyState(
                    title = stringResource(R.string.medication_no_medications),
                    message = stringResource(R.string.medication_no_medications_today),
                    icon = Icons.Default.Add, // Use pill icon in real app
                    action = {
                        viewModel.prepareNewMedication()
                        onAddMedicationClick()
                    },
                    actionTitle = stringResource(R.string.medication_add)
                )
            }
        }
    }
}

/**
 * Time slot section showing medications for a specific time of day
 */
@Composable
private fun TimeSlotSection(
    timeOfDay: TimeOfDay,
    medications: List<Medication>,
    viewModel: MedicationViewModel,
    today: kotlinx.datetime.Instant,
    isLast: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = NSSpacing.xs),
        verticalArrangement = Arrangement.spacedBy(NSSpacing.xs)
    ) {
        // Time of day header
        Row(
            modifier = Modifier.padding(bottom = NSSpacing.xs),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(timeOfDay.getColor())
            )

            Text(
                text = timeOfDay.displayName,
                style = NSTypography.bodyBold,
                color = DesignTokens.Colors.textPrimary
            )
        }

        // Medications for this time
        medications.forEach { medication ->
            MedicationRow(
                medication = medication,
                timeOfDay = timeOfDay,
                viewModel = viewModel,
                today = today
            )
        }
    }

    if (!isLast) {
        HorizontalDivider(color = DesignTokens.Colors.border)
    }
}

/**
 * Individual medication row with checkbox
 */
@Composable
private fun MedicationRow(
    medication: Medication,
    timeOfDay: TimeOfDay,
    viewModel: MedicationViewModel,
    today: kotlinx.datetime.Instant
) {
    val entries by viewModel.medicationEntries.collectAsState()

    val entry = remember(entries, medication.id, timeOfDay) {
        viewModel.getEntries(today).firstOrNull { entry ->
            entry.medicationId == medication.id && entry.timeOfDay == timeOfDay
        }
    }

    val taken = entry?.taken ?: false
    val isAsNeeded = medication.frequency == MedicationFrequency.AS_NEEDED

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp, horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Row(
                horizontalArrangement = Arrangement.spacedBy(NSSpacing.sm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = medication.name,
                    style = NSTypography.body,
                    color = DesignTokens.Colors.textPrimary
                )

                if (isAsNeeded) {
                    Surface(
                        color = DesignTokens.Colors.warning.copy(alpha = 0.8f),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = "PRN",
                            style = NSTypography.captionBold,
                            color = androidx.compose.ui.graphics.Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Text(
                text = medication.getDosage(timeOfDay),
                style = NSTypography.caption,
                color = DesignTokens.Colors.textSecondary
            )

            if (isAsNeeded && !taken) {
                Text(
                    text = stringResource(R.string.medication_take_as_needed),
                    style = NSTypography.caption,
                    color = DesignTokens.Colors.warning
                )
            }
        }

        // Checkbox button
        IconButton(
            onClick = {
                markMedicationTaken(
                    viewModel = viewModel,
                    medication = medication,
                    timeOfDay = timeOfDay,
                    today = today,
                    currentTaken = taken
                )
            }
        ) {
            if (isAsNeeded) {
                Icon(
                    imageVector = if (taken) Icons.Default.Check else Icons.Default.Add,
                    contentDescription = if (taken) "Taken" else "Mark as taken",
                    tint = if (taken) DesignTokens.Colors.success else DesignTokens.Colors.warning,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Icon(
                    imageVector = if (taken) Icons.Default.Check else Icons.Default.Add,
                    contentDescription = if (taken) "Taken" else "Mark as taken",
                    tint = if (taken) DesignTokens.Colors.success else DesignTokens.Colors.textSecondary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

/**
 * As needed medications section
 */
@Composable
private fun AsNeededSection(
    medications: List<Medication>,
    viewModel: MedicationViewModel,
    today: kotlinx.datetime.Instant
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = NSSpacing.xs),
        verticalArrangement = Arrangement.spacedBy(NSSpacing.xs)
    ) {
        // Section header
        Row(
            modifier = Modifier.padding(bottom = NSSpacing.xs),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(DesignTokens.Colors.warning.copy(alpha = 0.3f))
            )

            Text(
                text = "As Needed",
                style = NSTypography.bodyBold,
                color = DesignTokens.Colors.textPrimary
            )
        }

        // As needed medications
        medications.forEach { medication ->
            AsNeededMedicationRow(
                medication = medication,
                viewModel = viewModel,
                today = today
            )
        }
    }
}

/**
 * Row for as-needed medication with multiple doses tracking
 */
@Composable
private fun AsNeededMedicationRow(
    medication: Medication,
    viewModel: MedicationViewModel,
    today: kotlinx.datetime.Instant
) {
    val entries by viewModel.medicationEntries.collectAsState()

    val todayEntries = remember(entries, medication.id) {
        viewModel.getEntries(today).filter {
            it.medicationId == medication.id && it.taken
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = medication.name,
                    style = NSTypography.body,
                    color = DesignTokens.Colors.textPrimary
                )

                Surface(
                    color = DesignTokens.Colors.warning.copy(alpha = 0.8f),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = "PRN",
                        style = NSTypography.captionBold,
                        color = androidx.compose.ui.graphics.Color.White,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Text(
                text = medication.dosage,
                style = NSTypography.caption,
                color = DesignTokens.Colors.textSecondary
            )

            // Show taken times if any
            if (todayEntries.isNotEmpty()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    todayEntries.take(3).forEach { entry ->
                        Surface(
                            color = DesignTokens.Colors.success.copy(alpha = 0.1f),
                            shape = MaterialTheme.shapes.small,
                            modifier = Modifier.clickable {
                                viewModel.deleteEntry(entry.id)
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                horizontalArrangement = Arrangement.spacedBy(2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = DesignTokens.Colors.success,
                                    modifier = Modifier.size(10.dp)
                                )

                                Text(
                                    text = formatTime(entry.actualTimeAsInstant ?: entry.dateAsInstant),
                                    style = NSTypography.small,
                                    color = DesignTokens.Colors.success
                                )
                            }
                        }
                    }

                    if (todayEntries.size > 3) {
                        Text(
                            text = "+${todayEntries.size - 3}",
                            style = NSTypography.small,
                            color = DesignTokens.Colors.textSecondary
                        )
                    }
                }
            }
        }

        // Add dose button
        Button(
            onClick = {
                addAsNeededDose(viewModel, medication, today)
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = DesignTokens.Colors.warning.copy(alpha = 0.1f),
                contentColor = DesignTokens.Colors.warning
            ),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )

                Text(
                    text = "Take",
                    style = NSTypography.captionBold
                )
            }
        }
    }
}

// Helper functions

/**
 * Groups medications by their times of day
 */
private fun groupMedicationsByTimeOfDay(medications: List<Medication>): Map<TimeOfDay, List<Medication>> {
    val grouped = mutableMapOf<TimeOfDay, MutableList<Medication>>()

    for (medication in medications) {
        for (timeOfDay in medication.timeOfDay) {
            if (grouped[timeOfDay] == null) {
                grouped[timeOfDay] = mutableListOf()
            }
            grouped[timeOfDay]?.add(medication)
        }
    }

    return grouped
}

/**
 * Marks a medication as taken or not taken
 */
private fun markMedicationTaken(
    viewModel: MedicationViewModel,
    medication: Medication,
    timeOfDay: TimeOfDay,
    today: kotlinx.datetime.Instant,
    currentTaken: Boolean
) {
    val existingEntry = viewModel.getEntries(today).firstOrNull {
        it.medicationId == medication.id && it.timeOfDay == timeOfDay
    }

    if (existingEntry != null) {
        // Update existing entry
        val updatedEntry = existingEntry.copy(
            taken = !currentTaken,
            actualTime = if (!currentTaken) Clock.System.now().toEpochMilliseconds() else null
        )
        viewModel.updateEntry(updatedEntry)
    } else {
        // Create new entry
        val newEntry = MedicationEntry(
            date = today,
            medicationId = medication.id,
            medicationName = medication.name,
            timeOfDay = timeOfDay,
            taken = true,
            actualTime = Clock.System.now()
        )
        viewModel.addEntry(newEntry)
    }
}

/**
 * Adds a dose for an as-needed medication
 */
private fun addAsNeededDose(
    viewModel: MedicationViewModel,
    medication: Medication,
    today: kotlinx.datetime.Instant
) {
    val newEntry = MedicationEntry(
        date = today,
        medicationId = medication.id,
        medicationName = medication.name,
        timeOfDay = TimeOfDay.MORNING, // Placeholder for as-needed
        taken = true,
        actualTime = Clock.System.now(),
        sideEffects = emptyList(),
        notes = ""
    )
    viewModel.addEntry(newEntry)
}

/**
 * Format time for display
 */
private fun formatTime(instant: kotlinx.datetime.Instant): String {
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, localDateTime.hour)
        set(Calendar.MINUTE, localDateTime.minute)
    }

    val formatter = SimpleDateFormat("h:mm a", Locale.getDefault())
    return formatter.format(calendar.time)
}
