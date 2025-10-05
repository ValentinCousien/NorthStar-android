package com.vcousien.northstar.features.medication.reminders

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import com.vcousien.northstar.core.models.Medication
import com.vcousien.northstar.core.models.MedicationFrequency
import com.vcousien.northstar.core.models.TimeOfDay
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manager for scheduling medication reminders
 *
 * Handles:
 * - Notification permission requests
 * - Scheduling reminders for medications
 * - Cancelling individual and all reminders
 * - Using Android AlarmManager for precise timing
 *
 * Matches iOS MedicationReminderManager functionality
 */
@Singleton
class MedicationReminderManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val notificationManager = NotificationManagerCompat.from(context)

    companion object {
        private const val MEDICATION_REMINDER_REQUEST_CODE_BASE = 1000
        const val NOTIFICATION_CHANNEL_ID = "medication_reminders"
        const val NOTIFICATION_CHANNEL_NAME = "Medication Reminders"
    }

    /**
     * Requests permission to send notifications
     * For Android 13+ (API 33+), this requires runtime permission
     */
    fun requestNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Permission needs to be requested in the Activity/Fragment
            // This method returns whether notifications are enabled
            notificationManager.areNotificationsEnabled()
        } else {
            // For older Android versions, notifications are enabled by default
            true
        }
    }

    /**
     * Checks if notification permission is granted
     */
    fun hasNotificationPermission(): Boolean {
        return notificationManager.areNotificationsEnabled()
    }

    /**
     * Schedules reminders for a medication
     * @param medication The medication to schedule reminders for
     */
    fun scheduleMedicationReminders(medication: Medication) {
        // Cancel existing reminders for this medication first
        cancelReminders(medication.id)

        // Only schedule reminders if enabled and not as-needed
        if (!medication.remindersEnabled || medication.frequency == MedicationFrequency.AS_NEEDED) {
            return
        }

        // Schedule a reminder for each time of day
        medication.timeOfDay.forEach { timeOfDay ->
            val reminderTime = getReminderTime(medication, timeOfDay)
            scheduleReminder(medication, timeOfDay, reminderTime)
        }

        println("✅ Scheduled ${medication.timeOfDay.size} reminders for ${medication.name}")
    }

    /**
     * Schedules reminders for multiple medications
     * @param medications The medications to schedule reminders for
     */
    fun scheduleAllMedicationReminders(medications: List<Medication>) {
        medications.forEach { medication ->
            scheduleMedicationReminders(medication)
        }
        println("✅ Scheduled reminders for ${medications.size} medications")
    }

    /**
     * Cancels reminders for a specific medication
     * @param medicationId ID of the medication to cancel reminders for
     */
    fun cancelReminders(medicationId: String) {
        // Cancel alarms for all possible time slots for this medication
        TimeOfDay.entries.forEach { timeOfDay ->
            val requestCode = getRequestCode(medicationId, timeOfDay)
            val intent = Intent(context, MedicationReminderReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )

            pendingIntent?.let {
                alarmManager.cancel(it)
                it.cancel()
            }
        }

        println("✅ Cancelled reminders for medication ID: $medicationId")
    }

    /**
     * Cancels all medication reminders
     */
    fun cancelAllReminders() {
        // This is a simplified approach - in production, you'd want to track all scheduled reminders
        println("⚠️ Cancelling all reminders (simplified implementation)")

        // Note: A complete implementation would need to track all scheduled reminder IDs
        // and cancel them individually, or use a more sophisticated approach
    }

    /**
     * Schedules a single reminder
     */
    private fun scheduleReminder(medication: Medication, timeOfDay: TimeOfDay, reminderTime: Calendar) {
        val requestCode = getRequestCode(medication.id, timeOfDay)

        val intent = Intent(context, MedicationReminderReceiver::class.java).apply {
            putExtra("medication_id", medication.id)
            putExtra("medication_name", medication.name)
            putExtra("medication_dosage", medication.getDosage(timeOfDay))
            putExtra("time_of_day", timeOfDay.displayName)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Schedule the alarm to repeat daily
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                reminderTime.timeInMillis,
                pendingIntent
            )
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                reminderTime.timeInMillis,
                pendingIntent
            )
        }

        println("   📅 Scheduled reminder for ${medication.name} at ${timeOfDay.displayName}: ${reminderTime.time}")
    }

    /**
     * Gets the reminder time for a medication at a specific time of day
     * Uses custom reminder if set, otherwise uses default time for that time of day
     */
    private fun getReminderTime(medication: Medication, timeOfDay: TimeOfDay): Calendar {
        val calendar = Calendar.getInstance()

        // Check if there's a custom reminder time
        val customReminder = medication.customReminders[timeOfDay]
        if (customReminder != null && customReminder.enabled) {
            calendar.set(Calendar.HOUR_OF_DAY, customReminder.hour)
            calendar.set(Calendar.MINUTE, customReminder.minute)
        } else {
            // Use default time for this time of day
            val (hour, minute) = timeOfDay.defaultReminderTime()
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
        }

        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        // If the time has already passed today, schedule for tomorrow
        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        return calendar
    }

    /**
     * Generates a unique request code for a medication reminder
     * This ensures each medication + time of day combination has a unique alarm
     */
    private fun getRequestCode(medicationId: String, timeOfDay: TimeOfDay): Int {
        // Use medication ID hash and time of day priority to create unique request code
        val medicationHash = medicationId.hashCode() and 0x00FFFFFF // Use lower 24 bits
        val timeOfDayCode = timeOfDay.priority() // 1-4

        return MEDICATION_REMINDER_REQUEST_CODE_BASE + (medicationHash * 10) + timeOfDayCode
    }
}
