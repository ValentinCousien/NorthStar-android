package com.vcousien.northstar.features.mood.models

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.WbCloudy
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.vcousien.northstar.R
import com.vcousien.northstar.core.designsystem.DesignTokens
import kotlinx.serialization.Serializable

/**
 * Represents different mood levels on a scale from severely depressed to manic
 * This enum provides a 9-level scale (0-8) for mood tracking
 */
@Serializable
enum class MoodLevel(
    val value: Int,
    @StringRes val nameResId: Int
) {
    /**
     * Severely depressed mood state (Level 0)
     */
    SEVERELY_DEPRESSED(
        value = 0,
        nameResId = R.string.mood_severely_depressed
    ),

    /**
     * Depressed mood state (Level 1)
     */
    DEPRESSED(
        value = 1,
        nameResId = R.string.mood_depressed
    ),

    /**
     * Low mood state (Level 2)
     */
    LOW(
        value = 2,
        nameResId = R.string.mood_low
    ),

    /**
     * Slightly low mood state (Level 3)
     */
    SLIGHTLY_LOW(
        value = 3,
        nameResId = R.string.mood_slightly_low
    ),

    /**
     * Neutral mood state (Level 4)
     */
    NEUTRAL(
        value = 4,
        nameResId = R.string.mood_neutral
    ),

    /**
     * Slightly elevated mood state (Level 5)
     */
    SLIGHTLY_ELEVATED(
        value = 5,
        nameResId = R.string.mood_slightly_elevated
    ),

    /**
     * Elevated mood state (Level 6)
     */
    ELEVATED(
        value = 6,
        nameResId = R.string.mood_elevated
    ),

    /**
     * Highly elevated mood state (Level 7)
     */
    HIGHLY_ELEVATED(
        value = 7,
        nameResId = R.string.mood_highly_elevated
    ),

    /**
     * Manic mood state (Level 8)
     */
    MANIC(
        value = 8,
        nameResId = R.string.mood_manic
    );

    /**
     * Emoji icon for this mood level (for text display)
     */
    val icon: String
        get() = when (this) {
            SEVERELY_DEPRESSED -> "🌧️"
            DEPRESSED -> "☁️"
            LOW -> "🌥️"
            SLIGHTLY_LOW -> "⛅"
            NEUTRAL -> "😐"
            SLIGHTLY_ELEVATED -> "🙂"
            ELEVATED -> "😊"
            HIGHLY_ELEVATED -> "😄"
            MANIC -> "🤩"
        }

    /**
     * Get the appropriate Material icon for this mood level
     */
    fun getIcon(): ImageVector {
        return when (this) {
            SEVERELY_DEPRESSED, DEPRESSED -> Icons.Filled.Cloud
            LOW, SLIGHTLY_LOW -> Icons.Filled.WbCloudy
            NEUTRAL, SLIGHTLY_ELEVATED, ELEVATED, HIGHLY_ELEVATED, MANIC -> Icons.Filled.WbSunny
        }
    }

    /**
     * Non-localized display name (for use in non-Composable contexts)
     */
    val displayName: String
        get() = when (this) {
            SEVERELY_DEPRESSED -> "Severely Depressed"
            DEPRESSED -> "Depressed"
            LOW -> "Low"
            SLIGHTLY_LOW -> "Slightly Low"
            NEUTRAL -> "Neutral"
            SLIGHTLY_ELEVATED -> "Slightly Elevated"
            ELEVATED -> "Elevated"
            HIGHLY_ELEVATED -> "Highly Elevated"
            MANIC -> "Manic"
        }

    /**
     * Get the localized name for this mood level
     */
    @Composable
    fun getName(): String {
        return stringResource(nameResId)
    }

    /**
     * Color for this mood level (non-Composable access)
     */
    val color: Color
        get() = when (this) {
            SEVERELY_DEPRESSED -> Color(0xFF5B4E77)
            DEPRESSED -> Color(0xFF7161A0)
            LOW -> Color(0xFF8477B3)
            SLIGHTLY_LOW -> Color(0xFF9A8FC6)
            NEUTRAL -> Color(0xFFB0A7D9)
            SLIGHTLY_ELEVATED -> Color(0xFFC6BFEC)
            ELEVATED -> Color(0xFFDCD7FF)
            HIGHLY_ELEVATED -> Color(0xFFECE9FF)
            MANIC -> Color(0xFFFFF8F6)
        }

    /**
     * Get the color associated with this mood level (Composable version)
     */
    @Composable
    fun getColor(): Color {
        return DesignTokens.Colors.moodColor(value)
    }

    /**
     * Get mood level category (depressed, low, neutral, good, elevated)
     */
    fun getCategory(): MoodCategory {
        return when (this) {
            SEVERELY_DEPRESSED, DEPRESSED -> MoodCategory.DEPRESSED
            LOW, SLIGHTLY_LOW -> MoodCategory.LOW
            NEUTRAL -> MoodCategory.NEUTRAL
            SLIGHTLY_ELEVATED, ELEVATED -> MoodCategory.GOOD
            HIGHLY_ELEVATED, MANIC -> MoodCategory.ELEVATED
        }
    }

    /**
     * Check if this mood level indicates depression
     */
    fun isDepressed(): Boolean = this in listOf(SEVERELY_DEPRESSED, DEPRESSED)

    /**
     * Check if this mood level indicates low mood
     */
    fun isLow(): Boolean = this in listOf(LOW, SLIGHTLY_LOW)

    /**
     * Check if this mood level is neutral
     */
    fun isNeutral(): Boolean = this == NEUTRAL

    /**
     * Check if this mood level indicates good mood
     */
    fun isGood(): Boolean = this in listOf(SLIGHTLY_ELEVATED, ELEVATED)

    /**
     * Check if this mood level indicates elevated/manic mood
     */
    fun isElevated(): Boolean = this in listOf(HIGHLY_ELEVATED, MANIC)

    /**
     * Check if this mood level is concerning (severely depressed or manic)
     */
    fun isConcerning(): Boolean = this == SEVERELY_DEPRESSED || this == MANIC

    companion object {
        /**
         * Create MoodLevel from integer value
         */
        fun fromValue(value: Int): MoodLevel? {
            return entries.find { it.value == value }
        }

        /**
         * Get all mood levels in order
         */
        fun getAllLevels(): List<MoodLevel> = entries

        /**
         * Get the middle/neutral mood level
         */
        fun getDefaultLevel(): MoodLevel = NEUTRAL

        /**
         * Validate if a mood level value is within the valid range
         */
        fun isValidValue(value: Int): Boolean = value in 0..8
    }
}

/**
 * Mood categories for grouping mood levels
 */
@Serializable
enum class MoodCategory(
    @StringRes val nameResId: Int
) {
    DEPRESSED(R.string.mood_depressed),
    LOW(R.string.mood_low),
    NEUTRAL(R.string.mood_neutral),
    GOOD(R.string.mood_good),
    ELEVATED(R.string.mood_elevated);

    @Composable
    fun getName(): String {
        return stringResource(nameResId)
    }

    /**
     * Get the representative color for this mood category
     */
    @Composable
    fun getColor(): Color {
        return when (this) {
            DEPRESSED -> DesignTokens.Colors.moodColor(0)
            LOW -> DesignTokens.Colors.moodColor(2)
            NEUTRAL -> DesignTokens.Colors.moodColor(4)
            GOOD -> DesignTokens.Colors.moodColor(6)
            ELEVATED -> DesignTokens.Colors.moodColor(8)
        }
    }
}
