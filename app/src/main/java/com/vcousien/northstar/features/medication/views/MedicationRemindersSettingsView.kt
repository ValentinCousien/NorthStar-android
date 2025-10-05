package com.vcousien.northstar.features.medication.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vcousien.northstar.R
import com.vcousien.northstar.core.designsystem.DesignTokens
import com.vcousien.northstar.core.designsystem.NSSpacing
import com.vcousien.northstar.core.designsystem.NSTypography
import com.vcousien.northstar.core.models.Medication
import com.vcousien.northstar.core.models.MedicationReminder
import com.vcousien.northstar.core.models.TimeOfDay
import com.vcousien.northstar.features.medication.viewmodels.MedicationViewModel
import java.util.Calendar

/**
 * MedicationRemindersSettingsView - Settings screen for configuring medication reminders
 *
 * Features:
 * - General settings (enable/disable reminders)
 * - Per-time-of-day reminder configuration
 * - Custom time picker for each reminder
 * - Preview of enabled reminders
 * - Integration with MedicationReminderManager for scheduling
 * - Matches iOS MedicationRemindersSettingsView implementation
 *
 * @param medication The medication to configure reminders for
 * @param onDismiss Callback when the settings view is dismissed
 * @param viewModel MedicationViewModel for saving changes
 * @param modifier Modifier to be applied to the view
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationRemindersSettingsView(
    medication: Medication,
    onDismiss: () -> Unit,
    viewModel: MedicationViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    // Local state for editing reminders before saving
    var remindersEnabled by remember { mutableStateOf(medication.remindersEnabled) }
    var customReminders by remember { mutableStateOf(medication.customReminders.toMutableMap()) }
    var showTimePicker by remember { mutableStateOf<TimeOfDay?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.medication_reminders_settings),
                        style = NSTypography.heading2,
                        color = DesignTokens.Colors.textPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.common_cancel),
                            tint = DesignTokens.Colors.textSecondary
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            saveSettings(
                                medication = medication,
                                remindersEnabled = remindersEnabled,
                                customReminders = customReminders,
                                viewModel = viewModel,
                                onDismiss = onDismiss
                            )
                        }
                    ) {
                        Text(
                            text = stringResource(R.string.common_save),
                            style = NSTypography.bodyBold,
                            color = DesignTokens.Colors.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DesignTokens.Colors.surface
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
            Spacer(modifier = Modifier.height(NSSpacing.md))

            // General Settings Section
            GeneralSettingsSection(
                medication = medication,
                remindersEnabled = remindersEnabled,
                onRemindersEnabledChange = { remindersEnabled = it },
                modifier = Modifier.padding(horizontal = NSSpacing.screenEdge)
            )

            Spacer(modifier = Modifier.height(NSSpacing.lg))

            // Reminders Configuration Section
            RemindersConfigurationSection(
                medication = medication,
                remindersEnabled = remindersEnabled,
                customReminders = customReminders,
                onReminderToggle = { timeOfDay, enabled ->
                    val reminder = customReminders[timeOfDay] ?: MedicationReminder(
                        timeOfDay = timeOfDay,
                        hour = timeOfDay.defaultReminderTime().first,
                        minute = timeOfDay.defaultReminderTime().second
                    )
                    customReminders[timeOfDay] = reminder.copy(enabled = enabled)
                },
                onTimeClick = { timeOfDay -> showTimePicker = timeOfDay },
                modifier = Modifier.padding(horizontal = NSSpacing.screenEdge)
            )

            Spacer(modifier = Modifier.height(NSSpacing.lg))

            // Preview Section
            PreviewSection(
                medication = medication,
                remindersEnabled = remindersEnabled,
                customReminders = customReminders,
                modifier = Modifier.padding(horizontal = NSSpacing.screenEdge)
            )

            Spacer(modifier = Modifier.height(NSSpacing.xxl))
        }
    }

    // Time Picker Dialog
    showTimePicker?.let { timeOfDay ->
        val currentReminder = customReminders[timeOfDay] ?: MedicationReminder(
            timeOfDay = timeOfDay,
            hour = timeOfDay.defaultReminderTime().first,
            minute = timeOfDay.defaultReminderTime().second
        )

        TimePickerDialog(
            timeOfDay = timeOfDay,
            initialHour = currentReminder.hour,
            initialMinute = currentReminder.minute,
            onTimeSelected = { hour, minute ->
                customReminders[timeOfDay] = currentReminder.copy(
                    hour = hour,
                    minute = minute,
                    enabled = true
                )
                showTimePicker = null
            },
            onDismiss = { showTimePicker = null }
        )
    }
}

/**
 * General settings section
 */
