package com.vcousien.northstar.core.designsystem

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import com.vcousien.northstar.ui.theme.NSColors
import com.vcousien.northstar.ui.theme.northStarColors
import androidx.compose.material3.MaterialTheme

/**
 * Design Tokens for NorthStar App
 * Provides semantic color definitions, typography, spacing, and utility functions
 * Integrates with NSTypography and NSSpacing for consistent design system
 */
object DesignTokens {
    
    /**
     * Mood Scale Definitions
     * Based on the 0-8 mood scale used in the app
     */
    object Mood {
        const val DEPRESSED_MIN = 0
        const val DEPRESSED_MAX = 1
        const val LOW_MIN = 2
        const val LOW_MAX = 3
        const val NEUTRAL = 4
        const val GOOD_MIN = 5
        const val GOOD_MAX = 6
        const val ELEVATED_MIN = 7
        const val ELEVATED_MAX = 8
        
        /**
         * Get mood level category name
         */
        fun getCategoryName(moodLevel: Int): String {
            return when (moodLevel) {
                in DEPRESSED_MIN..DEPRESSED_MAX -> "Depressed"
                in LOW_MIN..LOW_MAX -> "Low"
                NEUTRAL -> "Neutral"
                in GOOD_MIN..GOOD_MAX -> "Good"
                in ELEVATED_MIN..ELEVATED_MAX -> "Elevated"
                else -> "Unknown"
            }
        }
        
        /**
         * Check if mood level is in depression range
         */
        fun isDepressed(moodLevel: Int): Boolean = moodLevel in DEPRESSED_MIN..DEPRESSED_MAX
        
        /**
         * Check if mood level is in low range
         */
        fun isLow(moodLevel: Int): Boolean = moodLevel in LOW_MIN..LOW_MAX
        
        /**
         * Check if mood level is neutral
         */
        fun isNeutral(moodLevel: Int): Boolean = moodLevel == NEUTRAL
        
        /**
         * Check if mood level is in good range
         */
        fun isGood(moodLevel: Int): Boolean = moodLevel in GOOD_MIN..GOOD_MAX
        
        /**
         * Check if mood level is in elevated range
         */
        fun isElevated(moodLevel: Int): Boolean = moodLevel in ELEVATED_MIN..ELEVATED_MAX
    }
    
    /**
     * Semantic Color Mappings
     */
    object Colors {
        
        /**
         * Get primary brand color
         */
        val primary: Color
            @Composable get() = MaterialTheme.colorScheme.primary
        
        /**
         * Get background color
         */
        val background: Color
            @Composable get() = MaterialTheme.colorScheme.background
        
        /**
         * Get surface color
         */
        val surface: Color
            @Composable get() = MaterialTheme.colorScheme.surface
        
        /**
         * Get card background color
         */
        val cardBackground: Color
            @Composable get() = MaterialTheme.northStarColors.cardBackground
        
        /**
         * Get primary text color
         */
        val textPrimary: Color
            @Composable get() = MaterialTheme.colorScheme.onBackground
        
        /**
         * Get secondary text color
         */
        val textSecondary: Color
            @Composable get() = MaterialTheme.northStarColors.textSecondary
        
        /**
         * Get divider color
         */
        val divider: Color
            @Composable get() = MaterialTheme.northStarColors.divider
        
        /**
         * Get error color
         */
        val error: Color
            @Composable get() = MaterialTheme.colorScheme.error
        
        /**
         * Get warning color
         */
        val warning: Color
            @Composable get() = MaterialTheme.northStarColors.warning
        
        /**
         * Get success color (green)
         */
        val success: Color
            @Composable get() = NSColors.success
        
        /**
         * Get info color
         */
        val info: Color
            @Composable get() = MaterialTheme.northStarColors.info
        
        /**
         * Get primary text on primary background color
         */
        val onPrimary: Color
            @Composable get() = MaterialTheme.colorScheme.onPrimary
            
        /**
         * Get text color on error background
         */
        val onError: Color
            @Composable get() = MaterialTheme.colorScheme.onError
        
        /**
         * Get outline color
         */
        val outline: Color
            @Composable get() = MaterialTheme.colorScheme.outline

