package com.vcousien.northstar.core.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.vcousien.northstar.R

/**
 * Centralized localization system for North Star app
 * Provides type-safe access to all localized strings
 * Similar to iOS Strings.swift but adapted for Android/Compose
 */
object Strings {

    // MARK: - App Navigation
    object Tabs {
        val home @Composable get() = stringResource(R.string.tabs_home)
        val mood @Composable get() = stringResource(R.string.tabs_mood)
        val sleep @Composable get() = stringResource(R.string.tabs_sleep)
        val medication @Composable get() = stringResource(R.string.tabs_medication)
        val settings @Composable get() = stringResource(R.string.tabs_settings)
    }

    // MARK: - Time of Day
    object TimeOfDay {
        val morning @Composable get() = stringResource(R.string.timeofday_morning)
        val noon @Composable get() = stringResource(R.string.timeofday_noon)
        val evening @Composable get() = stringResource(R.string.timeofday_evening)
        val bedtime @Composable get() = stringResource(R.string.timeofday_bedtime)
    }

    // MARK: - Onboarding
    object Onboarding {
        val welcome @Composable get() = stringResource(R.string.onboarding_welcome_title)
        val welcomeDescription @Composable get() = stringResource(R.string.onboarding_welcome_description)
        
        val moodTitle @Composable get() = stringResource(R.string.onboarding_mood_title)
        val moodDescription @Composable get() = stringResource(R.string.onboarding_mood_description)
        
        val sleepTitle @Composable get() = stringResource(R.string.onboarding_sleep_title)
        val sleepDescription @Composable get() = stringResource(R.string.onboarding_sleep_description)
        
        val medicationTitle @Composable get() = stringResource(R.string.onboarding_medication_title)
        val medicationDescription @Composable get() = stringResource(R.string.onboarding_medication_description)
        
        val privacyTitle @Composable get() = stringResource(R.string.onboarding_privacy_title)
        val privacyDescription @Composable get() = stringResource(R.string.onboarding_privacy_description)
        
        val previous @Composable get() = stringResource(R.string.onboarding_previous)
        val next @Composable get() = stringResource(R.string.onboarding_next)
        val getStarted @Composable get() = stringResource(R.string.onboarding_get_started)
    }

    // MARK: - Home View
    object Home {
        val title @Composable get() = stringResource(R.string.home_title)
        val goodMorning @Composable get() = stringResource(R.string.home_good_morning)
        val goodAfternoon @Composable get() = stringResource(R.string.home_good_afternoon)
        val goodEvening @Composable get() = stringResource(R.string.home_good_evening)
        val todayOverview @Composable get() = stringResource(R.string.home_today_overview)
        val quickActions @Composable get() = stringResource(R.string.home_quick_actions)

        // HomeView specific strings
        val todayMoodTitle @Composable get() = stringResource(R.string.home_today_mood_title)
        val howFeelingToday @Composable get() = stringResource(R.string.home_how_feeling_today)
        val recordMoodButton @Composable get() = stringResource(R.string.home_record_mood_button)
        val recordedAt @Composable get() = stringResource(R.string.home_recorded_at)
        val editButton @Composable get() = stringResource(R.string.home_edit_button)
        val noMedicationsToday @Composable get() = stringResource(R.string.home_no_medications_today)
        val noMedicationsTaken @Composable get() = stringResource(R.string.home_no_medications_taken)
        val medicationsTaken @Composable get() = stringResource(R.string.home_medications_taken)
        val remainingMedications @Composable get() = stringResource(R.string.home_remaining_medications)
        val allMedicationsTaken @Composable get() = stringResource(R.string.home_all_medications_taken)
        val medicationsToTake @Composable get() = stringResource(R.string.home_medications_to_take)
        val nextReminder @Composable get() = stringResource(R.string.home_next_reminder)
        val noRemindersScheduled @Composable get() = stringResource(R.string.home_no_reminders_scheduled)
        
        @Composable
        fun inHours(hours: Int) = stringResource(R.string.home_in_hours, hours)
        
        @Composable
        fun inMinutes(minutes: Int) = stringResource(R.string.home_in_minutes, minutes)
        
        val now @Composable get() = stringResource(R.string.home_now)
        val userDefault @Composable get() = stringResource(R.string.home_user_default)
    }

