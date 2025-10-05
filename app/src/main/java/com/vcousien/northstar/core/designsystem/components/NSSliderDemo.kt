package com.vcousien.northstar.core.designsystem.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vcousien.northstar.core.designsystem.DesignTokens
import com.vcousien.northstar.core.designsystem.NSSpacing
import com.vcousien.northstar.core.designsystem.NSTypography
import com.vcousien.northstar.ui.theme.NorthStarTheme

/**
 * NSSlider Demo and Examples
 * 
 * Demonstrates various configurations and use cases for the NSSlider component
 * including mood tracking, sleep quality, and custom rating scenarios.
 */
@Composable
fun NSSliderDemo() {
    var basicSliderValue by remember { mutableFloatStateOf(5f) }
    var moodLevel by remember { mutableIntStateOf(4) }
    var sleepQuality by remember { mutableIntStateOf(7) }
    var medicationAdherence by remember { mutableIntStateOf(80) }
    var customValue by remember { mutableFloatStateOf(3f) }
    var compactValue by remember { mutableFloatStateOf(2f) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(NSSpacing.screenEdge)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(NSSpacing.lg)
    ) {
        // Header
        Text(
            text = "NSSlider Components",
            style = NSTypography.heading2,
            color = DesignTokens.Colors.textPrimary
        )
        
        Text(
            text = "Various slider configurations for rating scales and user input",
            style = NSTypography.body,
            color = DesignTokens.Colors.textSecondary
        )
        
        Divider(color = DesignTokens.Colors.divider)
        
        // Basic NSSlider
        NSCard {
            Column(verticalArrangement = Arrangement.spacedBy(NSSpacing.sm)) {
                Text(
                    text = "Basic NSSlider",
                    style = NSTypography.subtitle,
                    color = DesignTokens.Colors.textPrimary
                )
                Text(
                    text = "Standard slider with customizable range and labels",
                    style = NSTypography.caption,
                    color = DesignTokens.Colors.textSecondary
                )
                
                NSSlider(
                    title = "Rate your experience",
                    value = basicSliderValue,
                    onValueChange = { basicSliderValue = it },
                    range = 0f..10f,
                    steps = 11,
                    labels = listOf("Poor", "Fair", "Good", "Great", "Excellent")
                )
                
                Text(
                    text = "Current value: ${basicSliderValue.toInt()}",
                    style = NSTypography.small,
                    color = DesignTokens.Colors.textPrimary
                )
            }
        }
        
        // Mood Slider
        NSCard {
            Column(verticalArrangement = Arrangement.spacedBy(NSSpacing.sm)) {
                Text(
                    text = "NSMoodSlider",
                    style = NSTypography.subtitle,
                    color = DesignTokens.Colors.textPrimary
                )
                Text(
                    text = "Pre-configured for mood tracking (0-8 scale) with dynamic colors",
                    style = NSTypography.caption,
                    color = DesignTokens.Colors.textSecondary
                )
                
                NSMoodSlider(
                    title = "How are you feeling today?",
                    moodLevel = moodLevel,
                    onMoodChange = { moodLevel = it },
                    showLabels = true
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Mood Level: $moodLevel",
                        style = NSTypography.small,
                        color = DesignTokens.Colors.textPrimary
                    )
                    Text(
                        text = "Category: ${DesignTokens.Mood.getCategoryName(moodLevel)}",
                        style = NSTypography.small,
                        color = DesignTokens.Colors.moodColor(moodLevel)
                    )
                }
            }
        }
        
        // Sleep Quality Slider
        NSCard {
            Column(verticalArrangement = Arrangement.spacedBy(NSSpacing.sm)) {
                Text(
                    text = "NSSleepQualitySlider",
                    style = NSTypography.subtitle,
                    color = DesignTokens.Colors.textPrimary
                )
                Text(
                    text = "Pre-configured for sleep quality rating (1-10 scale)",
                    style = NSTypography.caption,
                    color = DesignTokens.Colors.textSecondary
                )
                
                NSSleepQualitySlider(
                    title = "How well did you sleep last night?",
                    quality = sleepQuality,
                    onQualityChange = { sleepQuality = it }
                )
                
                Text(
                    text = "Sleep Quality: $sleepQuality/10",
                    style = NSTypography.small,
                    color = DesignTokens.Colors.textPrimary
                )
            }
        }
        
        // Medication Adherence Slider
        NSCard {
            Column(verticalArrangement = Arrangement.spacedBy(NSSpacing.sm)) {
                Text(
                    text = "NSAdherenceSlider",
                    style = NSTypography.subtitle,
                    color = DesignTokens.Colors.textPrimary
                )
                Text(
                    text = "Pre-configured for medication adherence tracking (0-100%)",
                    style = NSTypography.caption,
                    color = DesignTokens.Colors.textSecondary
                )
                
                NSAdherenceSlider(
                    title = "Medication adherence this week",
                    adherencePercent = medicationAdherence,
                    onAdherenceChange = { medicationAdherence = it }
                )
                
                Text(
                    text = "Adherence: $medicationAdherence%",
                    style = NSTypography.small,
                    color = when {
                        medicationAdherence >= 80 -> DesignTokens.Colors.success
                        medicationAdherence >= 60 -> DesignTokens.Colors.warning
                        else -> DesignTokens.Colors.error
                    }
                )
            }
        }
        
        // Custom Labels Slider
        NSCard {
            Column(verticalArrangement = Arrangement.spacedBy(NSSpacing.sm)) {
                Text(
                    text = "Custom Labels",
                    style = NSTypography.subtitle,
                    color = DesignTokens.Colors.textPrimary
                )
                Text(
                    text = "Slider with custom descriptive labels",
                    style = NSTypography.caption,
                    color = DesignTokens.Colors.textSecondary
                )
                
                NSSlider(
                    title = "Pain level",
                    value = customValue,
                    onValueChange = { customValue = it },
                    range = 0f..5f,
                    steps = 6,
                    labels = listOf("None", "Mild", "Moderate", "Severe", "Very Severe", "Worst")
                )
                
                Text(
                    text = "Pain Level: ${customValue.toInt()}",
                    style = NSTypography.small,
                    color = DesignTokens.Colors.textPrimary
                )
            }
        }
        
        // Compact Slider
        NSCard {
            Column(verticalArrangement = Arrangement.spacedBy(NSSpacing.sm)) {
                Text(
                    text = "NSSliderCompact",
                    style = NSTypography.subtitle,
                    color = DesignTokens.Colors.textPrimary
                )
                Text(
                    text = "Compact version with reduced spacing for forms",
                    style = NSTypography.caption,
                    color = DesignTokens.Colors.textSecondary
                )
                
                NSSliderCompact(
                    title = "Satisfaction",
                    value = compactValue,
                    onValueChange = { compactValue = it },
                    range = 0f..5f,
                    steps = 6,
                    labels = listOf("😞", "😕", "😐", "🙂", "😊", "😍")
                )
                
                Text(
                    text = "Rating: ${compactValue.toInt()}/5",
                    style = NSTypography.small,
                    color = DesignTokens.Colors.textPrimary
                )
            }
        }
        
        // Disabled State
        NSCard {
            Column(verticalArrangement = Arrangement.spacedBy(NSSpacing.sm)) {
                Text(
                    text = "Disabled State",
                    style = NSTypography.subtitle,
                    color = DesignTokens.Colors.textPrimary
                )
                Text(
                    text = "Slider in disabled state",
                    style = NSTypography.caption,
                    color = DesignTokens.Colors.textSecondary
                )
                
                NSSlider(
                    title = "Locked setting",
                    value = 5f,
                    onValueChange = { /* No-op for disabled */ },
                    range = 0f..10f,
                    steps = 11,
                    labels = listOf("Low", "Medium", "High"),
                    enabled = false
                )
                
                Text(
                    text = "This slider is disabled",
                    style = NSTypography.small,
                    color = DesignTokens.Colors.textSecondary
                )
            }
        }
        
        // No Labels Slider
        NSCard {
            Column(verticalArrangement = Arrangement.spacedBy(NSSpacing.sm)) {
                Text(
                    text = "No Labels",
                    style = NSTypography.subtitle,
                    color = DesignTokens.Colors.textPrimary
                )
                Text(
                    text = "Clean slider without bottom labels",
                    style = NSTypography.caption,
                    color = DesignTokens.Colors.textSecondary
                )
                
                NSSlider(
                    title = "Volume",
                    value = 7f,
                    onValueChange = { /* Demo only */ },
                    range = 0f..10f,
                    steps = 11,
                    labels = null // No labels
                )
            }
        }
        
        // Add some bottom padding for scroll
        Spacer(modifier = Modifier.height(NSSpacing.lg))
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun NSSliderDemoPreview() {
    NorthStarTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = DesignTokens.Colors.background
        ) {
            NSSliderDemo()
        }
    }
}

@Preview(name = "Individual Sliders", showBackground = true)
@Composable
fun IndividualSlidersPreview() {
    var moodValue by remember { mutableIntStateOf(6) }
    var basicValue by remember { mutableFloatStateOf(3f) }
    
    NorthStarTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            NSSlider(
                title = "Basic Rating",
                value = basicValue,
                onValueChange = { basicValue = it },
                range = 0f..5f,
                steps = 6,
                labels = listOf("Poor", "Fair", "Good", "Very Good", "Excellent")
            )
            
            NSMoodSlider(
                title = "Current Mood",
                moodLevel = moodValue,
                onMoodChange = { moodValue = it }
            )
        }
    }
}
