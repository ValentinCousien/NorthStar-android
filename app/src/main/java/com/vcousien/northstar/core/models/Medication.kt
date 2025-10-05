package com.vcousien.northstar.core.models

import androidx.compose.ui.graphics.Color
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import java.util.UUID

/**
 * Frequencies for medication administration
 */
@Serializable
enum class MedicationFrequency(val displayName: String) {
    DAILY("Daily"),
    WEEKLY("Weekly"),
    AS_NEEDED("As needed"),
    CUSTOM("Custom");

    companion object {
        fun fromString(value: String): MedicationFrequency {
            return entries.find { it.displayName.equals(value, ignoreCase = true) } ?: DAILY
        }
    }
}

/**
 * Times of day for medication administration
 */
@Serializable
enum class TimeOfDay(val displayName: String, val colorHex: String) {
    MORNING("Morning", "FFD700"),      // Gold for sunrise
    NOON("Noon", "FF8C00"),           // Dark orange for midday sun
    EVENING("Evening", "FF6B35"),     // Orange-red for sunset
    BEDTIME("Bedtime", "4B0082");     // Indigo for night

    /**
     * Priority value for chronological ordering
     */
    fun priority(): Int {
        return when (this) {
            MORNING -> 1
            NOON -> 2
            EVENING -> 3
            BEDTIME -> 4
        }
    }

    /**
     * Returns the thematic color for each time of day
     */
    fun getColor(): androidx.compose.ui.graphics.Color {
        return when (this) {
            MORNING -> androidx.compose.ui.graphics.Color(0xFFFFD700) // Gold for sunrise
            NOON -> androidx.compose.ui.graphics.Color(0xFFFF8C00) // Dark orange for midday sun
            EVENING -> androidx.compose.ui.graphics.Color(0xFFFF6B35) // Orange-red for sunset
            BEDTIME -> androidx.compose.ui.graphics.Color(0xFF4B0082) // Indigo for night
        }
    }

    /**
     * Returns the text color for contrast against the background color
     */
    fun getTextColor(): androidx.compose.ui.graphics.Color {
        return when (this) {
            MORNING -> androidx.compose.ui.graphics.Color.Black
            NOON -> androidx.compose.ui.graphics.Color.Black
            EVENING -> androidx.compose.ui.graphics.Color.White
            BEDTIME -> androidx.compose.ui.graphics.Color.White
        }
    }

    /**
     * Default reminder time for each moment of the day
     */
    fun defaultReminderTime(): Pair<Int, Int> {
        return when (this) {
            MORNING -> 8 to 0   // 8:00 AM
            NOON -> 12 to 0     // 12:00 PM
            EVENING -> 18 to 0  // 6:00 PM
            BEDTIME -> 22 to 0  // 10:00 PM
        }
    }

    companion object {
        fun fromString(value: String): TimeOfDay {
            return entries.find { it.displayName.equals(value, ignoreCase = true) } ?: MORNING
        }
    }
}

/**
 * Model representing a medication definition
 */
@Serializable
data class Medication(
    /** Unique identifier for the medication */
    val id: String = UUID.randomUUID().toString(),

    /** Name of the medication */
    val name: String = "",

    /** Default dosage information (e.g., "10mg") */
    val dosage: String = "",

    /** Dosages specific to times of day (optional) */
    val dosagesByTime: Map<TimeOfDay, String> = emptyMap(),

    /** How often the medication should be taken */
    val frequency: MedicationFrequency = MedicationFrequency.DAILY,

    /** Times of day when medication should be taken */
    val timeOfDay: List<TimeOfDay> = listOf(TimeOfDay.MORNING),

    /** Optional notes about this medication */
    val notes: String = "",

    /** Whether reminders are enabled for this medication */
    val remindersEnabled: Boolean = false,

    /** Custom reminders for each time of day */
    val customReminders: Map<TimeOfDay, MedicationReminder> = emptyMap(),

    /** Last modified date (as epoch milliseconds) */
    val lastModified: Long? = null
) {
    /**
     * Convenience constructor with Instant for lastModified
     */
    constructor(
        id: String = UUID.randomUUID().toString(),
        name: String = "",
        dosage: String = "",
        dosagesByTime: Map<TimeOfDay, String> = emptyMap(),
        frequency: MedicationFrequency = MedicationFrequency.DAILY,
        timeOfDay: List<TimeOfDay> = listOf(TimeOfDay.MORNING),
        notes: String = "",
        remindersEnabled: Boolean = false,
        customReminders: Map<TimeOfDay, MedicationReminder> = emptyMap(),
        lastModified: Instant?
    ) : this(
        id = id,
        name = name,
        dosage = dosage,
        dosagesByTime = dosagesByTime,
        frequency = frequency,
        timeOfDay = timeOfDay.sorted(),
        notes = notes,
        remindersEnabled = remindersEnabled,
        customReminders = customReminders,
        lastModified = lastModified?.toEpochMilliseconds()
    )

    /**
     * Get the lastModified as an Instant
     */
    val lastModifiedAsInstant: Instant?
        get() = lastModified?.let { Instant.fromEpochMilliseconds(it) }

    /**
     * Returns the dosage for a given time of day
     */
    fun getDosage(timeOfDay: TimeOfDay): String {
        return dosagesByTime[timeOfDay] ?: dosage
    }

    /**
     * Checks if the medication has different dosages according to time
     */
    val hasDifferentDosages: Boolean
        get() = dosagesByTime.isNotEmpty()

    /**
     * Display dosage for the interface
     */
    val displayDosage: String
        get() {
            return if (hasDifferentDosages) {
                timeOfDay.joinToString(", ") { "${it.displayName}: ${getDosage(it)}" }
            } else {
                dosage
            }
        }
}