    // MARK: - Mood Tracking
    object Mood {
        val title @Composable get() = stringResource(R.string.mood_title)
        val addEntry @Composable get() = stringResource(R.string.mood_add_entry)
        val todayMood @Composable get() = stringResource(R.string.mood_today)
        val noMoodToday @Composable get() = stringResource(R.string.mood_no_mood_today)
        val recordMood @Composable get() = stringResource(R.string.mood_record)
        val moodHistory @Composable get() = stringResource(R.string.mood_history)
        val trends @Composable get() = stringResource(R.string.mood_trends)

        // Mood levels
        val elevated @Composable get() = stringResource(R.string.mood_elevated)
        val good @Composable get() = stringResource(R.string.mood_good)
        val neutral @Composable get() = stringResource(R.string.mood_neutral)
        val low @Composable get() = stringResource(R.string.mood_low)
        val depressed @Composable get() = stringResource(R.string.mood_depressed)

        // Extended mood levels
        val severelyDepressed @Composable get() = stringResource(R.string.mood_severely_depressed)
        val slightlyLow @Composable get() = stringResource(R.string.mood_slightly_low)
        val slightlyElevated @Composable get() = stringResource(R.string.mood_slightly_elevated)
        val highlyElevated @Composable get() = stringResource(R.string.mood_highly_elevated)
        val manic @Composable get() = stringResource(R.string.mood_manic)

        // Mood entry form
        val selectMood @Composable get() = stringResource(R.string.mood_select)
        val addNote @Composable get() = stringResource(R.string.mood_add_note)
        val saveEntry @Composable get() = stringResource(R.string.mood_save)
        val cancel @Composable get() = stringResource(R.string.mood_cancel)

        // Empty states
        val noEntries @Composable get() = stringResource(R.string.mood_no_entries)
        val noEntriesDescription @Composable get() = stringResource(R.string.mood_no_entries_description)

        // Additional mood strings
        val howFeeling @Composable get() = stringResource(R.string.mood_how_feeling)
        val recordTitle @Composable get() = stringResource(R.string.mood_record_title)
        val energyLevel @Composable get() = stringResource(R.string.mood_energy_level)
        val anxietyLevel @Composable get() = stringResource(R.string.mood_anxiety_level)
        val notesLabel @Composable get() = stringResource(R.string.mood_notes_label)
        val energyVeryLow @Composable get() = stringResource(R.string.mood_energy_very_low)
        val energyAverage @Composable get() = stringResource(R.string.mood_energy_average)
        val energyVeryHigh @Composable get() = stringResource(R.string.mood_energy_very_high)
        val anxietyNone @Composable get() = stringResource(R.string.mood_anxiety_none)
        val anxietyModerate @Composable get() = stringResource(R.string.mood_anxiety_moderate)
        val anxietySevere @Composable get() = stringResource(R.string.mood_anxiety_severe)
    }

    // MARK: - Sleep Tracking
    object Sleep {
        val title @Composable get() = stringResource(R.string.sleep_title)
        val addEntry @Composable get() = stringResource(R.string.sleep_add_entry)
        val todaySleep @Composable get() = stringResource(R.string.sleep_today)
        val lastNight @Composable get() = stringResource(R.string.sleep_last_night)
        val sleepHistory @Composable get() = stringResource(R.string.sleep_history)
        val statistics @Composable get() = stringResource(R.string.sleep_stats)

        // Sleep entry form
        val bedtime @Composable get() = stringResource(R.string.sleep_bedtime)
        val wakeTime @Composable get() = stringResource(R.string.sleep_wake_time)
        val sleepQuality @Composable get() = stringResource(R.string.sleep_quality)
        val saveEntry @Composable get() = stringResource(R.string.sleep_save)

        // Sleep quality levels
        val excellent @Composable get() = stringResource(R.string.sleep_quality_excellent)
        val good @Composable get() = stringResource(R.string.sleep_quality_good)
        val fair @Composable get() = stringResource(R.string.sleep_quality_fair)
        val poor @Composable get() = stringResource(R.string.sleep_quality_poor)

        // Sleep stats
        val averageDuration @Composable get() = stringResource(R.string.sleep_avg_duration)
        val averageQuality @Composable get() = stringResource(R.string.sleep_avg_quality)
        val hoursSlept @Composable get() = stringResource(R.string.sleep_hours_slept)

