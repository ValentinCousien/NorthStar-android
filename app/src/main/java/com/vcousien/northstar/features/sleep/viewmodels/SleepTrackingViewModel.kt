package com.vcousien.northstar.features.sleep.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vcousien.northstar.core.models.SleepEntry
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
import kotlinx.datetime.toLocalDateTime
import java.util.UUID
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.time.Duration.Companion.hours

/**
 * ViewModel that manages sleep entry data and related operations
 */
@HiltViewModel
class SleepTrackingViewModel @javax.inject.Inject constructor(
    private val localStorage: LocalStorage
) : ViewModel() {

    // MARK: - Mutable State

    /** Collection of recorded sleep entries */
    private val _sleepEntries = MutableStateFlow<List<SleepEntry>>(emptyList())
    val sleepEntries: StateFlow<List<SleepEntry>> = _sleepEntries.asStateFlow()

    /** The currently active sleep entry being edited */
    var currentEntry by mutableStateOf(createDefaultEntry())
        private set

    /** Flag indicating if a new entry is being added */
    var isAddingNewEntry by mutableStateOf(false)
        private set

    /** Flag controlling the visibility of the add entry sheet */
    var shouldShowAddSheet by mutableStateOf(false)
        private set

    /** Currently selected date for viewing sleep data */
    var selectedDate by mutableStateOf(Clock.System.now())
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
     * Loads sleep entries from storage
     */
    private fun loadData() {
        viewModelScope.launch {
            try {
                val entries = localStorage.loadSleepEntries()
                _sleepEntries.value = entries.sortedByDescending { it.date }
            } catch (e: Exception) {
                // Handle error silently - start with empty list
                _sleepEntries.value = emptyList()
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
     * Adds a new sleep entry
     * @param entry Entry to add
     */
    fun addEntry(entry: SleepEntry) {
        viewModelScope.launch {
            val updatedEntries = _sleepEntries.value + entry
            _sleepEntries.value = updatedEntries.sortedByDescending { it.date }
            saveEntries()
        }
    }

    /**
     * Updates an existing sleep entry
     * @param entry Entry with updated values
     */
    fun updateEntry(entry: SleepEntry) {
        viewModelScope.launch {
            val updatedEntries = _sleepEntries.value.map { existingEntry ->
                if (existingEntry.id == entry.id) entry else existingEntry
            }
            _sleepEntries.value = updatedEntries.sortedByDescending { it.date }
            saveEntries()
        }
    }

    /**
     * Deletes a sleep entry by ID
     * @param id ID of the entry to delete
     */
    fun deleteEntry(id: String) {
        viewModelScope.launch {
            val updatedEntries = _sleepEntries.value.filter { it.id != id }
            _sleepEntries.value = updatedEntries
            saveEntries()
        }
    }

    /**
     * Saves all sleep entries to persistent storage
     */
    private suspend fun saveEntries() {
        try {
            localStorage.saveSleepEntries(_sleepEntries.value)
        } catch (e: Exception) {
            // Handle error silently for now
            // In a production app, you might want to show an error message
        }
    }

    // MARK: - Entry Queries

    /**
     * Gets a sleep entry for a specific date
     * @param date The date to find an entry for
     * @return The entry for that date, if any
     */
    fun getEntry(date: Instant): SleepEntry? {
        val targetLocalDate = date.toLocalDateTime(TimeZone.currentSystemDefault()).date
        return _sleepEntries.value.firstOrNull { entry ->
            val entryLocalDate = entry.dateAsInstant.toLocalDateTime(TimeZone.currentSystemDefault()).date
            entryLocalDate == targetLocalDate
        }
    }

    /**
     * Gets sleep entries within a date range
     * @param startDate Start of the date range
     * @param endDate End of the date range
     * @return Array of entries within the range, sorted by date
     */
    fun getEntries(startDate: Instant, endDate: Instant): List<SleepEntry> {
        return _sleepEntries.value.filter { entry ->
            entry.dateAsInstant >= startDate && entry.dateAsInstant <= endDate
        }.sortedBy { it.date }
    }

    /**
     * Gets recent sleep entries
     * @param limit Maximum number of entries to return
     * @return List of recent entries, sorted by date (most recent first)
     */
    fun getRecentEntries(limit: Int = 10): List<SleepEntry> {
        return _sleepEntries.value
            .sortedByDescending { it.date }
            .take(limit)
    }

    /**
     * Gets all sleep entries
     * @return List of all entries, sorted by date (most recent first)
     */
    fun getAllEntries(): List<SleepEntry> {
        return _sleepEntries.value.sortedByDescending { it.date }
    }

    // MARK: - Analytics

    /**
     * Calculates the average sleep duration over the past week
     * @return Average sleep duration in seconds
     */
    fun getAverageSleepDuration(): Long {
        val today = Clock.System.now()
        val startDate = today.minus(6, DateTimeUnit.DAY, TimeZone.currentSystemDefault())

        val lastWeekEntries = getEntries(startDate = startDate, endDate = today)

        if (lastWeekEntries.isEmpty()) return 0L

        // Calculate the average duration
        val totalDuration = lastWeekEntries.sumOf { it.durationSeconds }
        return totalDuration / lastWeekEntries.size
    }

    /**
     * Calculates the average sleep quality over a period
     * @param days Number of days to include
     * @return Average sleep quality rating
     */
    fun getAverageSleepQuality(days: Int = 7): Double {
        val today = Clock.System.now()
        val startDate = today.minus(days - 1, DateTimeUnit.DAY, TimeZone.currentSystemDefault())

        val entries = getEntries(startDate = startDate, endDate = today)

        if (entries.isEmpty()) return 0.0

        // Calculate the average quality
        val totalQuality = entries.sumOf { it.quality }
        return totalQuality / entries.size
    }

    /**
     * Calculates sleep regularity by analyzing consistent sleep/wake times
     * @param days Number of days to analyze
     * @return Regularity score from 0-1, higher is more regular
     */
    fun calculateSleepRegularity(days: Int = 7): Double {
        val today = Clock.System.now()
        val startDate = today.minus(days - 1, DateTimeUnit.DAY, TimeZone.currentSystemDefault())

        val recentEntries = getEntries(startDate = startDate, endDate = today)

        if (recentEntries.size < 3) return 0.0

        // Extract sleep and wake times, converting to minutes from midnight
        val sleepTimes = recentEntries.map { entry ->
            val localTime = entry.startTimeAsInstant.toLocalDateTime(TimeZone.currentSystemDefault())
            localTime.hour * 60 + localTime.minute
        }

        val wakeTimes = recentEntries.map { entry ->
            val localTime = entry.endTimeAsInstant.toLocalDateTime(TimeZone.currentSystemDefault())
            localTime.hour * 60 + localTime.minute
        }

        // Normalize times to be relative to 6 PM (18:00) to handle overnight sleep
        val normalizedSleepTimes = sleepTimes.map { time ->
            val adjustedTime = time - 18 * 60
            if (adjustedTime < 0) adjustedTime + 24 * 60 else adjustedTime
        }

        val normalizedWakeTimes = wakeTimes.map { time ->
            val adjustedTime = time - 18 * 60
            if (adjustedTime < 0) adjustedTime + 24 * 60 else adjustedTime
        }

        // Calculate variance (average difference from the mean)
        val sleepTimeVariance = if (normalizedSleepTimes.isNotEmpty()) {
            val avgSleepTime = normalizedSleepTimes.average()
            normalizedSleepTimes.map { abs(it - avgSleepTime) }.average()
        } else 0.0

        val wakeTimeVariance = if (normalizedWakeTimes.isNotEmpty()) {
            val avgWakeTime = normalizedWakeTimes.average()
            normalizedWakeTimes.map { abs(it - avgWakeTime) }.average()
        } else 0.0

        // Convert to hours and calculate regularity score (lower variance = higher regularity)
        val sleepVarianceHours = sleepTimeVariance / 60.0
        val wakeVarianceHours = wakeTimeVariance / 60.0

        // Score from 0-1, where 0 means 2+ hours variance and 1 means perfect consistency
        val maxVariance = 2.0 // 2 hours deviation gives a score of 0
        val sleepScore = (1.0 - sleepVarianceHours / maxVariance).coerceAtLeast(0.0)
        val wakeScore = (1.0 - wakeVarianceHours / maxVariance).coerceAtLeast(0.0)

        // Average of sleep and wake regularity scores
        return (sleepScore + wakeScore) / 2.0
    }

    // MARK: - UI State Management

    /**
     * Prepares a new sleep entry for editing with default values
     */
    fun prepareNewEntry() {
        val now = Clock.System.now()
        val timeZone = TimeZone.currentSystemDefault()

        // Default sleep time is 10 PM the previous day
        val yesterday = now.minus(1, DateTimeUnit.DAY, timeZone)
        val yesterdayLocal = yesterday.toLocalDateTime(timeZone)
        val defaultSleepTime = Instant.fromEpochMilliseconds(
            yesterdayLocal.date.toEpochDays() * 24L * 60 * 60 * 1000
        ).plus(22.hours)

        // Default wake time is 7 AM today
        val todayLocal = now.toLocalDateTime(timeZone)
        val defaultWakeTime = Instant.fromEpochMilliseconds(
            todayLocal.date.toEpochDays() * 24L * 60 * 60 * 1000
        ).plus(7.hours)

        // Create the entry with default values
        currentEntry = SleepEntry(
            id = UUID.randomUUID().toString(),
            date = now,
            startTime = defaultSleepTime,
            endTime = defaultWakeTime,
            quality = 5.0,
            interruptions = 0,
            notes = ""
        )

        isAddingNewEntry = true
        shouldShowAddSheet = true
    }

    /**
     * Prepares an existing entry for editing
     * @param entry The entry to edit
     */
    fun prepareEntryForEditing(entry: SleepEntry) {
        currentEntry = entry
        isAddingNewEntry = true
        shouldShowAddSheet = true
    }

    /**
     * Cancels the current editing operation
     */
    fun cancelEditing() {
        isAddingNewEntry = false
        shouldShowAddSheet = false
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
    fun updateCurrentEntry(entry: SleepEntry) {
        currentEntry = entry
    }

    // MARK: - Utility Methods

    /**
     * Creates a default sleep entry with current timestamp
     */
    private fun createDefaultEntry(): SleepEntry {
        val now = Clock.System.now()
        return SleepEntry(
            id = UUID.randomUUID().toString(),
            date = now,
            startTime = now.minus(8.hours),
            endTime = now,
            quality = 5.0,
            interruptions = 0,
            notes = ""
        )
    }

    /**
     * Resets all sleep tracking data
     * This is used when clearing all application data
     */
    fun reset() {
        println("🔄 Resetting SleepTrackingViewModel...")

        viewModelScope.launch {
            _sleepEntries.value = emptyList()
            currentEntry = createDefaultEntry()
            isAddingNewEntry = false
            shouldShowAddSheet = false
            selectedDate = Clock.System.now()

            // Clear data from storage as well
            try {
                localStorage.saveSleepEntries(emptyList())
            } catch (e: Exception) {
                // Handle error silently
            }

            println("✅ SleepTrackingViewModel reset complete")
        }
    }
}
