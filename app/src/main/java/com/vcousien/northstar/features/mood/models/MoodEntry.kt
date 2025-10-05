package com.vcousien.northstar.features.mood.models

import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.util.UUID

/**
 * Model representing a mood tracking entry
 * Contains all the information needed to track and analyze mood patterns
 */
@Serializable
data class MoodEntry(
    /**
     * Unique identifier for the entry
     */
    val id: String = UUID.randomUUID().toString(),
    
    /**
     * Date and time when the entry was recorded
     * Using ISO-8601 string format for serialization compatibility
     */
    val dateTime: String = LocalDateTime.now().toString(),
    
    /**
     * Mood level recorded for this entry (0-8 scale)
     */
    val moodLevel: Int,
    
    /**
     * Optional notes about this mood state
     */
    val notes: String = "",
    
    /**
     * Energy level on a scale of 0.0-10.0
     * 0.0 = No energy, 10.0 = Maximum energy
     */
    val energyLevel: Double = 5.0,
    
    /**
     * Anxiety level on a scale of 0.0-10.0
     * 0.0 = No anxiety, 10.0 = Maximum anxiety
     */
    val anxietyLevel: Double = 0.0,
    
    /**
     * Optional sleep quality associated with this mood (0.0-10.0)
     * null if not tracked or not applicable
     * 0.0 = Poor sleep, 10.0 = Excellent sleep
     */
    val sleepQuality: Double? = null,
    
    /**
     * Optional irritability level associated with this mood (0.0-10.0)
     * null if not tracked or not applicable
     * 0.0 = Not irritable, 10.0 = Very irritable
     */
    val irritabilityLevel: Double? = null
) {
    /**
     * Get the MoodLevel enum from the integer value
     */
    fun getMoodLevel(): MoodLevel? {
        return MoodLevel.fromValue(moodLevel)
    }

    /**
     * Get the LocalDateTime object from the string representation
     */
    fun getDateTime(): LocalDateTime {
        return try {
            LocalDateTime.parse(dateTime)
        } catch (e: Exception) {
            LocalDateTime.now()
        }
    }

    /**
     * Check if this entry has notes
     */
    fun hasNotes(): Boolean = notes.isNotBlank()

    /**
     * Check if sleep quality is tracked for this entry
     */
    fun hasSleepQuality(): Boolean = sleepQuality != null

    /**
     * Check if irritability is tracked for this entry
     */
    fun hasIrritability(): Boolean = irritabilityLevel != null

    /**
     * Get mood category for this entry
     */
    fun getMoodCategory(): MoodCategory? {
        return getMoodLevel()?.getCategory()
    }

    /**
     * Check if this mood entry indicates a concerning mood level
     */
    fun isConcerning(): Boolean {
        return getMoodLevel()?.isConcerning() == true
    }

    /**
     * Check if this is a high energy entry (energy level > 7.0)
     */
    fun isHighEnergy(): Boolean = energyLevel > 7.0

    /**
     * Check if this is a low energy entry (energy level < 3.0)
     */
    fun isLowEnergy(): Boolean = energyLevel < 3.0

    /**
     * Check if this entry indicates high anxiety (anxiety level > 7.0)
     */
    fun isHighAnxiety(): Boolean = anxietyLevel > 7.0

    /**
     * Check if sleep quality is poor (< 4.0) when tracked
     */
    fun hasPoorSleep(): Boolean = sleepQuality?.let { it < 4.0 } == true

    /**
     * Check if sleep quality is good (> 7.0) when tracked
     */
    fun hasGoodSleep(): Boolean = sleepQuality?.let { it > 7.0 } == true

    /**
     * Validate that all numeric values are within expected ranges
     */
    fun isValid(): Boolean {
        return moodLevel in 0..8 &&
                energyLevel in 0.0..10.0 &&
                anxietyLevel in 0.0..10.0 &&
                (sleepQuality == null || sleepQuality in 0.0..10.0) &&
                (irritabilityLevel == null || irritabilityLevel in 0.0..10.0)
    }

    companion object {
        /**
         * Create a default mood entry with neutral values
         */
        fun createDefault(): MoodEntry {
            return MoodEntry(
                moodLevel = MoodLevel.NEUTRAL.value,
                notes = "",
                energyLevel = 5.0,
                anxietyLevel = 0.0,
                sleepQuality = null,
                irritabilityLevel = null
            )
        }

        /**
         * Create a mood entry for a specific date/time
         */
        fun createForDateTime(
            dateTime: LocalDateTime,
            moodLevel: Int,
            notes: String = "",
            energyLevel: Double = 5.0,
            anxietyLevel: Double = 0.0,
            sleepQuality: Double? = null,
            irritabilityLevel: Double? = null
        ): MoodEntry {
            return MoodEntry(
                dateTime = dateTime.toString(),
                moodLevel = moodLevel,
                notes = notes,
                energyLevel = energyLevel,
                anxietyLevel = anxietyLevel,
                sleepQuality = sleepQuality,
                irritabilityLevel = irritabilityLevel
            )
        }

        /**
         * Create a minimal mood entry with just the mood level
         */
        fun createMinimal(moodLevel: Int): MoodEntry {
            return MoodEntry(
                moodLevel = moodLevel
            )
        }

        /**
         * Validate mood level value is within range
         */
        fun isValidMoodLevel(level: Int): Boolean = level in 0..8

        /**
         * Validate energy level value is within range
         */
        fun isValidEnergyLevel(level: Double): Boolean = level in 0.0..10.0

        /**
         * Validate anxiety level value is within range
         */
        fun isValidAnxietyLevel(level: Double): Boolean = level in 0.0..10.0

        /**
         * Validate sleep quality value is within range
         */
        fun isValidSleepQuality(quality: Double?): Boolean {
            return quality == null || quality in 0.0..10.0
        }

        /**
         * Validate irritability level value is within range
         */
        fun isValidIrritabilityLevel(level: Double?): Boolean {
            return level == null || level in 0.0..10.0
        }
    }
}

