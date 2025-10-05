package com.vcousien.northstar.core.designsystem.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.vcousien.northstar.core.designsystem.DesignTokens
import com.vcousien.northstar.core.designsystem.NSSpacing
import com.vcousien.northstar.core.designsystem.NSTypography
import kotlin.math.roundToInt

/**
 * NSSlider - Standardized slider component for ratings and scales
 * 
 * A reusable slider component that provides consistent styling and behavior
 * across the NorthStar app. Matches the iOS NSSlider implementation.
 * 
 * Features:
 * - Customizable title label
 * - Configurable value range and step size
 * - Optional labels below the slider for value descriptions
 * - Proper Material Design slider styling with NorthStar colors
 * - Value change callback for reactive updates
 * - Support for mood scale (0-8) and other rating systems
 * 
 * @param title The label text displayed above the slider
 * @param value The current slider value
 * @param onValueChange Callback invoked when the slider value changes
 * @param range The range of values the slider can represent (default: 0f..8f for mood scale)
 * @param steps Number of discrete steps in the range (default: 8 for mood scale)
 * @param labels Optional list of labels to display below the slider corresponding to values
 * @param modifier Modifier to be applied to the slider container
 * @param enabled Whether the slider is enabled for interaction (default: true)
 * @param colors Custom color configuration for the slider (optional)
 */
