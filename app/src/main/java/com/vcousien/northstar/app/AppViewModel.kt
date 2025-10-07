package com.vcousien.northstar.app

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vcousien.northstar.core.storage.LocalStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Main application ViewModel that manages global app state
 *
 * This ViewModel handles:
 * - Onboarding flow (first launch detection)
 * - Authentication state
 * - App initialization
 * - User settings loading
 * - Global app lifecycle
 *
 * Matches iOS AppViewModel implementation
 */
@HiltViewModel
class AppViewModel @Inject constructor(
    private val localStorage: LocalStorage
) : ViewModel() {

    // MARK: - State

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    var isAuthenticated by mutableStateOf(false)
        private set

    var isFirstLaunch by mutableStateOf(false)
        private set

    // User settings loaded from storage
    private val _userSettings = MutableStateFlow(com.vcousien.northstar.core.models.UserSettings.DEFAULT)
    val userSettings: StateFlow<com.vcousien.northstar.core.models.UserSettings> = _userSettings.asStateFlow()

    // MARK: - Initialization

    init {
        appDidLaunch()
    }

    /**
     * Called when the app launches to initialize necessary components
     * Loads user settings and determines if onboarding is needed
     */
    private fun appDidLaunch() {
        viewModelScope.launch {
            try {
                // Load user settings from storage
                val settings = localStorage.loadUserSettings()
                _userSettings.value = settings

                // Check if this is the first launch
                isFirstLaunch = !settings.hasCompletedOnboarding

                // If security is enabled and not first launch, require authentication
                if (!isFirstLaunch && settings.securityEnabled) {
                    isAuthenticated = false
                } else {
                    isAuthenticated = true
                }

                println("📱 App Launched:")
                println("   First Launch: $isFirstLaunch")
                println("   Security Enabled: ${settings.securityEnabled}")
                println("   Authenticated: $isAuthenticated")

            } catch (e: Exception) {
                println("❌ Failed to load settings: ${e.message}")
                // On error, treat as first launch
                isFirstLaunch = true
                isAuthenticated = true
            } finally {
                _isLoading.value = false
            }
        }
    }

    // MARK: - Onboarding

    /**
     * Completes the onboarding process
     * Saves completion status to storage and updates app state
     */
    fun completeOnboarding() {
        viewModelScope.launch {
            try {
                // Update user settings
                val updatedSettings = _userSettings.value.copy(
                    hasCompletedOnboarding = true
                )
                _userSettings.value = updatedSettings

                // Save to storage
                localStorage.saveUserSettings(updatedSettings)

                // Update state
                isFirstLaunch = false
                isAuthenticated = true

                println("✅ Onboarding completed and saved")
            } catch (e: Exception) {
                println("❌ Failed to save onboarding completion: ${e.message}")
            }
        }
    }

    // MARK: - Authentication

    /**
     * Completes the authentication process
     */
    fun completeAuthentication() {
        isAuthenticated = true
        println("✅ Authentication completed")
    }

    /**
     * Resets onboarding status (for debugging/testing)
     */
    fun resetOnboarding() {
        viewModelScope.launch {
            try {
                val updatedSettings = _userSettings.value.copy(
                    hasCompletedOnboarding = false
                )
                _userSettings.value = updatedSettings
                localStorage.saveUserSettings(updatedSettings)

                isFirstLaunch = true
                isAuthenticated = false

                println("🔄 Onboarding reset")
            } catch (e: Exception) {
                println("❌ Failed to reset onboarding: ${e.message}")
            }
        }
    }

    // MARK: - Settings Management

    /**
     * Updates the security settings
     * @param enabled Whether security should be enabled
     */
    fun updateSecuritySettings(enabled: Boolean) {
        viewModelScope.launch {
            try {
                val updatedSettings = _userSettings.value.copy(
                    securityEnabled = enabled
                )
                _userSettings.value = updatedSettings
                localStorage.saveUserSettings(updatedSettings)

                println("✅ Security settings updated: $enabled")
            } catch (e: Exception) {
                println("❌ Failed to update security settings: ${e.message}")
            }
        }
    }

    /**
     * Updates all user settings
     * @param settings The updated user settings
     */
    fun updateUserSettings(settings: com.vcousien.northstar.core.models.UserSettings) {
        viewModelScope.launch {
            try {
                _userSettings.value = settings
                localStorage.saveUserSettings(settings)

                println("✅ User settings updated:")
                println("   Name: ${settings.userName}")
                println("   Notifications: ${settings.notificationsEnabled}")
                println("   Energy Tracking: ${settings.energyTrackingEnabled}")
                println("   Anxiety Tracking: ${settings.anxietyTrackingEnabled}")
            } catch (e: Exception) {
                println("❌ Failed to update user settings: ${e.message}")
            }
        }
    }

    /**
     * Clears all app data including tracking data and settings
     * Resets the app to initial state
     */
    fun clearAllData() {
        viewModelScope.launch {
            try {
                println("🗑️ Clearing all app data...")

                // Clear all data from storage
                localStorage.clearAllData()

                // Reset user settings to default
                _userSettings.value = com.vcousien.northstar.core.models.UserSettings.DEFAULT

                // Reset app state
                isFirstLaunch = true
                isAuthenticated = false

                println("✅ All data cleared successfully")
            } catch (e: Exception) {
                println("❌ Failed to clear all data: ${e.message}")
            }
        }
    }
}
