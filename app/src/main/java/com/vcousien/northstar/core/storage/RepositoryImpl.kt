package com.vcousien.northstar.core.storage

import com.vcousien.northstar.core.models.MoodEntry
import com.vcousien.northstar.core.models.SleepEntry
import com.vcousien.northstar.core.models.Medication
import com.vcousien.northstar.core.models.MedicationEntry
import com.vcousien.northstar.core.models.UserSettings
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Default implementation of HealthDataRepository using SecureLocalStorage
 */
@Singleton
class DefaultHealthDataRepository @Inject constructor(
    private val secureLocalStorage: SecureLocalStorage
) : HealthDataRepository {
    
    // MARK: - Mood Entries
    
    override suspend fun saveMoodEntries(entries: List<MoodEntry>) {
        secureLocalStorage.saveMoodEntries(entries)
    }
    
    override suspend fun loadMoodEntries(): List<MoodEntry> {
        return secureLocalStorage.loadMoodEntries()
    }
    
    override suspend fun addMoodEntry(entry: MoodEntry) {
        val existingEntries = loadMoodEntries().toMutableList()
        
        // Remove existing entry with same ID if it exists
        existingEntries.removeAll { it.id == entry.id }
        
        // Add the new entry
        existingEntries.add(entry)
        
        // Sort by date (most recent first)
        existingEntries.sortByDescending { it.date }
        
        saveMoodEntries(existingEntries)
    }
    
    override suspend fun deleteMoodEntry(entryId: String) {
        val existingEntries = loadMoodEntries().toMutableList()
        existingEntries.removeAll { it.id == entryId }
        saveMoodEntries(existingEntries)
    }
    
    // MARK: - Sleep Entries
    
    override suspend fun saveSleepEntries(entries: List<SleepEntry>) {
        secureLocalStorage.saveSleepEntries(entries)
    }
    
    override suspend fun loadSleepEntries(): List<SleepEntry> {
        return secureLocalStorage.loadSleepEntries()
    }
    
    override suspend fun addSleepEntry(entry: SleepEntry) {
        val existingEntries = loadSleepEntries().toMutableList()
        
        // Remove existing entry with same ID if it exists
        existingEntries.removeAll { it.id == entry.id }
        
        // Add the new entry
        existingEntries.add(entry)
        
        // Sort by date (most recent first)
        existingEntries.sortByDescending { it.date }
        
        saveSleepEntries(existingEntries)
    }
    
    override suspend fun deleteSleepEntry(entryId: String) {
        val existingEntries = loadSleepEntries().toMutableList()
        existingEntries.removeAll { it.id == entryId }
        saveSleepEntries(existingEntries)
    }
    
    // MARK: - Medications
    
    override suspend fun saveMedications(medications: List<Medication>) {
        secureLocalStorage.saveMedications(medications)
    }
    
    override suspend fun loadMedications(): List<Medication> {
        return secureLocalStorage.loadMedications()
    }
    
    override suspend fun addMedication(medication: Medication) {
        val existingMedications = loadMedications().toMutableList()
        
        // Remove existing medication with same ID if it exists
        existingMedications.removeAll { it.id == medication.id }
        
        // Add the new medication
        existingMedications.add(medication)
        
        // Sort by name
        existingMedications.sortBy { it.name }
        
        saveMedications(existingMedications)
    }
    
    override suspend fun updateMedication(medication: Medication) {
        // Same implementation as add - it will replace existing
        addMedication(medication)
    }
    
    override suspend fun deleteMedication(medicationId: String) {
        val existingMedications = loadMedications().toMutableList()
        existingMedications.removeAll { it.id == medicationId }
        saveMedications(existingMedications)
    }
    
    // MARK: - Medication Entries
    
    override suspend fun saveMedicationEntries(entries: List<MedicationEntry>) {
        secureLocalStorage.saveMedicationEntries(entries)
    }
    
    override suspend fun loadMedicationEntries(): List<MedicationEntry> {
        return secureLocalStorage.loadMedicationEntries()
    }
    
    override suspend fun addMedicationEntry(entry: MedicationEntry) {
        val existingEntries = loadMedicationEntries().toMutableList()
        
        // Remove existing entry with same ID if it exists
        existingEntries.removeAll { it.id == entry.id }

        // Add the new entry
        existingEntries.add(entry)

        // Sort by date (most recent first)
        existingEntries.sortByDescending { it.date }

        saveMedicationEntries(existingEntries)
    }
    
    override suspend fun deleteMedicationEntry(entryId: String) {
        val existingEntries = loadMedicationEntries().toMutableList()
        existingEntries.removeAll { it.id == entryId }
        saveMedicationEntries(existingEntries)
    }
    
    // MARK: - Clear Data
    
    override suspend fun clearAllHealthData() {
        saveMoodEntries(emptyList())
        saveSleepEntries(emptyList())
        saveMedications(emptyList())
        saveMedicationEntries(emptyList())
    }
}

/**
 * Default implementation of UserSettingsRepository using SecureLocalStorage
 */
@Singleton
class DefaultUserSettingsRepository @Inject constructor(
    private val secureLocalStorage: SecureLocalStorage
) : UserSettingsRepository {
    
    override suspend fun saveUserSettings(settings: UserSettings) {
        secureLocalStorage.saveUserSettings(settings)
    }
    
    override suspend fun loadUserSettings(): UserSettings {
        return secureLocalStorage.loadUserSettings()
    }
    
    override fun getUserSettingsFlow(): Flow<UserSettings> {
        return secureLocalStorage.getUserSettingsFlow()
    }
    
    override suspend fun saveHasCompletedOnboarding(hasCompleted: Boolean) {
        secureLocalStorage.saveHasCompletedOnboarding(hasCompleted)
    }
    
    override suspend fun loadHasCompletedOnboarding(): Boolean {
        return secureLocalStorage.loadHasCompletedOnboarding()
    }
    
    override fun getHasCompletedOnboardingFlow(): Flow<Boolean> {
        return secureLocalStorage.getHasCompletedOnboardingFlow()
    }
    
    override suspend fun updateUserName(userName: String) {
        val currentSettings = loadUserSettings()
        saveUserSettings(currentSettings.copy(userName = userName))
    }
    
    override suspend fun updateNotificationsEnabled(enabled: Boolean) {
        val currentSettings = loadUserSettings()
        saveUserSettings(currentSettings.copy(notificationsEnabled = enabled))
    }
    
    override suspend fun updateSecurityEnabled(enabled: Boolean) {
        val currentSettings = loadUserSettings()
        saveUserSettings(currentSettings.copy(securityEnabled = enabled))
    }
    
    override suspend fun clearAllSettings() {
        secureLocalStorage.clearAllData()
    }
}