/**
 * Extension functions for working with collections of MoodEntry
 */

/**
 * Filter entries by mood level range
 */
fun List<MoodEntry>.filterByMoodRange(minLevel: Int, maxLevel: Int): List<MoodEntry> {
    return filter { it.moodLevel in minLevel..maxLevel }
}

/**
 * Filter entries by mood category
 */
fun List<MoodEntry>.filterByMoodCategory(category: MoodCategory): List<MoodEntry> {
    return filter { it.getMoodCategory() == category }
}

/**
 * Get entries that are concerning (severely depressed or manic)
 */
fun List<MoodEntry>.getConcerningEntries(): List<MoodEntry> {
    return filter { it.isConcerning() }
}

/**
 * Calculate average mood level from a list of entries
 */
fun List<MoodEntry>.averageMoodLevel(): Double {
    return if (isEmpty()) 0.0 else map { it.moodLevel.toDouble() }.average()
}

/**
 * Calculate average energy level from a list of entries
 */
fun List<MoodEntry>.averageEnergyLevel(): Double {
    return if (isEmpty()) 0.0 else map { it.energyLevel }.average()
}

/**
 * Calculate average anxiety level from a list of entries
 */
fun List<MoodEntry>.averageAnxietyLevel(): Double {
    return if (isEmpty()) 0.0 else map { it.anxietyLevel }.average()
}

/**
 * Get most recent entry from the list
 */
fun List<MoodEntry>.getMostRecent(): MoodEntry? {
    return maxByOrNull { it.getDateTime() }
}

/**
 * Sort entries by date/time (newest first)
 */
fun List<MoodEntry>.sortedByDateDesc(): List<MoodEntry> {
    return sortedByDescending { it.getDateTime() }
}

/**
 * Sort entries by date/time (oldest first)  
 */
fun List<MoodEntry>.sortedByDateAsc(): List<MoodEntry> {
    return sortedBy { it.getDateTime() }
}
