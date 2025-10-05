package com.vcousien.northstar.core.models

import kotlinx.datetime.LocalTime
import kotlinx.serialization.Serializable

/**
 * Data class for managing user settings and preferences
 *
 * This class handles app settings such as user name, notification preferences,
 * tracking options, and the onboarding completion status.
 */
@Serializable
data class UserSettings(
    /** User's display name */
    val userName: String = "",
    
    /** Whether notifications are enabled */
    val notificationsEnabled: Boolean = true,
    
    /** Time for daily reminders */
    val dailyReminderTime: LocalTime = LocalTime(20, 0),
    
    /** Whether security features are enabled */
    val securityEnabled: Boolean = false,
    
    // MARK: - Tracking Options
    
    /** Whether energy level tracking is enabled */
    val energyTrackingEnabled: Boolean = true,
    
    /** Whether anxiety level tracking is enabled */
    val anxietyTrackingEnabled: Boolean = true,
    
    /** Whether sleep tracking is enabled */
    val sleepTrackingEnabled: Boolean = true,
    
    /** Whether irritability tracking is enabled */
    val irritabilityTrackingEnabled: Boolean = true,
    
    /** Whether the onboarding has been completed */
    val hasCompletedOnboarding: Boolean = false
) {
    
    /**
     * Returns settings with onboarding marked as completed
     */
    fun withCompletedOnboarding(): UserSettings {
        return copy(hasCompletedOnboarding = true)
    }
    
    companion object {
        /**
         * Default user settings
         */
        val DEFAULT = UserSettings()
    }
}
