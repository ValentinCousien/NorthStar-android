package com.vcousien.northstar.features.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vcousien.northstar.core.designsystem.DesignTokens
import com.vcousien.northstar.core.designsystem.NSSpacing
import com.vcousien.northstar.core.designsystem.NSTypography
import com.vcousien.northstar.core.designsystem.components.*
import com.vcousien.northstar.ui.theme.NorthStarTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Mock Home Screen for NorthStar App
 * 
 * Demonstrates the NSSlider components in a realistic home page context
 * showing today's tracking, quick entries, and recent history.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MockHomeScreen() {
    // State for tracking entries
    var todayMoodLevel by remember { mutableIntStateOf(5) }
    var todayEnergyLevel by remember { mutableFloatStateOf(6f) }
    var lastNightSleepQuality by remember { mutableIntStateOf(7) }
    var medicationAdherence by remember { mutableIntStateOf(85) }
    var anxietyLevel by remember { mutableFloatStateOf(3f) }
    var painLevel by remember { mutableFloatStateOf(2f) }
    
    // Mock data state
    var hasEntryToday by remember { mutableStateOf(false) }
    
    val today = LocalDate.now()
    val dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d")
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Column {
                    Text(
                        text = "Good ${getTimeOfDayGreeting()}!",
                        style = NSTypography.heading3,
                        color = DesignTokens.Colors.textPrimary
                    )
                    Text(
                        text = today.format(dateFormatter),
                        style = NSTypography.caption,
                        color = DesignTokens.Colors.textSecondary
                    )
                }
            },
            actions = {
                IconButton(onClick = { /* Settings */ }) {
                    Icon(
                        Icons.Outlined.Settings,
                        contentDescription = "Settings",
                        tint = DesignTokens.Colors.textSecondary
                    )
                }
                IconButton(onClick = { /* Notifications */ }) {
                    Icon(
                        Icons.Outlined.Notifications,
                        contentDescription = "Notifications",
                        tint = DesignTokens.Colors.textSecondary
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = DesignTokens.Colors.background
            )
        )
        
        Column(
            modifier = Modifier.padding(NSSpacing.screenEdge),
            verticalArrangement = Arrangement.spacedBy(NSSpacing.lg)
        ) {
            // Today's Check-in Section
            NSCard {
                Column(
                    verticalArrangement = Arrangement.spacedBy(NSSpacing.md)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Today's Check-in",
                            style = NSTypography.heading3,
                            color = DesignTokens.Colors.textPrimary
                        )
                        if (hasEntryToday) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = "Completed",
                                tint = DesignTokens.Colors.success,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    
                    // Primary mood slider
                    NSMoodSlider(
                        title = "How are you feeling right now?",
                        moodLevel = todayMoodLevel,
                        onMoodChange = { 
                            todayMoodLevel = it
                            hasEntryToday = true
                        }
                    )
                    
                    // Mood insight
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Mood Category: ${DesignTokens.Mood.getCategoryName(todayMoodLevel)}",
                            style = NSTypography.caption,
                            color = DesignTokens.Colors.moodColor(todayMoodLevel)
                        )
                        if (todayMoodLevel != 4) {
                            Text(
                                text = if (todayMoodLevel > 4) "↗️ Above neutral" else "↘️ Below neutral",
                                style = NSTypography.caption,
                                color = DesignTokens.Colors.textSecondary
                            )
                        }
                    }
                }
            }
            
            // Quick Trackers Section
            NSCard {
                Column(
                    verticalArrangement = Arrangement.spacedBy(NSSpacing.md)
                ) {
                    Text(
                        text = "Quick Trackers",
                        style = NSTypography.heading3,
                        color = DesignTokens.Colors.textPrimary
                    )
                    
                    // Energy Level
                    NSSlider(
                        title = "Energy Level",
                        value = todayEnergyLevel,
                        onValueChange = { todayEnergyLevel = it },
                        range = 0f..10f,
                        steps = 11,
                        labels = listOf("Exhausted", "Low", "Medium", "High", "Energized")
                    )
                    
                    Divider(color = DesignTokens.Colors.divider)
                    
                    // Anxiety Level
                    NSSlider(
                        title = "Anxiety Level",
                        value = anxietyLevel,
                        onValueChange = { anxietyLevel = it },
                        range = 0f..10f,
                        steps = 11,
                        labels = listOf("Calm", "Mild", "Moderate", "High", "Severe"),
                        colors = SliderDefaults.colors(
                            thumbColor = when {
                                anxietyLevel <= 3f -> DesignTokens.Colors.success
                                anxietyLevel <= 6f -> DesignTokens.Colors.warning
                                else -> DesignTokens.Colors.error
                            },
                            activeTrackColor = when {
                                anxietyLevel <= 3f -> DesignTokens.Colors.success
                                anxietyLevel <= 6f -> DesignTokens.Colors.warning
                                else -> DesignTokens.Colors.error
                            }
                        )
                    )
                    
                    Divider(color = DesignTokens.Colors.divider)
                    
                    // Pain Level
                    NSSlider(
                        title = "Physical Discomfort",
                        value = painLevel,
                        onValueChange = { painLevel = it },
                        range = 0f..10f,
                        steps = 11,
                        labels = listOf("None", "Mild", "Moderate", "Severe", "Unbearable"),
                        colors = SliderDefaults.colors(
                            thumbColor = when {
                                painLevel <= 2f -> DesignTokens.Colors.success
                                painLevel <= 5f -> DesignTokens.Colors.warning  
                                else -> DesignTokens.Colors.error
                            },
                            activeTrackColor = when {
                                painLevel <= 2f -> DesignTokens.Colors.success
                                painLevel <= 5f -> DesignTokens.Colors.warning
                                else -> DesignTokens.Colors.error
                            }
                        )
                    )
                }
            }
            
            // Sleep Quality Section
            NSCard {
                Column(
                    verticalArrangement = Arrangement.spacedBy(NSSpacing.md)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Last Night's Sleep",
                            style = NSTypography.heading3,
                            color = DesignTokens.Colors.textPrimary
                        )
                        Icon(
                            Icons.Outlined.Bedtime,
                            contentDescription = "Sleep",
                            tint = DesignTokens.Colors.info,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    
                    NSSleepQualitySlider(
                        title = "How well did you sleep?",
                        quality = lastNightSleepQuality,
                        onQualityChange = { lastNightSleepQuality = it }
                    )
                    
                    // Sleep summary
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${lastNightSleepQuality}/10 Quality",
                            style = NSTypography.caption,
                            color = DesignTokens.Colors.textPrimary
                        )
                        Text(
                            text = when {
                                lastNightSleepQuality >= 8 -> "😴 Great rest!"
                                lastNightSleepQuality >= 6 -> "😊 Good sleep"
                                lastNightSleepQuality >= 4 -> "😐 Okay sleep"
                                else -> "😞 Poor sleep"
                            },
                            style = NSTypography.caption,
                            color = DesignTokens.Colors.textSecondary
                        )
                    }
                }
            }
            
            // Medication Adherence Section
            NSCard {
                Column(
                    verticalArrangement = Arrangement.spacedBy(NSSpacing.md)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Medication Adherence",
                            style = NSTypography.heading3,
                            color = DesignTokens.Colors.textPrimary
                        )
                        Icon(
                            Icons.Outlined.LocalPharmacy,
                            contentDescription = "Medications",
                            tint = DesignTokens.Colors.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    
                    Text(
                        text = "This week",
                        style = NSTypography.caption,
                        color = DesignTokens.Colors.textSecondary
                    )
                    
                    NSAdherenceSlider(
                        title = "How consistent were you with medications?",
                        adherencePercent = medicationAdherence,
                        onAdherenceChange = { medicationAdherence = it }
                    )
                    
                    // Adherence feedback
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$medicationAdherence% Adherence",
                            style = NSTypography.caption,
                            color = when {
                                medicationAdherence >= 90 -> DesignTokens.Colors.success
                                medicationAdherence >= 70 -> DesignTokens.Colors.warning
                                else -> DesignTokens.Colors.error
                            }
                        )
                        Text(
                            text = when {
                                medicationAdherence >= 90 -> "🎯 Excellent!"
                                medicationAdherence >= 80 -> "👍 Good job!"
                                medicationAdherence >= 70 -> "⚠️ Can improve"
                                else -> "❗ Needs attention"
                            },
                            style = NSTypography.caption,
                            color = DesignTokens.Colors.textSecondary
                        )
                    }
                }
            }
            
            // Quick Actions
            NSCard {
                Column(
                    verticalArrangement = Arrangement.spacedBy(NSSpacing.md)
                ) {
                    Text(
                        text = "Quick Actions",
                        style = NSTypography.heading3,
                        color = DesignTokens.Colors.textPrimary
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        QuickActionButton(
                            icon = Icons.Outlined.Add,
                            label = "Add Entry",
                            onClick = { /* Navigate to add entry */ }
                        )
                        QuickActionButton(
                            icon = Icons.Outlined.Timeline,
                            label = "View Trends",
                            onClick = { /* Navigate to insights */ }
                        )
                        QuickActionButton(
                            icon = Icons.Outlined.Schedule,
                            label = "Set Reminder",
                            onClick = { /* Navigate to reminders */ }
                        )
                    }
                }
            }
            
            // Recent Entries Summary
            NSCard {
                Column(
                    verticalArrangement = Arrangement.spacedBy(NSSpacing.sm)
                ) {
                    Text(
                        text = "This Week Summary",
                        style = NSTypography.heading3,
                        color = DesignTokens.Colors.textPrimary
                    )
                    
                    SummaryRow(
                        label = "Average Mood",
                        value = "5.2",
                        trend = "+0.3",
                        isPositive = true
                    )
                    SummaryRow(
                        label = "Sleep Quality",
                        value = "7.1/10",
                        trend = "-0.2",
                        isPositive = false
                    )
                    SummaryRow(
                        label = "Medication Adherence",
                        value = "$medicationAdherence%",
                        trend = "+5%",
                        isPositive = true
                    )
                }
            }
            
            // Bottom padding for scroll
            Spacer(modifier = Modifier.height(NSSpacing.xxl))
        }
    }
}

