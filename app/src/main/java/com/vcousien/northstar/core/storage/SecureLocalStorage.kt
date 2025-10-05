package com.vcousien.northstar.core.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.vcousien.northstar.core.models.MoodEntry
import com.vcousien.northstar.core.models.SleepEntry
import com.vcousien.northstar.core.models.Medication
import com.vcousien.northstar.core.models.MedicationEntry
import com.vcousien.northstar.core.models.UserSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalTime
import javax.inject.Inject
import javax.inject.Singleton

// Extension to create DataStore instance
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "northstar_preferences")

/**
 * Secure storage manager for persisting sensitive data and user preferences
 *
 * This class provides an abstraction layer over different storage mechanisms:
 * - Encrypted SharedPreferences (via SecureStorageManager) for sensitive health data
 * - DataStore for non-sensitive app settings
 */
@Singleton
class SecureLocalStorage @Inject constructor(
    private val context: Context,
    private val secureStorageManager: SecureStorageManager
) {
    
    // MARK: - Private Storage Keys
    
    /**
     * Keys for encrypted storage (sensitive health data)
     */
    private object SecureStorageKey {
        const val MOOD_ENTRIES = "mood_entries"
        const val SLEEP_ENTRIES = "sleep_entries"
        const val MEDICATIONS = "medications"
        const val MEDICATION_ENTRIES = "medication_entries"
    }
    
    /**
     * Keys for DataStore (non-sensitive preferences)
     */
    private object PreferenceKey {
        val HAS_COMPLETED_ONBOARDING = booleanPreferencesKey("has_completed_onboarding")
        val USER_NAME = stringPreferencesKey("user_name")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val DAILY_REMINDER_HOUR = intPreferencesKey("daily_reminder_hour")
        val DAILY_REMINDER_MINUTE = intPreferencesKey("daily_reminder_minute")
        val SECURITY_ENABLED = booleanPreferencesKey("security_enabled")
        val ENERGY_TRACKING_ENABLED = booleanPreferencesKey("energy_tracking_enabled")
        val ANXIETY_TRACKING_ENABLED = booleanPreferencesKey("anxiety_tracking_enabled")
        val SLEEP_TRACKING_ENABLED = booleanPreferencesKey("sleep_tracking_enabled")
        val IRRITABILITY_TRACKING_ENABLED = booleanPreferencesKey("irritability_tracking_enabled")
    }
    
    private val dataStore = context.dataStore
    
    // MARK: - Mood Entries (Sensitive Data - Encrypted Storage)
    
    /**
     * Saves mood entries to secure storage
     * @param entries List of mood entries to save
     */
    suspend fun saveMoodEntries(entries: List<MoodEntry>) {
        try {
            secureStorageManager.saveArray(SecureStorageKey.MOOD_ENTRIES, entries)
        } catch (e: Exception) {
            println("Error saving mood entries: ${e.message}")
            throw e
        }
    }
    
    /**
     * Loads mood entries from secure storage
     * @return List of mood entries, or empty list if none found
     */
    suspend fun loadMoodEntries(): List<MoodEntry> {
        return try {
            secureStorageManager.loadArrayOrEmpty(SecureStorageKey.MOOD_ENTRIES)
        } catch (e: Exception) {
            println("Error loading mood entries: ${e.message}")
            emptyList()
        }
    }
    
    // MARK: - Sleep Entries (Sensitive Data - Encrypted Storage)
    
    /**
     * Saves sleep entries to secure storage
     * @param entries List of sleep entries to save
     */
    suspend fun saveSleepEntries(entries: List<SleepEntry>) {
        try {
            secureStorageManager.saveArray(SecureStorageKey.SLEEP_ENTRIES, entries)
        } catch (e: Exception) {
            println("Error saving sleep entries: ${e.message}")
            throw e
        }
    }
    
    /**
     * Loads sleep entries from secure storage
     * @return List of sleep entries, or empty list if none found
     */
    suspend fun loadSleepEntries(): List<SleepEntry> {
        return try {
            secureStorageManager.loadArrayOrEmpty(SecureStorageKey.SLEEP_ENTRIES)
        } catch (e: Exception) {
            println("Error loading sleep entries: ${e.message}")
            emptyList()
        }
    }
    
    // MARK: - Medications (Sensitive Data - Encrypted Storage)
    
    /**
     * Saves medications to secure storage
     * @param medications List of medications to save
     */
    suspend fun saveMedications(medications: List<Medication>) {
        try {
            secureStorageManager.saveArray(SecureStorageKey.MEDICATIONS, medications)
        } catch (e: Exception) {
            println("Error saving medications: ${e.message}")
            throw e
        }
    }
    
    /**
     * Loads medications from secure storage
     * @return List of medications, or empty list if none found
     */
    suspend fun loadMedications(): List<Medication> {
        return try {
            secureStorageManager.loadArrayOrEmpty(SecureStorageKey.MEDICATIONS)
        } catch (e: Exception) {
            println("Error loading medications: ${e.message}")
            emptyList()
        }
    }
    
    // MARK: - Medication Entries (Sensitive Data - Encrypted Storage)
    
    /**
     * Saves medication entries to secure storage
     * @param entries List of medication entries to save
     */
    suspend fun saveMedicationEntries(entries: List<MedicationEntry>) {
        try {
            secureStorageManager.saveArray(SecureStorageKey.MEDICATION_ENTRIES, entries)
        } catch (e: Exception) {
            println("Error saving medication entries: ${e.message}")
            throw e
        }
    }
    
    /**
     * Loads medication entries from secure storage
     * @return List of medication entries, or empty list if none found
     */
    suspend fun loadMedicationEntries(): List<MedicationEntry> {
        return try {
            secureStorageManager.loadArrayOrEmpty(SecureStorageKey.MEDICATION_ENTRIES)
        } catch (e: Exception) {
            println("Error loading medication entries: ${e.message}")
            emptyList()
        }
    }
    
    // MARK: - User Settings (Non-sensitive data - DataStore)
    
    /**
     * Saves user settings to DataStore
     * @param settings User settings object to save
     */
    suspend fun saveUserSettings(settings: UserSettings) {
        dataStore.edit { preferences ->
            preferences[PreferenceKey.USER_NAME] = settings.userName
            preferences[PreferenceKey.NOTIFICATIONS_ENABLED] = settings.notificationsEnabled
            preferences[PreferenceKey.DAILY_REMINDER_HOUR] = settings.dailyReminderTime.hour
            preferences[PreferenceKey.DAILY_REMINDER_MINUTE] = settings.dailyReminderTime.minute
            preferences[PreferenceKey.SECURITY_ENABLED] = settings.securityEnabled
            preferences[PreferenceKey.ENERGY_TRACKING_ENABLED] = settings.energyTrackingEnabled
            preferences[PreferenceKey.ANXIETY_TRACKING_ENABLED] = settings.anxietyTrackingEnabled
            preferences[PreferenceKey.SLEEP_TRACKING_ENABLED] = settings.sleepTrackingEnabled
            preferences[PreferenceKey.IRRITABILITY_TRACKING_ENABLED] = settings.irritabilityTrackingEnabled
            preferences[PreferenceKey.HAS_COMPLETED_ONBOARDING] = settings.hasCompletedOnboarding
        }
    }
    
    /**
     * Loads user settings from DataStore
     * @return User settings object with loaded values
     */
    suspend fun loadUserSettings(): UserSettings {
        val preferences = dataStore.data
            .catch { exception ->
                // Handle errors gracefully
                println("Error reading preferences: ${exception.message}")
                emit(emptyPreferences())
            }
            .first()
        
        return UserSettings(
            userName = preferences[PreferenceKey.USER_NAME] ?: "",
            notificationsEnabled = preferences[PreferenceKey.NOTIFICATIONS_ENABLED] ?: false,
            dailyReminderTime = LocalTime(
                hour = preferences[PreferenceKey.DAILY_REMINDER_HOUR] ?: 9,
                minute = preferences[PreferenceKey.DAILY_REMINDER_MINUTE] ?: 0
            ),
            securityEnabled = preferences[PreferenceKey.SECURITY_ENABLED] ?: false,
            energyTrackingEnabled = preferences[PreferenceKey.ENERGY_TRACKING_ENABLED] ?: false,
            anxietyTrackingEnabled = preferences[PreferenceKey.ANXIETY_TRACKING_ENABLED] ?: false,
            sleepTrackingEnabled = preferences[PreferenceKey.SLEEP_TRACKING_ENABLED] ?: false,
            irritabilityTrackingEnabled = preferences[PreferenceKey.IRRITABILITY_TRACKING_ENABLED] ?: false,
            hasCompletedOnboarding = preferences[PreferenceKey.HAS_COMPLETED_ONBOARDING] ?: false
        )
    }
    
    /**
     * Gets a Flow of user settings for reactive updates
     * @return Flow of UserSettings that updates when preferences change
     */
    fun getUserSettingsFlow(): Flow<UserSettings> {
        return dataStore.data
            .catch { exception ->
                // Handle errors gracefully
                println("Error reading preferences: ${exception.message}")
                emit(emptyPreferences())
            }
            .map { preferences ->
                UserSettings(
                    userName = preferences[PreferenceKey.USER_NAME] ?: "",
                    notificationsEnabled = preferences[PreferenceKey.NOTIFICATIONS_ENABLED] ?: false,
                    dailyReminderTime = LocalTime(
                        hour = preferences[PreferenceKey.DAILY_REMINDER_HOUR] ?: 9,
                        minute = preferences[PreferenceKey.DAILY_REMINDER_MINUTE] ?: 0
                    ),
                    securityEnabled = preferences[PreferenceKey.SECURITY_ENABLED] ?: false,
                    energyTrackingEnabled = preferences[PreferenceKey.ENERGY_TRACKING_ENABLED] ?: false,
                    anxietyTrackingEnabled = preferences[PreferenceKey.ANXIETY_TRACKING_ENABLED] ?: false,
                    sleepTrackingEnabled = preferences[PreferenceKey.SLEEP_TRACKING_ENABLED] ?: false,
                    irritabilityTrackingEnabled = preferences[PreferenceKey.IRRITABILITY_TRACKING_ENABLED] ?: false,
                    hasCompletedOnboarding = preferences[PreferenceKey.HAS_COMPLETED_ONBOARDING] ?: false
                )
            }
    }
    
    /**
     * Saves the onboarding completion status
     * @param hasCompleted Whether onboarding has been completed
     */
    suspend fun saveHasCompletedOnboarding(hasCompleted: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferenceKey.HAS_COMPLETED_ONBOARDING] = hasCompleted
        }
    }
    
    /**
     * Loads the onboarding completion status
     * @return Whether onboarding has been completed
     */
    suspend fun loadHasCompletedOnboarding(): Boolean {
        val preferences = dataStore.data
            .catch { exception ->
                println("Error reading onboarding status: ${exception.message}")
                emit(emptyPreferences())
            }
            .first()
        
        return preferences[PreferenceKey.HAS_COMPLETED_ONBOARDING] ?: false
    }
    
    /**
     * Gets a Flow for onboarding completion status
     * @return Flow of Boolean that updates when onboarding status changes
     */
    fun getHasCompletedOnboardingFlow(): Flow<Boolean> {
        return dataStore.data
            .catch { exception ->
                println("Error reading onboarding status: ${exception.message}")
                emit(emptyPreferences())
            }
            .map { preferences ->
                preferences[PreferenceKey.HAS_COMPLETED_ONBOARDING] ?: false
            }
    }
    
    // MARK: - Clear All Data
    
    /**
     * Clears all data from both encrypted storage and DataStore
     * This is a destructive operation that cannot be undone
     */
    suspend fun clearAllData() {
        // Clear all encrypted storage data
        clearSecureData()
        
        // Clear all DataStore preferences
        clearPreferences()
    }
    
    /**
     * Clears all health data from encrypted storage (mood, sleep, medication entries)
     */
    private suspend fun clearSecureData() {
        val secureKeys = listOf(
            SecureStorageKey.MOOD_ENTRIES,
            SecureStorageKey.SLEEP_ENTRIES,
            SecureStorageKey.MEDICATIONS,
            SecureStorageKey.MEDICATION_ENTRIES
        )
        
        secureKeys.forEach { key ->
            try {
                secureStorageManager.delete(key)
                println("✅ Cleared secure data for key: $key")
            } catch (e: Exception) {
                println("⚠️ Error clearing secure data for key $key: ${e.message}")
            }
        }
    }
    
    /**
     * Clears all user settings from DataStore
     */
    private suspend fun clearPreferences() {
        try {
            dataStore.edit { preferences ->
                preferences.clear()
            }
            println("✅ All DataStore preferences cleared")
        } catch (e: Exception) {
            println("⚠️ Error clearing DataStore preferences: ${e.message}")
            throw e
        }
    }
    
    // MARK: - Individual Setting Methods (for convenience)
    
    /**
     * Updates just the user name
     */
    suspend fun saveUserName(userName: String) {
        dataStore.edit { preferences ->
            preferences[PreferenceKey.USER_NAME] = userName
        }
    }
    
    /**
     * Updates just the notifications enabled setting
     */
    suspend fun saveNotificationsEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferenceKey.NOTIFICATIONS_ENABLED] = enabled
        }
    }
    
    /**
     * Updates just the daily reminder time
     */
    suspend fun saveDailyReminderTime(time: LocalTime) {
        dataStore.edit { preferences ->
            preferences[PreferenceKey.DAILY_REMINDER_HOUR] = time.hour
            preferences[PreferenceKey.DAILY_REMINDER_MINUTE] = time.minute
        }
    }
    
    /**
     * Updates just the security enabled setting
     */
    suspend fun saveSecurityEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferenceKey.SECURITY_ENABLED] = enabled
        }
    }
}
