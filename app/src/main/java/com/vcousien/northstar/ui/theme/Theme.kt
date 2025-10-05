package com.vcousien.northstar.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext

/**
 * Extended color scheme for NorthStar-specific colors
 */
data class NorthStarColors(
    val moodDepressed: androidx.compose.ui.graphics.Color,
    val moodLow: androidx.compose.ui.graphics.Color,
    val moodNeutral: androidx.compose.ui.graphics.Color,
    val moodGood: androidx.compose.ui.graphics.Color,
    val moodElevated: androidx.compose.ui.graphics.Color,
    val textSecondary: androidx.compose.ui.graphics.Color,
    val divider: androidx.compose.ui.graphics.Color,
    val cardBackground: androidx.compose.ui.graphics.Color,
    val warning: androidx.compose.ui.graphics.Color,
    val info: androidx.compose.ui.graphics.Color
)

val LocalNorthStarColors = staticCompositionLocalOf {
    NorthStarColors(
        moodDepressed = NSColors.moodDepressed,
        moodLow = NSColors.moodLow,
        moodNeutral = NSColors.moodNeutral,
        moodGood = NSColors.moodGood,
        moodElevated = NSColors.moodElevated,
        textSecondary = NSColors.textSecondary,
        divider = NSColors.divider,
        cardBackground = NSColors.cardBackground,
        warning = NSColors.warning,
        info = NSColors.info
    )
}

private val DarkColorScheme = darkColorScheme(
    primary = NSColors.primary,
    primaryContainer = NSColors.primaryDark,
    secondary = NSColors.textSecondaryDark,
    surface = NSColors.primaryDark,
    background = androidx.compose.ui.graphics.Color(0xFF121212), // Standard dark background
    error = NSColors.error,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    onSurface = androidx.compose.ui.graphics.Color.White,
    onBackground = androidx.compose.ui.graphics.Color.White,
    onError = androidx.compose.ui.graphics.Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = NSColors.primary,
    primaryContainer = NSColors.primaryLight,
    secondary = NSColors.textSecondary,
    surface = NSColors.surface,
    background = NSColors.background,
    error = NSColors.error,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    onSecondary = NSColors.textPrimary,
    onSurface = NSColors.textPrimary,
    onBackground = NSColors.textPrimary,
    onError = androidx.compose.ui.graphics.Color.White
)

private val DarkNorthStarColors = NorthStarColors(
    moodDepressed = NSColors.moodDepressedDark,
    moodLow = NSColors.moodLowDark,
    moodNeutral = NSColors.moodNeutralDark,
    moodGood = NSColors.moodGoodDark,
    moodElevated = NSColors.moodElevatedDark,
    textSecondary = NSColors.textSecondaryDark,
    divider = NSColors.dividerDark,
    cardBackground = NSColors.primaryDark,
    warning = NSColors.warning,
    info = NSColors.info
)

private val LightNorthStarColors = NorthStarColors(
    moodDepressed = NSColors.moodDepressed,
    moodLow = NSColors.moodLow,
    moodNeutral = NSColors.moodNeutral,
    moodGood = NSColors.moodGood,
    moodElevated = NSColors.moodElevated,
    textSecondary = NSColors.textSecondary,
    divider = NSColors.divider,
    cardBackground = NSColors.cardBackground,
    warning = NSColors.warning,
    info = NSColors.info
)

@Composable
fun NorthStarTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    // Disabled by default to maintain consistent branding
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val northStarColors = if (darkTheme) DarkNorthStarColors else LightNorthStarColors

    CompositionLocalProvider(LocalNorthStarColors provides northStarColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

/**
 * Helper extension to access NorthStar-specific colors
 */
val MaterialTheme.northStarColors: NorthStarColors
    @Composable
    get() = LocalNorthStarColors.current

/**
 * Helper function to get mood color based on level
 */
@Composable
fun getMoodColor(moodLevel: Int): androidx.compose.ui.graphics.Color {
    val colors = MaterialTheme.northStarColors
    return when (moodLevel) {
        0, 1 -> colors.moodDepressed
        2, 3 -> colors.moodLow
        4 -> colors.moodNeutral
        5, 6 -> colors.moodGood
        7, 8 -> colors.moodElevated
        else -> colors.moodNeutral
    }
}
