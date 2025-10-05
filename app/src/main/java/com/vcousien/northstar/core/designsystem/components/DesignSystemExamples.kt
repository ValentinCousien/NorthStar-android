package com.vcousien.northstar.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vcousien.northstar.core.designsystem.*
import com.vcousien.northstar.ui.theme.NorthStarTheme

/**
 * Example implementation showing proper usage of NorthStar design system
 * Demonstrates typography, spacing, and color integration
 */
@Composable
fun MoodTrackingCardExample() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(NSSpacing.Padding.screen),
        shape = NSSpacing.CornerRadius.mediumShape,
        colors = CardDefaults.cardColors(
            containerColor = DesignTokens.Colors.cardBackground
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = DesignTokens.Elevation.CARD.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(NSSpacing.Padding.card),
            verticalArrangement = Arrangement.spacedBy(NSSpacing.md)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Today's Tracking",
                    style = DesignTokens.Typography.heading2
                )
                
                Text(
                    text = "March 15",
                    style = DesignTokens.Typography.caption,
                    color = DesignTokens.Colors.textSecondary
                )
            }
            
            // Mood Section
            TrackingSection(
                icon = Icons.Default.Favorite,
                title = "Mood Level",
                value = "6",
                description = "Good mood today",
                color = moodColorFor(6)
            )
            
            // Sleep Section  
            TrackingSection(
                icon = Icons.Default.Home,
                title = "Sleep Quality",
                value = "7.5hrs",
                description = "Restful sleep",
                color = DesignTokens.Colors.info
            )
            
            // Medication Section
            TrackingSection(
                icon = Icons.Default.Add,
                title = "Medications",
                value = "2/2",
                description = "All doses taken",
                color = DesignTokens.Colors.success
            )
            
            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(NSSpacing.sm)
            ) {
                Button(
                    onClick = { /* Add tracking */ },
                    modifier = Modifier
                        .weight(1f)
                        .padding(NSSpacing.Padding.button),
                    shape = NSSpacing.CornerRadius.smallShape
                ) {
                    Text(
                        text = "Update",
                        style = DesignTokens.Typography.captionBold
                    )
                }
                
                OutlinedButton(
                    onClick = { /* View details */ },
                    modifier = Modifier
                        .weight(1f)
                        .padding(NSSpacing.Padding.button),
                    shape = NSSpacing.CornerRadius.smallShape
                ) {
                    Text(
                        text = "Details",
                        style = DesignTokens.Typography.captionBold
                    )
                }
            }
        }
    }
}

@Composable
private fun TrackingSection(
    icon: ImageVector,
    title: String,
    value: String,
    description: String,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(NSSpacing.md)
    ) {
        // Icon with colored background
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(NSSpacing.CornerRadius.smallShape)
                .background(color.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
        }
        
        // Content
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(NSSpacing.xs)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = DesignTokens.Typography.subtitle
                )
                
                Text(
                    text = value,
                    style = DesignTokens.Typography.bodyBold,
                    color = color
                )
            }
            
            Text(
                text = description,
                style = DesignTokens.Typography.caption,
                color = DesignTokens.Colors.textSecondary
            )
        }
    }
}

/**
 * Example of a settings list item using the design system
 */
@Composable
fun SettingsItemExample() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = NSSpacing.screenEdge),
        shape = NSSpacing.CornerRadius.mediumShape,
        colors = CardDefaults.cardColors(
            containerColor = DesignTokens.Colors.surface
        )
    ) {
        Column {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DesignTokens.Colors.primary.copy(alpha = 0.05f))
                    .padding(NSSpacing.Padding.listItem)
            ) {
                Text(
                    text = "Preferences",
                    style = DesignTokens.Typography.heading3,
                    color = DesignTokens.Colors.primary
                )
            }
            
            // Settings items
            SettingsItem(
                title = "Dark Mode",
                description = "Enable dark theme",
                hasSwitch = true
            )
            
            HorizontalDivider(
                color = DesignTokens.Colors.divider,
                modifier = Modifier.padding(horizontal = NSSpacing.md)
            )
            
            SettingsItem(
                title = "Notifications",
                description = "Daily reminders and alerts"
            )
            
            HorizontalDivider(
                color = DesignTokens.Colors.divider,
                modifier = Modifier.padding(horizontal = NSSpacing.md)
            )
            
            SettingsItem(
                title = "Export Data",
                description = "Download your tracking history"
            )
        }
    }
}

