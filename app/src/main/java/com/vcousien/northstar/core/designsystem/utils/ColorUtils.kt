package com.vcousien.northstar.core.designsystem.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import com.vcousien.northstar.core.designsystem.DesignTokens
import com.vcousien.northstar.ui.theme.northStarColors
import androidx.compose.material3.MaterialTheme

/**
 * Utility functions for working with colors in NorthStar
 */
object ColorUtils {
    
    /**
     * Calculate if a color is considered "dark" based on its luminance
     */
    fun isDarkColor(color: Color): Boolean {
        return color.luminance() < 0.5f
    }
    
    /**
     * Get contrasting text color for accessibility
     */
    fun getContrastingTextColor(backgroundColor: Color): Color {
        return if (isDarkColor(backgroundColor)) Color.White else Color.Black
    }
    
    /**
     * Apply alpha/opacity to a color
     */
    fun Color.withAlpha(alpha: Float): Color {
        return this.copy(alpha = alpha)
    }
    
    /**
     * Create a lighter version of the color
     */
    fun Color.lighter(amount: Float = 0.2f): Color {
        return Color(
            red = (red + (1f - red) * amount).coerceIn(0f, 1f),
            green = (green + (1f - green) * amount).coerceIn(0f, 1f),
            blue = (blue + (1f - blue) * amount).coerceIn(0f, 1f),
            alpha = alpha
        )
    }
    
    /**
     * Create a darker version of the color
     */
    fun Color.darker(amount: Float = 0.2f): Color {
        return Color(
            red = (red * (1f - amount)).coerceIn(0f, 1f),
            green = (green * (1f - amount)).coerceIn(0f, 1f),
            blue = (blue * (1f - amount)).coerceIn(0f, 1f),
            alpha = alpha
        )
    }
    
    /**
     * Convert Color to hex string
     */
    fun Color.toHexString(): String {
        val red = (red * 255).toInt()
        val green = (green * 255).toInt()
        val blue = (blue * 255).toInt()
        val alpha = (alpha * 255).toInt()
        
        return if (alpha == 255) {
            "#%02X%02X%02X".format(red, green, blue)
        } else {
            "#%02X%02X%02X%02X".format(alpha, red, green, blue)
        }
    }
    
    /**
     * Create color from hex string
     */
    fun colorFromHex(hex: String): Color {
        val cleanHex = hex.removePrefix("#")
        return when (cleanHex.length) {
            6 -> Color(android.graphics.Color.parseColor("#$cleanHex"))
            8 -> Color(android.graphics.Color.parseColor("#$cleanHex"))
            else -> Color.Transparent
        }
    }
}

/**
 * Mood-specific color utilities
 */
object MoodColorUtils {
    
    /**
     * Get mood color with optional transparency
     */
    @Composable
    fun getMoodColor(level: Int, alpha: Float = 1f): Color {
        val baseColor = DesignTokens.Colors.moodColor(level)
        return baseColor.copy(alpha = alpha)
    }
    
    /**
     * Get mood color for data visualization (more saturated)
     */
    @Composable
    fun getMoodColorForChart(level: Int): Color {
        return when (level) {
            in 0..1 -> MaterialTheme.northStarColors.moodDepressed
            in 2..3 -> MaterialTheme.northStarColors.moodLow
            4 -> MaterialTheme.northStarColors.moodNeutral
            in 5..6 -> MaterialTheme.northStarColors.moodGood
            in 7..8 -> MaterialTheme.northStarColors.moodElevated
            else -> MaterialTheme.northStarColors.moodNeutral
        }
    }
    
    /**
     * Get a gradient of mood colors for smooth transitions
     */
    @Composable
    fun getMoodColorGradient(): List<Color> {
        return listOf(
            MaterialTheme.northStarColors.moodDepressed,
            MaterialTheme.northStarColors.moodLow,
            MaterialTheme.northStarColors.moodNeutral,
            MaterialTheme.northStarColors.moodGood,
            MaterialTheme.northStarColors.moodElevated
        )
    }
    
    /**
     * Interpolate between two mood levels for smooth animations
     */
    @Composable
    fun interpolateMoodColor(fromLevel: Int, toLevel: Int, fraction: Float): Color {
        val fromColor = getMoodColor(fromLevel)
        val toColor = getMoodColor(toLevel)
        
        return Color(
            red = fromColor.red + (toColor.red - fromColor.red) * fraction,
            green = fromColor.green + (toColor.green - fromColor.green) * fraction,
            blue = fromColor.blue + (toColor.blue - fromColor.blue) * fraction,
            alpha = fromColor.alpha + (toColor.alpha - fromColor.alpha) * fraction
        )
    }
    
    /**
     * Get appropriate text color for mood level backgrounds
     */
    @Composable
    fun getMoodTextColor(moodLevel: Int): Color {
        val backgroundColor = getMoodColor(moodLevel)
        return ColorUtils.getContrastingTextColor(backgroundColor)
    }
    
    /**
     * Check if mood level should be highlighted as concerning
     */
    fun isConcerningMoodLevel(level: Int): Boolean {
        return level <= DesignTokens.Mood.DEPRESSED_MAX
    }
    
    /**
     * Check if mood level should be highlighted as positive
     */
    fun isPositiveMoodLevel(level: Int): Boolean {
        return level >= DesignTokens.Mood.GOOD_MIN
    }
}

/**
 * Extension functions for easier color manipulation
 */
@Composable
fun Color.contrastingText(): Color = ColorUtils.getContrastingTextColor(this)

fun Color.withOpacity(opacity: Float): Color = this.copy(alpha = opacity)

fun Color.lighten(amount: Float = 0.2f): Color = ColorUtils.run { lighter(amount) }

fun Color.darken(amount: Float = 0.2f): Color = ColorUtils.run { darker(amount) }

fun Color.toHex(): String = ColorUtils.run { toHexString() }
