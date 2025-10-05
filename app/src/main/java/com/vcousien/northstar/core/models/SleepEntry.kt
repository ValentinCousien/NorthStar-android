package com.vcousien.northstar.core.models

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import java.util.UUID
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * Model representing a sleep tracking entry
 */
@Serializable
data class SleepEntry(
    /** Unique identifier for the entry */
    val id: String = UUID.randomUUID().toString(),
    
    /** Date when the entry was recorded (as epoch milliseconds) */
    val date: Long,
    
    /** Sleep duration in seconds */
    val durationSeconds: Long,
    
    /** Sleep quality rating on a scale of 0-10 */
    val quality: Double = 5.0,
    
    /** Time when sleep started (as epoch milliseconds) */
    val startTime: Long,
    
    /** Time when sleep ended (as epoch milliseconds) */
    val endTime: Long,
    
    /** Number of sleep interruptions */
    val interruptions: Int = 0,
    
    /** Optional notes about this sleep session */
    val notes: String = ""
) {
    /**
     * Convenience constructor with Instant objects
     */
    constructor(
        id: String = UUID.randomUUID().toString(),
        date: Instant,
        startTime: Instant,
        endTime: Instant,
        quality: Double = 5.0,
        interruptions: Int = 0,
        notes: String = ""
    ) : this(
        id = id,
        date = date.toEpochMilliseconds(),
        durationSeconds = calculateDuration(startTime, endTime).inWholeSeconds,
        quality = quality,
        startTime = startTime.toEpochMilliseconds(),
        endTime = endTime.toEpochMilliseconds(),
        interruptions = interruptions,
        notes = notes
    )
    
    /**
     * Get the date as an Instant
     */
    val dateAsInstant: Instant
        get() = Instant.fromEpochMilliseconds(date)
    
    /**
     * Get the start time as an Instant
     */
    val startTimeAsInstant: Instant
        get() = Instant.fromEpochMilliseconds(startTime)
    
    /**
     * Get the end time as an Instant
     */
    val endTimeAsInstant: Instant
        get() = Instant.fromEpochMilliseconds(endTime)
    
    /**
     * Duration as a Duration object
     */
    val duration: Duration
        get() = durationSeconds.seconds
    
    /**
     * Duration in hours (convenience accessor)
     */
    val durationInHours: Double
        get() = durationSeconds / 3600.0
    
    companion object {
        /**
         * Calculates sleep duration properly handling overnight sleep
         * @param startTime Time when sleep started
         * @param endTime Time when sleep ended
         * @return Sleep duration
         */
        private fun calculateDuration(startTime: Instant, endTime: Instant): Duration {
            // Simple duration calculation - if end is before start, assume next day
            val duration = endTime - startTime
            return if (duration.isNegative()) {
                // Add 24 hours for overnight sleep
                duration + Duration.parse("24h")
            } else {
                duration
            }
        }
    }
}
