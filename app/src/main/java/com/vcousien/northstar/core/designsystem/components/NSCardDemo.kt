package com.vcousien.northstar.core.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vcousien.northstar.core.designsystem.DesignTokens
import com.vcousien.northstar.core.designsystem.NSSpacing
import com.vcousien.northstar.core.designsystem.NSTypography
import com.vcousien.northstar.ui.theme.NorthStarTheme
import com.vcousien.northstar.ui.theme.northStarColors

/**
 * Demo showcasing NSCard component usage and variants
 * This file demonstrates the various NSCard configurations and use cases
 */

@Composable
fun NSCardDemo(modifier: Modifier = Modifier) {
    var hasShadow by remember { mutableStateOf(true) }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(NSSpacing.screenEdge)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(NSSpacing.lg)
    ) {
        // Demo controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "NSCard Demo",
                style = NSTypography.heading1,
                color = DesignTokens.Colors.textPrimary
            )
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Shadow",
                    style = NSTypography.body,
                    color = DesignTokens.Colors.textSecondary
                )
                Spacer(modifier = Modifier.width(NSSpacing.sm))
                Switch(
                    checked = hasShadow,
                    onCheckedChange = { hasShadow = it }
                )
            }
        }
        
        // Basic NSCard
        SectionCard(title = "Basic NSCard") {
            NSCard(hasShadow = hasShadow) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(NSSpacing.sm)
                ) {
                    Text(
                        text = "Basic Card Content",
                        style = NSTypography.subtitle,
                        color = DesignTokens.Colors.textPrimary
                    )
                    Text(
                        text = "This is a standard NSCard with default padding (16dp) and optional shadow.",
                        style = NSTypography.body,
                        color = DesignTokens.Colors.textSecondary
                    )
                }
            }
        }
        
        // Compact variant
        SectionCard(title = "Compact NSCard") {
            NSCardCompact(hasShadow = hasShadow) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(NSSpacing.sm)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Star",
                        tint = DesignTokens.Colors.warning
                    )
                    Text(
                        text = "Compact card with smaller padding",
                        style = NSTypography.body,
                        color = DesignTokens.Colors.textPrimary
                    )
                }
            }
        }
        
        // Large variant
        SectionCard(title = "Large NSCard") {
            NSCardLarge(hasShadow = hasShadow) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(NSSpacing.md)
                ) {
                    Text(
                        text = "Large Card Title",
                        style = NSTypography.heading2,
                        color = DesignTokens.Colors.textPrimary
                    )
                    Text(
                        text = "This large card has more generous padding (24dp) and is perfect for main content areas or feature sections.",
                        style = NSTypography.body,
                        color = DesignTokens.Colors.textSecondary
                    )
                    Button(
                        onClick = { /* Demo action */ },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text("Action Button")
                    }
                }
            }
        }
        
        // Flat variant (no shadow)
        SectionCard(title = "Flat NSCard") {
            NSCardFlat {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(NSSpacing.sm)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Info",
                        tint = DesignTokens.Colors.info
                    )
                    Text(
                        text = "This card never has shadow, regardless of the global setting",
                        style = NSTypography.body,
                        color = DesignTokens.Colors.textPrimary
                    )
                }
            }
        }
        
        // Custom background color
        SectionCard(title = "Custom Background Color") {
            NSCard(
                hasShadow = hasShadow,
                backgroundColor = MaterialTheme.northStarColors.moodGood.copy(alpha = 0.1f)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(NSSpacing.sm)
                ) {
                    Text(
                        text = "Custom Background",
                        style = NSTypography.subtitle,
                        color = DesignTokens.Colors.textPrimary
                    )
                    Text(
                        text = "This card has a custom background color with mood-based tinting.",
                        style = NSTypography.body,
                        color = DesignTokens.Colors.textSecondary
                    )
                }
            }
        }
        
        // Interactive card example
        SectionCard(title = "Interactive Card") {
            var clickCount by remember { mutableStateOf(0) }
            
            NSCard(hasShadow = hasShadow) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(NSSpacing.md)
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Heart",
                        tint = if (clickCount > 0) Color.Red else DesignTokens.Colors.textSecondary,
                        modifier = Modifier
                    )
                    Text(
                        text = "Interactive Content",
                        style = NSTypography.subtitle,
                        color = DesignTokens.Colors.textPrimary
                    )
                    Text(
                        text = "Clicked $clickCount times",
                        style = NSTypography.body,
                        color = DesignTokens.Colors.textSecondary
                    )
                    Button(
                        onClick = { clickCount++ }
                    ) {
                        Text("Click Me!")
                    }
                }
            }
        }
        
        // Mood-based cards example
        SectionCard(title = "Mood-Based Card Colors") {
            Column(
                verticalArrangement = Arrangement.spacedBy(NSSpacing.sm)
            ) {
                for (moodLevel in 0..8 step 2) {
                    val moodColor = DesignTokens.Colors.moodColor(moodLevel)
                    val categoryName = DesignTokens.Mood.getCategoryName(moodLevel)
                    
                    NSCardCompact(
                        hasShadow = hasShadow,
                        backgroundColor = moodColor.copy(alpha = 0.15f)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "$categoryName (Level $moodLevel)",
                                style = NSTypography.body.copy(fontWeight = FontWeight.Medium),
                                color = DesignTokens.Colors.textPrimary
                            )
                            Text(
                                text = "●",
                                style = NSTypography.heading2,
                                color = moodColor
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(NSSpacing.sm)
    ) {
        Text(
            text = title,
            style = NSTypography.heading3,
            color = DesignTokens.Colors.textPrimary,
            modifier = Modifier.padding(horizontal = NSSpacing.xs)
        )
        content()
    }
}

/**
 * Preview for NSCard component
 */
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun NSCardDemoPreview() {
    NorthStarTheme {
        NSCardDemo()
    }
}

@Preview(showBackground = true, name = "Dark Mode")
@Composable
fun NSCardDemoDarkPreview() {
    NorthStarTheme(darkTheme = true) {
        NSCardDemo()
    }
}

/**
 * Simple preview showing just basic card variants
 */
@Preview(showBackground = true, widthDp = 360, heightDp = 400)
@Composable
fun NSCardVariantsPreview() {
    NorthStarTheme {
        Column(
            modifier = Modifier
                .padding(NSSpacing.md)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(NSSpacing.md)
        ) {
            // Basic card
            NSCard {
                Text(
                    text = "Basic NSCard",
                    style = NSTypography.body,
                    color = DesignTokens.Colors.textPrimary
                )
            }
            
            // Compact card
            NSCardCompact {
                Text(
                    text = "Compact NSCard",
                    style = NSTypography.body,
                    color = DesignTokens.Colors.textPrimary
                )
            }
            
            // Large card
            NSCardLarge {
                Text(
                    text = "Large NSCard",
                    style = NSTypography.body,
                    color = DesignTokens.Colors.textPrimary
                )
            }
            
            // Flat card
            NSCardFlat {
                Text(
                    text = "Flat NSCard",
                    style = NSTypography.body,
                    color = DesignTokens.Colors.textPrimary
                )
            }
        }
    }
}
