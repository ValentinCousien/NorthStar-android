package com.vcousien.northstar.features.settings.views

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vcousien.northstar.R
import com.vcousien.northstar.app.AppViewModel
import com.vcousien.northstar.core.designsystem.DesignTokens
import com.vcousien.northstar.core.designsystem.NSSpacing
import com.vcousien.northstar.core.designsystem.NSTypography
import com.vcousien.northstar.core.designsystem.components.NSCard
import com.vcousien.northstar.core.models.UserSettings
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalTime

/**
 * UserSettingsEditView - Comprehensive user settings edit screen
 *
 * Features:
 * - Profile information (Display name)
 * - Notifications settings (Daily reminders, Reminder time)
 * - Tracking preferences (Energy, Anxiety, Sleep, Irritability)
 * - Change detection and save/cancel handling
 * - Success toast on save
 * - Discard confirmation dialog
 * - Matches iOS UserSettingsEditView implementation
 *
 * @param appViewModel Main app ViewModel for settings management
 * @param onDismiss Callback when view is dismissed
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserSettingsEditView(
    appViewModel: AppViewModel = viewModel(),
    onDismiss: () -> Unit
) {
    val userSettings by appViewModel.userSettings.collectAsState()

    // Local state for editing
    var editedUserName by remember { mutableStateOf(userSettings.userName) }
    var editedNotificationsEnabled by remember { mutableStateOf(userSettings.notificationsEnabled) }
    var editedDailyReminderTime by remember { mutableStateOf(userSettings.dailyReminderTime) }
    var editedEnergyTrackingEnabled by remember { mutableStateOf(userSettings.energyTrackingEnabled) }
    var editedAnxietyTrackingEnabled by remember { mutableStateOf(userSettings.anxietyTrackingEnabled) }
    var editedSleepTrackingEnabled by remember { mutableStateOf(userSettings.sleepTrackingEnabled) }
    var editedIrritabilityTrackingEnabled by remember { mutableStateOf(userSettings.irritabilityTrackingEnabled) }

    // UI state
    var showDiscardDialog by remember { mutableStateOf(false) }
    var showSaveSuccess by remember { mutableStateOf(false) }
    var showTimePickerDialog by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    // Check if there are changes
    val hasChanges = remember(
        editedUserName,
        editedNotificationsEnabled,
        editedDailyReminderTime,
        editedEnergyTrackingEnabled,
        editedAnxietyTrackingEnabled,
        editedSleepTrackingEnabled,
        editedIrritabilityTrackingEnabled
    ) {
        editedUserName != userSettings.userName ||
                editedNotificationsEnabled != userSettings.notificationsEnabled ||
                editedDailyReminderTime != userSettings.dailyReminderTime ||
                editedEnergyTrackingEnabled != userSettings.energyTrackingEnabled ||
                editedAnxietyTrackingEnabled != userSettings.anxietyTrackingEnabled ||
                editedSleepTrackingEnabled != userSettings.sleepTrackingEnabled ||
                editedIrritabilityTrackingEnabled != userSettings.irritabilityTrackingEnabled
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.user_settings_edit_title),
                        style = NSTypography.heading2
                    )
                },
                navigationIcon = {
                    TextButton(
                        onClick = {
                            if (hasChanges) {
                                showDiscardDialog = true
                            } else {
                                onDismiss()
                            }
                        }
                    ) {
                        Text(
                            text = stringResource(R.string.common_cancel),
                            color = if (hasChanges) DesignTokens.Colors.error else DesignTokens.Colors.textSecondary
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            // Save changes
                            val updatedSettings = userSettings.copy(
                                userName = editedUserName,
                                notificationsEnabled = editedNotificationsEnabled,
                                dailyReminderTime = editedDailyReminderTime,
                                energyTrackingEnabled = editedEnergyTrackingEnabled,
                                anxietyTrackingEnabled = editedAnxietyTrackingEnabled,
                                sleepTrackingEnabled = editedSleepTrackingEnabled,
                                irritabilityTrackingEnabled = editedIrritabilityTrackingEnabled
                            )
                            appViewModel.updateUserSettings(updatedSettings)

                            // Show success and dismiss
                            scope.launch {
                                showSaveSuccess = true
                                delay(1500)
                                onDismiss()
                            }
                        },
                        enabled = hasChanges
                    ) {
                        Text(
                            text = stringResource(R.string.common_save),
                            style = NSTypography.bodyBold,
                            color = if (hasChanges) DesignTokens.Colors.primary else DesignTokens.Colors.textSecondary
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
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = NSSpacing.screenEdge)
            ) {
                Spacer(modifier = Modifier.height(NSSpacing.md))

                // Profile Section
                ProfileSection(
                    userName = editedUserName,
                    onUserNameChange = { editedUserName = it }
                )

                Spacer(modifier = Modifier.height(NSSpacing.lg))

                // Notifications Section
                NotificationsSection(
                    notificationsEnabled = editedNotificationsEnabled,
                    onNotificationsEnabledChange = { editedNotificationsEnabled = it },
                    reminderTime = editedDailyReminderTime,
                    onReminderTimeClick = { showTimePickerDialog = true }
                )

                Spacer(modifier = Modifier.height(NSSpacing.lg))

                // Tracking Preferences Section
                TrackingPreferencesSection(
                    energyTrackingEnabled = editedEnergyTrackingEnabled,
                    onEnergyTrackingChange = { editedEnergyTrackingEnabled = it },
                    anxietyTrackingEnabled = editedAnxietyTrackingEnabled,
                    onAnxietyTrackingChange = { editedAnxietyTrackingEnabled = it },
                    sleepTrackingEnabled = editedSleepTrackingEnabled,
                    onSleepTrackingChange = { editedSleepTrackingEnabled = it },
                    irritabilityTrackingEnabled = editedIrritabilityTrackingEnabled,
                    onIrritabilityTrackingChange = { editedIrritabilityTrackingEnabled = it }
                )

                Spacer(modifier = Modifier.height(NSSpacing.xxl))
            }

            // Success Toast
            AnimatedVisibility(
                visible = showSaveSuccess,
                enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = NSSpacing.md)
            ) {
                SuccessToast()
            }
        }
    }

    // Discard Changes Dialog
    if (showDiscardDialog) {
        AlertDialog(
            onDismissRequest = { showDiscardDialog = false },
            title = {
                Text(
                    text = stringResource(R.string.user_settings_discard_title),
                    style = NSTypography.heading3
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.user_settings_discard_message),
                    style = NSTypography.body
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDiscardDialog = false
                        onDismiss()
                    }
                ) {
                    Text(
                        text = stringResource(R.string.user_settings_discard_action),
                        color = DesignTokens.Colors.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDiscardDialog = false }) {
                    Text(text = stringResource(R.string.common_cancel))
                }
            },
            containerColor = DesignTokens.Colors.cardBackground
        )
    }

    // Time Picker Dialog
    if (showTimePickerDialog) {
        TimePickerDialog(
            initialTime = editedDailyReminderTime,
            onTimeSelected = { hour, minute ->
                editedDailyReminderTime = LocalTime(hour, minute)
                showTimePickerDialog = false
            },
            onDismiss = { showTimePickerDialog = false }
        )
    }
}

/**
 * Profile Section
 */
