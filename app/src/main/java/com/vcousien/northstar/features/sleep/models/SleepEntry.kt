package com.vcousien.northstar.features.sleep.models

import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import java.util.UUID

/**
 * Model representing a sleep tracking entry
 * Contains all the information needed to track and analyze sleep patterns
 */
@Serializable
data class SleepEntry(
    /**
     * Unique identifier for the entry
     */
    val id: String = UUID.randomUUID().toString(),
    
    /**
     * Date when the entry was recorded (ISO-8601 format)
     * Represents the date the sleep session is associated with
     */
    val date: String = LocalDate.now().toString(),
    
    /**
     * Time when sleep started (ISO-8601 format)
     * Defaults to 10:00 PM on the previous day
     */
    val startTime: String = LocalDateTime.of(LocalDate.now().minusDays(1), LocalTime.of(22, 0)).toString(),
    
    /**
     * Time when sleep ended (ISO-8601 format)
     * Defaults to 7:00 AM on the current day
     */
    val endTime: String = LocalDateTime.of(LocalDate.now(), LocalTime.of(7, 0)).toString(),
    
    /**
     * Sleep duration in seconds
     * Calculated based on start and end times, handling overnight sleep properly
     */
    val duration: Long = calculateDuration(
        LocalDateTime.of(LocalDate.now().minusDays(1), LocalTime.of(22, 0)).toString(),
        LocalDateTime.of(LocalDate.now(), LocalTime.of(7, 0)).toString()
    ),
    
    /**
     * Sleep quality rating on a scale of 0.0-10.0
     * 0.0 = Poor sleep, 10.0 = Excellent sleep
     */
    val quality: Double = 5.0,
    
    /**
     * Number of sleep interruptions during the night
     */
    val interruptions: Int = 0,
    
    /**
     * Optional notes about this sleep session
     */
    val notes: String = ""
) {
    /**
     * Get the LocalDate object from the string representation
     */
    fun getDate(): LocalDate {
        return try {
            LocalDate.parse(date)
        } catch (e: Exception) {
            LocalDate.now()
        }
    }

    /**
     * Get the start time as LocalDateTime object
     */
    fun getStartTime(): LocalDateTime {
        return try {
            LocalDateTime.parse(startTime)
        } catch (e: Exception) {
            LocalDateTime.now().minusHours(8)
        }
    }

    /**
     * Get the end time as LocalDateTime object
     */
    fun getEndTime(): LocalDateTime {
        return try {
            LocalDateTime.parse(endTime)
        } catch (e: Exception) {
            LocalDateTime.now()
        }
    }

    /**
     * Duration in hours (convenience accessor)
     */
    fun getDurationInHours(): Double {
        return duration / 3600.0
    }

    /**
     * Duration in minutes (convenience accessor)
     */
    fun getDurationInMinutes(): Long {
        return duration / 60
    }

    /**
     * Get formatted duration string (e.g., "7h 30m")
     */
    fun getFormattedDuration(): String {
        val hours = duration / 3600
        val minutes = (duration % 3600) / 60
        
        return when {
            hours > 0 && minutes > 0 -> "${hours}h ${minutes}m"
            hours > 0 -> "${hours}h"
            minutes > 0 -> "${minutes}m"
            else -> "0m"
        }
    }

    /**
     * Check if this entry has notes
     */
    fun hasNotes(): Boolean = notes.isNotBlank()

    /**
     * Check if this is a short sleep session (< 6 hours)
     */
    fun isShortSleep(): Boolean = getDurationInHours() < 6.0

    /**
     * Check if this is a long sleep session (> 9 hours)
     */
    fun isLongSleep(): Boolean = getDurationInHours() > 9.0

    /**
     * Check if this is normal duration sleep (6-9 hours)
     */
    fun isNormalSleep(): Boolean = getDurationInHours() in 6.0..9.0

    /**
     * Check if sleep quality is poor (< 4.0)
     */
    fun hasPoorQuality(): Boolean = quality < 4.0

    /**
     * Check if sleep quality is good (> 7.0)
     */
    fun hasGoodQuality(): Boolean = quality > 7.0

    /**
     * Check if there were many interruptions (> 3)
     */
    fun hasManyInterruptions(): Boolean = interruptions > 3

    /**
     * Get sleep quality category
     */
    fun getQualityCategory(): SleepQualityCategory {
        return when {
            quality < 3.0 -> SleepQualityCategory.POOR
            quality < 5.0 -> SleepQualityCategory.BELOW_AVERAGE
            quality < 7.0 -> SleepQualityCategory.AVERAGE
            quality < 8.5 -> SleepQualityCategory.GOOD
            else -> SleepQualityCategory.EXCELLENT
        }
    }

    /**
     * Get sleep duration category
     */
    fun getDurationCategory(): SleepDurationCategory {
        val hours = getDurationInHours()
        return when {
            hours < 5.0 -> SleepDurationCategory.VERY_SHORT
            hours < 6.0 -> SleepDurationCategory.SHORT
            hours < 9.0 -> SleepDurationCategory.NORMAL
            hours < 10.0 -> SleepDurationCategory.LONG
            else -> SleepDurationCategory.VERY_LONG
        }
    }

    /**
     * Check if this sleep entry indicates concerning sleep patterns
     * (poor quality AND short duration) OR excessive interruptions
     */
    fun isConcerning(): Boolean {
        return (hasPoorQuality() && isShortSleep()) || interruptions > 5
    }

    /**
     * Validate that all values are within expected ranges
     */
    fun isValid(): Boolean {
        return quality in 0.0..10.0 &&
                interruptions >= 0 &&
                duration >= 0 &&
                getDurationInHours() <= 24.0 // Maximum 24 hours of sleep
    }

    companion object {
        /**
         * Create a default sleep entry with typical values
         */
        fun createDefault(): SleepEntry {
            return SleepEntry()
        }

        /**
         * Create a sleep entry for a specific date
         */
        fun createForDate(
            date: LocalDate,
            startTime: LocalDateTime = LocalDateTime.of(date.minusDays(1), LocalTime.of(22, 0)),
            endTime: LocalDateTime = LocalDateTime.of(date, LocalTime.of(7, 0)),
            quality: Double = 5.0,
            interruptions: Int = 0,
            notes: String = ""
        ): SleepEntry {
            return SleepEntry(
                date = date.toString(),
                startTime = startTime.toString(),
                endTime = endTime.toString(),
                duration = calculateDuration(startTime.toString(), endTime.toString()),
                quality = quality,
                interruptions = interruptions,
                notes = notes
            )
        }

        /**
         * Create a minimal sleep entry with just duration and quality
         */
        fun createMinimal(
            durationHours: Double,
            quality: Double
        ): SleepEntry {
            val now = LocalDateTime.now()
            val startTime = now.minusHours(durationHours.toLong()).minusMinutes(((durationHours % 1) * 60).toLong())
            
            return SleepEntry(
                startTime = startTime.toString(),
                endTime = now.toString(),
                duration = (durationHours * 3600).toLong(),
                quality = quality
            )
        }

        /**
         * Calculates sleep duration properly handling overnight sleep
         * @param startTimeStr Time when sleep started (ISO-8601 format)
         * @param endTimeStr Time when sleep ended (ISO-8601 format)
         * @return Sleep duration in seconds
         */
        fun calculateDuration(startTimeStr: String, endTimeStr: String): Long {
            return try {
                val startTime = LocalDateTime.parse(startTimeStr)
                val endTime = LocalDateTime.parse(endTimeStr)
                
                // Calculate duration between the two times
                val duration = ChronoUnit.SECONDS.between(startTime, endTime)
                
                // If duration is negative, it means we crossed midnight
                // Add 24 hours to get the correct duration
                if (duration < 0) {
                    duration + (24 * 3600)
                } else {
                    duration
                }
            } catch (e: Exception) {
                // Default to 8 hours if parsing fails
                8 * 3600L
            }
        }

        /**
         * Validate sleep quality value is within range
         */
        fun isValidQuality(quality: Double): Boolean = quality in 0.0..10.0

        /**
         * Validate interruptions value is valid
         */
        fun isValidInterruptions(interruptions: Int): Boolean = interruptions >= 0

        /**
         * Validate duration is reasonable (0-24 hours)
         */
        fun isValidDuration(durationSeconds: Long): Boolean {
            val hours = durationSeconds / 3600.0
            return hours in 0.0..24.0
        }
    }
}

