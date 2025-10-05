package com.vcousien.northstar.core.storage

import com.vcousien.northstar.core.models.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Local storage manager (Present for compatibility with existing code)
 * Redirects to SecureLocalStorage for secure storage
 * 
 * This class provides a simple abstraction layer over the secure storage implementation,
 * similar to how iOS LocalStorage acts as a facade for SecureLocalStorage.
 */
@Singleton
class LocalStorage @Inject constructor(
    private val secureStorage: SecureLocalStorage
) {
    
    // MARK: - Mood Entries
    
    /**
     * Saves mood entries to secure storage
     * @param entries List of mood entries to save
     */
    suspend fun saveMoodEntries(entries: List<MoodEntry>) {
        secureStorage.saveMoodEntries(entries)
    }
    
    /**
     * Loads mood entries from secure storage
     * @return List of mood entries, or empty list if none found
     */
    suspend fun loadMoodEntries(): List<MoodEntry> {
        return secureStorage.loadMoodEntries()
    }
    
    // MARK: - Sleep Entries
    
    /**
     * Saves sleep entries to secure storage
     * @param entries List of sleep entries to save
     */
    suspend fun saveSleepEntries(entries: List<SleepEntry>) {
        secureStorage.saveSleepEntries(entries)
    }
    
    /**
     * Loads sleep entries from secure storage
     * @return List of sleep entries, or empty list if none found
     */
    suspend fun loadSleepEntries(): List<SleepEntry> {
        return secureStorage.loadSleepEntries()
    }
    
    // MARK: - Medications
    
    /**
     * Saves medications to secure storage
     * @param medications List of medications to save
     */
    suspend fun saveMedications(medications: List<Medication>) {
        secureStorage.saveMedications(medications)
    }
    
    /**
     * Loads medications from secure storage
     * @return List of medications, or empty list if none found
     */
    suspend fun loadMedications(): List<Medication> {
        return secureStorage.loadMedications()
    }
    
    // MARK: - Medication Entries
    
    /**
     * Saves medication entries to secure storage
     * @param entries List of medication entries to save
     */
    suspend fun saveMedicationEntries(entries: List<MedicationEntry>) {
        secureStorage.saveMedicationEntries(entries)
    }
    
    /**
     * Loads medication entries from secure storage
     * @return List of medication entries, or empty list if none found
     */
    suspend fun loadMedicationEntries(): List<MedicationEntry> {
        return secureStorage.loadMedicationEntries()
    }
    
    // MARK: - User Settings
    
    /**
     * Saves user settings to storage
     * @param settings User settings object to save
     */
    suspend fun saveUserSettings(settings: UserSettings) {
        secureStorage.saveUserSettings(settings)
    }
    
    /**
     * Loads user settings from storage
     * @return User settings object with loaded values
     */
    suspend fun loadUserSettings(): UserSettings {
        return secureStorage.loadUserSettings()
    }
    
    /**
     * Gets a Flow of user settings for reactive updates
     * @return Flow of UserSettings that updates when preferences change
     */
    fun getUserSettingsFlow(): Flow<UserSettings> {
        return secureStorage.getUserSettingsFlow()
    }
    
    /**
     * Marks onboarding as completed and persists this status
     * @param hasCompleted Whether onboarding has been completed
     */
    suspend fun saveHasCompletedOnboarding(hasCompleted: Boolean) {
        secureStorage.saveHasCompletedOnboarding(hasCompleted)
    }
    
    /**
     * Loads the onboarding completion status
     * @return Whether onboarding has been completed
     */
    suspend fun loadHasCompletedOnboarding(): Boolean {
        return secureStorage.loadHasCompletedOnboarding()
    }
    
    /**
     * Gets a Flow for onboarding completion status
     * @return Flow of Boolean that updates when onboarding status changes
     */
    fun getHasCompletedOnboardingFlow(): Flow<Boolean> {
        return secureStorage.getHasCompletedOnboardingFlow()
    }
    
    // MARK: - Individual Setting Conveniences
    
    /**
     * Updates just the user name
     * @param userName The new user name
     */
    suspend fun saveUserName(userName: String) {
        secureStorage.saveUserName(userName)
    }
    
    /**
     * Updates just the notifications enabled setting
     * @param enabled Whether notifications should be enabled
     */
    suspend fun saveNotificationsEnabled(enabled: Boolean) {
        secureStorage.saveNotificationsEnabled(enabled)
    }
    
    /**
     * Updates just the daily reminder time
     * @param time The new reminder time
     */
    suspend fun saveDailyReminderTime(time: kotlinx.datetime.LocalTime) {
        secureStorage.saveDailyReminderTime(time)
    }
    
    /**
     * Updates just the security enabled setting
     * @param enabled Whether security should be enabled
     */
    suspend fun saveSecurityEnabled(enabled: Boolean) {
        secureStorage.saveSecurityEnabled(enabled)
    }
    
    // MARK: - Clear All Data
    
    /**
     * Clears all data from storage
     * This is a destructive operation that cannot be undone
     */
    suspend fun clearAllData() {
        secureStorage.clearAllData()
    }
}
