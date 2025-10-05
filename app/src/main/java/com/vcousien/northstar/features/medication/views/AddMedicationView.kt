package com.vcousien.northstar.features.medication.views

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vcousien.northstar.R
import com.vcousien.northstar.core.designsystem.NSSpacing
import com.vcousien.northstar.core.designsystem.NSTypography
import com.vcousien.northstar.core.designsystem.DesignTokens
import com.vcousien.northstar.core.designsystem.components.NSPrimaryButton
import com.vcousien.northstar.core.models.*
import com.vcousien.northstar.features.medication.viewmodels.MedicationViewModel

/**
 * AddMedicationView - View for adding or editing medications
 *
 * Features:
 * - Medication name input
 * - Dosage with optional different dosages by time of day
 * - Frequency selection
 * - Time of day selection with color indicators
 * - Notes field
 * - Form validation
 * - Matches iOS AddMedicationView implementation
 *
 * @param viewModel MedicationViewModel for medication management
 * @param onDismiss Callback when the view is dismissed
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMedicationView(
    viewModel: MedicationViewModel = viewModel(),
    onDismiss: () -> Unit = {}
) {
    val existingMedication = viewModel.currentMedication
    val isEditing = existingMedication != null && !viewModel.isAddingNewMedication

    // State variables
    var name by remember { mutableStateOf(existingMedication?.name ?: "") }
    var dosage by remember { mutableStateOf(existingMedication?.dosage ?: "") }
    var selectedFrequency by remember { mutableStateOf(existingMedication?.frequency ?: MedicationFrequency.DAILY) }
    var selectedTimes by remember {
        mutableStateOf(existingMedication?.timeOfDay?.toSet() ?: setOf(TimeOfDay.MORNING))
    }
    var notes by remember { mutableStateOf(existingMedication?.notes ?: "") }
    var hasDifferentDosages by remember { mutableStateOf(existingMedication?.hasDifferentDosages ?: false) }
    var dosagesByTime by remember {
        mutableStateOf<Map<TimeOfDay, String>>(existingMedication?.dosagesByTime ?: emptyMap())
    }

    // Form validation
    val isFormValid = remember(name, dosage, selectedTimes) {
        name.trim().isNotEmpty() &&
                dosage.trim().isNotEmpty() &&
                selectedTimes.isNotEmpty()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditing) {
                            "Edit Medication"
                        } else {
                            stringResource(R.string.medication_add_medication_title)
                        },
                        style = NSTypography.heading2
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.medication_cancel_action)
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            saveMedication(
                                viewModel = viewModel,
                                existingMedication = existingMedication,
                                isEditing = isEditing,
                                name = name.trim(),
                                dosage = dosage.trim(),
                                dosagesByTime = if (hasDifferentDosages) dosagesByTime else emptyMap(),
                                frequency = selectedFrequency,
                                timeOfDay = selectedTimes.toList().sortedBy { it.priority() },
                                notes = notes.trim()
                            )
                            onDismiss()
                        },
                        enabled = isFormValid
                    ) {
                        Text(
                            text = stringResource(R.string.medication_save_action),
                            style = NSTypography.bodyBold,
                            color = if (isFormValid) DesignTokens.Colors.primary else DesignTokens.Colors.textSecondary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DesignTokens.Colors.cardBackground
                )
            )
        },
        containerColor = DesignTokens.Colors.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(NSSpacing.screenEdge)
        ) {
            // Information Section
            SectionHeader(stringResource(R.string.medication_information_header))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(R.string.medication_medication_name_field)) },
                modifier = Modifier.fillMaxWidth(),
                textStyle = NSTypography.body,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = DesignTokens.Colors.primary,
                    unfocusedBorderColor = DesignTokens.Colors.border
                )
            )

            Spacer(modifier = Modifier.height(NSSpacing.xl))

            // Dosage Section
            SectionHeader(stringResource(R.string.medication_dosage_header))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.medication_different_dosages),
                    style = NSTypography.body,
                    color = DesignTokens.Colors.textPrimary
                )

                Switch(
                    checked = hasDifferentDosages,
                    onCheckedChange = { hasDifferentDosages = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = DesignTokens.Colors.primary,
                        checkedTrackColor = DesignTokens.Colors.primary.copy(alpha = 0.5f)
                    )
                )
            }

            Spacer(modifier = Modifier.height(NSSpacing.md))

            if (hasDifferentDosages) {
                // Different dosages by time
                selectedTimes.sortedBy { it.priority() }.forEach { timeOfDay ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = NSSpacing.sm),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(NSSpacing.sm),
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
                                style = NSTypography.body,
                                color = DesignTokens.Colors.textPrimary
                            )
                        }

                        OutlinedTextField(
                            value = dosagesByTime[timeOfDay] ?: "",
                            onValueChange = { newValue ->
                                dosagesByTime = dosagesByTime.toMutableMap().apply {
                                    put(timeOfDay, newValue)
                                }
                            },
                            placeholder = { Text(stringResource(R.string.medication_dosage_header)) },
                            modifier = Modifier.width(120.dp),
                            textStyle = NSTypography.body,
                            singleLine = true
                        )
                    }
                }
            } else {
                // Uniform dosage
                OutlinedTextField(
                    value = dosage,
                    onValueChange = { dosage = it },
                    placeholder = { Text(stringResource(R.string.medication_dosage_example)) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = NSTypography.body
                )
            }

            Spacer(modifier = Modifier.height(NSSpacing.xl))

            // Frequency Section
            SectionHeader(stringResource(R.string.medication_frequency_header))

            var expanded by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedFrequency.displayName,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    textStyle = NSTypography.body,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DesignTokens.Colors.primary
                    )
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    MedicationFrequency.entries.forEach { frequency ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = frequency.displayName,
                                    style = NSTypography.body
                                )
                            },
                            onClick = {
                                selectedFrequency = frequency
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(NSSpacing.xl))

            // Time of Day Section
            SectionHeader(stringResource(R.string.medication_time_of_day_header))

            TimeOfDay.entries.forEach { timeOfDay ->
                TimeOfDayRow(
                    timeOfDay = timeOfDay,
                    isSelected = selectedTimes.contains(timeOfDay),
                    onToggle = {
                        selectedTimes = if (selectedTimes.contains(timeOfDay)) {
                            selectedTimes - timeOfDay
                        } else {
                            selectedTimes + timeOfDay
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(NSSpacing.xl))

            // Notes Section
            SectionHeader(stringResource(R.string.medication_notes_header))

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                textStyle = NSTypography.body,
                placeholder = {
                    Text(
                        text = "Optional notes...",
                        style = NSTypography.body,
                        color = DesignTokens.Colors.textSecondary
                    )
                }
            )

            Spacer(modifier = Modifier.height(NSSpacing.xxl))

            // Save Button
            NSPrimaryButton(
                title = stringResource(R.string.medication_save_action),
                onClick = {
                    saveMedication(
                        viewModel = viewModel,
                        existingMedication = existingMedication,
                        isEditing = isEditing,
                        name = name.trim(),
                        dosage = dosage.trim(),
                        dosagesByTime = if (hasDifferentDosages) dosagesByTime else emptyMap(),
                        frequency = selectedFrequency,
                        timeOfDay = selectedTimes.toList().sortedBy { it.priority() },
                        notes = notes.trim()
                    )
                    onDismiss()
                },
                enabled = isFormValid,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(NSSpacing.lg))
        }
    }
}

/**
 * Section header composable
 */
