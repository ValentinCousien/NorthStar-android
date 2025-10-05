# Medication Reminder System

## Overview
This module handles scheduling and displaying medication reminders using Android's AlarmManager and notification system.

## Components

### MedicationReminderManager
- Singleton service for managing medication reminders
- Schedules daily reminders using AlarmManager
- Supports custom reminder times per time-of-day
- Handles permission checks and reminder cancellation

### MedicationReminderReceiver
- BroadcastReceiver triggered by scheduled alarms
- Creates and displays medication reminder notifications
- Opens the app when notification is tapped

## Android Manifest Setup

Add the following to your `AndroidManifest.xml`:

```xml
<!-- Medication Reminder Receiver -->
<receiver
    android:name=".features.medication.reminders.MedicationReminderReceiver"
    android:enabled="true"
    android:exported="false" />

<!-- Permissions for notifications and alarms -->
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" />
<uses-permission android:name="android.permission.USE_EXACT_ALARM" />
```

## Notification Permission (Android 13+)

For Android 13 (API 33) and above, you need to request notification permission at runtime:

```kotlin
// In your Activity or Fragment
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    requestPermissions(
        arrayOf(Manifest.permission.POST_NOTIFICATIONS),
        NOTIFICATION_PERMISSION_REQUEST_CODE
    )
}
```

## Usage

### Scheduling Reminders

```kotlin
// Inject the manager
@Inject lateinit var reminderManager: MedicationReminderManager

// Schedule reminders for a single medication
reminderManager.scheduleMedicationReminders(medication)

// Schedule reminders for all medications
reminderManager.scheduleAllMedicationReminders(medications)
```

### Cancelling Reminders

```kotlin
// Cancel reminders for a specific medication
reminderManager.cancelReminders(medicationId)

// Cancel all reminders
reminderManager.cancelAllReminders()
```

### Custom Reminder Times

Medications support custom reminder times via the `customReminders` map:

```kotlin
val medication = Medication(
    name = "Example Med",
    timeOfDay = listOf(TimeOfDay.MORNING, TimeOfDay.EVENING),
    customReminders = mapOf(
        TimeOfDay.MORNING to MedicationReminder(
            hour = 9,
            minute = 30,
            enabled = true,
            timeOfDay = TimeOfDay.MORNING
        )
    )
)
```

## Default Reminder Times

If no custom reminder is set, these defaults are used:
- **Morning**: 8:00 AM
- **Noon**: 12:00 PM
- **Evening**: 6:00 PM
- **Bedtime**: 10:00 PM

## Notes

- Reminders are NOT scheduled for "as needed" medications
- Reminders are only scheduled when `medication.remindersEnabled = true`
- Each medication + time-of-day combination gets a unique alarm
- Alarms are set to repeat daily
- If reminder time has passed, it's scheduled for the next day
- Uses `setExactAndAllowWhileIdle()` for precise timing even in Doze mode
