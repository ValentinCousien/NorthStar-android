package com.vcousien.northstar.features.sleep.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
import com.vcousien.northstar.core.designsystem.components.NSPrimaryButton
import com.vcousien.northstar.core.designsystem.components.NSSlider
import com.vcousien.northstar.core.models.SleepEntry
import com.vcousien.northstar.features.sleep.viewmodels.SleepTrackingViewModel
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.text.SimpleDateFormat
import java.util.*
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

/**
 * AddSleepEntryView - View for adding or editing sleep entries
 *
 * Features:
 * - Date/time pickers for bedtime and wake time
 * - Quality slider (0-10 scale) with star visualization
 * - Interruptions stepper
 * - Notes text field
 * - Real-time duration calculation with validation
 * - Warning for unusual durations
 * - Form validation
 * - Matches iOS AddSleepEntryView implementation
 *
 * @param viewModel SleepTrackingViewModel for sleep data management
 * @param onDismiss Callback when the view is dismissed
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSleepEntryView(
    viewModel: SleepTrackingViewModel = viewModel(),
    onDismiss: () -> Unit = {}
) {
    val today = Clock.System.now()
    val existingEntry = viewModel.getEntry(today)

    // State variables
    var startTime by remember {
        mutableStateOf(existingEntry?.startTimeAsInstant ?: viewModel.currentEntry.startTimeAsInstant)
    }
    var endTime by remember {
        mutableStateOf(existingEntry?.endTimeAsInstant ?: viewModel.currentEntry.endTimeAsInstant)
    }
    var quality by remember {
        mutableStateOf(existingEntry?.quality ?: 5.0)
    }
    var interruptions by remember {
        mutableStateOf(existingEntry?.interruptions ?: 0)
    }
    var notes by remember {
        mutableStateOf(existingEntry?.notes ?: "")
    }

    // Show date/time pickers
    var showStartDateTimePicker by remember { mutableStateOf(false) }
    var showEndDateTimePicker by remember { mutableStateOf(false) }

    // Calculate duration and validate
    val duration = calculateDuration(startTime, endTime)
    val durationHours = duration.inWholeSeconds / 3600.0
    val showDurationWarning = durationHours > 16 && durationHours <= 24
    val isFormValid = durationHours > 0 && durationHours <= 24

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (existingEntry != null)
                            stringResource(R.string.sleep_add_entry)
                        else
                            stringResource(R.string.sleep_add_sleep_title),
                        style = NSTypography.heading2
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.common_cancel)
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
            // Sleep Times Section
            Text(
                text = stringResource(R.string.sleep_sleep_times_header),
                style = NSTypography.heading3,
                color = DesignTokens.Colors.textPrimary,
                modifier = Modifier.padding(bottom = NSSpacing.md)
            )

            // Bedtime picker
            SleepTimePickerButton(
                label = stringResource(R.string.sleep_bedtime_label),
                time = startTime,
                onClick = { showStartDateTimePicker = true }
            )

            Spacer(modifier = Modifier.height(NSSpacing.md))

            // Wake time picker
            SleepTimePickerButton(
                label = stringResource(R.string.sleep_wake_time_label),
                time = endTime,
                onClick = { showEndDateTimePicker = true }
            )

            // Duration display
            if (durationHours > 0) {
                Spacer(modifier = Modifier.height(NSSpacing.md))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Duration:",
                        style = NSTypography.caption,
                        color = DesignTokens.Colors.textSecondary
                    )
                    Text(
                        text = formatDuration(duration),
                        style = NSTypography.bodyBold,
                        color = durationColor(durationHours)
                    )
                }
            }

            // Warning for unusual duration
            if (showDurationWarning) {
                Spacer(modifier = Modifier.height(NSSpacing.sm))
                Text(
                    text = "⚠️ This sleep duration seems unusual (over 16 hours). Please verify your dates.",
                    style = NSTypography.caption,
                    color = DesignTokens.Colors.warning,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(NSSpacing.xl))

            // Quality Section
            Text(
                text = stringResource(R.string.sleep_quality_header),
                style = NSTypography.heading3,
                color = DesignTokens.Colors.textPrimary,
                modifier = Modifier.padding(bottom = NSSpacing.md)
            )

            Text(
                text = stringResource(R.string.sleep_quality_question),
                style = NSTypography.body,
                color = DesignTokens.Colors.textPrimary,
                modifier = Modifier.padding(bottom = NSSpacing.sm)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.sleep_quality_poor_label),
                    style = NSTypography.caption,
                    color = DesignTokens.Colors.textSecondary
                )
                Text(
                    text = stringResource(R.string.sleep_quality_excellent_label),
                    style = NSTypography.caption,
                    color = DesignTokens.Colors.textSecondary
                )
            }

            NSSlider(
                title = stringResource(R.string.sleep_quality_label),
                value = quality.toFloat(),
                onValueChange = { quality = it.toDouble() },
                range = 0f..10f,
                steps = 9,
                modifier = Modifier.padding(vertical = NSSpacing.sm)
            )

            // Star rating visualization
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(5) { index ->
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = if (index < (quality / 2).toInt()) {
                            DesignTokens.Colors.primary
                        } else {
                            DesignTokens.Colors.border
                        },
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(NSSpacing.lg))

            // Interruptions stepper
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${stringResource(R.string.sleep_interruptions_label)}: $interruptions",
                    style = NSTypography.body,
                    color = DesignTokens.Colors.textPrimary
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(NSSpacing.sm)
                ) {
                    Button(
                        onClick = { if (interruptions > 0) interruptions-- },
                        enabled = interruptions > 0,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DesignTokens.Colors.cardBackground
                        )
                    ) {
                        Text("-", style = NSTypography.heading2)
                    }

                    Button(
                        onClick = { if (interruptions < 10) interruptions++ },
                        enabled = interruptions < 10,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DesignTokens.Colors.cardBackground
                        )
                    ) {
                        Text("+", style = NSTypography.heading2)
                    }
                }
            }

            Spacer(modifier = Modifier.height(NSSpacing.xl))

            // Notes Section
            Text(
                text = stringResource(R.string.sleep_notes_header),
                style = NSTypography.heading3,
                color = DesignTokens.Colors.textPrimary,
                modifier = Modifier.padding(bottom = NSSpacing.md)
            )

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                textStyle = NSTypography.body,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = DesignTokens.Colors.primary,
                    unfocusedBorderColor = DesignTokens.Colors.border,
                    focusedTextColor = DesignTokens.Colors.textPrimary,
                    unfocusedTextColor = DesignTokens.Colors.textPrimary
                ),
                placeholder = {
                    Text(
                        text = "Optional notes about your sleep...",
                        style = NSTypography.body,
                        color = DesignTokens.Colors.textSecondary
                    )
                }
            )

            Spacer(modifier = Modifier.height(NSSpacing.xl))

            // Save button
            NSPrimaryButton(
                title = stringResource(R.string.sleep_save_button),
                onClick = {
                    saveSleepEntry(
                        viewModel = viewModel,
                        existingEntry = existingEntry,
                        startTime = startTime,
                        endTime = endTime,
                        quality = quality,
                        interruptions = interruptions,
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

    // Date/Time Pickers
    if (showStartDateTimePicker) {
        SimpleDateTimePicker(
            initialTime = startTime,
            onConfirm = { newTime ->
                startTime = newTime
                showStartDateTimePicker = false
            },
            onDismiss = { showStartDateTimePicker = false }
        )
    }

    if (showEndDateTimePicker) {
        SimpleDateTimePicker(
            initialTime = endTime,
            onConfirm = { newTime ->
                endTime = newTime
                showEndDateTimePicker = false
            },
            onDismiss = { showEndDateTimePicker = false }
        )
    }
}

/**
 * Sleep time picker button component
 */