/**
 * Enum representing different sleep quality categories
 */
enum class SleepQualityCategory(val displayName: String, val range: String) {
    POOR("Poor", "0.0 - 2.9"),
    BELOW_AVERAGE("Below Average", "3.0 - 4.9"),
    AVERAGE("Average", "5.0 - 6.9"),
    GOOD("Good", "7.0 - 8.4"),
    EXCELLENT("Excellent", "8.5 - 10.0");

    /**
     * Check if this category indicates concerning sleep quality
     */
    fun isConcerning(): Boolean = this == POOR

    /**
     * Check if this category indicates good sleep quality
     */
    fun isGood(): Boolean = this == GOOD || this == EXCELLENT
}

/**
 * Enum representing different sleep duration categories
 */
enum class SleepDurationCategory(val displayName: String, val range: String) {
    VERY_SHORT("Very Short", "< 5h"),
    SHORT("Short", "5h - 6h"),
    NORMAL("Normal", "6h - 9h"),
    LONG("Long", "9h - 10h"),
    VERY_LONG("Very Long", "> 10h");

    /**
     * Check if this category indicates concerning sleep duration
     */
    fun isConcerning(): Boolean = this == VERY_SHORT || this == VERY_LONG

    /**
     * Check if this category indicates healthy sleep duration
     */
    fun isHealthy(): Boolean = this == NORMAL
}

