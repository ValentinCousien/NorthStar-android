package com.vcousien.northstar.app

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.vcousien.northstar.R

/**
 * Enum representing the main navigation tabs in the app
 * Matches iOS AppTab enum
 */
enum class AppTab(
    val titleRes: Int,
    val icon: ImageVector
) {
    HOME(
        titleRes = R.string.tabs_home,
        icon = Icons.Default.Home
    ),
    MOOD(
        titleRes = R.string.tabs_mood,
        icon = Icons.Default.FavoriteBorder
    ),
    SLEEP(
        titleRes = R.string.tabs_sleep,
        icon = Icons.Default.Bedtime
    ),
    MEDICATION(
        titleRes = R.string.tabs_medication,
        icon = Icons.Default.MedicalServices
    ),
    SETTINGS(
        titleRes = R.string.tabs_settings,
        icon = Icons.Default.Settings
    )
}
