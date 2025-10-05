package com.vcousien.northstar.core.storage

import com.vcousien.northstar.core.models.UserSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalTime
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Class for managing user settings and preferences
 * 
 * This class handles app settings such as user name, notification preferences,
 * tracking options, and the onboarding completion status. Settings are persisted
 * between app launches using SecureLocalStorage.
 * 
 * Android equivalent of iOS UserSettings (ObservableObject).
 * Uses StateFlow for reactive updates instead of @Published properties.
 */
@Singleton
class UserSettingsManager @Inject constructor(
    private val storage: SecureLocalStorage
) {
    
    // MARK: - Coroutine Scope for internal operations
    
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    // MARK: - StateFlow Properties (equivalent to @Published in iOS)
    
    private val _userSettings = MutableStateFlow(UserSettings.DEFAULT)
    
    /**
     * Current user settings as a StateFlow for reactive updates
     */
    val userSettings: StateFlow<UserSettings> = _userSettings.asStateFlow()
    
    /**
     * Individual property flows for granular observation
     */
    val userName: StateFlow<String> = userSettings.map { it.userName }.stateIn(
        scope = scope,
        started = SharingStarted.Lazily,
        initialValue = ""
    )
    
    val notificationsEnabled: StateFlow<Boolean> = userSettings.map { it.notificationsEnabled }.stateIn(
        scope = scope,
        started = SharingStarted.Lazily,
        initialValue = true
    )
    
    val dailyReminderTime: StateFlow<LocalTime> = userSettings.map { it.dailyReminderTime }.stateIn(
        scope = scope,
        started = SharingStarted.Lazily,
        initialValue = LocalTime(20, 0)
    )
    
    val securityEnabled: StateFlow<Boolean> = userSettings.map { it.securityEnabled }.stateIn(
        scope = scope,
        started = SharingStarted.Lazily,
        initialValue = false
    )
    
    val energyTrackingEnabled: StateFlow<Boolean> = userSettings.map { it.energyTrackingEnabled }.stateIn(
        scope = scope,
        started = SharingStarted.Lazily,
        initialValue = true
    )
    
    val anxietyTrackingEnabled: StateFlow<Boolean> = userSettings.map { it.anxietyTrackingEnabled }.stateIn(
        scope = scope,
        started = SharingStarted.Lazily,
        initialValue = true
    )
    
    val sleepTrackingEnabled: StateFlow<Boolean> = userSettings.map { it.sleepTrackingEnabled }.stateIn(
        scope = scope,
        started = SharingStarted.Lazily,
        initialValue = true
    )
    
    val irritabilityTrackingEnabled: StateFlow<Boolean> = userSettings.map { it.irritabilityTrackingEnabled }.stateIn(
        scope = scope,
        started = SharingStarted.Lazily,
        initialValue = true
    )
    
    val hasCompletedOnboarding: StateFlow<Boolean> = userSettings.map { it.hasCompletedOnboarding }.stateIn(
        scope = scope,
        started = SharingStarted.Lazily,
        initialValue = false
    )
    
    // MARK: - Initialization
    
    init {
        loadFromSecureStorage()
    }
    
    // MARK: - Persistence Methods
    
    /**
     * Saves all settings to secure storage
     */
    fun saveToSecureStorage() {
        scope.launch {
            try {
                storage.saveUserSettings(_userSettings.value)
            } catch (e: Exception) {
                println("❌ Error saving user settings: ${e.message}")
            }
        }
    }
    
    /**
     * Loads all settings from secure storage
     */
    private fun loadFromSecureStorage() {
        scope.launch {
            try {
                val loadedSettings = storage.loadUserSettings()
                _userSettings.value = loadedSettings
                println("✅ User settings loaded successfully")
            } catch (e: Exception) {
                println("⚠️ Error loading user settings, using defaults: ${e.message}")
                _userSettings.value = UserSettings.DEFAULT
            }
        }
    }
    
    /**
     * Marks onboarding as completed and persists this status
     */
    fun completeOnboarding() {
        scope.launch {
            try {
                val updatedSettings = _userSettings.value.withCompletedOnboarding()
                _userSettings.value = updatedSettings
                storage.saveUserSettings(updatedSettings)
                println("✅ Onboarding completed and saved")
            } catch (e: Exception) {
                println("❌ Error completing onboarding: ${e.message}")
            }
        }
    }
    
    /**
     * Resets all settings to their default values
     * This is used when clearing all data
     */
    fun resetToDefaults() {
        scope.launch {
            try {
                println("🔄 Resetting UserSettings to defaults...")
                _userSettings.value = UserSettings.DEFAULT
                storage.saveUserSettings(UserSettings.DEFAULT)
                println("✅ UserSettings reset to defaults")
            } catch (e: Exception) {
                println("❌ Error resetting user settings: ${e.message}")
            }
        }
    }
    
    // MARK: - Individual Setting Updates
    
    /**
     * Updates the user name
     */
    fun updateUserName(newUserName: String) {
        scope.launch {
            try {
                val updatedSettings = _userSettings.value.copy(userName = newUserName)
                _userSettings.value = updatedSettings
                storage.saveUserSettings(updatedSettings)
            } catch (e: Exception) {
                println("❌ Error updating user name: ${e.message}")
            }
        }
    }
    
    /**
     * Updates the notifications enabled setting
     */
    fun updateNotificationsEnabled(enabled: Boolean) {
        scope.launch {
            try {
                val updatedSettings = _userSettings.value.copy(notificationsEnabled = enabled)
                _userSettings.value = updatedSettings
                storage.saveUserSettings(updatedSettings)
            } catch (e: Exception) {
                println("❌ Error updating notifications setting: ${e.message}")
            }
        }
    }
    
    /**
     * Updates the daily reminder time
     */
    fun updateDailyReminderTime(newTime: LocalTime) {
        scope.launch {
            try {
                val updatedSettings = _userSettings.value.copy(dailyReminderTime = newTime)
                _userSettings.value = updatedSettings
                storage.saveUserSettings(updatedSettings)
            } catch (e: Exception) {
                println("❌ Error updating daily reminder time: ${e.message}")
            }
        }
    }
    
    /**
     * Updates the security enabled setting
     */
    fun updateSecurityEnabled(enabled: Boolean) {
        scope.launch {
            try {
                val updatedSettings = _userSettings.value.copy(securityEnabled = enabled)
                _userSettings.value = updatedSettings
                storage.saveUserSettings(updatedSettings)
            } catch (e: Exception) {
                println("❌ Error updating security setting: ${e.message}")
            }
        }
    }
    
    /**
     * Updates energy tracking enabled setting
     */
    fun updateEnergyTrackingEnabled(enabled: Boolean) {
        scope.launch {
            try {
                val updatedSettings = _userSettings.value.copy(energyTrackingEnabled = enabled)
                _userSettings.value = updatedSettings
                storage.saveUserSettings(updatedSettings)
            } catch (e: Exception) {
                println("❌ Error updating energy tracking setting: ${e.message}")
            }
        }
    }
    
    /**
     * Updates anxiety tracking enabled setting
     */
    fun updateAnxietyTrackingEnabled(enabled: Boolean) {
        scope.launch {
            try {
                val updatedSettings = _userSettings.value.copy(anxietyTrackingEnabled = enabled)
                _userSettings.value = updatedSettings
                storage.saveUserSettings(updatedSettings)
            } catch (e: Exception) {
                println("❌ Error updating anxiety tracking setting: ${e.message}")
            }
        }
    }
    
    /**
     * Updates sleep tracking enabled setting
     */
    fun updateSleepTrackingEnabled(enabled: Boolean) {
        scope.launch {
            try {
                val updatedSettings = _userSettings.value.copy(sleepTrackingEnabled = enabled)
                _userSettings.value = updatedSettings
                storage.saveUserSettings(updatedSettings)
            } catch (e: Exception) {
                println("❌ Error updating sleep tracking setting: ${e.message}")
            }
        }
    }
    
    /**
     * Updates irritability tracking enabled setting
     */
    fun updateIrritabilityTrackingEnabled(enabled: Boolean) {
        scope.launch {
            try {
                val updatedSettings = _userSettings.value.copy(irritabilityTrackingEnabled = enabled)
                _userSettings.value = updatedSettings
                storage.saveUserSettings(updatedSettings)
            } catch (e: Exception) {
                println("❌ Error updating irritability tracking setting: ${e.message}")
            }
        }
    }
    
    // MARK: - Synchronous Getters for immediate access
    
    /**
     * Gets current user settings synchronously
     */
    fun getCurrentSettings(): UserSettings = _userSettings.value
    
    /**
     * Gets current user name synchronously
     */
    fun getCurrentUserName(): String = _userSettings.value.userName
    
    /**
     * Gets current onboarding status synchronously
     */
    fun getCurrentOnboardingStatus(): Boolean = _userSettings.value.hasCompletedOnboarding
}