/**
 * Extension functions for working with collections of SleepEntry
 */

/**
 * Filter entries by sleep quality range
 */
fun List<SleepEntry>.filterByQualityRange(minQuality: Double, maxQuality: Double): List<SleepEntry> {
    return filter { it.quality in minQuality..maxQuality }
}

/**
 * Filter entries by sleep duration range (in hours)
 */
fun List<SleepEntry>.filterByDurationRange(minHours: Double, maxHours: Double): List<SleepEntry> {
    return filter { it.getDurationInHours() in minHours..maxHours }
}

/**
 * Filter entries by quality category
 */
fun List<SleepEntry>.filterByQualityCategory(category: SleepQualityCategory): List<SleepEntry> {
    return filter { it.getQualityCategory() == category }
}

/**
 * Filter entries by duration category
 */
fun List<SleepEntry>.filterByDurationCategory(category: SleepDurationCategory): List<SleepEntry> {
    return filter { it.getDurationCategory() == category }
}

/**
 * Get entries that are concerning (poor quality and short duration, or many interruptions)
 */
fun List<SleepEntry>.getConcerningEntries(): List<SleepEntry> {
    return filter { it.isConcerning() }
}

/**
 * Calculate average sleep duration in hours from a list of entries
 */
fun List<SleepEntry>.averageDurationHours(): Double {
    return if (isEmpty()) 0.0 else map { it.getDurationInHours() }.average()
}

/**
 * Calculate average sleep quality from a list of entries
 */
fun List<SleepEntry>.averageQuality(): Double {
    return if (isEmpty()) 0.0 else map { it.quality }.average()
}

/**
 * Calculate average interruptions from a list of entries
 */
fun List<SleepEntry>.averageInterruptions(): Double {
    return if (isEmpty()) 0.0 else map { it.interruptions.toDouble() }.average()
}

/**
 * Get total sleep duration in hours for all entries
 */
fun List<SleepEntry>.totalDurationHours(): Double {
    return sumOf { it.getDurationInHours() }
}

/**
 * Get most recent entry from the list
 */
fun List<SleepEntry>.getMostRecent(): SleepEntry? {
    return maxByOrNull { it.getDate() }
}

/**
 * Sort entries by date (newest first)
 */
fun List<SleepEntry>.sortedByDateDesc(): List<SleepEntry> {
    return sortedByDescending { it.getDate() }
}

/**
 * Sort entries by date (oldest first)  
 */
fun List<SleepEntry>.sortedByDateAsc(): List<SleepEntry> {
    return sortedBy { it.getDate() }
}

/**
 * Sort entries by sleep quality (best first)
 */
fun List<SleepEntry>.sortedByQualityDesc(): List<SleepEntry> {
    return sortedByDescending { it.quality }
}

/**
 * Sort entries by sleep duration (longest first)
 */
fun List<SleepEntry>.sortedByDurationDesc(): List<SleepEntry> {
    return sortedByDescending { it.getDurationInHours() }
}

/**
 * Get entries from the last N days
 */
fun List<SleepEntry>.getLastNDays(days: Int): List<SleepEntry> {
    val cutoffDate = LocalDate.now().minusDays(days.toLong())
    return filter { it.getDate().isAfter(cutoffDate) || it.getDate().isEqual(cutoffDate) }
}

/**
 * Get entries for a specific date range
 */
fun List<SleepEntry>.getDateRange(startDate: LocalDate, endDate: LocalDate): List<SleepEntry> {
    return filter { entry ->
        val entryDate = entry.getDate()
        (entryDate.isAfter(startDate) || entryDate.isEqual(startDate)) &&
        (entryDate.isBefore(endDate) || entryDate.isEqual(endDate))
    }
}