@Composable
private fun ProfileSection(
    userName: String,
    onUserNameChange: (String) -> Unit
) {
    NSCard {
        Column(
            modifier = Modifier.padding(NSSpacing.md),
            verticalArrangement = Arrangement.spacedBy(NSSpacing.lg)
        ) {
            SectionHeader(
                title = stringResource(R.string.user_settings_profile_section),
                icon = Icons.Default.AccountCircle,
                iconColor = DesignTokens.Colors.primary
            )

            Column(verticalArrangement = Arrangement.spacedBy(NSSpacing.md)) {
                // Display Name field
                Column(verticalArrangement = Arrangement.spacedBy(NSSpacing.xs)) {
                    Text(
                        text = stringResource(R.string.user_settings_display_name),
                        style = NSTypography.captionBold,
                        color = DesignTokens.Colors.textSecondary
                    )

                    OutlinedTextField(
                        value = userName,
                        onValueChange = onUserNameChange,
                        placeholder = {
                            Text(stringResource(R.string.user_settings_name_placeholder))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DesignTokens.Colors.primary,
                            unfocusedBorderColor = DesignTokens.Colors.border,
                            focusedContainerColor = DesignTokens.Colors.surface,
                            unfocusedContainerColor = DesignTokens.Colors.surface
                        )
                    )
                }

                // Helper text
                Text(
                    text = stringResource(R.string.user_settings_name_helper),
                    style = NSTypography.caption,
                    color = DesignTokens.Colors.textSecondary
                )
            }
        }
    }
}