@Composable
private fun QuickActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(NSSpacing.xs)
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier.size(56.dp)
        ) {
            Icon(
                icon,
                contentDescription = label,
                tint = DesignTokens.Colors.primary,
                modifier = Modifier.size(24.dp)
            )
        }
        Text(
            text = label,
            style = NSTypography.small,
            color = DesignTokens.Colors.textSecondary,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
    trend: String,
    isPositive: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = NSTypography.caption,
            color = DesignTokens.Colors.textSecondary
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(NSSpacing.xs),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = value,
                style = NSTypography.captionBold,
                color = DesignTokens.Colors.textPrimary
            )
            Text(
                text = trend,
                style = NSTypography.small,
                color = if (isPositive) DesignTokens.Colors.success else DesignTokens.Colors.error
            )
        }
    }
}

private fun getTimeOfDayGreeting(): String {
    val hour = java.time.LocalTime.now().hour
    return when {
        hour < 12 -> "morning"
        hour < 17 -> "afternoon"
        else -> "evening"
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MockHomeScreenPreview() {
    NorthStarTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = DesignTokens.Colors.background
        ) {
            MockHomeScreen()
        }
    }
}

@Preview(name = "Dark Theme", showBackground = true, showSystemUi = true)
@Composable  
fun MockHomeScreenDarkPreview() {
    NorthStarTheme(darkTheme = true) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = DesignTokens.Colors.background
        ) {
            MockHomeScreen()
        }
    }
}
