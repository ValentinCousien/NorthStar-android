package com.vcousien.northstar.app

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vcousien.northstar.core.designsystem.DesignTokens
import com.vcousien.northstar.features.insights.views.InsightsView
import com.vcousien.northstar.features.medication.views.MedicationLogView
import com.vcousien.northstar.features.mood.views.MoodLogView
import com.vcousien.northstar.features.sleep.views.SleepLogView

/**
 * Navigation destinations for the app
 */
sealed class NavDestination(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : NavDestination("home", "Home", Icons.Default.Home)
    object Mood : NavDestination("mood", "Mood", Icons.Default.Face)
    object Sleep : NavDestination("sleep", "Sleep", Icons.Default.Star)
    object Medication : NavDestination("medication", "Medication", Icons.Default.List)
    object Insights : NavDestination("insights", "Insights", Icons.Default.Info)
}

/**
 * Main content view for the NorthStar application
 *
 * This composable serves as the root of the application's UI hierarchy
 * and handles the main application state and navigation.
 */
@Composable
fun NorthStarApp(
    appViewModel: AppViewModel = viewModel()
) {
    var currentDestination by remember { mutableStateOf<NavDestination>(NavDestination.Home) }

    val destinations = listOf(
        NavDestination.Home,
        NavDestination.Mood,
        NavDestination.Sleep,
        NavDestination.Medication,
        NavDestination.Insights
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = DesignTokens.Colors.cardBackground
            ) {
                destinations.forEach { destination ->
                    NavigationBarItem(
                        icon = { Icon(destination.icon, contentDescription = destination.title) },
                        label = { Text(destination.title) },
                        selected = currentDestination == destination,
                        onClick = { currentDestination = destination },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = DesignTokens.Colors.primary,
                            selectedTextColor = DesignTokens.Colors.primary,
                            indicatorColor = DesignTokens.Colors.primaryLight,
                            unselectedIconColor = DesignTokens.Colors.textSecondary,
                            unselectedTextColor = DesignTokens.Colors.textSecondary
                        )
                    )
                }
            }
        },
        containerColor = DesignTokens.Colors.background
    ) { paddingValues ->
        when (currentDestination) {
            NavDestination.Home -> HomeView(modifier = Modifier.padding(paddingValues))
            NavDestination.Mood -> MoodLogView(modifier = Modifier.padding(paddingValues))
            NavDestination.Sleep -> SleepLogView(modifier = Modifier.padding(paddingValues))
            NavDestination.Medication -> MedicationLogView(modifier = Modifier.padding(paddingValues))
            NavDestination.Insights -> InsightsView(modifier = Modifier.padding(paddingValues))
        }
    }
}
