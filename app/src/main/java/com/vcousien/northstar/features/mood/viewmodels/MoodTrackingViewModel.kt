package com.vcousien.northstar.features.mood.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vcousien.northstar.core.models.MoodEntry
import com.vcousien.northstar.features.mood.models.MoodLevel
import com.vcousien.northstar.core.models.UserSettings
import com.vcousien.northstar.core.storage.LocalStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import java.util.UUID
import javax.inject.Inject
import kotlin.math.abs

/**
 * ViewModel that manages mood entry data and related operations
 */
@HiltViewModel
class MoodTrackingViewModel @Inject constructor(
    private val localStorage: LocalStorage
) : ViewModel() {
    
    // MARK: - Mutable State
    
    /** Collection of recorded mood entries */
    private val _moodEntries = MutableStateFlow<List<MoodEntry>>(emptyList())
    val moodEntries: StateFlow<List<MoodEntry>> = _moodEntries.asStateFlow()
    
    /** The currently active mood entry being edited */
    var currentEntry by mutableStateOf(createDefaultEntry())
        private set
    
    /** Flag indicating if a new entry is being added */
    var isAddingNewEntry by mutableStateOf(false)
        private set
    
    /** Flag controlling the visibility of the add entry sheet */
    var shouldShowAddSheet by mutableStateOf(false)
        private set
    
    /** Currently selected date for viewing mood data */
    var selectedDate by mutableStateOf(Clock.System.now())
        private set
    
    /** Flag controlling the visibility of entry detail sheet */
    var showingEntryDetail by mutableStateOf(false)
        private set
    
    /** ID of the currently selected entry */
    var selectedEntryId by mutableStateOf<String?>(null)
        private set
    
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
     * Loads mood entries from storage
     */
    private fun loadData() {
        viewModelScope.launch {
            try {
                val entries = localStorage.loadMoodEntries()
                _moodEntries.value = entries.sortedByDescending { it.date }
            } catch (e: Exception) {
                // Handle error silently - start with empty list
                _moodEntries.value = emptyList()
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
                // Handle error silently - use default settings
                _userSettings.value = UserSettings.DEFAULT
            }
        }
    }
    
    // MARK: - Entry Management
    
    /**
     * Adds a new mood entry
     * @param entry Entry to add
     */
    fun addEntry(entry: MoodEntry) {
        viewModelScope.launch {
            val updatedEntries = _moodEntries.value + entry
            _moodEntries.value = updatedEntries.sortedByDescending { it.date }
            saveEntries()
        }
    }
    
    /**
     * Updates an existing mood entry
     * @param entry Entry with updated values
     */
    fun updateEntry(entry: MoodEntry) {
        viewModelScope.launch {
            val updatedEntries = _moodEntries.value.map { existingEntry ->
                if (existingEntry.id == entry.id) entry else existingEntry
            }
            _moodEntries.value = updatedEntries.sortedByDescending { it.date }
            saveEntries()
        }
    }
    
    /**
     * Deletes a mood entry by ID
     * @param id ID of the entry to delete
     */
    fun deleteEntry(id: String) {
        viewModelScope.launch {
            val updatedEntries = _moodEntries.value.filter { it.id != id }
            _moodEntries.value = updatedEntries
            saveEntries()
        }
    }
    
    /**
     * Saves all mood entries to persistent storage
     */
    private suspend fun saveEntries() {
        try {
            localStorage.saveMoodEntries(_moodEntries.value)
        } catch (e: Exception) {
            // Handle error silently for now
            // In a production app, you might want to show an error message
        }
    }
    
    // MARK: - Entry Queries
    
    /**
     * Gets a mood entry for a specific date
     * @param date The date to find an entry for
     * @return The entry for that date, if any
     */
    fun getEntry(date: Instant): MoodEntry? {
        val targetLocalDate = date.toLocalDateTime(TimeZone.currentSystemDefault()).date
        return _moodEntries.value.firstOrNull { entry ->
            val entryLocalDate = entry.dateAsInstant.toLocalDateTime(TimeZone.currentSystemDefault()).date
            entryLocalDate == targetLocalDate
        }
    }
    
    /**
     * Gets mood entries within a date range
     * @param startDate Start of the date range
     * @param endDate End of the date range
     * @return Array of entries within the range, sorted by date
     */
    fun getEntries(startDate: Instant, endDate: Instant): List<MoodEntry> {
        return _moodEntries.value.filter { entry ->
            entry.dateAsInstant >= startDate && entry.dateAsInstant <= endDate
        }.sortedBy { it.date }
    }
    
    /**
     * Gets the mood trend data for the past 7 days
     * @return Array of mood level values for the last 7 days
     */
    fun getMoodTrend(): List<Double> {
        val today = Clock.System.now()
        val trend = mutableListOf<Double>()
        
        // Get data for each of the past 7 days
        for (dayOffset in 6 downTo 0) {
            val date = today.minus(dayOffset, DateTimeUnit.DAY, TimeZone.currentSystemDefault())
            val entry = getEntry(date)
            trend.add(entry?.level?.value?.toDouble() ?: MoodLevel.NEUTRAL.value.toDouble())
        }
        
        return trend
    }
    
    // MARK: - UI State Management
    
    /**
     * Selects an entry for displaying details
     * @param id ID of the entry to select
     */
    fun selectEntry(id: String) {
        selectedEntryId = id
        showingEntryDetail = true
    }
    
    /**
     * Prepares a new mood entry for editing
     */
    fun prepareNewEntry() {
        currentEntry = createDefaultEntry()
        isAddingNewEntry = true
        shouldShowAddSheet = true
    }
    
    /**
     * Prepares an existing entry for editing
     * @param entry The entry to edit
     */
    fun prepareEntryForEditing(entry: MoodEntry) {
        currentEntry = entry
        isAddingNewEntry = true
        shouldShowAddSheet = true
    }
    
    /**
     * Cancels the current editing operation
     */
    fun cancelEditing() {
        isAddingNewEntry = false
        selectedEntryId = null
        showingEntryDetail = false
    }
    
    /**
     * Gets the currently selected entry
     * @return The selected entry, if any
     */
    fun getSelectedEntry(): MoodEntry? {
        return selectedEntryId?.let { id ->
            _moodEntries.value.firstOrNull { it.id == id }
        }
    }
    
    /**
     * Updates the selected date
     * @param date New selected date
     */
    fun updateSelectedDate(date: Instant) {
        selectedDate = date
    }
    
    /**
     * Updates the current entry being edited
     * @param entry Updated entry
     */
    fun updateCurrentEntry(entry: MoodEntry) {
        currentEntry = entry
    }
    
    // MARK: - Analytics
    
    /**
     * Calculates average mood level for a given period
     * @param days Number of days to include
     * @return Average mood level value
     */
    fun calculateAverageMood(days: Int = 7): Double {
        val today = Clock.System.now()
        val startDate = today.minus(days - 1, DateTimeUnit.DAY, TimeZone.currentSystemDefault())
        
        val relevantEntries = getEntries(startDate = startDate, endDate = today)
        
        if (relevantEntries.isEmpty()) {
            return MoodLevel.NEUTRAL.value.toDouble()
        }
        
        val sum = relevantEntries.sumOf { it.level.value.toDouble() }
        return sum / relevantEntries.size
    }
    
    /**
     * Checks if there are significant mood swings in recent entries
     * @param threshold The threshold that constitutes a significant change
     * @return True if significant mood swings are detected
     */
    fun hasMoodSwings(threshold: Int = 3): Boolean {
        val today = Clock.System.now()
        val startDate = today.minus(14, DateTimeUnit.DAY, TimeZone.currentSystemDefault())
        
        val recentEntries = getEntries(startDate = startDate, endDate = today)
        
        if (recentEntries.size < 2) return false
        
        // Check for significant differences between consecutive entries
        for (i in 0 until recentEntries.size - 1) {
            val difference = abs(recentEntries[i].level.value - recentEntries[i + 1].level.value)
            if (difference >= threshold) {
                return true
            }
        }
        
        return false
    }
    
    // MARK: - Utility Methods
    
    /**
     * Creates a default mood entry with current timestamp
     */
    private fun createDefaultEntry(): MoodEntry {
        return MoodEntry(
            id = UUID.randomUUID().toString(),
            date = Clock.System.now(),
            level = MoodLevel.NEUTRAL,
            notes = "",
            energyLevel = 5.0,
            anxietyLevel = 0.0,
            sleepQuality = null,
            irritabilityLevel = null
        )
    }
    
    /**
     * Resets all mood tracking data
     * This is used when clearing all application data
     */
    fun reset() {
        println("🔄 Resetting MoodTrackingViewModel...")
        
        viewModelScope.launch {
            _moodEntries.value = emptyList()
            currentEntry = createDefaultEntry()
            isAddingNewEntry = false
            shouldShowAddSheet = false
            selectedDate = Clock.System.now()
            showingEntryDetail = false
            selectedEntryId = null
            
            // Clear data from storage as well
            try {
                localStorage.saveMoodEntries(emptyList())
            } catch (e: Exception) {
                // Handle error silently
            }
            
            println("✅ MoodTrackingViewModel reset complete")
        }
    }
}