        /**
         * Get border color
         */
        val border: Color
            @Composable get() = MaterialTheme.northStarColors.divider

        /**
         * Get primary light color
         */
        val primaryLight: Color
            @Composable get() = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)

        /**
         * Get mood color for specific level
         */
        @Composable
        fun moodColor(level: Int): Color {
            val colors = MaterialTheme.northStarColors
            return when (level) {
                in Mood.DEPRESSED_MIN..Mood.DEPRESSED_MAX -> colors.moodDepressed
                in Mood.LOW_MIN..Mood.LOW_MAX -> colors.moodLow
                Mood.NEUTRAL -> colors.moodNeutral
                in Mood.GOOD_MIN..Mood.GOOD_MAX -> colors.moodGood
                in Mood.ELEVATED_MIN..Mood.ELEVATED_MAX -> colors.moodElevated
                else -> colors.moodNeutral
            }
        }
        
        /**
         * Get all mood colors for charts/graphs
         */
        @Composable
        fun allMoodColors(): List<Color> {
            val colors = MaterialTheme.northStarColors
            return listOf(
                colors.moodDepressed,
                colors.moodLow,
                colors.moodNeutral,
                colors.moodGood,
                colors.moodElevated
            )
        }
    }
    
    /**
     * Alpha values for consistent transparency
     */
    object Alpha {
        const val DISABLED = 0.38f
        const val INACTIVE = 0.60f
        const val DIVIDER = 0.12f
        const val OVERLAY = 0.16f
        const val PRESSED = 0.12f
        const val FOCUSED = 0.12f
        const val SELECTED = 0.08f
        const val HOVER = 0.04f
    }
    
    /**
     * Elevation values following Material Design guidelines
     */
    object Elevation {
        const val NONE = 0
        const val CARD = 1
        const val NAVIGATION = 3
        const val FAB = 6
        const val NAVIGATION_DRAWER = 16
        const val MODAL = 24
    }
    
    /**
     * Animation durations
     */
    object Animation {
        const val SHORT = 150
        const val MEDIUM = 300
        const val LONG = 500
    }
    
    /**
     * Typography system integration
     * Quick access to common text styles from NSTypography
     */
    object Typography {
        val heading1: TextStyle get() = NSTypography.heading1
        val heading2: TextStyle get() = NSTypography.heading2
        val heading3: TextStyle get() = NSTypography.heading3
        val subtitle: TextStyle get() = NSTypography.subtitle
        val body: TextStyle get() = NSTypography.body
        val bodyBold: TextStyle get() = NSTypography.bodyBold
        val caption: TextStyle get() = NSTypography.caption
        val captionBold: TextStyle get() = NSTypography.captionBold
        val small: TextStyle get() = NSTypography.small
    }
    
    /**
     * Spacing system integration
     * Quick access to common spacing values from NSSpacing
     */
    object Spacing {
        val xxs: Dp get() = NSSpacing.xxs
        val xs: Dp get() = NSSpacing.xs
        val sm: Dp get() = NSSpacing.sm
        val md: Dp get() = NSSpacing.md
        val lg: Dp get() = NSSpacing.lg
        val xl: Dp get() = NSSpacing.xl
        val xxl: Dp get() = NSSpacing.xxl
        val xxxl: Dp get() = NSSpacing.xxxl
        val screenEdge: Dp get() = NSSpacing.screenEdge
        val stackDefault: Dp get() = NSSpacing.stackDefault
    }
}

/**
 * Extension functions for easier color access
 */
@Composable
fun moodColorFor(level: Int): Color = DesignTokens.Colors.moodColor(level)

@Composable
fun allMoodColors(): List<Color> = DesignTokens.Colors.allMoodColors()

/**
 * Helper function to get contrasting text color
 */
@Composable
fun getContrastingTextColor(backgroundColor: Color): Color {
    // Simple luminance-based contrast calculation
    val luminance = 0.299 * backgroundColor.red + 0.587 * backgroundColor.green + 0.114 * backgroundColor.blue
    return if (luminance > 0.5) Color.Black else Color.White
}