@Composable
private fun SettingsItem(
    title: String,
    description: String,
    hasSwitch: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(NSSpacing.Padding.listItem),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(NSSpacing.xs)
        ) {
            Text(
                text = title,
                style = DesignTokens.Typography.body
            )
            
            Text(
                text = description,
                style = DesignTokens.Typography.caption,
                color = DesignTokens.Colors.textSecondary
            )
        }
        
        if (hasSwitch) {
            Switch(
                checked = false,
                onCheckedChange = { }
            )
        }
    }
}

/**
 * Helper function to get mood color based on level (0-8 scale)
 */
@Composable
private fun moodColorFor(level: Int): Color {
    return when (level) {
        0, 1 -> DesignTokens.Colors.error // Severely depressed, depressed
        2, 3 -> DesignTokens.Colors.warning // Low, slightly low
        4 -> DesignTokens.Colors.textSecondary // Neutral
        5, 6 -> DesignTokens.Colors.success // Slightly elevated, elevated
        7, 8 -> DesignTokens.Colors.info // Highly elevated, manic
        else -> DesignTokens.Colors.textSecondary
    }
}

/**
 * Example of button components usage
 */
@Composable
fun ButtonExamples() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(NSSpacing.md),
        verticalArrangement = Arrangement.spacedBy(NSSpacing.lg)
    ) {
        // Primary button examples
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = NSSpacing.CornerRadius.mediumShape,
            colors = CardDefaults.cardColors(
                containerColor = DesignTokens.Colors.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(NSSpacing.md),
                verticalArrangement = Arrangement.spacedBy(NSSpacing.md)
            ) {
                Text(
                    text = "Button Hierarchy",
                    style = NSTypography.subtitle,
                    color = DesignTokens.Colors.textPrimary
                )
                
                // Primary action
                NSPrimaryButton(
                    title = "Save Changes",
                    onClick = { /* Handle save */ },
                    icon = Icons.Default.Check
                )
                
                // Secondary actions
                Row(
                    horizontalArrangement = Arrangement.spacedBy(NSSpacing.md)
                ) {
                    NSSecondaryButton(
                        title = "Cancel",
                        onClick = { },
                        modifier = Modifier.weight(1f),
                        width = NSButtonWidth.FillMax
                    )
                    
                    NSTextButton(
                        title = "Preview",
                        onClick = { },
                        icon = Icons.Default.Visibility
                    )
                }
            }
        }
        
        // Destructive action example
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = NSSpacing.CornerRadius.mediumShape,
            colors = CardDefaults.cardColors(
                containerColor = DesignTokens.Colors.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(NSSpacing.md),
                verticalArrangement = Arrangement.spacedBy(NSSpacing.md)
            ) {
                Text(
                    text = "Dangerous Actions",
                    style = NSTypography.subtitle,
                    color = DesignTokens.Colors.textPrimary
                )
                
                NSDestructiveButton(
                    title = "Delete All Data",
                    onClick = { /* Handle delete */ },
                    icon = Icons.Default.Delete
                )
            }
        }
    }
}

/**
 * Example of empty state component usage
 */
@Composable
fun EmptyStateExample() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(NSSpacing.md),
        verticalArrangement = Arrangement.spacedBy(NSSpacing.lg)
    ) {
        // Basic empty state
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            shape = NSSpacing.CornerRadius.mediumShape,
            colors = CardDefaults.cardColors(
                containerColor = DesignTokens.Colors.surface
            )
        ) {
            NSEmptyState(
                title = "No Data Available",
                message = "Start tracking your mood to see insights and patterns here.",
                icon = Icons.Default.Favorite,
                action = { /* Handle action */ },
                actionTitle = "Add Entry"
            )
        }
        
        // Loading state
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            shape = NSSpacing.CornerRadius.mediumShape,
            colors = CardDefaults.cardColors(
                containerColor = DesignTokens.Colors.surface
            )
        ) {
            NSLoadingState(
                message = "Loading your data..."
            )
        }
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun MoodTrackingCardExamplePreview() {
    NorthStarTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(DesignTokens.Colors.background),
            verticalArrangement = Arrangement.spacedBy(NSSpacing.lg)
        ) {
            MoodTrackingCardExample()
            SettingsItemExample()
            ButtonExamples()
            EmptyStateExample()
        }
    }
}
