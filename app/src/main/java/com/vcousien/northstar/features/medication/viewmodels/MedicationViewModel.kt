package com.vcousien.northstar.features.medication.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vcousien.northstar.core.models.*
import com.vcousien.northstar.core.storage.LocalStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.math.abs

/**
 * ViewModel that manages medication data and related operations
 * Ported from iOS MedicationViewModel
 */
@HiltViewModel
class MedicationViewModel @javax.inject.Inject constructor(
    private val localStorage: LocalStorage
) : ViewModel() {

    // MARK: - Mutable State

    /** Collection of defined medications */
    private val _medications = MutableStateFlow<List<Medication>>(emptyList())
    val medications: StateFlow<List<Medication>> = _medications.asStateFlow()

    /** Collection of recorded medication entries */
    private val _medicationEntries = MutableStateFlow<List<MedicationEntry>>(emptyList())
    val medicationEntries: StateFlow<List<MedicationEntry>> = _medicationEntries.asStateFlow()

    /** The currently active medication being edited */
    var currentMedication by mutableStateOf<Medication?>(null)
        private set

    /** The currently active medication entry being edited */
    var currentEntry by mutableStateOf<MedicationEntry?>(null)
        private set

    /** Flag indicating if a new medication is being added */
    var isAddingNewMedication by mutableStateOf(false)
        private set

    /** Flag indicating if a new entry is being added */
    var isAddingNewEntry by mutableStateOf(false)
        private set

    /** Flag controlling the visibility of the add medication sheet */
    var shouldShowAddSheet by mutableStateOf(false)

    /** Flag controlling the visibility of the add entry sheet */
    var shouldShowEntrySheet by mutableStateOf(false)

    /** Currently selected date for viewing medication data */
    var selectedDate by mutableStateOf(Clock.System.now())

    /** User settings */
    private val _userSettings = MutableStateFlow(UserSettings.DEFAULT)
    val userSettings: StateFlow<UserSettings> = _userSettings.asStateFlow()

    // MARK: - Initialization

    init {
        loadData()
        loadUserSettings()
    }

    // MARK: - Data Loading

    /**
     * Loads medication data from storage
     */
    private fun loadData() {
        viewModelScope.launch {
            try {
                println("📂 LOADING MEDICATION DATA FROM STORAGE...")
                val loadedMedications = localStorage.loadMedications()
                val loadedEntries = localStorage.loadMedicationEntries()

                _medications.value = loadedMedications
                _medicationEntries.value = loadedEntries

                println("   ✅ Loaded ${loadedMedications.size} medications")
                println("   ✅ Loaded ${loadedEntries.size} entries")
                println("📂 DATA LOADING COMPLETE\n")
            } catch (e: Exception) {
                println("   ❌ Failed to load medication data: ${e.message}")
                _medications.value = emptyList()
                _medicationEntries.value = emptyList()
            }
        }
    }

    /**
     * Loads user settings from storage
     */
    private fun loadUserSettings() {
        viewModelScope.launch {
            try {
                val settings = localStorage.loadUserSettings()
                _userSettings.value = settings
            } catch (e: Exception) {
                _userSettings.value = UserSettings.DEFAULT
            }
        }
    }

    // MARK: - Medication Management

    /**
     * Adds a new medication
     * @param medication Medication to add
     */
    fun addMedication(medication: Medication) {
        viewModelScope.launch {
            val updatedMedications = _medications.value + medication
            _medications.value = updatedMedications
            saveMedications()
            println("✅ Added medication: ${medication.name}")
        }
    }

    /**
     * Updates an existing medication
     * @param medication Medication with updated values
     */
    fun updateMedication(medication: Medication) {
        viewModelScope.launch {
            val updatedMedications = _medications.value.map { existingMedication ->
                if (existingMedication.id == medication.id) medication else existingMedication
            }
            _medications.value = updatedMedications

            // Also update all references to this medication in entries
            updateMedicationReferences(medication)

            saveMedications()
            println("✅ Updated medication: ${medication.name}")
        }
    }

    /**
     * Updates all medication entry references when a medication is updated
     * @param medication The updated medication
     */
    private fun updateMedicationReferences(medication: Medication) {
        viewModelScope.launch {
            val updatedEntries = _medicationEntries.value.map { entry ->
                if (entry.medicationId == medication.id) {
                    entry.copy(medicationName = medication.name)
                } else {
                    entry
                }
            }
            _medicationEntries.value = updatedEntries
            saveEntries()
            println("🔄 Updated ${updatedEntries.count { it.medicationId == medication.id }} entries for medication ${medication.name}")
        }
    }

    /**
     * Deletes a medication
     * @param id ID of the medication to delete
     */
    fun deleteMedication(id: String) {
        viewModelScope.launch {
            val medication = _medications.value.firstOrNull { it.id == id }
            val entriesToDelete = _medicationEntries.value.filter { it.medicationId == id }

            _medications.value = _medications.value.filter { it.id != id }
            _medicationEntries.value = _medicationEntries.value.filter { it.medicationId != id }

            saveMedications()
            saveEntries()

            println("✅ Deleted medication: ${medication?.name}")
            println("✅ Deleted ${entriesToDelete.size} associated entries")
        }
    }

    /**
     * Saves all medications to persistent storage
     */
    private suspend fun saveMedications() {
        try {
            localStorage.saveMedications(_medications.value)
        } catch (e: Exception) {
            println("❌ Failed to save medications: ${e.message}")
        }
    }

    // MARK: - Entry Management

    /**
     * Adds a new medication entry
     * @param entry Entry to add
     */
    fun addEntry(entry: MedicationEntry) {
        viewModelScope.launch {
            println("➕ ADDING ENTRY:")
            println("   Entry ID: ${entry.id}")
            println("   Medication: ${entry.medicationName} (ID: ${entry.medicationId})")
            println("   Time of Day: ${entry.timeOfDay.displayName}")
            println("   Taken: ${entry.taken}")
            println("   Date: ${entry.dateAsInstant}")

            val updatedEntries = _medicationEntries.value + entry
            _medicationEntries.value = updatedEntries
            saveEntries()

            println("✅ Saved medication entry")
            println("   Total entries now: ${_medicationEntries.value.size}")
        }
    }

    /**
     * Updates an existing medication entry
     * @param entry Entry with updated values
     */
    fun updateEntry(entry: MedicationEntry) {
        viewModelScope.launch {
            println("🔄 UPDATING ENTRY:")
            println("   Entry ID: ${entry.id}")
            println("   Medication: ${entry.medicationName} (ID: ${entry.medicationId})")
            println("   Time of Day: ${entry.timeOfDay.displayName}")
            println("   Taken: ${entry.taken}")

            val existingIndex = _medicationEntries.value.indexOfFirst { it.id == entry.id }
            if (existingIndex != -1) {
                val updatedEntries = _medicationEntries.value.toMutableList()
                updatedEntries[existingIndex] = entry
                _medicationEntries.value = updatedEntries
                saveEntries()
                println("✅ Updated medication entry")
            } else {
                println("   ⚠️ Entry not found - adding instead")
                addEntry(entry)
            }
        }
    }

    /**
     * Deletes a medication entry
     * @param id ID of the entry to delete
     */
    fun deleteEntry(id: String) {
        viewModelScope.launch {
            _medicationEntries.value = _medicationEntries.value.filter { it.id != id }
            saveEntries()
            println("✅ Deleted medication entry")
        }
    }

    /**
     * Saves all medication entries to persistent storage
     */
    private suspend fun saveEntries() {
        try {
            localStorage.saveMedicationEntries(_medicationEntries.value)
        } catch (e: Exception) {
            println("❌ Failed to save medication entries: ${e.message}")
        }
    }

    // MARK: - Entry Queries

    /**
     * Gets medication entries for a specific date
     * @param date The date to find entries for
     * @return List of entries for that date
     */
    fun getEntries(date: Instant): List<MedicationEntry> {
        val timeZone = TimeZone.currentSystemDefault()
        val targetLocalDate = date.toLocalDateTime(timeZone).date

        return _medicationEntries.value.filter { entry ->
            val entryLocalDate = entry.dateAsInstant.toLocalDateTime(timeZone).date
            entryLocalDate == targetLocalDate
        }
    }

    /**
     * Gets medication entries within a date range
     * @param startDate Start of the date range
     * @param endDate End of the date range
     * @return List of entries within the range, sorted by date
     */
    fun getEntries(startDate: Instant, endDate: Instant): List<MedicationEntry> {
        return _medicationEntries.value.filter { entry ->
            entry.dateAsInstant >= startDate && entry.dateAsInstant <= endDate
        }.sortedBy { it.date }
    }

    /**
     * Gets medications that should be taken today
     * @return List of medications scheduled for today
     */
    fun getMedicationsForToday(): List<Medication> {
        return _medications.value.filter { medication ->
            when (medication.frequency) {
                MedicationFrequency.DAILY -> true
                MedicationFrequency.WEEKLY -> false // Simplified - in real app would check day of week
                MedicationFrequency.AS_NEEDED -> false
                MedicationFrequency.CUSTOM -> false
            }
        }
    }

    /**
     * Gets medication dosages scheduled for a specific time of day
     * @param timeOfDay The time of day to check
     * @return List of medications due at the specified time
     */
    fun getMedicationsForTimeOfDay(timeOfDay: TimeOfDay): List<Medication> {
        return getMedicationsForToday().filter { medication ->
            medication.timeOfDay.contains(timeOfDay)
        }
    }

    /**
     * Checks if all medications have been taken for a specific date
     * @param date The date to check
     * @return True if all scheduled medications have been taken
     */
    fun allMedicationsTaken(date: Instant): Boolean {
        val scheduledMedications = getMedicationsForToday()
        if (scheduledMedications.isEmpty()) {
            return false
        }

        val dayEntries = getEntries(date)

        // Check that each scheduled medication has ALL its time-of-day doses taken
        for (medication in scheduledMedications) {
            for (timeOfDay in medication.timeOfDay) {
                val takenForThisTime = dayEntries.any { entry ->
                    entry.medicationId == medication.id &&
                            entry.timeOfDay == timeOfDay &&
                            entry.taken
                }

                if (!takenForThisTime) {
                    return false
                }
            }
        }

        return true
    }

    // MARK: - Analytics

    /**
     * Calculates medication adherence percentage over a period
     * @param days Number of days to include (excludes today since it's incomplete)
     * @return Adherence percentage (0-100)
     */
    fun calculateAdherence(days: Int = 7): Double {
        val today = Clock.System.now()
        val timeZone = TimeZone.currentSystemDefault()

        var totalScheduled = 0
        var totalTaken = 0

        // For each day in the period (excluding today - start from yesterday)
        for (dayOffset in 1..days) {
            val date = today.minus(dayOffset, DateTimeUnit.DAY, timeZone)
            val dayEntries = getEntries(date)

            // Count scheduled doses for this day (each medication x each timeOfDay)
            val scheduledMedications = getMedicationsForToday()
            for (medication in scheduledMedications) {
                totalScheduled += medication.timeOfDay.size

                // Count taken doses for this medication
                for (timeOfDay in medication.timeOfDay) {
                    val takenForThisTime = dayEntries.any { entry ->
                        entry.medicationId == medication.id &&
                                entry.timeOfDay == timeOfDay &&
                                entry.taken
                    }

                    if (takenForThisTime) {
                        totalTaken++
                    }
                }
            }
        }

        // Calculate adherence percentage
        return if (totalScheduled == 0) {
            0.0
        } else {
            (totalTaken.toDouble() / totalScheduled.toDouble()) * 100.0
        }
    }

    /**
     * Gets entry statistics for a medication
     * @param medicationId ID of the medication to analyze
     * @param days Number of days to include
     * @return Map with statistics about the medication usage
     */
    fun getMedicationStats(medicationId: String, days: Int = 30): Map<String, Any> {
        val today = Clock.System.now()
        val timeZone = TimeZone.currentSystemDefault()
        val startDate = today.minus(days - 1, DateTimeUnit.DAY, timeZone)

        // Get relevant entries
        val entries = _medicationEntries.value.filter { entry ->
            entry.medicationId == medicationId && entry.dateAsInstant >= startDate && entry.dateAsInstant <= today
        }

        // Calculate statistics
        val totalEntries = entries.size
        val takenEntries = entries.count { it.taken }
        val adherenceRate = if (totalEntries > 0) {
            (takenEntries.toDouble() / totalEntries.toDouble()) * 100.0
        } else {
            0.0
        }

        // Get common side effects
        val sideEffectCounts = mutableMapOf<String, Int>()
        for (entry in entries) {
            for (sideEffect in entry.sideEffects) {
                sideEffectCounts[sideEffect.name] = (sideEffectCounts[sideEffect.name] ?: 0) + 1
            }
        }

        // Sort side effects by frequency (most common first)
        val sortedSideEffects = sideEffectCounts.entries
            .sortedByDescending { it.value }
            .take(3)
            .map { it.key }

        return mapOf(
            "totalEntries" to totalEntries,
            "takenEntries" to takenEntries,
            "adherenceRate" to adherenceRate,
            "commonSideEffects" to sortedSideEffects
        )
    }

    /**
     * Filters medications by name
     * @param searchText Text to search for in medication names
     * @return List of medications matching the search criteria
     */
    fun searchMedications(searchText: String): List<Medication> {
        if (searchText.isEmpty()) {
            return _medications.value
        }

        return _medications.value.filter { medication ->
            medication.name.contains(searchText, ignoreCase = true)
        }
    }

    /**
     * Gets the next upcoming medication reminder
     * @return NextReminderInfo with medication and time, or null if no reminders are scheduled
     */
    fun getNextReminder(): NextReminderInfo? {
        val now = Clock.System.now()
        val timeZone = TimeZone.currentSystemDefault()
        val currentDateTime = now.toLocalDateTime(timeZone)
        val currentHour = currentDateTime.hour
        val currentMinute = currentDateTime.minute
        val currentTimeInMinutes = currentHour * 60 + currentMinute

        // Get all medications with reminders enabled
        val medicationsWithReminders = _medications.value.filter { medication ->
            medication.remindersEnabled && medication.frequency != MedicationFrequency.AS_NEEDED
        }

        if (medicationsWithReminders.isEmpty()) {
            return null
        }

        // Build a list of all upcoming reminders for today and tomorrow
        val allReminders = mutableListOf<NextReminderInfo>()

        for (medication in medicationsWithReminders) {
            for (timeOfDay in medication.timeOfDay) {
                // Get the reminder time (custom or default)
                val customReminder = medication.customReminders[timeOfDay]
                val (hour, minute) = if (customReminder != null && customReminder.enabled) {
                    Pair(customReminder.hour, customReminder.minute)
                } else {
                    timeOfDay.defaultReminderTime()
                }

                val reminderTimeInMinutes = hour * 60 + minute

                // Calculate the instant for this reminder
                val reminderDateTime = if (reminderTimeInMinutes > currentTimeInMinutes) {
                    // Today
                    LocalDateTime(
                        currentDateTime.date,
                        LocalTime(hour, minute)
                    )
                } else {
                    // Tomorrow
                    val tomorrowDate = currentDateTime.date.plus(1, DateTimeUnit.DAY)
                    LocalDateTime(
                        tomorrowDate,
                        LocalTime(hour, minute)
                    )
                }

                val reminderInstant = reminderDateTime.toInstant(timeZone)

                allReminders.add(
                    NextReminderInfo(
                        medication = medication,
                        timeOfDay = timeOfDay,
                        time = reminderInstant
                    )
                )
            }
        }

        // Return the nearest reminder
        return allReminders.minByOrNull { it.time }
    }

    // MARK: - UI State Management

    /**
     * Prepares a new medication for editing
     */
    fun prepareNewMedication() {
        currentMedication = Medication()
        isAddingNewMedication = true
        shouldShowAddSheet = true
    }

    /**
     * Prepares an existing medication for editing
     * @param medication The medication to edit
     */
    fun prepareMedicationForEditing(medication: Medication) {
        currentMedication = medication
        isAddingNewMedication = false
        shouldShowAddSheet = true
    }

    /**
     * Prepares a new entry for a specific medication and time of day
     * @param medication The medication to create an entry for
     * @param timeOfDay The specific time of day for this entry
     */
    fun prepareNewEntry(medication: Medication, timeOfDay: TimeOfDay) {
        val entry = MedicationEntry(
            date = selectedDate,
            medicationId = medication.id,
            medicationName = medication.name,
            timeOfDay = timeOfDay,
            taken = false,
            actualTime = null,
            sideEffects = emptyList(),
            notes = ""
        )
        currentEntry = entry
        isAddingNewEntry = true
        shouldShowEntrySheet = true
    }

    /**
     * Cancels the current editing operation
     */
    fun cancelEditing() {
        isAddingNewMedication = false
        isAddingNewEntry = false
        shouldShowAddSheet = false
        shouldShowEntrySheet = false
    }

    /**
     * Updates the selected date
     * @param date New selected date
     */
    fun updateSelectedDate(date: Instant) {
        selectedDate = date
    }

    // MARK: - Utility Methods

    /**
     * Resets all medication tracking data
     * This is used when clearing all application data
     */
    fun reset() {
        println("🔄 Resetting MedicationViewModel...")

        viewModelScope.launch {
            _medications.value = emptyList()
            _medicationEntries.value = emptyList()
            currentMedication = null
            currentEntry = null
            isAddingNewMedication = false
            isAddingNewEntry = false
            shouldShowAddSheet = false
            shouldShowEntrySheet = false
            selectedDate = Clock.System.now()

            // Clear data from storage as well
            try {
                localStorage.saveMedications(emptyList())
                localStorage.saveMedicationEntries(emptyList())
            } catch (e: Exception) {
                println("❌ Failed to clear medication data: ${e.message}")
            }

            println("✅ MedicationViewModel reset complete")
        }
    }
}

/**
 * Data class representing the next medication reminder
 * @property medication The medication to be taken
 * @property timeOfDay The time of day for the reminder
 * @property time The exact time of the reminder
 */
data class NextReminderInfo(
    val medication: Medication,
    val timeOfDay: TimeOfDay,
    val time: Instant
)
