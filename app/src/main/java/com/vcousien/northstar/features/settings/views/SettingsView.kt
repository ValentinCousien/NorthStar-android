package com.vcousien.northstar.features.settings.views

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vcousien.northstar.R
import com.vcousien.northstar.app.AppViewModel
import com.vcousien.northstar.core.authentication.SecurityToggleRow
import com.vcousien.northstar.core.designsystem.DesignTokens
import com.vcousien.northstar.core.designsystem.NSSpacing
import com.vcousien.northstar.core.designsystem.NSTypography
import com.vcousien.northstar.core.designsystem.components.NSCard
import com.vcousien.northstar.core.storage.UserSettingsManager
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Hilt EntryPoint to access UserSettingsManager from Composable
 */
@EntryPoint
@InstallIn(SingletonComponent::class)
interface UserSettingsManagerEntryPoint {
    fun userSettingsManager(): UserSettingsManager
}

/**
 * SettingsView - Main settings screen for the application
 *
 * Features:
 * - User profile section with editable name
 * - Data & Sync settings (Cloud sync, Export)
 * - Security settings (Biometric authentication toggle)
 * - Application settings (Insights, Notifications, Reminders)
 * - Support section (Clear all data)
 * - App info (Version, Build)
 * - Hidden debug mode (tap logo 7 times)
 * - Matches iOS SettingsView implementation
 *
 * @param appViewModel Main app ViewModel for settings management
 * @param onNavigateToUserSettings Callback to navigate to user settings edit screen
 * @param onNavigateToInsights Callback to navigate to insights screen
 * @param onNavigateToDebug Callback to navigate to debug screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsView(
    appViewModel: AppViewModel = viewModel(),
    onNavigateToInsights: () -> Unit = {}
) {
    val userSettings by appViewModel.userSettings.collectAsState()
    val context = LocalContext.current

    // Get UserSettingsManager from Hilt
    val appContext = context.applicationContext
    val userSettingsManager = remember {
        val entryPoint = EntryPointAccessors.fromApplication(
            appContext,
            UserSettingsManagerEntryPoint::class.java
        )
        entryPoint.userSettingsManager()
    }

    // State for logo tap counter (debug mode)
    var logoTapCount by remember { mutableStateOf(0) }
    val scope = rememberCoroutineScope()

    // State for clear data confirmation
    var showClearDataDialog by remember { mutableStateOf(false) }

    // State for user settings edit modal
    var showUserSettingsEdit by remember { mutableStateOf(false) }

    // State for debug view modal
    var showDebugView by remember { mutableStateOf(false) }

    // Get app version
    val versionInfo = remember {
        try {
            val packageInfo: PackageInfo = context.packageManager
                .getPackageInfo(context.packageName, 0)
            Pair(packageInfo.versionName ?: "1.0.0", packageInfo.versionCode.toString())
        } catch (e: PackageManager.NameNotFoundException) {
            Pair("1.0.0", "1")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.settings_title),
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
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = NSSpacing.screenEdge)
        ) {
            Spacer(modifier = Modifier.height(NSSpacing.md))

            // User Profile Card
            NSCard {
                Column(modifier = Modifier.padding(NSSpacing.md)) {
                    Text(
                        text = stringResource(R.string.settings_profile),
                        style = NSTypography.heading3,
                        color = DesignTokens.Colors.textPrimary,
                        modifier = Modifier.padding(bottom = NSSpacing.md)
                    )

                    HorizontalDivider(color = DesignTokens.Colors.border)

                    Spacer(modifier = Modifier.height(NSSpacing.md))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = { showUserSettingsEdit = true })
                            .padding(vertical = NSSpacing.sm),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = null,
                            tint = DesignTokens.Colors.primary,
                            modifier = Modifier.size(48.dp)
                        )

                        Spacer(modifier = Modifier.width(NSSpacing.md))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = userSettings.userName.ifEmpty { "User" },
                                style = NSTypography.bodyBold,
                                color = DesignTokens.Colors.textPrimary
                            )
                            Text(
                                text = stringResource(R.string.settings_tap_to_edit_profile),
                                style = NSTypography.caption,
                                color = DesignTokens.Colors.textSecondary
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = DesignTokens.Colors.textSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(NSSpacing.lg))

            // Data & Sync Card
            NSCard {
                Column(modifier = Modifier.padding(NSSpacing.md)) {
                    Text(
                        text = stringResource(R.string.settings_data),
                        style = NSTypography.heading3,
                        color = DesignTokens.Colors.textPrimary,
                        modifier = Modifier.padding(bottom = NSSpacing.md)
                    )

                    HorizontalDivider(color = DesignTokens.Colors.border)

                    Spacer(modifier = Modifier.height(NSSpacing.sm))

                    SettingsRow(
                        icon = Icons.Default.Cloud,
                        iconColor = DesignTokens.Colors.info,
                        title = stringResource(R.string.settings_cloud_sync),
                        description = stringResource(R.string.settings_cloud_sync_description),
                        onClick = { /* TODO: Navigate to cloud sync */ }
                    )

                    HorizontalDivider(
                        color = DesignTokens.Colors.border,
                        modifier = Modifier.padding(vertical = NSSpacing.sm)
                    )

                    SettingsRow(
                        icon = Icons.Default.Upload,
                        iconColor = DesignTokens.Colors.success,
                        title = stringResource(R.string.settings_export),
                        description = stringResource(R.string.settings_export_description),
                        onClick = { /* TODO: Navigate to export */ }
                    )
                }
            }

            Spacer(modifier = Modifier.height(NSSpacing.lg))

            // Security Settings Card
            NSCard {
                Column(modifier = Modifier.padding(NSSpacing.md)) {
                    Text(
                        text = stringResource(R.string.settings_security),
                        style = NSTypography.heading3,
                        color = DesignTokens.Colors.textPrimary,
                        modifier = Modifier.padding(bottom = NSSpacing.md)
                    )

                    HorizontalDivider(color = DesignTokens.Colors.border)

                    Spacer(modifier = Modifier.height(NSSpacing.sm))

                    SecurityToggleRow(
                        userSettingsManager = userSettingsManager,
                        onToggle = { enabled ->
                            appViewModel.updateSecuritySettings(enabled)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(NSSpacing.lg))

            // Application Settings Card
            NSCard {
                Column(modifier = Modifier.padding(NSSpacing.md)) {
                    Text(
                        text = stringResource(R.string.settings_application),
                        style = NSTypography.heading3,
                        color = DesignTokens.Colors.textPrimary,
                        modifier = Modifier.padding(bottom = NSSpacing.md)
                    )

                    HorizontalDivider(color = DesignTokens.Colors.border)

                    Spacer(modifier = Modifier.height(NSSpacing.sm))

                    SettingsRow(
                        icon = Icons.Default.BarChart,
                        iconColor = DesignTokens.Colors.primary,
                        title = stringResource(R.string.insights_title),
                        description = stringResource(R.string.settings_insights_description),
                        onClick = onNavigateToInsights
                    )

                    HorizontalDivider(
                        color = DesignTokens.Colors.border,
                        modifier = Modifier.padding(vertical = NSSpacing.sm)
                    )

                    SettingsRow(
                        icon = Icons.Default.Notifications,
                        iconColor = DesignTokens.Colors.warning,
                        title = stringResource(R.string.settings_notifications),
                        description = stringResource(R.string.settings_notifications_description),
                        onClick = { /* TODO: Navigate to notifications */ }
                    )

                    HorizontalDivider(
                        color = DesignTokens.Colors.border,
                        modifier = Modifier.padding(vertical = NSSpacing.sm)
                    )

                    SettingsRow(
                        icon = Icons.Default.Schedule,
                        iconColor = DesignTokens.Colors.primary,
                        title = stringResource(R.string.settings_daily_reminders),
                        description = stringResource(R.string.settings_daily_reminders_description),
                        onClick = { /* TODO: Navigate to reminders */ }
                    )
                }
            }

            Spacer(modifier = Modifier.height(NSSpacing.lg))

            // Support Card
            NSCard {
                Column(modifier = Modifier.padding(NSSpacing.md)) {
                    Text(
                        text = stringResource(R.string.settings_support),
                        style = NSTypography.heading3,
                        color = DesignTokens.Colors.textPrimary,
                        modifier = Modifier.padding(bottom = NSSpacing.md)
                    )

                    HorizontalDivider(color = DesignTokens.Colors.border)

                    Spacer(modifier = Modifier.height(NSSpacing.sm))

                    SettingsRow(
                        icon = Icons.Default.Delete,
                        iconColor = DesignTokens.Colors.error,
                        title = stringResource(R.string.settings_clear_all_data),
                        description = stringResource(R.string.settings_clear_all_data_description),
                        isDestructive = true,
                        onClick = { showClearDataDialog = true }
                    )
                }
            }

            Spacer(modifier = Modifier.height(NSSpacing.lg))

            // App Info Card
            NSCard {
                Column(modifier = Modifier.padding(NSSpacing.md)) {
                    Text(
                        text = stringResource(R.string.settings_about),
                        style = NSTypography.heading3,
                        color = DesignTokens.Colors.textPrimary,
                        modifier = Modifier.padding(bottom = NSSpacing.md)
                    )

                    HorizontalDivider(color = DesignTokens.Colors.border)

                    Spacer(modifier = Modifier.height(NSSpacing.sm))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = NSSpacing.xs),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(R.string.settings_version),
                            style = NSTypography.body,
                            color = DesignTokens.Colors.textPrimary
                        )
                        Text(
                            text = versionInfo.first,
                            style = NSTypography.body,
                            color = DesignTokens.Colors.textSecondary
                        )
                    }

                    HorizontalDivider(
                        color = DesignTokens.Colors.border,
                        modifier = Modifier.padding(vertical = NSSpacing.sm)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = NSSpacing.xs),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(R.string.settings_build),
                            style = NSTypography.body,
                            color = DesignTokens.Colors.textPrimary
                        )
                        Text(
                            text = versionInfo.second,
                            style = NSTypography.body,
                            color = DesignTokens.Colors.textSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(NSSpacing.lg))

            // Logo with debug functionality
            val rotation by animateFloatAsState(
                targetValue = if (logoTapCount > 0) 10f else 0f,
                label = "logo rotation"
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = NSSpacing.lg),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(
                    onClick = {
                        logoTapCount++
                        if (logoTapCount >= 7) {
                            showDebugView = true
                            logoTapCount = 0
                        } else {
                            scope.launch {
                                delay(3000)
                                logoTapCount = 0
                            }
                        }
                    }
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = DesignTokens.Colors.primary,
                            modifier = Modifier
                                .size(32.dp)
                                .rotate(rotation)
                        )
                        Text(
                            text = "NorthStar",
                            style = NSTypography.caption,
                            color = DesignTokens.Colors.textSecondary
                        )
                    }
                }

                // Hidden tap count indicator
                if (logoTapCount > 3) {
                    Text(
                        text = "$logoTapCount/7",
                        style = NSTypography.caption.copy(fontSize = 10.sp),
                        color = DesignTokens.Colors.textSecondary.copy(alpha = 0.5f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(NSSpacing.xxl))
        }
    }

    // Clear Data Confirmation Dialog
    if (showClearDataDialog) {
        AlertDialog(
            onDismissRequest = { showClearDataDialog = false },
            title = {
                Text(
                    text = stringResource(R.string.settings_clear_all_data),
                    style = NSTypography.heading3
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.settings_clear_all_data_warning),
                    style = NSTypography.body
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        appViewModel.clearAllData()
                        showClearDataDialog = false
                    }
                ) {
                    Text(
                        text = stringResource(R.string.settings_clear_all_data),
                        color = DesignTokens.Colors.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDataDialog = false }) {
                    Text(text = stringResource(R.string.common_cancel))
                }
            },
            containerColor = DesignTokens.Colors.cardBackground
        )
    }

    // User Settings Edit Modal
    if (showUserSettingsEdit) {
        androidx.compose.material3.ModalBottomSheet(
            onDismissRequest = { showUserSettingsEdit = false },
            containerColor = DesignTokens.Colors.background
        ) {
            UserSettingsEditView(
                appViewModel = appViewModel,
                onDismiss = { showUserSettingsEdit = false }
            )
        }
    }

    // Debug View Modal
    if (showDebugView) {
        androidx.compose.material3.ModalBottomSheet(
            onDismissRequest = { showDebugView = false },
            containerColor = DesignTokens.Colors.background
        ) {
            DebugView(
                appViewModel = appViewModel,
                onDismiss = { showDebugView = false }
            )
        }
    }
}

/**
 * SettingsRow - Individual settings row with icon, title, description, and chevron
 */
@Composable
fun SettingsRow(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    description: String,
    isDestructive: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = NSSpacing.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(NSSpacing.md))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = NSTypography.body,
                color = if (isDestructive) DesignTokens.Colors.error else DesignTokens.Colors.textPrimary
            )
            Text(
                text = description,
                style = NSTypography.caption,
                color = DesignTokens.Colors.textSecondary
            )
        }

        if (!isDestructive) {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = DesignTokens.Colors.textSecondary,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
