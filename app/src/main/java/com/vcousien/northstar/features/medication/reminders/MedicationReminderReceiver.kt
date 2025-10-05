package com.vcousien.northstar.features.medication.reminders

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.vcousien.northstar.R

/**
 * BroadcastReceiver for medication reminder notifications
 *
 * Triggered by AlarmManager when it's time to take medication
 * Creates and displays a notification to remind the user
 */
class MedicationReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val medicationId = intent.getStringExtra("medication_id") ?: return
        val medicationName = intent.getStringExtra("medication_name") ?: "Your medication"
        val dosage = intent.getStringExtra("medication_dosage") ?: ""
        val timeOfDay = intent.getStringExtra("time_of_day") ?: ""

        // Create notification channel (required for Android 8.0+)
        createNotificationChannel(context)

        // Build and show the notification
        showNotification(context, medicationId, medicationName, dosage, timeOfDay)
    }

    /**
     * Creates the notification channel for medication reminders
     * Required for Android 8.0 (API 26) and above
     */
    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                MedicationReminderManager.NOTIFICATION_CHANNEL_ID,
                MedicationReminderManager.NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminders to take your medications"
                enableVibration(true)
                setShowBadge(true)
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Shows the medication reminder notification
     */
    private fun showNotification(
        context: Context,
        medicationId: String,
        medicationName: String,
        dosage: String,
        timeOfDay: String
    ) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create intent to open the app when notification is tapped
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)?.apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("medication_id", medicationId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            medicationId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Build notification content
        val title = context.getString(R.string.notification_medication_reminder)
        val message = buildNotificationMessage(medicationName, dosage, timeOfDay)

        val notification = NotificationCompat.Builder(context, MedicationReminderManager.NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Use proper medication icon in production
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        // Show the notification with unique ID based on medication and time
        val notificationId = (medicationId.hashCode() + timeOfDay.hashCode()) and 0x7FFFFFFF
        notificationManager.notify(notificationId, notification)

        println("📢 Showed notification for $medicationName ($timeOfDay)")
    }

    /**
     * Builds the notification message text
     */
    private fun buildNotificationMessage(medicationName: String, dosage: String, timeOfDay: String): String {
        return buildString {
            append("Time to take $medicationName")
            if (dosage.isNotEmpty()) {
                append(" ($dosage)")
            }
            if (timeOfDay.isNotEmpty()) {
                append(" - $timeOfDay")
            }
        }
    }
}
