package com.vcousien.northstar.core.designsystem.extensions

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vcousien.northstar.core.designsystem.DesignTokens
import com.vcousien.northstar.core.designsystem.NSSpacing
import com.vcousien.northstar.core.designsystem.NSTypography
import com.vcousien.northstar.core.designsystem.components.NSCard
import com.vcousien.northstar.core.designsystem.components.NSPrimaryButton
import com.vcousien.northstar.ui.theme.NorthStarTheme

/**
 * Demo screen showcasing UI extensions and utility composables
 * 
 * This demo helps visualize and test all the extension functions and
 * utility composables provided by the NorthStar design system.
 */

@Composable
fun UIExtensionsDemo() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .withDefaultBackground()
            .withContentPadding(),
        verticalArrangement = Arrangement.spacedBy(NSSpacing.lg)
    ) {
        item {
            Text(
                text = "UI Extensions & Utilities Demo",
                style = NSTypography.heading1,
                color = DesignTokens.Colors.textPrimary
            )
        }
        
        // Modifier Extensions Demo
        item {
            NSCard {
                SectionHeader(title = "Modifier Extensions")
                VerticalSpacerMD()
                
                SpacedColumn(spacing = NSSpacing.md) {
                    // Background variations
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .withCardBackground(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Card Background", style = NSTypography.body)
                    }
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .withMoodBackground(7),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Mood Background (Level 7)", style = NSTypography.body)
                    }
                    
                    // Corner variations
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(NSSpacing.sm)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .withCardBackground()
                                .withSmallCorners(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Small", style = NSTypography.caption)
                        }
                        
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .withCardBackground()
                                .withStandardCorners(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Standard", style = NSTypography.caption)
                        }
                        
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .withCardBackground()
                                .withLargeCorners(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Large", style = NSTypography.caption)
                        }
                    }
                }
            }
        }
        
        // Status Indicators Demo
        item {
            NSCard {
                SectionHeader(title = "Status Indicators")
                VerticalSpacerMD()
                
                SpacedColumn {
                    SpacedRow {
                        StatusIndicator(StatusType.Success)
                        StatusIndicator(StatusType.Error)
                        StatusIndicator(StatusType.Warning)
                        StatusIndicator(StatusType.Info)
                    }
                    
                    Text("Different sizes:", style = NSTypography.caption)
                    SpacedRow {
                        StatusIndicator(StatusType.Success, size = IndicatorSize.Small)
                        StatusIndicator(StatusType.Success, size = IndicatorSize.Medium)
                        StatusIndicator(StatusType.Success, size = IndicatorSize.Large)
                    }
                }
            }
        }
        
        // Badges Demo
        item {
            NSCard {
                SectionHeader(title = "Badges")
                VerticalSpacerMD()
                
                SpacedColumn {
                    Text("Badge styles:", style = NSTypography.caption)
                    SpacedRow {
                        NSBadge("3", style = BadgeStyle.Default)
                        NSBadge("New", style = BadgeStyle.Rounded)
                        NSBadge("12", style = BadgeStyle.Square)
                    }
                    
                    Text("Custom colors:", style = NSTypography.caption)
                    SpacedRow {
                        NSBadge("Success", backgroundColor = DesignTokens.Colors.success)
                        NSBadge("Warning", backgroundColor = DesignTokens.Colors.warning)
                        NSBadge("Error", backgroundColor = DesignTokens.Colors.error)
                    }
                }
            }
        }
        
        // Chips Demo
        item {
            NSCard {
                SectionHeader(title = "Chips")
                VerticalSpacerMD()
                
                var selectedChips by remember { mutableStateOf(setOf<String>()) }
                
                SpacedColumn {
                    Text("Selectable chips:", style = NSTypography.caption)
                    
                    val chipLabels = listOf("Mood", "Sleep", "Medication", "Exercise", "Diet")
                    FlexBoxRow {
                        chipLabels.forEach { label ->
                            NSChip(
                                label = label,
                                isSelected = selectedChips.contains(label),
                                onSelectionChanged = { isSelected ->
                                    selectedChips = if (isSelected) {
                                        selectedChips + label
                                    } else {
                                        selectedChips - label
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
        
        // Avatars Demo
        item {
            NSCard {
                SectionHeader(title = "Avatars")
                VerticalSpacerMD()
                
                SpacedColumn {
                    Text("Avatar sizes:", style = NSTypography.caption)
                    SpacedRow {
                        NSAvatar("JD", size = AvatarSize.Small)
                        NSAvatar("JD", size = AvatarSize.Medium)
                        NSAvatar("JD", size = AvatarSize.Large)
                        NSAvatar("JD", size = AvatarSize.ExtraLarge)
                    }
                    
                    Text("Custom colors:", style = NSTypography.caption)
                    SpacedRow {
                        NSAvatar("AB", backgroundColor = DesignTokens.Colors.success)
                        NSAvatar("CD", backgroundColor = DesignTokens.Colors.warning)
                        NSAvatar("EF", backgroundColor = DesignTokens.Colors.error)
                    }
                }
            }
        }
        
        // Progress Indicators Demo
        item {
            NSCard {
                SectionHeader(title = "Progress Indicators")
                VerticalSpacerMD()
                
                SpacedColumn {
                    NSProgressIndicator(
                        progress = 0.3f,
                        label = "Mood tracking",
                        showPercentage = true
                    )
                    
                    NSProgressIndicator(
                        progress = 0.7f,
                        label = "Sleep goals",
                        showPercentage = true,
                        color = DesignTokens.Colors.success
                    )
                    
                    NSProgressIndicator(
                        progress = 0.9f,
                        label = "Medication adherence",
                        showPercentage = true,
                        color = DesignTokens.Colors.info
                    )
                }
            }
        }
        
        // Mood Indicators Demo
        item {
            NSCard {
                SectionHeader(title = "Mood Indicators")
                VerticalSpacerMD()
                
                SpacedColumn {
                    Text("Mood levels with labels:", style = NSTypography.caption)
                    (0..8).forEach { level ->
                        MoodIndicator(
                            level = level,
                            showLabel = true,
                            size = IndicatorSize.Medium
                        )
                    }
                }
            }
        }
        
        // Info Cards Demo
        item {
            NSCard {
                SectionHeader(title = "Info Cards")
                VerticalSpacerMD()
                
                SpacedColumn {
                    InfoCard(
                        message = "This is a success message with helpful information.",
                        type = StatusType.Success
                    )
                    
                    InfoCard(
                        message = "This is a warning about something you should know.",
                        type = StatusType.Warning
                    )
                    
                    InfoCard(
                        message = "This is an error message that needs attention.",
                        type = StatusType.Error,
                        onDismiss = { /* Handle dismiss */ }
                    )
                    
                    InfoCard(
                        message = "This is general information that might be useful.",
                        type = StatusType.Info
                    )
                }
            }
        }
        
        // Expandable Section Demo
        item {
            NSCard {
                SectionHeader(title = "Expandable Sections")
                VerticalSpacerMD()
                
                SpacedColumn {
                    ExpandableSection(
                        title = "Advanced Settings",
                        initiallyExpanded = false
                    ) {
                        Text(
                            "Here are some advanced settings that can be configured. " +
                                    "This content is collapsible to keep the interface clean.",
                            style = NSTypography.body,
                            color = DesignTokens.Colors.textSecondary
                        )
                        VerticalSpacerSM()
                        NSPrimaryButton(
                            title = "Configure",
                            onClick = { /* Handle configuration */ }
                        )
                    }
                    
                    StandardDivider()
                    
                    ExpandableSection(
                        title = "Help & Support",
                        initiallyExpanded = true
                    ) {
                        Text(
                            "Get help with using the app or contact our support team.",
                            style = NSTypography.body,
                            color = DesignTokens.Colors.textSecondary
                        )
                    }
                }
            }
        }
        
        // Animation Demo
        item {
            NSCard {
                SectionHeader(title = "Animations")
                VerticalSpacerMD()
                
                var showAnimations by remember { mutableStateOf(false) }
                
                SpacedColumn {
                    NSPrimaryButton(
                        title = if (showAnimations) "Reset Animations" else "Show Animations",
                        onClick = { showAnimations = !showAnimations }
                    )
                    
                    if (showAnimations) {
                        VerticalSpacerMD()
                        
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                                .withCardBackground()
                                .fadeIn(delayMs = 100),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Fade In Animation", style = NSTypography.body)
                        }
                        
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                                .withCardBackground()
                                .slideInFromBottom(delayMs = 300),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Slide In Animation", style = NSTypography.body)
                        }
                        
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                                .withCardBackground()
                                .scaleIn(delayMs = 500),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Scale In Animation", style = NSTypography.body)
                        }
                    }
                }
            }
        }
        
        // Empty state demo
        item {
            NSCard {
                SectionHeader(title = "Empty State")
                VerticalSpacerMD()
                
                EmptyStatePlaceholder(
                    title = "No data available",
                    subtitle = "Start by adding your first mood entry",
                    icon = Icons.Default.Mood,
                    action = {
                        NSPrimaryButton(
                            title = "Add Entry",
                            onClick = { /* Handle add entry */ }
                        )
                    }
                )
            }
        }
    }
}

/**
 * Simple FlexBox-like row for chips
 */
@Composable
fun FlexBoxRow(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(NSSpacing.sm)
    ) {
        content()
    }
}

@Preview(showBackground = true)
@Composable
fun UIExtensionsDemoPreview() {
    NorthStarTheme {
        UIExtensionsDemo()
    }
}

@Preview(showBackground = true, name = "Dark Theme")
@Composable
fun UIExtensionsDemoDarkPreview() {
    NorthStarTheme(darkTheme = true) {
        UIExtensionsDemo()
    }
}