@Composable
private fun GeneralSettingsSection(
    medication: Medication,
    remindersEnabled: Boolean,
    onRemindersEnabledChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = DesignTokens.Colors.surface,
        shape = RoundedCornerShape(NSSpacing.CornerRadius.md)
    ) {
        Column(
            modifier = Modifier.padding(NSSpacing.md),
            verticalArrangement = Arrangement.spacedBy(NSSpacing.md)
        ) {
            // Section Header
            Text(
                text = stringResource(R.string.medication_general_settings),
                style = NSTypography.caption,
                color = DesignTokens.Colors.textSecondary
            )

            // Medication Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = medication.name,
                    style = NSTypography.bodyBold,
                    color = DesignTokens.Colors.textPrimary
                )

                Text(
                    text = medication.displayDosage,
                    style = NSTypography.body,
                    color = DesignTokens.Colors.textSecondary
                )
            }

            HorizontalDivider(color = DesignTokens.Colors.border)

            // Enable Reminders Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.medication_enable_reminders),
                    style = NSTypography.body,
                    color = DesignTokens.Colors.textPrimary
                )

                Switch(
                    checked = remindersEnabled,
                    onCheckedChange = onRemindersEnabledChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = androidx.compose.ui.graphics.Color.White,
                        checkedTrackColor = DesignTokens.Colors.primary
                    )
                )
            }
        }
    }
}

/**
 * Reminders configuration section
 */
@Composable
private fun RemindersConfigurationSection(
    medication: Medication,
    remindersEnabled: Boolean,
    customReminders: Map<TimeOfDay, MedicationReminder>,
    onReminderToggle: (TimeOfDay, Boolean) -> Unit,
    onTimeClick: (TimeOfDay) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = DesignTokens.Colors.surface,
        shape = RoundedCornerShape(NSSpacing.CornerRadius.md)
    ) {
        Column(
            modifier = Modifier.padding(NSSpacing.md),
            verticalArrangement = Arrangement.spacedBy(NSSpacing.md)
        ) {
            // Section Header
            Text(
                text = stringResource(R.string.medication_reminder_schedule),
                style = NSTypography.caption,
                color = DesignTokens.Colors.textSecondary
            )

            if (remindersEnabled) {
                medication.timeOfDay.forEachIndexed { index, timeOfDay ->
                    if (index > 0) {
                        HorizontalDivider(color = DesignTokens.Colors.border)
                    }

                    ReminderRow(
                        timeOfDay = timeOfDay,
                        medication = medication,
                        reminder = customReminders[timeOfDay],
                        onReminderToggle = { enabled -> onReminderToggle(timeOfDay, enabled) },
                        onTimeClick = { onTimeClick(timeOfDay) }
                    )
                }
            } else {
                Text(
                    text = stringResource(R.string.medication_enable_reminders_to_configure),
                    style = NSTypography.body,
                    color = DesignTokens.Colors.textSecondary,
                    modifier = Modifier.padding(vertical = NSSpacing.md)
                )
            }
        }
    }
}

/**
 * Individual reminder row
 */
@Composable
private fun ReminderRow(
    timeOfDay: TimeOfDay,
    medication: Medication,
    reminder: MedicationReminder?,
    onReminderToggle: (Boolean) -> Unit,
    onTimeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentReminder = reminder ?: MedicationReminder(
        timeOfDay = timeOfDay,
        hour = timeOfDay.defaultReminderTime().first,
        minute = timeOfDay.defaultReminderTime().second,
        enabled = false
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(NSSpacing.sm)
    ) {
        // Time of Day Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(NSSpacing.sm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Color indicator
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

                Text(
                    text = medication.getDosage(timeOfDay),
                    style = NSTypography.caption,
                    color = DesignTokens.Colors.textSecondary
                )
            }

            Switch(
                checked = currentReminder.enabled,
                onCheckedChange = onReminderToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = androidx.compose.ui.graphics.Color.White,
                    checkedTrackColor = timeOfDay.getColor()
                )
            )
        }

        // Time Picker Button (shown when enabled)
        if (currentReminder.enabled) {
            OutlinedButton(
                onClick = onTimeClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = DesignTokens.Colors.textPrimary
                ),
                shape = RoundedCornerShape(NSSpacing.CornerRadius.sm)
            ) {
                Text(
                    text = "${stringResource(R.string.medication_reminder_time)}: ${currentReminder.formattedTime()}",
                    style = NSTypography.body
                )
            }
        }
    }
}