@Composable
fun NSSlider(
    title: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    range: ClosedFloatingPointRange<Float> = 0f..8f,
    steps: Int = 8,
    labels: List<String>? = null,
    enabled: Boolean = true,
    colors: SliderColors? = null
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(NSSpacing.sm)
    ) {
        // Title label
        Text(
            text = title,
            style = NSTypography.caption,
            color = DesignTokens.Colors.textSecondary
        )
        
        // Slider component
        val sliderColors = colors ?: SliderDefaults.colors(
            thumbColor = DesignTokens.Colors.primary,
            activeTrackColor = DesignTokens.Colors.primary,
            inactiveTrackColor = DesignTokens.Colors.primary.copy(alpha = DesignTokens.Alpha.INACTIVE),
            disabledThumbColor = DesignTokens.Colors.primary.copy(alpha = DesignTokens.Alpha.DISABLED),
            disabledActiveTrackColor = DesignTokens.Colors.primary.copy(alpha = DesignTokens.Alpha.DISABLED),
            disabledInactiveTrackColor = DesignTokens.Colors.primary.copy(alpha = DesignTokens.Alpha.DISABLED)
        )
        
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            steps = if (steps > 0) steps - 1 else 0, // Adjust for Material Slider step counting
            enabled = enabled,
            colors = sliderColors,
            modifier = Modifier.fillMaxWidth()
        )
        
        // Optional labels below the slider
        if (labels != null && labels.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                labels.forEachIndexed { index, label ->
                    Text(
                        text = label,
                        style = NSTypography.small,
                        color = DesignTokens.Colors.textSecondary,
                        textAlign = when {
                            index == 0 -> TextAlign.Start
                            index == labels.lastIndex -> TextAlign.End
                            else -> TextAlign.Center
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

/**
 * NSSlider variant specifically designed for mood tracking
 * Pre-configured with mood scale range (0-8) and appropriate labels
 * 
 * @param title The label text displayed above the slider
 * @param moodLevel The current mood level (0-8)
 * @param onMoodChange Callback invoked when the mood level changes
 * @param modifier Modifier to be applied to the slider container
 * @param showLabels Whether to show mood level labels below the slider (default: true)
 * @param enabled Whether the slider is enabled for interaction (default: true)
 */
@Composable
fun NSMoodSlider(
    title: String,
    moodLevel: Int,
    onMoodChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    showLabels: Boolean = true,
    enabled: Boolean = true
) {
    val moodLabels = if (showLabels) {
        listOf("0", "1", "2", "3", "4", "5", "6", "7", "8")
    } else {
        null
    }
    
    // Create a mood-specific color scheme
    val currentMoodColor = DesignTokens.Colors.moodColor(moodLevel)
    val moodColors = SliderDefaults.colors(
        thumbColor = currentMoodColor,
        activeTrackColor = currentMoodColor,
        inactiveTrackColor = currentMoodColor.copy(alpha = DesignTokens.Alpha.INACTIVE)
    )
    
    NSSlider(
        title = title,
        value = moodLevel.toFloat(),
        onValueChange = { newValue -> onMoodChange(newValue.roundToInt()) },
        range = 0f..8f,
        steps = 9, // 0-8 inclusive = 9 possible values
        labels = moodLabels,
        enabled = enabled,
        colors = moodColors,
        modifier = modifier
    )
}

/**
 * NSSlider variant for sleep quality rating
 * Pre-configured with 1-10 scale for sleep quality assessment
 * 
 * @param title The label text displayed above the slider
 * @param quality The current sleep quality rating (1-10)
 * @param onQualityChange Callback invoked when the quality rating changes
 * @param modifier Modifier to be applied to the slider container
 * @param showLabels Whether to show quality labels below the slider (default: true)
 * @param enabled Whether the slider is enabled for interaction (default: true)
 */
@Composable
fun NSSleepQualitySlider(
    title: String,
    quality: Int,
    onQualityChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    showLabels: Boolean = true,
    enabled: Boolean = true
) {
    val qualityLabels = if (showLabels) {
        listOf("Poor", "", "", "", "Fair", "", "", "", "", "Excellent")
    } else {
        null
    }
    
    NSSlider(
        title = title,
        value = quality.toFloat(),
        onValueChange = { newValue -> onQualityChange(newValue.roundToInt()) },
        range = 1f..10f,
        steps = 10, // 1-10 inclusive = 10 possible values
        labels = qualityLabels,
        enabled = enabled,
        modifier = modifier
    )
}

/**
 * NSSlider variant for medication adherence rating
 * Pre-configured with percentage scale (0-100%) for adherence tracking
 * 
 * @param title The label text displayed above the slider
 * @param adherencePercent The current adherence percentage (0-100)
 * @param onAdherenceChange Callback invoked when the adherence percentage changes
 * @param modifier Modifier to be applied to the slider container
 * @param showLabels Whether to show percentage labels below the slider (default: true)
 * @param enabled Whether the slider is enabled for interaction (default: true)
 */
@Composable
fun NSAdherenceSlider(
    title: String,
    adherencePercent: Int,
    onAdherenceChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    showLabels: Boolean = true,
    enabled: Boolean = true
) {
    val adherenceLabels = if (showLabels) {
        listOf("0%", "25%", "50%", "75%", "100%")
    } else {
        null
    }
    
    NSSlider(
        title = title,
        value = adherencePercent.toFloat(),
        onValueChange = { newValue -> onAdherenceChange(newValue.roundToInt()) },
        range = 0f..100f,
        steps = 21, // 0, 5, 10, ..., 100 = 21 steps (5% increments)
        labels = adherenceLabels,
        enabled = enabled,
        modifier = modifier
    )
}

/**
 * Compact NSSlider variant with reduced spacing
 * Useful for forms or condensed layouts where space is limited
 * 
 * @param title The label text displayed above the slider
 * @param value The current slider value
 * @param onValueChange Callback invoked when the slider value changes
 * @param range The range of values the slider can represent
 * @param steps Number of discrete steps in the range
 * @param labels Optional list of labels to display below the slider
 * @param modifier Modifier to be applied to the slider container
 * @param enabled Whether the slider is enabled for interaction
 */
@Composable
fun NSSliderCompact(
    title: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    range: ClosedFloatingPointRange<Float> = 0f..10f,
    steps: Int = 10,
    labels: List<String>? = null,
    enabled: Boolean = true
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(NSSpacing.xs)
    ) {
        // Compact title
        Text(
            text = title,
            style = NSTypography.small,
            color = DesignTokens.Colors.textSecondary
        )
        
        // Slider with compact spacing
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            steps = if (steps > 0) steps - 1 else 0,
            enabled = enabled,
            colors = SliderDefaults.colors(
                thumbColor = DesignTokens.Colors.primary,
                activeTrackColor = DesignTokens.Colors.primary,
                inactiveTrackColor = DesignTokens.Colors.primary.copy(alpha = DesignTokens.Alpha.INACTIVE)
            ),
            modifier = Modifier.fillMaxWidth()
        )
        
        // Compact labels
        if (labels != null && labels.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                labels.forEachIndexed { index, label ->
                    Text(
                        text = label,
                        style = NSTypography.small.copy(fontSize = NSTypography.FontSize.xs),
                        color = DesignTokens.Colors.textSecondary,
                        textAlign = when {
                            index == 0 -> TextAlign.Start
                            index == labels.lastIndex -> TextAlign.End
                            else -> TextAlign.Center
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}