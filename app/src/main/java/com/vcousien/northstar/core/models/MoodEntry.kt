package com.vcousien.northstar.core.models

import com.vcousien.northstar.features.mood.models.MoodLevel
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import java.util.UUID

/**
 * Model representing a mood tracking entry
 */
@Serializable
data class MoodEntry(
    /** Unique identifier for the entry */
    val id: String = UUID.randomUUID().toString(),
    
    /** Date when the entry was recorded (as epoch milliseconds) */
    val date: Long,
    
    /** Mood level recorded for this entry */
    val level: MoodLevel,
    
    /** Optional notes about this mood state */
    val notes: String = "",
    
    /** Energy level on a scale of 0-10 */
    val energyLevel: Double = 5.0,
    
    /** Anxiety level on a scale of 0-10 */
    val anxietyLevel: Double = 0.0,
    
    /** Optional sleep quality associated with this mood (0-10) */
    val sleepQuality: Double? = null,
    
    /** Optional irritability level associated with this mood (0-10) */
    val irritabilityLevel: Double? = null
) {
    /**
     * Convenience constructor with Instant
     */
    constructor(
        id: String = UUID.randomUUID().toString(),
        date: Instant,
        level: MoodLevel,
        notes: String = "",
        energyLevel: Double = 5.0,
        anxietyLevel: Double = 0.0,
        sleepQuality: Double? = null,
        irritabilityLevel: Double? = null
    ) : this(
        id = id,
        date = date.toEpochMilliseconds(),
        level = level,
        notes = notes,
        energyLevel = energyLevel,
        anxietyLevel = anxietyLevel,
        sleepQuality = sleepQuality,
        irritabilityLevel = irritabilityLevel
    )
    
    /**
     * Get the date as an Instant
     */
    val dateAsInstant: Instant
        get() = Instant.fromEpochMilliseconds(date)
}