@Composable
private fun SleepTimePickerButton(
    label: String,
    time: Instant,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = DesignTokens.Colors.cardBackground
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = NSTypography.body,
                color = DesignTokens.Colors.textPrimary
            )
            Text(
                text = formatDateTime(time),
                style = NSTypography.bodyBold,
                color = DesignTokens.Colors.primary
            )
        }
    }
}

/**
 * Material3 Date and Time Picker Dialog
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SimpleDateTimePicker(
    initialTime: Instant,
    onConfirm: (Instant) -> Unit,
    onDismiss: () -> Unit
) {
    val timeZone = TimeZone.currentSystemDefault()
    val localDateTime = initialTime.toLocalDateTime(timeZone)
    val now = Clock.System.now()

    var selectedDate by remember { mutableStateOf(localDateTime.date) }
    var showDatePicker by remember { mutableStateOf(false) }

    val timePickerState = rememberTimePickerState(
        initialHour = localDateTime.hour,
        initialMinute = localDateTime.minute,
        is24Hour = false
    )

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = localDateTime.date.toEpochDays() * 24L * 60 * 60 * 1000,
        yearRange = IntRange(2020, now.toLocalDateTime(timeZone).year),
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                // Only allow dates up to today
                return utcTimeMillis <= now.toEpochMilliseconds()
            }

            override fun isSelectableYear(year: Int): Boolean {
                return year <= now.toLocalDateTime(timeZone).year
            }
        }
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth()
    ) {
        Surface(
            shape = MaterialTheme.shapes.large,
            tonalElevation = 6.dp,
            color = DesignTokens.Colors.cardBackground
        ) {
            Column(
                modifier = Modifier.padding(NSSpacing.lg),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Select Date & Time",
                    style = NSTypography.heading3,
                    color = DesignTokens.Colors.textPrimary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = NSSpacing.md)
                )

                // Date selector button
                OutlinedButton(
                    onClick = { showDatePicker = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = NSSpacing.md),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = DesignTokens.Colors.background
                    )
                ) {
                    Text(
                        text = formatDateForDisplay(selectedDate),
                        style = NSTypography.body,
                        color = DesignTokens.Colors.textPrimary
                    )
                }

                TimePicker(
                    state = timePickerState,
                    colors = TimePickerDefaults.colors(
                        clockDialColor = DesignTokens.Colors.background,
                        selectorColor = DesignTokens.Colors.primary,
                        containerColor = DesignTokens.Colors.cardBackground,
                        periodSelectorBorderColor = DesignTokens.Colors.border,
                        clockDialSelectedContentColor = DesignTokens.Colors.onPrimary,
                        clockDialUnselectedContentColor = DesignTokens.Colors.textPrimary,
                        periodSelectorSelectedContainerColor = DesignTokens.Colors.primary,
                        periodSelectorUnselectedContainerColor = DesignTokens.Colors.background,
                        periodSelectorSelectedContentColor = DesignTokens.Colors.onPrimary,
                        periodSelectorUnselectedContentColor = DesignTokens.Colors.textPrimary,
                        timeSelectorSelectedContainerColor = DesignTokens.Colors.primary,
                        timeSelectorUnselectedContainerColor = DesignTokens.Colors.background,
                        timeSelectorSelectedContentColor = DesignTokens.Colors.onPrimary,
                        timeSelectorUnselectedContentColor = DesignTokens.Colors.textPrimary
                    )
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = NSSpacing.lg),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(
                            "Cancel",
                            color = DesignTokens.Colors.textPrimary
                        )
                    }

                    Spacer(modifier = Modifier.width(NSSpacing.md))

                    TextButton(onClick = {
                        // Create new Instant with selected date and time
                        val dayStart = selectedDate.toEpochDays() * 24L * 60 * 60 * 1000
                        val newTime = Instant.fromEpochMilliseconds(dayStart) +
                                timePickerState.hour.hours + timePickerState.minute.minutes

                        // Validate that the time is not in the future
                        if (newTime <= now) {
                            onConfirm(newTime)
                        }
                    }) {
                        Text(
                            "OK",
                            color = DesignTokens.Colors.primary
                        )
                    }
                }
            }
        }
    }

    // Date Picker Dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        selectedDate = kotlinx.datetime.Instant.fromEpochMilliseconds(millis)
                            .toLocalDateTime(timeZone).date
                    }
                    showDatePicker = false
                }) {
                    Text("OK", color = DesignTokens.Colors.primary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel", color = DesignTokens.Colors.textPrimary)
                }
            },
            colors = DatePickerDefaults.colors(
                containerColor = DesignTokens.Colors.cardBackground
            )
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    containerColor = DesignTokens.Colors.cardBackground,
                    selectedDayContainerColor = DesignTokens.Colors.primary,
                    todayContentColor = DesignTokens.Colors.primary,
                    todayDateBorderColor = DesignTokens.Colors.primary
                )
            )
        }
    }
}

/**
 * Format date for display
 */