/**
 * Notifications Section
 */
@Composable
private fun NotificationsSection(
    notificationsEnabled: Boolean,
    onNotificationsEnabledChange: (Boolean) -> Unit,
    reminderTime: LocalTime,
    onReminderTimeClick: () -> Unit
) {
    NSCard {
        Column(
            modifier = Modifier.padding(NSSpacing.md),
            verticalArrangement = Arrangement.spacedBy(NSSpacing.lg)
        ) {
            SectionHeader(
                title = stringResource(R.string.user_settings_notifications_section),
                icon = Icons.Default.Notifications,
                iconColor = DesignTokens.Colors.warning
            )

            Column(verticalArrangement = Arrangement.spacedBy(NSSpacing.md)) {
                // Notifications toggle
                SettingsToggleRow(
                    icon = Icons.Default.Notifications,
                    iconColor = DesignTokens.Colors.warning,
                    title = stringResource(R.string.user_settings_daily_reminders),
                    description = stringResource(R.string.user_settings_daily_reminders_description),
                    isOn = notificationsEnabled,
                    onToggle = onNotificationsEnabledChange
                )

                if (notificationsEnabled) {
                    HorizontalDivider(color = DesignTokens.Colors.border)

                    // Reminder time picker
                    Column(
                        modifier = Modifier.padding(top = NSSpacing.sm),
                        verticalArrangement = Arrangement.spacedBy(NSSpacing.sm)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(onClick = onReminderTimeClick)
                                .padding(vertical = NSSpacing.sm),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(NSSpacing.md)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = DesignTokens.Colors.info,
                                modifier = Modifier.size(24.dp)
                            )

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = stringResource(R.string.user_settings_reminder_time),
                                    style = NSTypography.bodyBold,
                                    color = DesignTokens.Colors.textPrimary
                                )
                                Text(
                                    text = stringResource(R.string.user_settings_reminder_time_description),
                                    style = NSTypography.caption,
                                    color = DesignTokens.Colors.textSecondary
                                )
                            }

                            Text(
                                text = String.format("%02d:%02d", reminderTime.hour, reminderTime.minute),
                                style = NSTypography.bodyBold,
                                color = DesignTokens.Colors.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Tracking Preferences Section
 */
@Composable
private fun TrackingPreferencesSection(
    energyTrackingEnabled: Boolean,
    onEnergyTrackingChange: (Boolean) -> Unit,
    anxietyTrackingEnabled: Boolean,
    onAnxietyTrackingChange: (Boolean) -> Unit,
    sleepTrackingEnabled: Boolean,
    onSleepTrackingChange: (Boolean) -> Unit,
    irritabilityTrackingEnabled: Boolean,
    onIrritabilityTrackingChange: (Boolean) -> Unit
) {
    NSCard {
        Column(
            modifier = Modifier.padding(NSSpacing.md),
            verticalArrangement = Arrangement.spacedBy(NSSpacing.lg)
        ) {
            SectionHeader(
                title = stringResource(R.string.user_settings_tracking_section),
                icon = Icons.Default.TrendingUp,
                iconColor = DesignTokens.Colors.info
            )

            Column(verticalArrangement = Arrangement.spacedBy(NSSpacing.sm)) {
                Text(
                    text = stringResource(R.string.user_settings_tracking_description),
                    style = NSTypography.body,
                    color = DesignTokens.Colors.textSecondary,
                    modifier = Modifier.padding(bottom = NSSpacing.xs)
                )

                SettingsToggleRow(
                    icon = Icons.Default.BatteryChargingFull,
                    iconColor = Color(0xFF4CAF50), // Green
                    title = stringResource(R.string.user_settings_energy_level),
                    description = stringResource(R.string.user_settings_energy_description),
                    isOn = energyTrackingEnabled,
                    onToggle = onEnergyTrackingChange
                )

                HorizontalDivider(color = DesignTokens.Colors.border)

                SettingsToggleRow(
                    icon = Icons.Default.Favorite,
                    iconColor = Color(0xFFFF9800), // Orange
                    title = stringResource(R.string.user_settings_anxiety_level),
                    description = stringResource(R.string.user_settings_anxiety_description),
                    isOn = anxietyTrackingEnabled,
                    onToggle = onAnxietyTrackingChange
                )

                HorizontalDivider(color = DesignTokens.Colors.border)

                SettingsToggleRow(
                    icon = Icons.Default.Bedtime,
                    iconColor = Color(0xFF9C27B0), // Purple
                    title = stringResource(R.string.user_settings_sleep_tracking),
                    description = stringResource(R.string.user_settings_sleep_description),
                    isOn = sleepTrackingEnabled,
                    onToggle = onSleepTrackingChange
                )

                HorizontalDivider(color = DesignTokens.Colors.border)

                SettingsToggleRow(
                    icon = Icons.Default.Fireplace,
                    iconColor = Color(0xFFF44336), // Red
                    title = stringResource(R.string.user_settings_irritability_level),
                    description = stringResource(R.string.user_settings_irritability_description),
                    isOn = irritabilityTrackingEnabled,
                    onToggle = onIrritabilityTrackingChange
                )
            }
        }
    }
}

/**
 * Section Header
 */
@Composable
private fun SectionHeader(
    title: String,
    icon: ImageVector,
    iconColor: Color
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(NSSpacing.md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(28.dp)
        )

        Text(
            text = title,
            style = NSTypography.heading3,
            color = DesignTokens.Colors.textPrimary
        )
    }
}

/**
 * Settings Toggle Row
 */
@Composable
private fun SettingsToggleRow(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    description: String,
    isOn: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle(!isOn) }
            .padding(vertical = NSSpacing.sm),
        horizontalArrangement = Arrangement.spacedBy(NSSpacing.md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(24.dp)
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = NSTypography.bodyBold,
                color = DesignTokens.Colors.textPrimary
            )
            Text(
                text = description,
                style = NSTypography.caption,
                color = DesignTokens.Colors.textSecondary
            )
        }

        Switch(
            checked = isOn,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = iconColor,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = DesignTokens.Colors.textSecondary.copy(alpha = 0.3f)
            )
        )
    }
}

