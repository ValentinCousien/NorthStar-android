package com.vcousien.northstar.core.storage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vcousien.northstar.core.models.MoodEntry
import com.vcousien.northstar.features.mood.models.MoodLevel
import com.vcousien.northstar.core.models.SleepEntry
import com.vcousien.northstar.core.models.Medication
import com.vcousien.northstar.core.models.MedicationFrequency
import com.vcousien.northstar.core.models.TimeOfDay
import com.vcousien.northstar.core.models.UserSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalTime
import javax.inject.Inject

/**
 * Example ViewModel demonstrating how to use the secure storage repositories
 */
@HiltViewModel
class ExampleStorageViewModel @Inject constructor(
    private val healthDataRepository: HealthDataRepository,
    private val userSettingsRepository: UserSettingsRepository
) : ViewModel() {

    // MARK: - UI State
    
    private val _moodEntries = MutableStateFlow<List<MoodEntry>>(emptyList())
    val moodEntries: StateFlow<List<MoodEntry>> = _moodEntries.asStateFlow()
    
    private val _medications = MutableStateFlow<List<Medication>>(emptyList())
    val medications: StateFlow<List<Medication>> = _medications.asStateFlow()
    
    // User settings as Flow for reactive updates
    val userSettings: StateFlow<UserSettings> = userSettingsRepository
        .getUserSettingsFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserSettings.DEFAULT
        )
    
    init {
        loadHealthData()
    }
    
    // MARK: - Public Methods
    
    /**
     * Adds a new mood entry
     */
    fun addMoodEntry(
        level: MoodLevel,
        notes: String = "",
        energyLevel: Double = 5.0,
        anxietyLevel: Double = 0.0
    ) {
        viewModelScope.launch {
            try {
                val entry = MoodEntry(
                    date = Clock.System.now(),
                    level = level,
                    notes = notes,
                    energyLevel = energyLevel,
                    anxietyLevel = anxietyLevel
                )
                
                healthDataRepository.addMoodEntry(entry)
                loadMoodEntries() // Refresh the list
                
            } catch (e: Exception) {
                // Handle error - in real app, you'd want proper error handling
                println("Error adding mood entry: ${e.message}")
            }
        }
    }
    
    /**
     * Adds a new sleep entry
     */
    fun addSleepEntry(
        quality: Double,
        durationHours: Double,
        notes: String = ""
    ) {
        viewModelScope.launch {
            try {
                val now = Clock.System.now()
                val startTime = now.minus(kotlin.time.Duration.parse("${durationHours}h"))
                
                val entry = SleepEntry(
                    date = now,
                    startTime = startTime,
                    endTime = now,
                    quality = quality,
                    notes = notes
                )
                
                healthDataRepository.addSleepEntry(entry)
                
            } catch (e: Exception) {
                println("Error adding sleep entry: ${e.message}")
            }
        }
    }
    
    /**
     * Adds a new medication
     */
    fun addMedication(
        name: String,
        dosage: String,
        frequency: MedicationFrequency = MedicationFrequency.DAILY,
        timeOfDay: List<TimeOfDay> = listOf(TimeOfDay.MORNING),
        notes: String = ""
    ) {
        viewModelScope.launch {
            try {
                val medication = Medication(
                    name = name,
                    dosage = dosage,
                    frequency = frequency,
                    timeOfDay = timeOfDay,
                    notes = notes,
                    lastModified = Clock.System.now().toEpochMilliseconds()
                )

                healthDataRepository.addMedication(medication)
                loadMedications() // Refresh the list

            } catch (e: Exception) {
                println("Error adding medication: ${e.message}")
            }
        }
    }
    
    /**
     * Updates user name
     */
    fun updateUserName(name: String) {
        viewModelScope.launch {
            try {
                userSettingsRepository.updateUserName(name)
            } catch (e: Exception) {
                println("Error updating user name: ${e.message}")
            }
        }
    }
    
    /**
     * Updates notification settings
     */
    fun updateNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            try {
                userSettingsRepository.updateNotificationsEnabled(enabled)
            } catch (e: Exception) {
                println("Error updating notifications: ${e.message}")
            }
        }
    }
    
    /**
     * Updates security settings
     */
    fun updateSecurityEnabled(enabled: Boolean) {
        viewModelScope.launch {
            try {
                userSettingsRepository.updateSecurityEnabled(enabled)
            } catch (e: Exception) {
                println("Error updating security: ${e.message}")
            }
        }
    }
    
    /**
     * Completes onboarding
     */
    fun completeOnboarding() {
        viewModelScope.launch {
            try {
                userSettingsRepository.saveHasCompletedOnboarding(true)
            } catch (e: Exception) {
                println("Error completing onboarding: ${e.message}")
            }
        }
    }
    
    /**
     * Clears all user data (for account deletion, etc.)
     */
    fun clearAllData() {
        viewModelScope.launch {
            try {
                healthDataRepository.clearAllHealthData()
                userSettingsRepository.clearAllSettings()
                
                // Refresh UI
                loadHealthData()
                
            } catch (e: Exception) {
                println("Error clearing data: ${e.message}")
            }
        }
    }
    
    // MARK: - Private Methods
    
    private fun loadHealthData() {
        loadMoodEntries()
        loadMedications()
    }
    
    private fun loadMoodEntries() {
        viewModelScope.launch {
            try {
                val entries = healthDataRepository.loadMoodEntries()
                _moodEntries.value = entries
            } catch (e: Exception) {
                println("Error loading mood entries: ${e.message}")
                _moodEntries.value = emptyList()
            }
        }
    }
    
    private fun loadMedications() {
        viewModelScope.launch {
            try {
                val medications = healthDataRepository.loadMedications()
                _medications.value = medications
            } catch (e: Exception) {
                println("Error loading medications: ${e.message}")
                _medications.value = emptyList()
            }
        }
    }
}