        // Empty states
        val noEntries @Composable get() = stringResource(R.string.sleep_no_entries)
        val noEntriesDescription @Composable get() = stringResource(R.string.sleep_no_entries_description)

        // Additional sleep strings
        val noSleepRecorded @Composable get() = stringResource(R.string.sleep_no_sleep_recorded)
        val recordLastNight @Composable get() = stringResource(R.string.sleep_record_last_night)
        val qualityLabel @Composable get() = stringResource(R.string.sleep_quality_label)
        val addSleepTitle @Composable get() = stringResource(R.string.sleep_add_sleep_title)
        val sleepTimesHeader @Composable get() = stringResource(R.string.sleep_sleep_times_header)
        val bedtimeLabel @Composable get() = stringResource(R.string.sleep_bedtime_label)
        val wakeTimeLabel @Composable get() = stringResource(R.string.sleep_wake_time_label)
        val qualityHeader @Composable get() = stringResource(R.string.sleep_quality_header)
        val qualityQuestion @Composable get() = stringResource(R.string.sleep_quality_question)
        val qualityPoorLabel @Composable get() = stringResource(R.string.sleep_quality_poor_label)
        val qualityExcellentLabel @Composable get() = stringResource(R.string.sleep_quality_excellent_label)
        val interruptionsLabel @Composable get() = stringResource(R.string.sleep_interruptions_label)
        val notesHeader @Composable get() = stringResource(R.string.sleep_notes_header)
        val saveButton @Composable get() = stringResource(R.string.sleep_save_button)
        val cancelButton @Composable get() = stringResource(R.string.sleep_cancel_button)
        val qualityShort @Composable get() = stringResource(R.string.sleep_quality_short)
        val noSleepToday @Composable get() = stringResource(R.string.sleep_no_sleep_today)
        val recordLastNightSleep @Composable get() = stringResource(R.string.sleep_record_last_night_sleep)
        val addAction @Composable get() = stringResource(R.string.sleep_add_action)
    }

    // MARK: - Medication
    object Medication {
        val title @Composable get() = stringResource(R.string.medication_title)
        val addMedication @Composable get() = stringResource(R.string.medication_add)
        val medications @Composable get() = stringResource(R.string.medication_medications)
        val tracking @Composable get() = stringResource(R.string.medication_tracking)
        val statistics @Composable get() = stringResource(R.string.medication_stats)

        // Medication form
        val medicationName @Composable get() = stringResource(R.string.medication_name)
        val dosage @Composable get() = stringResource(R.string.medication_dosage)
        val frequency @Composable get() = stringResource(R.string.medication_frequency)
        val startDate @Composable get() = stringResource(R.string.medication_start_date)
        val endDate @Composable get() = stringResource(R.string.medication_end_date)
        val notes @Composable get() = stringResource(R.string.medication_notes)
        val color @Composable get() = stringResource(R.string.medication_color)

        // Medication tracking
        val todayMedications @Composable get() = stringResource(R.string.medication_today)
        val taken @Composable get() = stringResource(R.string.medication_taken)
        val notTaken @Composable get() = stringResource(R.string.medication_not_taken)
        val markTaken @Composable get() = stringResource(R.string.medication_mark_taken)
        val markNotTaken @Composable get() = stringResource(R.string.medication_mark_not_taken)
        val myMedications @Composable get() = stringResource(R.string.medication_my_medications)

        // Reminders
        val reminders @Composable get() = stringResource(R.string.medication_reminders)
        val reminderTime @Composable get() = stringResource(R.string.medication_reminder_time)
        val enableReminders @Composable get() = stringResource(R.string.medication_enable_reminders)
        val reminderScheduled @Composable get() = stringResource(R.string.medication_reminder_scheduled)

        // Statistics
        val adherenceRate @Composable get() = stringResource(R.string.medication_adherence_rate)
        val totalDoses @Composable get() = stringResource(R.string.medication_total_doses)
        val missedDoses @Composable get() = stringResource(R.string.medication_missed_doses)
        val streak @Composable get() = stringResource(R.string.medication_streak)
        val days @Composable get() = stringResource(R.string.medication_days)

