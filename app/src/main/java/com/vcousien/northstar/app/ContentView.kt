package com.vcousien.northstar.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vcousien.northstar.core.authentication.AuthenticationView
import com.vcousien.northstar.core.designsystem.DesignTokens
import com.vcousien.northstar.features.onboarding.views.OnboardingView

/**
 * ContentView - Root container for the entire application
 *
 * This is the main entry point that manages the app flow:
 * 1. Loading state (while app initializes)
 * 2. Onboarding flow (first launch)
 * 3. Authentication flow (if security enabled)
 * 4. Main app navigation (MainTabView)
 *
 * Matches iOS ContentView implementation
 *
 * @param modifier Modifier to be applied to the view
 * @param appViewModel The main application ViewModel
 */
@Composable
fun ContentView(
    modifier: Modifier = Modifier,
    appViewModel: AppViewModel = viewModel()
) {
    val isLoading by appViewModel.isLoading.collectAsState()
    val userSettings by appViewModel.userSettings.collectAsState()

    when {
        // Show loading indicator while app initializes
        isLoading -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = DesignTokens.Colors.primary
                )
            }
        }

        // First launch - show onboarding
        appViewModel.isFirstLaunch -> {
            OnboardingView(
                onComplete = {
                    appViewModel.completeOnboarding()
                }
            )
        }

        // Security enabled and not authenticated - show authentication
        !appViewModel.isAuthenticated && userSettings.securityEnabled -> {
            AuthenticationView(
                appViewModel = appViewModel
            )
        }

        // Main app
        else -> {
            MainTabView(modifier = modifier)
        }
    }
}
