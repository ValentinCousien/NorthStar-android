package com.vcousien.northstar.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * NorthStar Color System
 * Colors converted from iOS design system
 */

// Primary Colors
val SpaceCadet = Color(0xFF1E2952)        // Primary
val Seashell = Color(0xFFFFF5EE)          // Primary Light & Card Background
val RussianViolet = Color(0xFF32174D)     // Primary Dark

// Surface & Background Colors
val Apricot = Color(0xFFFBCEB1)           // Surface & Warning
val Seashell2 = Color(0xFFFFEEEE)         // Background

// Text Colors
val TextPrimary = Color(0xFF000000)       // Black
val SoftLavender = Color(0xFFA696BF)      // Text Secondary
val PaleMint = Color(0x4D99C0B8)          // Divider (30% opacity)

// Semantic Colors
val Success = Color(0xFF4CAF50)           // Standard Green
val Folly = Color(0xFFFF004F)             // Error
val NonPhotoBlue = Color(0xFFA4DDED)      // Info

// Mood Colors (Light Theme)
val MoodDepressed = Color(0xCC0000FF)     // Blue with 80% opacity
val MoodLow = Color(0x800000FF)           // Blue with 50% opacity
val MoodNeutral = Color(0xFFA3B8C0)       // Neutral brownish-gray
val MoodGood = Color(0xFFFF6600)          // Orange-like
val MoodElevated = Color(0xFFFF0000)      // Red

// Mood Colors (Dark Theme)
val MoodDepressedDark = Color(0xCC3333FF) // Lighter blue with 80% opacity
val MoodLowDark = Color(0x806666FF)       // Lighter blue with 50% opacity  
val MoodNeutralDark = Color(0xFFB3C8CC)   // Lighter neutral
val MoodGoodDark = Color(0xFFFF8833)      // Lighter orange
val MoodElevatedDark = Color(0xFFFF3333)  // Lighter red

// Additional Dark Theme Colors
val SoftLavenderDark = Color(0xFFB8AECC) // Lighter lavender for dark theme
val PaleMintDark = Color(0x4DB3D9CC)     // Lighter mint for dark theme (30% opacity)

/**
 * Design System Color Tokens
 */
object NSColors {
    // Primary Colors
    val primary = SpaceCadet
    val primaryLight = Seashell
    val primaryDark = RussianViolet
    
    // Surface Colors
    val surface = Apricot
    val background = Seashell2
    val cardBackground = Seashell
    
    // Text Colors
    val textPrimary = TextPrimary
    val textSecondary = SoftLavender
    val textSecondaryDark = SoftLavenderDark
    val divider = PaleMint
    val dividerDark = PaleMintDark
    
    // Semantic Colors
    val success = Success
    val warning = Apricot
    val error = Folly
    val info = NonPhotoBlue
    
    // Mood Colors - Light Theme
    val moodDepressed = MoodDepressed
    val moodLow = MoodLow
    val moodNeutral = MoodNeutral
    val moodGood = MoodGood
    val moodElevated = MoodElevated
    
    // Mood Colors - Dark Theme
    val moodDepressedDark = MoodDepressedDark
    val moodLowDark = MoodLowDark
    val moodNeutralDark = MoodNeutralDark
    val moodGoodDark = MoodGoodDark
    val moodElevatedDark = MoodElevatedDark
}

/**
 * Mood Color Helper
 * Maps mood levels (0-8) to appropriate colors
 */
object MoodColors {
    /**
     * Get mood color based on mood level and theme
     * @param moodLevel Mood level from 0 (depressed) to 8 (elevated)
     * @param isDark Whether using dark theme
     * @return Color for the mood level
     */
    fun getColorForMood(moodLevel: Int, isDark: Boolean = false): Color {
        return when (moodLevel) {
            0, 1 -> if (isDark) NSColors.moodDepressedDark else NSColors.moodDepressed
            2, 3 -> if (isDark) NSColors.moodLowDark else NSColors.moodLow
            4 -> if (isDark) NSColors.moodNeutralDark else NSColors.moodNeutral
            5, 6 -> if (isDark) NSColors.moodGoodDark else NSColors.moodGood
            7, 8 -> if (isDark) NSColors.moodElevatedDark else NSColors.moodElevated
            else -> if (isDark) NSColors.moodNeutralDark else NSColors.moodNeutral
        }
    }
    
    /**
     * Get all mood colors for charts/visualization
     */
    fun getAllMoodColors(isDark: Boolean = false): List<Color> {
        return listOf(
            if (isDark) NSColors.moodDepressedDark else NSColors.moodDepressed,
            if (isDark) NSColors.moodLowDark else NSColors.moodLow,
            if (isDark) NSColors.moodNeutralDark else NSColors.moodNeutral,
            if (isDark) NSColors.moodGoodDark else NSColors.moodGood,
            if (isDark) NSColors.moodElevatedDark else NSColors.moodElevated
        )
    }
}

/**
 * Legacy Colors (for compatibility during migration)
 */
val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)