        // Empty states
        val noMedications @Composable get() = stringResource(R.string.medication_no_medications)
        val noMedicationsDescription @Composable get() = stringResource(R.string.medication_no_medications_description)
        val noEntries @Composable get() = stringResource(R.string.medication_no_entries)
        val noEntriesDescription @Composable get() = stringResource(R.string.medication_no_entries_description)

        // Additional medication strings
        val informationHeader @Composable get() = stringResource(R.string.medication_information_header)
        val medicationNameField @Composable get() = stringResource(R.string.medication_medication_name_field)
        val dosageHeader @Composable get() = stringResource(R.string.medication_dosage_header)
        val differentDosages @Composable get() = stringResource(R.string.medication_different_dosages)
        val dosageExample @Composable get() = stringResource(R.string.medication_dosage_example)
        val frequencyHeader @Composable get() = stringResource(R.string.medication_frequency_header)
        val timeOfDayHeader @Composable get() = stringResource(R.string.medication_time_of_day_header)
        val notesHeader @Composable get() = stringResource(R.string.medication_notes_header)
        val saveAction @Composable get() = stringResource(R.string.medication_save_action)
        val cancelAction @Composable get() = stringResource(R.string.medication_cancel_action)
        val addMedicationTitle @Composable get() = stringResource(R.string.medication_add_medication_title)
        val noMedicationsToday @Composable get() = stringResource(R.string.medication_no_medications_today)
        val allMedicationsTakenToday @Composable get() = stringResource(R.string.medication_all_medications_taken_today)
        val medicationsToTakeToday @Composable get() = stringResource(R.string.medication_medications_to_take_today)

        // Tracker and stats strings
        val trackingTitle @Composable get() = stringResource(R.string.medication_tracking_title)
        val periodLabel @Composable get() = stringResource(R.string.medication_period_label)
        val daysSuffix @Composable get() = stringResource(R.string.medication_days_suffix)
        val excellentAdherence @Composable get() = stringResource(R.string.medication_excellent_adherence)
        val goodAdherence @Composable get() = stringResource(R.string.medication_good_adherence)
        val averageAdherence @Composable get() = stringResource(R.string.medication_average_adherence)
        val lowAdherence @Composable get() = stringResource(R.string.medication_low_adherence)
        val trackingForDate @Composable get() = stringResource(R.string.medication_tracking_for_date)
        val takenAt @Composable get() = stringResource(R.string.medication_taken_at)
        val notTakenStatus @Composable get() = stringResource(R.string.medication_not_taken_status)
        val noMedicationsDate @Composable get() = stringResource(R.string.medication_no_medications_date)
        val noMedicationsScheduledDate @Composable get() = stringResource(R.string.medication_no_medications_scheduled_date)
        val sideEffectsTitle @Composable get() = stringResource(R.string.medication_side_effects_title)
        val severityLabel @Composable get() = stringResource(R.string.medication_severity_label)
        val noSideEffectsDate @Composable get() = stringResource(R.string.medication_no_side_effects_date)
        val analysisPeriod @Composable get() = stringResource(R.string.medication_analysis_period)
        val overallAdherence @Composable get() = stringResource(R.string.medication_overall_adherence)
        val adherenceByMedication @Composable get() = stringResource(R.string.medication_adherence_by_medication)
        val sideEffectsSummary @Composable get() = stringResource(R.string.medication_side_effects_summary)
        val noSideEffectsReported @Composable get() = stringResource(R.string.medication_no_side_effects_reported)
        val addMedicationsStats @Composable get() = stringResource(R.string.medication_add_medications_stats)

        // As needed medication strings
        val takeAsNeeded @Composable get() = stringResource(R.string.medication_take_as_needed)
    }

    // MARK: - Settings
    object Settings {
        val title @Composable get() = stringResource(R.string.settings_title)
        val general @Composable get() = stringResource(R.string.settings_general)
        val notifications @Composable get() = stringResource(R.string.settings_notifications)
        val privacy @Composable get() = stringResource(R.string.settings_privacy)
        val data @Composable get() = stringResource(R.string.settings_data)
        val export @Composable get() = stringResource(R.string.settings_export)
        val about @Composable get() = stringResource(R.string.settings_about)
        val version @Composable get() = stringResource(R.string.settings_version)

        // Cloud sync settings
        val cloudSync @Composable get() = stringResource(R.string.settings_cloud_sync)
        val cloudSyncDescription @Composable get() = stringResource(R.string.settings_cloud_sync_description)
    }

