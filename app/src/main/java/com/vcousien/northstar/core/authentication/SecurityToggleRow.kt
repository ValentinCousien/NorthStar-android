//
// SecurityToggleRow.kt
// NorthStar Android
//
// Created by Claude on 26/09/2025.
// Port of iOS SecurityToggleRow.swift to Android Compose

package com.vcousien.northstar.core.authentication

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vcousien.northstar.core.designsystem.DesignTokens
import com.vcousien.northstar.core.designsystem.NSSpacing
import com.vcousien.northstar.core.designsystem.NSTypography
import com.vcousien.northstar.core.designsystem.components.NSCard
import com.vcousien.northstar.core.storage.UserSettingsManager
import kotlinx.coroutines.launch

/**
 * Enhanced settings row for biometric authentication with better UX
 * Android port of the iOS SecurityToggleRow component
 * 
 * Features:
 * - Interactive toggle for enabling/disabling biometric security
 * - Dynamic icon and status indicators
 * - Real-time biometric availability checking
 * - Setup guidance for users without biometric authentication
 * - Material Design 3 styling with NorthStar design tokens
 * 
 * @param userSettingsManager The user settings manager for security state
 * @param authManager The biometric authentication manager
 * @param onToggle Callback when the security setting is toggled
 * @param modifier Modifier to be applied to the component
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecurityToggleRow(
    userSettingsManager: UserSettingsManager,
    authManager: BiometricAuthManager = hiltViewModel(),
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as? FragmentActivity
    val coroutineScope = rememberCoroutineScope()
    
    // Collect state from managers
    val securityEnabled by userSettingsManager.securityEnabled.collectAsStateWithLifecycle()
    val isAuthenticating by authManager.isAuthenticating.collectAsStateWithLifecycle()
    val authenticationError by authManager.authenticationError.collectAsStateWithLifecycle()
    
    // Local state
    var showingAlert by remember { mutableStateOf(false) }
    var alertTitle by remember { mutableStateOf("") }
    var alertMessage by remember { mutableStateOf("") }
    var showingBiometricSetup by remember { mutableStateOf(false) }
    var isToggleEnabled by remember { mutableStateOf(true) }
    
    // Animation states
    val iconScale by animateFloatAsState(
        targetValue = if (securityEnabled) 1.1f else 1f,
        animationSpec = spring(),
        label = "icon_scale"
    )
    
    val statusIndicatorAlpha by animateFloatAsState(
        targetValue = if (securityEnabled) 1f else 0f,
        label = "status_indicator_alpha"
    )
    
    // Computed properties
    val toggleTitle = if (securityEnabled) {
        "App Security Enabled"
    } else {
        "Secure App with ${authManager.biometricType.displayName}"
    }
    
    val toggleDescription = if (securityEnabled) {
        "Your app is protected with ${authManager.biometricType.displayName}"
    } else {
        authManager.biometricType.description
    }
    
    val iconBackgroundColor by animateColorAsState(
        targetValue = when {
            securityEnabled -> DesignTokens.Colors.success.copy(alpha = 0.1f)
            authManager.canAuthenticate -> DesignTokens.Colors.primary.copy(alpha = 0.1f)
            else -> DesignTokens.Colors.warning.copy(alpha = 0.1f)
        },
        label = "icon_background_color"
    )
    
    val iconColor by animateColorAsState(
        targetValue = when {
            securityEnabled -> DesignTokens.Colors.success
            authManager.canAuthenticate -> DesignTokens.Colors.primary
            else -> DesignTokens.Colors.warning
        },
        label = "icon_color"
    )
    
    // Handle toggle changes
    fun handleToggleChange(newValue: Boolean) {
        isToggleEnabled = false
        
        when {
            newValue && authManager.canAuthenticate -> {
                // Enable security - test authentication first
                coroutineScope.launch {
                    activity?.let { fragmentActivity ->
                        val success = authManager.authenticate(fragmentActivity)
                        isToggleEnabled = true
                        if (success) {
                            onToggle(true)
                            // Success feedback could be added here with haptics if needed
                        } else {
                            authenticationError?.let { error ->
                                alertTitle = "Authentication Failed"
                                alertMessage = error
                                showingBiometricSetup = false
                                showingAlert = true
                            }
                        }
                    } ?: run {
                        isToggleEnabled = true
                        alertTitle = "Error"
                        alertMessage = "Unable to access authentication system"
                        showingBiometricSetup = false
                        showingAlert = true
                    }
                }
            }
            newValue && !authManager.canAuthenticate -> {
                // Biometric not available
                isToggleEnabled = true
                alertTitle = "${authManager.biometricType.displayName} Not Available"
                alertMessage = "Please set up ${authManager.biometricType.displayName} in your device settings to enable app security."
                showingBiometricSetup = true
                showingAlert = true
            }
            !newValue -> {
                // Disable security
                isToggleEnabled = true
                onToggle(false)
            }
        }
    }
    
    // Open device settings
    fun openDeviceSettings() {
        try {
            val intent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED, 
                    androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            // Fallback to general security settings
            try {
                val intent = Intent(Settings.ACTION_SECURITY_SETTINGS)
                context.startActivity(intent)
            } catch (e: Exception) {
                // Final fallback to main settings
                val intent = Intent(Settings.ACTION_SETTINGS)
                context.startActivity(intent)
            }
        }
    }
    
    NSCard(modifier = modifier) {
        Column(
            verticalArrangement = Arrangement.spacedBy(NSSpacing.md)
        ) {
            // Main toggle row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(NSSpacing.md),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon with status indicator
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    // Background circle
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .scale(iconScale)
                            .clip(RoundedCornerShape(NSSpacing.sm))
                            .background(iconBackgroundColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = getBiometricIcon(authManager.biometricType),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = iconColor
                        )
                    }
                    
                    // Status indicator
                    if (securityEnabled) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .offset(x = 14.dp, y = (-14).dp)
                                .scale(statusIndicatorAlpha)
                                .clip(CircleShape)
                                .background(DesignTokens.Colors.success),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(DesignTokens.Colors.background)
                            )
                        }
                    }
                }
                
                // Content
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = toggleTitle,
                        style = NSTypography.bodyBold,
                        color = DesignTokens.Colors.textPrimary
                    )
                    
                    Text(
                        text = toggleDescription,
                        style = NSTypography.caption,
                        color = DesignTokens.Colors.textSecondary
                    )
                }
                
                // Toggle switch
                Switch(
                    checked = securityEnabled,
                    onCheckedChange = ::handleToggleChange,
                    enabled = isToggleEnabled && !isAuthenticating,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = DesignTokens.Colors.primary,
                        checkedTrackColor = DesignTokens.Colors.primary.copy(alpha = 0.3f),
                        uncheckedThumbColor = DesignTokens.Colors.textSecondary,
                        uncheckedTrackColor = DesignTokens.Colors.divider
                    )
                )
            }
            
            // Status and action section
            if (!authManager.canAuthenticate || securityEnabled) {
                HorizontalDivider(
                    color = DesignTokens.Colors.divider
                )
                
                when {
                    !authManager.canAuthenticate -> {
                        BiometricUnavailableSection(
                            biometricType = authManager.biometricType,
                            onSetupClick = {
                                alertTitle = "${authManager.biometricType.displayName} Not Available"
                                alertMessage = "Please set up ${authManager.biometricType.displayName} in your device settings to enable app security."
                                showingBiometricSetup = true
                                showingAlert = true
                            }
                        )
                    }
                    securityEnabled -> {
                        SecurityEnabledSection(
                            biometricType = authManager.biometricType
                        )
                    }
                }
            }
        }
    }
    
    // Alert Dialog
    if (showingAlert) {
        AlertDialog(
            onDismissRequest = { showingAlert = false },
            title = { Text(alertTitle) },
            text = { Text(alertMessage) },
            confirmButton = {
                TextButton(
                    onClick = { showingAlert = false }
                ) {
                    Text("OK")
                }
            },
            dismissButton = if (showingBiometricSetup) {
                {
                    TextButton(
                        onClick = {
                            showingAlert = false
                            openDeviceSettings()
                        }
                    ) {
                        Text("Open Settings")
                    }
                }
            } else null
        )
    }
}

@Composable
private fun BiometricUnavailableSection(
    biometricType: BiometricType,
    onSetupClick: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(NSSpacing.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = DesignTokens.Colors.warning
        )
        
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = "Setup Required",
                style = NSTypography.captionBold,
                color = DesignTokens.Colors.warning
            )
            
            Text(
                text = "${biometricType.displayName} is not set up on this device",
                style = NSTypography.caption,
                color = DesignTokens.Colors.textSecondary
            )
        }
        
        TextButton(
            onClick = onSetupClick,
            colors = ButtonDefaults.textButtonColors(
                contentColor = DesignTokens.Colors.primary
            )
        ) {
            Text(
                text = "Setup",
                style = NSTypography.captionBold
            )
        }
    }
}

@Composable
private fun SecurityEnabledSection(
    biometricType: BiometricType
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(NSSpacing.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.VerifiedUser,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = DesignTokens.Colors.success
        )
        
        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = "Security Active",
                style = NSTypography.captionBold,
                color = DesignTokens.Colors.success
            )
            
            Text(
                text = "Your data is protected with ${biometricType.displayName}",
                style = NSTypography.caption,
                color = DesignTokens.Colors.textSecondary
            )
        }
    }
}

private fun getBiometricIcon(biometricType: BiometricType): ImageVector {
    return when (biometricType) {
        BiometricType.FINGERPRINT -> Icons.Default.Fingerprint
        BiometricType.FACE -> Icons.Default.Face
        BiometricType.IRIS -> Icons.Default.Visibility
        BiometricType.NONE -> Icons.Default.Lock
    }
}

@Preview(showBackground = true)
@Composable
fun SecurityToggleRowPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Preview with mock data is challenging due to dependencies
            // This would typically be tested in the app with real managers
            Text(
                text = "SecurityToggleRow Preview",
                style = NSTypography.heading2
            )
            Text(
                text = "This component requires UserSettingsManager and BiometricAuthManager to function properly.",
                style = NSTypography.body,
                color = DesignTokens.Colors.textSecondary
            )
        }
    }
}
