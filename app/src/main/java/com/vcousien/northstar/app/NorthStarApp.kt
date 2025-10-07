package com.vcousien.northstar.app

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel

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
    // Use ContentView which handles onboarding, authentication, and main navigation
    ContentView(appViewModel = appViewModel)
}
