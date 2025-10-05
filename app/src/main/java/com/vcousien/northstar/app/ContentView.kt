package com.vcousien.northstar.app

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * ContentView - Root container for the entire application
 *
 * This is the main entry point that manages:
 * - Onboarding flow (first launch) - TODO: Implement in future prompts
 * - Authentication flow (if security enabled) - TODO: Implement in future prompts
 * - Main app navigation (MainTabView)
 *
 * Currently shows MainTabView directly.
 * Will be enhanced with onboarding and authentication in future iterations.
 *
 * Matches iOS ContentView implementation structure
 *
 * @param modifier Modifier to be applied to the view
 */
@Composable
fun ContentView(
    modifier: Modifier = Modifier
) {
    // TODO: Add state management for:
    // - isFirstLaunch (show onboarding)
    // - isAuthenticated (show authentication)
    // - userSettings (security enabled)

    // For now, directly show the main app
    MainTabView(modifier = modifier)

    // Future implementation will be:
    // when {
    //     appState.isFirstLaunch -> OnboardingView()
    //     !appState.isAuthenticated && appState.userSettings.securityEnabled -> AuthenticationView()
    //     else -> MainTabView()
    // }
}
