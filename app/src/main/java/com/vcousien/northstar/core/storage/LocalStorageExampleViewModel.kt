package com.vcousien.northstar.core.storage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vcousien.northstar.core.models.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalTime
import javax.inject.Inject

/**
 * Example ViewModel demonstrating how to use the LocalStorage abstraction layer
 * and UserSettingsManager for managing user preferences and health data.
 * 
 * This shows the Android equivalent of using iOS UserSettings and LocalStorage.
 */
@HiltViewModel
class LocalStorageExampleViewModel @Inject constructor(
    private val localStorage: LocalStorage,
    private val userSettingsManager: UserSettingsManager
) : ViewModel() {
    
    // MARK: - StateFlow Properties for UI observation
    
    /**
     * User settings as StateFlow for reactive UI updates
     */
    val userSettings: StateFlow<UserSettings> = userSettingsManager.userSettings
    
    /**
     * Individual settings for granular observation
     */
    val userName: StateFlow<String> = userSettingsManager.userName
    val hasCompletedOnboarding: StateFlow<Boolean> = userSettingsManager.hasCompletedOnboarding
    val notificationsEnabled: StateFlow<Boolean> = userSettingsManager.notificationsEnabled
    
    // MARK: - Data Loading States
    
    private val _moodEntries = MutableStateFlow<List<MoodEntry>>(emptyList())
    val moodEntries: StateFlow<List<MoodEntry>> = _moodEntries.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    // MARK: - Initialization
    
    init {
        loadInitialData()
    }
    
    // MARK: - User Settings Management (iOS UserSettings equivalent)
    
    /**
     * Updates the user name (equivalent to iOS UserSettings.userName)
     */
    fun updateUserName(newName: String) {
        userSettingsManager.updateUserName(newName)
    }
    
    /**
     * Toggles notifications setting
     */
    fun toggleNotifications() {
        val currentSetting = userSettingsManager.notificationsEnabled.value
        userSettingsManager.updateNotificationsEnabled(!currentSetting)
    }
    
    /**
     * Updates the daily reminder time
     */
    fun updateReminderTime(hour: Int, minute: Int) {
        val newTime = LocalTime(hour, minute)
        userSettingsManager.updateDailyReminderTime(newTime)
    }
    
    /**
     * Marks onboarding as completed
     */
    fun completeOnboarding() {
        userSettingsManager.completeOnboarding()
    }
    
    /**
     * Toggles anxiety tracking setting
     */
    fun toggleAnxietyTracking() {
        val currentSetting = userSettingsManager.anxietyTrackingEnabled.value
        userSettingsManager.updateAnxietyTrackingEnabled(!currentSetting)
    }
    
    // MARK: - Health Data Management (using LocalStorage)
    
    /**
     * Loads initial data from storage
     */
    private fun loadInitialData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Load mood entries using LocalStorage
                val entries = localStorage.loadMoodEntries()
                _moodEntries.value = entries
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load data: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Saves a new mood entry
     */
    fun saveMoodEntry(moodEntry: MoodEntry) {
        viewModelScope.launch {
            try {
                val currentEntries = _moodEntries.value.toMutableList()
                currentEntries.add(moodEntry)
                
                // Save using LocalStorage abstraction
                localStorage.saveMoodEntries(currentEntries)
                _moodEntries.value = currentEntries
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Failed to save mood entry: ${e.message}"
            }
        }
    }
    
    /**
     * Loads medications from storage
     */
    fun loadMedications(): Flow<List<Medication>> = flow {
        try {
            val medications = localStorage.loadMedications()
            emit(medications)
        } catch (e: Exception) {
            _errorMessage.value = "Failed to load medications: ${e.message}"
            emit(emptyList())
        }
    }
    
    /**
     * Saves medications to storage
     */
    fun saveMedications(medications: List<Medication>) {
        viewModelScope.launch {
            try {
                localStorage.saveMedications(medications)
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Failed to save medications: ${e.message}"
            }
        }
    }
    
    // MARK: - Data Management Actions
    
    /**
     * Clears all app data (equivalent to iOS LocalStorage.clearAllData())
     */
    fun clearAllData() {
        viewModelScope.launch {
            try {
                // Clear all data using LocalStorage
                localStorage.clearAllData()
                
                // Reset user settings to defaults
                userSettingsManager.resetToDefaults()
                
                // Clear local state
                _moodEntries.value = emptyList()
                _errorMessage.value = null
                
            } catch (e: Exception) {
                _errorMessage.value = "Failed to clear data: ${e.message}"
            }
        }
    }
    
    /**
     * Refresh all data from storage
     */
    fun refreshData() {
        loadInitialData()
    }
    
    // MARK: - Utility Methods
    
    /**
     * Gets current settings synchronously for immediate access
     */
    fun getCurrentUserName(): String {
        return userSettingsManager.getCurrentUserName()
    }
    
    /**
     * Checks if onboarding has been completed synchronously
     */
    fun isOnboardingCompleted(): Boolean {
        return userSettingsManager.getCurrentOnboardingStatus()
    }
    
    /**
     * Demonstrates combining user settings with health data
     */
    fun getUserDataSummary(): Flow<Pair<UserSettings, List<MoodEntry>>> {
        return combine(
            userSettings,
            moodEntries
        ) { settings, entries ->
            Pair(settings, entries)
        }
    }
    
    // MARK: - Error Handling
    
    /**
     * Clears the current error message
     */
    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}