/**
 * Preview section showing all enabled reminders
 */
@Composable
private fun PreviewSection(
    medication: Medication,
    remindersEnabled: Boolean,
    customReminders: Map<TimeOfDay, MedicationReminder>,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = DesignTokens.Colors.surface,
        shape = RoundedCornerShape(NSSpacing.CornerRadius.md)
    ) {
        Column(
            modifier = Modifier.padding(NSSpacing.md),
            verticalArrangement = Arrangement.spacedBy(NSSpacing.md)
        ) {
            // Section Header
            Text(
                text = stringResource(R.string.medication_preview),
                style = NSTypography.caption,
                color = DesignTokens.Colors.textSecondary
            )

            if (remindersEnabled) {
                val enabledReminders = medication.timeOfDay
                    .mapNotNull { timeOfDay ->
                        customReminders[timeOfDay]?.takeIf { it.enabled }
                    }
                    .sortedBy { it.hour * 60 + it.minute }

                if (enabledReminders.isNotEmpty()) {
                    enabledReminders.forEachIndexed { index, reminder ->
                        if (index > 0) {
                            HorizontalDivider(color = DesignTokens.Colors.border)
                        }

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
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(reminder.timeOfDay.getColor())
                                )

                                Text(
                                    text = reminder.timeOfDay.displayName,
                                    style = NSTypography.body,
                                    color = DesignTokens.Colors.textPrimary
                                )
                            }

                            Text(
                                text = reminder.formattedTime(),
                                style = NSTypography.body,
                                color = DesignTokens.Colors.textSecondary
                            )
                        }
                    }
                } else {
                    Text(
                        text = stringResource(R.string.medication_no_reminders_enabled),
                        style = NSTypography.body,
                        color = DesignTokens.Colors.textSecondary,
                        modifier = Modifier.padding(vertical = NSSpacing.sm)
                    )
                }
            } else {
                Text(
                    text = stringResource(R.string.medication_reminders_disabled),
                    style = NSTypography.body,
                    color = DesignTokens.Colors.textSecondary,
                    modifier = Modifier.padding(vertical = NSSpacing.sm)
                )
            }
        }
    }
}

/**
 * Time picker dialog
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerDialog(
    timeOfDay: TimeOfDay,
    initialHour: Int,
    initialMinute: Int,
    onTimeSelected: (hour: Int, minute: Int) -> Unit,
    onDismiss: () -> Unit
) {
    val timePickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = true
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "${stringResource(R.string.medication_reminder_time)} - ${timeOfDay.displayName}",
                style = NSTypography.heading3
            )
        },
        text = {
            TimePicker(
                state = timePickerState,
                colors = TimePickerDefaults.colors(
                    clockDialColor = DesignTokens.Colors.surface,
                    selectorColor = timeOfDay.getColor(),
                    timeSelectorSelectedContainerColor = timeOfDay.getColor(),
                    timeSelectorSelectedContentColor = androidx.compose.ui.graphics.Color.White
                )
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onTimeSelected(timePickerState.hour, timePickerState.minute)
                }
            ) {
                Text(
                    text = stringResource(R.string.common_confirm),
                    color = DesignTokens.Colors.primary
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.common_cancel),
                    color = DesignTokens.Colors.textSecondary
                )
            }
        },
        containerColor = DesignTokens.Colors.surface
    )
}

/**
 * Save settings and schedule reminders
 */
private fun saveSettings(
    medication: Medication,
    remindersEnabled: Boolean,
    customReminders: Map<TimeOfDay, MedicationReminder>,
    viewModel: MedicationViewModel,
    onDismiss: () -> Unit
) {
    // Create updated medication with new reminder settings
    val updatedMedication = medication.copy(
        remindersEnabled = remindersEnabled,
        customReminders = customReminders
    )

    // Save to ViewModel (which will persist and schedule/cancel reminders)
    viewModel.updateMedication(updatedMedication)

    println("💾 Reminders saved for ${medication.name}")
    println("🔔 Reminders enabled: $remindersEnabled")

    val enabledReminders = customReminders.values.filter { it.enabled }
    println("⏰ Enabled reminders: ${enabledReminders.size}")
    enabledReminders.forEach { reminder ->
        println("   ${reminder.timeOfDay.displayName} at ${reminder.formattedTime()}")
    }

    onDismiss()
}