/**
 * Model representing a custom medication reminder
 */
@Serializable
data class MedicationReminder(
    /** Hour for the reminder (0-23) */
    val hour: Int,

    /** Minute for the reminder (0-59) */
    val minute: Int,

    /** Whether this reminder is enabled */
    val enabled: Boolean = true,

    /** Time of day this reminder is for */
    val timeOfDay: TimeOfDay
) {
    companion object {
        /**
         * Create with default time for a given TimeOfDay
         */
        fun forTimeOfDay(timeOfDay: TimeOfDay, enabled: Boolean = true): MedicationReminder {
            val (hour, minute) = timeOfDay.defaultReminderTime()
            return MedicationReminder(hour, minute, enabled, timeOfDay)
        }
    }

    /**
     * Returns a formatted time string
     */
    fun formattedTime(): String {
        val period = if (hour < 12) "AM" else "PM"
        val displayHour = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour
        return String.format("%d:%02d %s", displayHour, minute, period)
    }

    /**
     * Unique identifier for notification scheduling
     */
    val notificationIdentifier: String
        get() = "${timeOfDay.displayName}-$hour-$minute"
}

/**
 * Model representing a medication administration event
 */
@Serializable
data class MedicationEntry(
    /** Unique identifier for the entry */
    val id: String = UUID.randomUUID().toString(),

    /** Date when the entry was recorded (as epoch milliseconds) */
    val date: Long,

    /** ID of the medication that was taken */
    val medicationId: String,

    /** Name of the medication (denormalized for performance) */
    val medicationName: String,

    /** Time of day for this specific dose */
    val timeOfDay: TimeOfDay,

    /** Whether the medication was taken */
    val taken: Boolean = false,

    /** Time when the medication was actually taken (as epoch milliseconds) */
    val actualTime: Long? = null,

    /** Side effects experienced */
    val sideEffects: List<SideEffect> = emptyList(),

    /** Optional notes about this administration */
    val notes: String = ""
) {
    /**
     * Convenience constructor with Instant objects
     */
    constructor(
        id: String = UUID.randomUUID().toString(),
        date: Instant,
        medicationId: String,
        medicationName: String,
        timeOfDay: TimeOfDay,
        taken: Boolean = false,
        actualTime: Instant? = null,
        sideEffects: List<SideEffect> = emptyList(),
        notes: String = ""
    ) : this(
        id = id,
        date = date.toEpochMilliseconds(),
        medicationId = medicationId,
        medicationName = medicationName,
        timeOfDay = timeOfDay,
        taken = taken,
        actualTime = actualTime?.toEpochMilliseconds(),
        sideEffects = sideEffects,
        notes = notes
    )

    /**
     * Get the date as an Instant
     */
    val dateAsInstant: Instant
        get() = Instant.fromEpochMilliseconds(date)

    /**
     * Get the actual time as an Instant
     */
    val actualTimeAsInstant: Instant?
        get() = actualTime?.let { Instant.fromEpochMilliseconds(it) }
}

/**
 * Model representing a medication side effect
 */
@Serializable
data class SideEffect(
    /** Unique identifier for the side effect */
    val id: String = UUID.randomUUID().toString(),

    /** Name of the side effect */
    val name: String,

    /** Severity rating on a scale of 0-10 */
    val severity: Double = 5.0
)
