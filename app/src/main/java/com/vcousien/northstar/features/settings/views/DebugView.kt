package com.vcousien.northstar.features.settings.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vcousien.northstar.R
import com.vcousien.northstar.app.AppViewModel
import com.vcousien.northstar.core.designsystem.DesignTokens
import com.vcousien.northstar.core.designsystem.NSSpacing
import com.vcousien.northstar.core.designsystem.NSTypography
import com.vcousien.northstar.features.medication.viewmodels.MedicationViewModel
import com.vcousien.northstar.features.mood.viewmodels.MoodTrackingViewModel
import com.vcousien.northstar.features.sleep.viewmodels.SleepTrackingViewModel

/**
 * DebugView - Development and testing debug console
 *
 * Features:
 * - App state display (First launch, Authentication, Security, Onboarding)
 * - Data status (Entry counts for Sleep, Mood, Medications)
 * - Reset controls (Authentication, Onboarding, All data)
 * - Simplified for Android (no CloudKit purge functions)
 * - Matches iOS DebugView implementation
 *
 * @param appViewModel Main app ViewModel
 * @param moodViewModel Mood tracking ViewModel
 * @param sleepViewModel Sleep tracking ViewModel
 * @param medicationViewModel Medication ViewModel
 * @param onDismiss Callback when view is dismissed
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebugView(
    appViewModel: AppViewModel = viewModel(),
    moodViewModel: MoodTrackingViewModel = viewModel(),
    sleepViewModel: SleepTrackingViewModel = viewModel(),
    medicationViewModel: MedicationViewModel = viewModel(),
    onDismiss: () -> Unit
) {
    val userSettings by appViewModel.userSettings.collectAsState()
    val sleepEntries by sleepViewModel.sleepEntries.collectAsState()
    val moodEntries by moodViewModel.moodEntries.collectAsState()
    val medications by medicationViewModel.medications.collectAsState()
    val medicationEntries by medicationViewModel.medicationEntries.collectAsState()

    var showResetOnboardingDialog by remember { mutableStateOf(false) }
    var showClearAllDataDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.debug_title),
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
                .padding(NSSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(NSSpacing.lg)
        ) {
            // Header
            Text(
                text = "\uD83D\uDD27 ${stringResource(R.string.debug_console)}",
                style = NSTypography.heading1,
                color = DesignTokens.Colors.textPrimary
            )

            // App State Section
            DebugSection(
                title = stringResource(R.string.debug_app_state),
                backgroundColor = Color.Gray.copy(alpha = 0.1f)
            ) {
                DebugInfoRow(
                    label = stringResource(R.string.debug_first_launch),
                    value = if (appViewModel.isFirstLaunch) "Yes" else "No"
                )
                DebugInfoRow(
                    label = stringResource(R.string.debug_authenticated),
                    value = if (appViewModel.isAuthenticated) "Yes" else "No"
                )
                DebugInfoRow(
                    label = stringResource(R.string.debug_security_enabled),
                    value = if (userSettings.securityEnabled) "Yes" else "No"
                )
                DebugInfoRow(
                    label = stringResource(R.string.debug_onboarding_complete),
                    value = if (userSettings.hasCompletedOnboarding) "Yes" else "No"
                )
            }

            // Data Status Section
            DebugSection(
                title = stringResource(R.string.debug_data_status),
                backgroundColor = Color.Blue.copy(alpha = 0.1f)
            ) {
                DebugInfoRow(
                    label = stringResource(R.string.debug_sleep_entries),
                    value = sleepEntries.size.toString()
                )
                DebugInfoRow(
                    label = stringResource(R.string.debug_mood_entries),
                    value = moodEntries.size.toString()
                )
                DebugInfoRow(
                    label = stringResource(R.string.debug_medications),
                    value = medications.size.toString()
                )
                DebugInfoRow(
                    label = stringResource(R.string.debug_medication_entries),
                    value = medicationEntries.size.toString()
                )
            }

            // User Settings Section
            DebugSection(
                title = stringResource(R.string.debug_user_settings),
                backgroundColor = Color.Green.copy(alpha = 0.1f)
            ) {
                DebugInfoRow(
                    label = stringResource(R.string.debug_user_name),
                    value = userSettings.userName.ifEmpty { "Not set" }
                )
                DebugInfoRow(
                    label = stringResource(R.string.debug_notifications_enabled),
                    value = if (userSettings.notificationsEnabled) "Yes" else "No"
                )
                DebugInfoRow(
                    label = stringResource(R.string.debug_energy_tracking),
                    value = if (userSettings.energyTrackingEnabled) "Yes" else "No"
                )
                DebugInfoRow(
                    label = stringResource(R.string.debug_anxiety_tracking),
                    value = if (userSettings.anxietyTrackingEnabled) "Yes" else "No"
                )
                DebugInfoRow(
                    label = stringResource(R.string.debug_sleep_tracking),
                    value = if (userSettings.sleepTrackingEnabled) "Yes" else "No"
                )
                DebugInfoRow(
                    label = stringResource(R.string.debug_irritability_tracking),
                    value = if (userSettings.irritabilityTrackingEnabled) "Yes" else "No"
                )
            }

            // App Controls Section
            Text(
                text = stringResource(R.string.debug_app_controls),
                style = NSTypography.heading3,
                fontWeight = FontWeight.Bold,
                color = DesignTokens.Colors.textPrimary
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(NSSpacing.sm)
            ) {
                // Reset Authentication
                Button(
                    onClick = {
                        appViewModel.completeAuthentication()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DesignTokens.Colors.primary
                    )
                ) {
                    Text(stringResource(R.string.debug_complete_authentication))
                }

                // Reset Onboarding
                OutlinedButton(
                    onClick = { showResetOnboardingDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.debug_reset_onboarding))
                }

                // Clear All Data
                Button(
                    onClick = { showClearAllDataDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DesignTokens.Colors.error
                    )
                ) {
                    Text(stringResource(R.string.debug_clear_all_data))
                }
            }

            Spacer(modifier = Modifier.height(NSSpacing.xl))
        }
    }

    // Reset Onboarding Dialog
    if (showResetOnboardingDialog) {
        AlertDialog(
            onDismissRequest = { showResetOnboardingDialog = false },
            title = {
                Text(
                    text = stringResource(R.string.debug_reset_onboarding),
                    style = NSTypography.heading3
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.debug_reset_onboarding_message),
                    style = NSTypography.body
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        appViewModel.resetOnboarding()
                        showResetOnboardingDialog = false
                    }
                ) {
                    Text(
                        text = stringResource(R.string.debug_reset),
                        color = DesignTokens.Colors.warning
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetOnboardingDialog = false }) {
                    Text(text = stringResource(R.string.common_cancel))
                }
            },
            containerColor = DesignTokens.Colors.cardBackground
        )
    }

    // Clear All Data Dialog
    if (showClearAllDataDialog) {
        AlertDialog(
            onDismissRequest = { showClearAllDataDialog = false },
            title = {
                Text(
                    text = stringResource(R.string.debug_clear_all_data),
                    style = NSTypography.heading3
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.debug_clear_all_data_message),
                    style = NSTypography.body
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        appViewModel.clearAllData()
                        showClearAllDataDialog = false
                        onDismiss()
                    }
                ) {
                    Text(
                        text = stringResource(R.string.debug_clear),
                        color = DesignTokens.Colors.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearAllDataDialog = false }) {
                    Text(text = stringResource(R.string.common_cancel))
                }
            },
            containerColor = DesignTokens.Colors.cardBackground
        )
    }
}

/**
 * Debug Section Container
 */
@Composable
private fun DebugSection(
    title: String,
    backgroundColor: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(NSSpacing.md),
        verticalArrangement = Arrangement.spacedBy(NSSpacing.sm)
    ) {
        Text(
            text = title,
            style = NSTypography.heading3,
            fontWeight = FontWeight.Bold,
            color = DesignTokens.Colors.textPrimary,
            modifier = Modifier.padding(bottom = NSSpacing.xs)
        )

        content()
    }
}

/**
 * Debug Info Row
 */
@Composable
private fun DebugInfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = NSTypography.body,
            color = DesignTokens.Colors.textPrimary
        )
        Text(
            text = value,
            style = NSTypography.bodyBold,
            color = DesignTokens.Colors.primary
        )
    }
}
