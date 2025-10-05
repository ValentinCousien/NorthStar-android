package com.vcousien.northstar.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vcousien.northstar.core.designsystem.DesignTokens
import com.vcousien.northstar.core.designsystem.allMoodColors
import com.vcousien.northstar.core.designsystem.getContrastingTextColor
import com.vcousien.northstar.core.designsystem.moodColorFor
import com.vcousien.northstar.ui.theme.NorthStarTheme
import com.vcousien.northstar.ui.theme.getMoodColor
import com.vcousien.northstar.ui.theme.northStarColors

/**
 * Color System Demo Component
 * Displays all NorthStar colors for design system verification
 */
@Composable
fun ColorSystemDemo(
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "NorthStar Color System",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = DesignTokens.Colors.textPrimary
            )
        }
        
        item {
            PrimaryColorsSection()
        }
        
        item {
            SemanticColorsSection()
        }
        
        item {
            MoodColorsSection()
        }
        
        item {
            TextAndSurfaceColorsSection()
        }
        
        item {
            MoodScaleDemoSection()
        }
    }
}

@Composable
private fun PrimaryColorsSection() {
    ColorSection(
        title = "Primary Colors",
        colors = listOf(
            ColorInfo("Primary", DesignTokens.Colors.primary),
            ColorInfo("Background", DesignTokens.Colors.background),
            ColorInfo("Surface", DesignTokens.Colors.surface),
            ColorInfo("Card Background", DesignTokens.Colors.cardBackground)
        )
    )
}

@Composable
private fun SemanticColorsSection() {
    ColorSection(
        title = "Semantic Colors",
        colors = listOf(
            ColorInfo("Success", DesignTokens.Colors.success),
            ColorInfo("Warning", DesignTokens.Colors.warning),
            ColorInfo("Error", DesignTokens.Colors.error),
            ColorInfo("Info", DesignTokens.Colors.info)
        )
    )
}

@Composable
private fun MoodColorsSection() {
    ColorSection(
        title = "Mood Colors",
        colors = listOf(
            ColorInfo("Depressed (0-1)", MaterialTheme.northStarColors.moodDepressed),
            ColorInfo("Low (2-3)", MaterialTheme.northStarColors.moodLow),
            ColorInfo("Neutral (4)", MaterialTheme.northStarColors.moodNeutral),
            ColorInfo("Good (5-6)", MaterialTheme.northStarColors.moodGood),
            ColorInfo("Elevated (7-8)", MaterialTheme.northStarColors.moodElevated)
        )
    )
}

@Composable
private fun TextAndSurfaceColorsSection() {
    ColorSection(
        title = "Text & UI Colors",
        colors = listOf(
            ColorInfo("Primary Text", DesignTokens.Colors.textPrimary),
            ColorInfo("Secondary Text", DesignTokens.Colors.textSecondary),
            ColorInfo("Divider", DesignTokens.Colors.divider)
        )
    )
}

@Composable
private fun MoodScaleDemoSection() {
    Column {
        Text(
            text = "Mood Scale Demo (0-8)",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Medium,
            color = DesignTokens.Colors.textPrimary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            (0..8).forEach { level ->
                MoodLevelIndicator(
                    level = level,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            (0..8).forEach { level ->
                Text(
                    text = level.toString(),
                    style = MaterialTheme.typography.bodySmall,
                    color = DesignTokens.Colors.textSecondary,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun MoodLevelIndicator(
    level: Int,
    modifier: Modifier = Modifier
) {
    val color = moodColorFor(level)
    val category = DesignTokens.Mood.getCategoryName(level)
    
    Column(
        modifier = modifier.padding(horizontal = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(
                    color = color,
                    shape = RoundedCornerShape(8.dp)
                )
        ) {
            Text(
                text = level.toString(),
                color = getContrastingTextColor(color),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        
        Text(
            text = category.take(3), // First 3 characters
            style = MaterialTheme.typography.labelSmall,
            color = DesignTokens.Colors.textSecondary,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
private fun ColorSection(
    title: String,
    colors: List<ColorInfo>
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Medium,
            color = DesignTokens.Colors.textPrimary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            colors.chunked(2).forEach { rowColors ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowColors.forEach { colorInfo ->
                        ColorCard(
                            colorInfo = colorInfo,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    // Fill remaining space if odd number of colors
                    if (rowColors.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun ColorCard(
    colorInfo: ColorInfo,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(80.dp),
        colors = CardDefaults.cardColors(containerColor = colorInfo.color),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = colorInfo.name,
                style = MaterialTheme.typography.labelMedium,
                color = getContrastingTextColor(colorInfo.color),
                fontWeight = FontWeight.Medium
            )
            
            Text(
                text = colorInfo.hexValue,
                style = MaterialTheme.typography.labelSmall,
                color = getContrastingTextColor(colorInfo.color).copy(alpha = 0.8f)
            )
        }
    }
}

private data class ColorInfo(
    val name: String,
    val color: Color
) {
    val hexValue: String
        get() = "#${color.value.toString(16).uppercase().padStart(8, '0')}"
}

@Preview(showBackground = true, name = "Color System Light")
@Composable
private fun ColorSystemDemoPreview() {
    NorthStarTheme(darkTheme = false) {
        ColorSystemDemo()
    }
}

@Preview(showBackground = true, name = "Color System Dark")
@Composable
private fun ColorSystemDemoDarkPreview() {
    NorthStarTheme(darkTheme = true) {
        ColorSystemDemo()
    }
}