@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text,
        style = NSTypography.caption,
        color = DesignTokens.Colors.textSecondary,
        modifier = Modifier.padding(bottom = NSSpacing.sm)
    )
}

/**
 * Time of day selection row
 */
@Composable
private fun TimeOfDayRow(
    timeOfDay: TimeOfDay,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .padding(vertical = NSSpacing.sm),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(NSSpacing.sm),
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
                style = NSTypography.body,
                color = DesignTokens.Colors.textPrimary
            )
        }

        Icon(
            imageVector = if (isSelected) Icons.Default.Check else Icons.Default.Close,
            contentDescription = null,
            tint = if (isSelected) DesignTokens.Colors.primary else DesignTokens.Colors.textSecondary,
            modifier = Modifier.size(20.dp)
        )
    }
}

/**
 * Save the medication
 */
private fun saveMedication(
    viewModel: MedicationViewModel,
    existingMedication: Medication?,
    isEditing: Boolean,
    name: String,
    dosage: String,
    dosagesByTime: Map<TimeOfDay, String>,
    frequency: MedicationFrequency,
    timeOfDay: List<TimeOfDay>,
    notes: String
) {
    val medication = Medication(
        id = existingMedication?.id ?: java.util.UUID.randomUUID().toString(),
        name = name,
        dosage = dosage,
        dosagesByTime = dosagesByTime,
        frequency = frequency,
        timeOfDay = timeOfDay,
        notes = notes,
        remindersEnabled = existingMedication?.remindersEnabled ?: false,
        customReminders = existingMedication?.customReminders ?: emptyMap(),
        lastModified = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
    )

    if (isEditing) {
        viewModel.updateMedication(medication)
    } else {
        viewModel.addMedication(medication)
    }
}
