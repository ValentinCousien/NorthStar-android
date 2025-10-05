package com.vcousien.northstar.features.medication.models

import kotlinx.serialization.Serializable

/**
 * Frequencies for medication administration
 * Represents how often a medication should be taken
 */
@Serializable
enum class MedicationFrequency(val rawValue: String) {
    /**
     * Medication taken every day
     */
    DAILY("Daily"),

    /**
     * Medication taken weekly
     */
    WEEKLY("Weekly"),

    /**
     * Medication taken only when needed
     */
    AS_NEEDED("As needed"),

    /**
     * Medication on a custom schedule
     */
    CUSTOM("Custom");

    /**
     * Get display name for the frequency
     */
    fun getDisplayName(): String {
        return rawValue
    }

    /**
     * Check if this frequency requires scheduled reminders
     */
    fun requiresScheduledReminders(): Boolean {
        return when (this) {
            DAILY, WEEKLY, CUSTOM -> true
            AS_NEEDED -> false
        }
    }

    /**
     * Check if this frequency is regular (daily/weekly)
     */
    fun isRegular(): Boolean {
        return when (this) {
            DAILY, WEEKLY -> true
            AS_NEEDED, CUSTOM -> false
        }
    }

    /**
     * Get the typical interval in days (for scheduling purposes)
     */
    fun getTypicalIntervalDays(): Int {
        return when (this) {
            DAILY -> 1
            WEEKLY -> 7
            AS_NEEDED -> 0 // No fixed interval
            CUSTOM -> 1 // Default to daily for custom schedules
        }
    }

    companion object {
        /**
         * Get MedicationFrequency from raw value string
         */
        fun fromRawValue(value: String): MedicationFrequency? {
            return values().find { it.rawValue == value }
        }

        /**
         * Get all regular frequencies (excluding as-needed)
         */
        fun getRegularFrequencies(): List<MedicationFrequency> {
            return values().filter { it.isRegular() }
        }

        /**
         * Get all frequencies that support reminders
         */
        fun getReminderSupportedFrequencies(): List<MedicationFrequency> {
            return values().filter { it.requiresScheduledReminders() }
        }
    }
}

/**
 * Extension functions for MedicationFrequency
 */

/**
 * Check if this frequency should show time-of-day options
 */
fun MedicationFrequency.shouldShowTimeOptions(): Boolean {
    return this != MedicationFrequency.AS_NEEDED
}

/**
 * Check if this frequency supports multiple times per day
 */
fun MedicationFrequency.supportsMultipleTimesPerDay(): Boolean {
    return this == MedicationFrequency.DAILY || this == MedicationFrequency.CUSTOM
}