/**
 * Success Toast
 */
@Composable
private fun SuccessToast() {
    Row(
        modifier = Modifier
            .padding(horizontal = NSSpacing.md)
            .background(
                color = DesignTokens.Colors.success.copy(alpha = 0.1f),
                shape = RoundedCornerShape(NSSpacing.md)
            )
            .border(
                width = 1.dp,
                color = DesignTokens.Colors.success.copy(alpha = 0.3f),
                shape = RoundedCornerShape(NSSpacing.md)
            )
            .padding(NSSpacing.md),
        horizontalArrangement = Arrangement.spacedBy(NSSpacing.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = DesignTokens.Colors.success
        )
        Text(
            text = stringResource(R.string.user_settings_save_success),
            style = NSTypography.bodyBold,
            color = DesignTokens.Colors.success
        )
    }
}

/**
 * Time Picker Dialog
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerDialog(
    initialTime: LocalTime,
    onTimeSelected: (Int, Int) -> Unit,
    onDismiss: () -> Unit
) {
    val timePickerState = rememberTimePickerState(
        initialHour = initialTime.hour,
        initialMinute = initialTime.minute,
        is24Hour = true
    )

    AlertDialog(
        onDismissRequest = onDismiss
    ) {
        Surface(
            shape = MaterialTheme.shapes.large,
            color = DesignTokens.Colors.cardBackground
        ) {
            Column(
                modifier = Modifier.padding(NSSpacing.lg),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.user_settings_select_time),
                    style = NSTypography.heading3,
                    modifier = Modifier.padding(bottom = NSSpacing.md)
                )

                TimePicker(
                    state = timePickerState,
                    colors = TimePickerDefaults.colors(
                        clockDialColor = DesignTokens.Colors.background,
                        selectorColor = DesignTokens.Colors.primary
                    )
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = NSSpacing.lg),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(R.string.common_cancel))
                    }

                    Spacer(modifier = Modifier.width(NSSpacing.md))

                    TextButton(
                        onClick = {
                            onTimeSelected(timePickerState.hour, timePickerState.minute)
                        }
                    ) {
                        Text(
                            text = stringResource(R.string.common_ok),
                            color = DesignTokens.Colors.primary
                        )
                    }
                }
            }
        }
    }
}