private fun formatDateForDisplay(date: kotlinx.datetime.LocalDate): String {
    val formatter = SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault())
    val calendar = Calendar.getInstance().apply {
        set(Calendar.YEAR, date.year)
        set(Calendar.MONTH, date.monthNumber - 1)
        set(Calendar.DAY_OF_MONTH, date.dayOfMonth)
    }
    return formatter.format(calendar.time)
}

/**
 * Calculate sleep duration handling overnight sleep
 */
private fun calculateDuration(startTime: Instant, endTime: Instant): kotlin.time.Duration {
    val duration = endTime - startTime
    return if (duration.isNegative()) {
        duration + 24.hours
    } else {
        duration
    }
}

/**
 * Format duration for display
 */
private fun formatDuration(duration: kotlin.time.Duration): String {
    val hours = duration.inWholeHours
    val minutes = (duration.inWholeMinutes % 60)
    return "${hours}h ${minutes}min"
}

/**
 * Get color based on duration reasonableness
 */
@Composable
private fun durationColor(durationHours: Double): androidx.compose.ui.graphics.Color {
    return when {
        durationHours < 0 -> DesignTokens.Colors.error
        durationHours > 16 -> DesignTokens.Colors.warning
        else -> DesignTokens.Colors.primary
    }
}

/**
 * Format date and time for display
 */
private fun formatDateTime(instant: Instant): String {
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    val calendar = Calendar.getInstance().apply {
        set(Calendar.YEAR, localDateTime.year)
        set(Calendar.MONTH, localDateTime.monthNumber - 1)
        set(Calendar.DAY_OF_MONTH, localDateTime.dayOfMonth)
        set(Calendar.HOUR_OF_DAY, localDateTime.hour)
        set(Calendar.MINUTE, localDateTime.minute)
    }

    val formatter = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())
    return formatter.format(calendar.time)
}

/**
 * Save the sleep entry
 */
private fun saveSleepEntry(
    viewModel: SleepTrackingViewModel,
    existingEntry: SleepEntry?,
    startTime: Instant,
    endTime: Instant,
    quality: Double,
    interruptions: Int,
    notes: String
) {
    val entry = SleepEntry(
        date = Clock.System.now(),
        startTime = startTime,
        endTime = endTime,
        quality = quality,
        interruptions = interruptions,
        notes = notes
    )

    if (existingEntry != null) {
        // Update existing entry
        viewModel.updateEntry(entry.copy(id = existingEntry.id))
    } else {
        // Add new entry
        viewModel.addEntry(entry)
    }
}
