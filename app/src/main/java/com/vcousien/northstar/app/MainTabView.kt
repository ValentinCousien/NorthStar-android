package com.vcousien.northstar.app

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.vcousien.northstar.core.designsystem.DesignTokens
import com.vcousien.northstar.features.insights.views.InsightsView
import com.vcousien.northstar.features.medication.views.MedicationLogView
import com.vcousien.northstar.features.mood.views.MoodLogView
import com.vcousien.northstar.features.settings.views.SettingsView
import com.vcousien.northstar.features.sleep.views.SleepLogView

/**
 * MainTabView - Main navigation container with bottom navigation bar
 *
 * Features:
 * - Bottom navigation bar with 5 tabs (Home, Mood, Sleep, Medication, Settings)
 * - Material 3 NavigationBar design
 * - Matches iOS MainTabView implementation
 *
 * @param modifier Modifier to be applied to the view
 */
@Composable
fun MainTabView(
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(AppTab.HOME) }

    Scaffold(
        modifier = modifier,
        bottomBar = {
            NavigationBar(
                containerColor = DesignTokens.Colors.surface,
                contentColor = DesignTokens.Colors.textPrimary
            ) {
                AppTab.values().forEach { tab ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = stringResource(tab.titleRes)
                            )
                        },
                        label = {
                            Text(text = stringResource(tab.titleRes))
                        },
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = DesignTokens.Colors.primary,
                            selectedTextColor = DesignTokens.Colors.primary,
                            unselectedIconColor = DesignTokens.Colors.textSecondary,
                            unselectedTextColor = DesignTokens.Colors.textSecondary,
                            indicatorColor = DesignTokens.Colors.primary.copy(alpha = 0.1f)
                        )
                    )
                }
            }
        },
        containerColor = DesignTokens.Colors.background
    ) { paddingValues ->
        // Tab content
        when (selectedTab) {
            AppTab.HOME -> {
                HomeView(
                    modifier = Modifier.padding(paddingValues)
                )
            }
            AppTab.MOOD -> {
                MoodLogView(
                    modifier = Modifier.padding(paddingValues)
                )
            }
            AppTab.SLEEP -> {
                SleepLogView(
                    modifier = Modifier.padding(paddingValues)
                )
            }
            AppTab.MEDICATION -> {
                MedicationLogView(
                    modifier = Modifier.padding(paddingValues)
                )
            }
            AppTab.SETTINGS -> {
                SettingsView(
                    onNavigateToInsights = {
                        selectedTab = AppTab.HOME // Navigate to insights via home for now
                    }
                )
            }
        }
    }
}
