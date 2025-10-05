package com.vcousien.northstar.core.storage

import com.vcousien.northstar.core.models.MoodEntry
import com.vcousien.northstar.core.models.SleepEntry
import com.vcousien.northstar.core.models.Medication
import com.vcousien.northstar.core.models.MedicationEntry
import com.vcousien.northstar.core.models.UserSettings
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for health data storage operations
 * 
 * This interface abstracts the underlying storage implementation and provides
 * a clean API for accessing health data throughout the app.
 */
interface HealthDataRepository {
    
    // MARK: - Mood Entries
    
    /**
     * Saves a list of mood entries
     */
    suspend fun saveMoodEntries(entries: List<MoodEntry>)
    
    /**
     * Loads all mood entries
     */
    suspend fun loadMoodEntries(): List<MoodEntry>
    
    /**
     * Adds a single mood entry
     */
    suspend fun addMoodEntry(entry: MoodEntry)
    
    /**
     * Deletes a mood entry by ID
     */
    suspend fun deleteMoodEntry(entryId: String)
    
    // MARK: - Sleep Entries
    
    /**
     * Saves a list of sleep entries
     */
    suspend fun saveSleepEntries(entries: List<SleepEntry>)
    
    /**
     * Loads all sleep entries
     */
    suspend fun loadSleepEntries(): List<SleepEntry>
    
    /**
     * Adds a single sleep entry
     */
    suspend fun addSleepEntry(entry: SleepEntry)
    
    /**
     * Deletes a sleep entry by ID
     */
    suspend fun deleteSleepEntry(entryId: String)
    
    // MARK: - Medications
    
    /**
     * Saves a list of medications
     */
    suspend fun saveMedications(medications: List<Medication>)
    
    /**
     * Loads all medications
     */
    suspend fun loadMedications(): List<Medication>
    
    /**
     * Adds a single medication
     */
    suspend fun addMedication(medication: Medication)
    
    /**
     * Updates a medication
     */
    suspend fun updateMedication(medication: Medication)
    
    /**
     * Deletes a medication by ID
     */
    suspend fun deleteMedication(medicationId: String)
    
    // MARK: - Medication Entries
    
    /**
     * Saves a list of medication entries
     */
    suspend fun saveMedicationEntries(entries: List<MedicationEntry>)
    
    /**
     * Loads all medication entries
     */
    suspend fun loadMedicationEntries(): List<MedicationEntry>
    
    /**
     * Adds a single medication entry
     */
    suspend fun addMedicationEntry(entry: MedicationEntry)
    
    /**
     * Deletes a medication entry by ID
     */
    suspend fun deleteMedicationEntry(entryId: String)
    
    // MARK: - Clear Data
    
    /**
     * Clears all health data
     */
    suspend fun clearAllHealthData()
}

/**
 * Repository interface for user settings storage operations
 */
interface UserSettingsRepository {
    
    /**
     * Saves user settings
     */
    suspend fun saveUserSettings(settings: UserSettings)
    
    /**
     * Loads user settings
     */
    suspend fun loadUserSettings(): UserSettings
    
    /**
     * Gets a Flow of user settings for reactive updates
     */
    fun getUserSettingsFlow(): Flow<UserSettings>
    
    /**
     * Saves onboarding completion status
     */
    suspend fun saveHasCompletedOnboarding(hasCompleted: Boolean)
    
    /**
     * Loads onboarding completion status
     */
    suspend fun loadHasCompletedOnboarding(): Boolean
    
    /**
     * Gets a Flow for onboarding completion status
     */
    fun getHasCompletedOnboardingFlow(): Flow<Boolean>
    
    /**
     * Updates individual settings
     */
    suspend fun updateUserName(userName: String)
    suspend fun updateNotificationsEnabled(enabled: Boolean)
    suspend fun updateSecurityEnabled(enabled: Boolean)
    
    /**
     * Clears all user settings
     */
    suspend fun clearAllSettings()
}
