package com.vcousien.northstar.core.models

import androidx.compose.ui.graphics.Color

/**
 * Extension to get the Color for a TimeOfDay
 */
fun TimeOfDay.getColor(): Color {
    return Color(android.graphics.Color.parseColor("#$colorHex"))
}

/**
 * Extension to get text color that contrasts well with the background color
 */
fun TimeOfDay.getTextColor(): Color {
    return when (this) {
        TimeOfDay.MORNING, TimeOfDay.NOON, TimeOfDay.EVENING -> Color.Black
        TimeOfDay.BEDTIME -> Color.White
    }
}

/**
 * Extension to sort a list of TimeOfDay chronologically
 */
fun List<TimeOfDay>.sortedChronologically(): List<TimeOfDay> {
    return this.sortedBy { it.priority() }
}