    // MARK: - Insights
    object Insights {
        val title @Composable get() = stringResource(R.string.insights_title)
        val moodTrends @Composable get() = stringResource(R.string.insights_mood_trends)
        val sleepCorrelation @Composable get() = stringResource(R.string.insights_sleep_correlation)
        val medicationEffect @Composable get() = stringResource(R.string.insights_medication_effect)
        val patterns @Composable get() = stringResource(R.string.insights_patterns)
        val recommendations @Composable get() = stringResource(R.string.insights_recommendations)

        // Coming soon
        val comingSoon @Composable get() = stringResource(R.string.insights_coming_soon)
        val comingSoonDescription @Composable get() = stringResource(R.string.insights_coming_soon_description)
    }

    // MARK: - Common UI Elements
    object Common {
        val save @Composable get() = stringResource(R.string.common_save)
        val cancel @Composable get() = stringResource(R.string.common_cancel)
        val delete @Composable get() = stringResource(R.string.common_delete)
        val edit @Composable get() = stringResource(R.string.common_edit)
        val done @Composable get() = stringResource(R.string.common_done)
        val ok @Composable get() = stringResource(R.string.common_ok)
        val yes @Composable get() = stringResource(R.string.common_yes)
        val no @Composable get() = stringResource(R.string.common_no)
        val loading @Composable get() = stringResource(R.string.common_loading)
        val error @Composable get() = stringResource(R.string.common_error)
        val retry @Composable get() = stringResource(R.string.common_retry)
        val today @Composable get() = stringResource(R.string.common_today)
        val yesterday @Composable get() = stringResource(R.string.common_yesterday)
        val thisWeek @Composable get() = stringResource(R.string.common_this_week)
        val thisMonth @Composable get() = stringResource(R.string.common_this_month)
        val optional @Composable get() = stringResource(R.string.common_optional)
        val add @Composable get() = stringResource(R.string.common_add)
        val take @Composable get() = stringResource(R.string.common_take)
        val taken @Composable get() = stringResource(R.string.common_taken)
    }

    // MARK: - Error Messages
    object Error {
        val genericError @Composable get() = stringResource(R.string.error_generic)
        val networkError @Composable get() = stringResource(R.string.error_network)
        val dataError @Composable get() = stringResource(R.string.error_data)
        val saveError @Composable get() = stringResource(R.string.error_save)
        val syncError @Composable get() = stringResource(R.string.error_sync)
        val notificationError @Composable get() = stringResource(R.string.error_notification)
    }

    // MARK: - Notifications
    object Notifications {
        val medicationReminder @Composable get() = stringResource(R.string.notification_medication_reminder)
        val moodReminder @Composable get() = stringResource(R.string.notification_mood_reminder)
        val sleepReminder @Composable get() = stringResource(R.string.notification_sleep_reminder)
    }

    // MARK: - Date Formats
    object DateFormat {
        val short @Composable get() = stringResource(R.string.date_format_short)
        val medium @Composable get() = stringResource(R.string.date_format_medium)
        val time @Composable get() = stringResource(R.string.date_format_time)
        val dateTime @Composable get() = stringResource(R.string.date_format_datetime)
    }
}

/**
 * Extension functions for non-Composable contexts
 * These require a Context parameter for accessing string resources
 */
object StringsContext {
    
    fun getString(context: android.content.Context, stringRes: Int): String {
        return context.getString(stringRes)
    }
    
    fun getString(context: android.content.Context, stringRes: Int, vararg formatArgs: Any): String {
        return context.getString(stringRes, *formatArgs)
    }
    
    // Convenience functions for common use cases
    object Home {
        fun inHours(context: android.content.Context, hours: Int): String {
            return context.getString(R.string.home_in_hours, hours)
        }
        
        fun inMinutes(context: android.content.Context, minutes: Int): String {
            return context.getString(R.string.home_in_minutes, minutes)
        }
    }
}

/**
 * Helper extension for Context to easily access string resources
 */
fun android.content.Context.getNorthStarString(stringRes: Int): String = getString(stringRes)
fun android.content.Context.getNorthStarString(stringRes: Int, vararg formatArgs: Any): String = getString(stringRes, *formatArgs)
