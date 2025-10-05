//
// SecurityToggleRowDemo.kt
// NorthStar Android
//
// Created by Claude on 26/09/2025.
// Demo showing how to use SecurityToggleRow

package com.vcousien.northstar.core.authentication

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vcousien.northstar.core.designsystem.NSTypography
import com.vcousien.northstar.core.designsystem.DesignTokens
import com.vcousien.northstar.core.storage.UserSettingsManager

/**
 * Demo composable showing how to use SecurityToggleRow
 * This would typically be part of a Settings screen
 */
@Composable
fun SecurityToggleRowDemo(
    userSettingsManager: UserSettingsManager = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Page header
        Text(
            text = "Security Settings",
            style = NSTypography.heading1,
            color = DesignTokens.Colors.textPrimary
        )
        
        Text(
            text = "Protect your health data with biometric authentication",
            style = NSTypography.body,
            color = DesignTokens.Colors.textSecondary
        )
        
        // Security toggle row
        SecurityToggleRow(
            userSettingsManager = userSettingsManager,
            onToggle = { isEnabled ->
                // Handle the security toggle
                userSettingsManager.updateSecurityEnabled(isEnabled)
            }
        )
        
        // Additional settings could go here
        Text(
            text = "Other security settings would appear below...",
            style = NSTypography.caption,
            color = DesignTokens.Colors.textSecondary
        )
    }
}

/**
 * Example of how SecurityToggleRow would be used in a complete Settings screen
 */
@Composable
fun SettingsScreenWithSecurity(
    userSettingsManager: UserSettingsManager = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "App Settings",
            style = NSTypography.heading1,
            color = DesignTokens.Colors.textPrimary
        )
        
        // Security section
        Text(
            text = "Security & Privacy",
            style = NSTypography.heading3,
            color = DesignTokens.Colors.textPrimary
        )
        
        SecurityToggleRow(
            userSettingsManager = userSettingsManager,
            onToggle = { isEnabled ->
                userSettingsManager.updateSecurityEnabled(isEnabled)
            }
        )
        
        // Other settings sections would follow...
    }
}

@Preview(showBackground = true)
@Composable
fun SecurityToggleRowDemoPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "SecurityToggleRow Demo Preview",
                style = NSTypography.heading2
            )
            Text(
                text = "This component requires UserSettingsManager and BiometricAuthManager dependencies to function properly. Run the app to see the full functionality.",
                style = NSTypography.body,
                color = DesignTokens.Colors.textSecondary
            )
        }
    }
}
