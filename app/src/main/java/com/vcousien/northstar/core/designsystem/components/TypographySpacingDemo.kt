package com.vcousien.northstar.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.vcousien.northstar.core.designsystem.*
import com.vcousien.northstar.ui.theme.NorthStarTheme

/**
 * Demo component showcasing the NorthStar Typography and Spacing systems
 */
@Composable
fun TypographySpacingDemo() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(NSSpacing.Padding.screen)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(NSSpacing.lg)
    ) {
        // Typography Section
        TypographySection()
        
        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
        
        // Spacing Section  
        SpacingSection()
        
        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
        
        // Corner Radius Section
        CornerRadiusSection()
        
        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
        
        // Padding Patterns Section
        PaddingPatternsSection()
    }
}

@Composable
private fun TypographySection() {
    Column(
        verticalArrangement = Arrangement.spacedBy(NSSpacing.md)
    ) {
        Text(
            text = "Typography System",
            style = NSTypography.heading2,
            color = MaterialTheme.colorScheme.primary
        )
        
        // Typography examples with labels
        val typographyExamples = listOf(
            "heading1" to NSTypography.heading1,
            "heading2" to NSTypography.heading2, 
            "heading3" to NSTypography.heading3,
            "subtitle" to NSTypography.subtitle,
            "body" to NSTypography.body,
            "bodyBold" to NSTypography.bodyBold,
            "caption" to NSTypography.caption,
            "captionBold" to NSTypography.captionBold,
            "small" to NSTypography.small
        )
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(NSSpacing.Padding.card),
                verticalArrangement = Arrangement.spacedBy(NSSpacing.sm)
            ) {
                typographyExamples.forEach { (name, style) ->
                    TypographyExample(name = name, style = style)
                }
            }
        }
    }
}

@Composable
private fun TypographyExample(name: String, style: TextStyle) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Sample Text",
                style = style
            )
            Text(
                text = name,
                style = NSTypography.caption,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
        
        Text(
            text = "${style.fontSize} / ${style.fontWeight}",
            style = NSTypography.small,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
    }
}

@Composable
private fun SpacingSection() {
    Column(
        verticalArrangement = Arrangement.spacedBy(NSSpacing.md)
    ) {
        Text(
            text = "Spacing System",
            style = NSTypography.heading2,
            color = MaterialTheme.colorScheme.primary
        )
        
        val spacingExamples = listOf(
            "xxs" to NSSpacing.xxs,
            "xs" to NSSpacing.xs,
            "sm" to NSSpacing.sm,
            "md" to NSSpacing.md,
            "lg" to NSSpacing.lg,
            "xl" to NSSpacing.xl,
            "xxl" to NSSpacing.xxl,
            "xxxl" to NSSpacing.xxxl
        )
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(NSSpacing.Padding.card),
                verticalArrangement = Arrangement.spacedBy(NSSpacing.sm)
            ) {
                spacingExamples.forEach { (name, spacing) ->
                    SpacingExample(name = name, spacing = spacing)
                }
            }
        }
    }
}

@Composable
private fun SpacingExample(name: String, spacing: Dp) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = name,
            style = NSTypography.caption,
            modifier = Modifier.width(40.dp)
        )
        
        Box(
            modifier = Modifier
                .width(spacing)
                .height(20.dp)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
        )
        
        Spacer(modifier = Modifier.width(NSSpacing.sm))
        
        Text(
            text = "${spacing.value.toInt()}dp",
            style = NSTypography.small,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

@Composable
private fun CornerRadiusSection() {
    Column(
        verticalArrangement = Arrangement.spacedBy(NSSpacing.md)
    ) {
        Text(
            text = "Corner Radius System", 
            style = NSTypography.heading2,
            color = MaterialTheme.colorScheme.primary
        )
        
        val cornerRadiusExamples = listOf(
            "sm" to NSSpacing.CornerRadius.sm,
            "md" to NSSpacing.CornerRadius.md,
            "lg" to NSSpacing.CornerRadius.lg,
            "xl" to NSSpacing.CornerRadius.xl
        )
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(NSSpacing.Padding.card),
                verticalArrangement = Arrangement.spacedBy(NSSpacing.md)
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(NSSpacing.md),
                    verticalArrangement = Arrangement.spacedBy(NSSpacing.md),
                    modifier = Modifier.height(200.dp)
                ) {
                    items(cornerRadiusExamples.size) { index ->
                        val (name, radius) = cornerRadiusExamples[index]
                        CornerRadiusExample(name = name, radius = radius)
                    }
                    
                    // Add pill example
                    item {
                        CornerRadiusExample(name = "pill", radius = NSSpacing.CornerRadius.pill)
                    }
                }
            }
        }
    }
}

@Composable
private fun CornerRadiusExample(name: String, radius: Dp) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(NSSpacing.xs)
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(radius))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                    RoundedCornerShape(radius)
                )
        )
        
        Text(
            text = name,
            style = NSTypography.caption
        )
        
        Text(
            text = if (radius == NSSpacing.CornerRadius.pill) "999dp" else "${radius.value.toInt()}dp",
            style = NSTypography.small,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

@Composable
private fun PaddingPatternsSection() {
    Column(
        verticalArrangement = Arrangement.spacedBy(NSSpacing.md)
    ) {
        Text(
            text = "Padding Patterns",
            style = NSTypography.heading2, 
            color = MaterialTheme.colorScheme.primary
        )
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(NSSpacing.Padding.card),
                verticalArrangement = Arrangement.spacedBy(NSSpacing.md)
            ) {
                // Screen padding example
                PaddingPatternExample(
                    title = "Screen Padding",
                    description = "Standard screen edge padding",
                    paddingValues = NSSpacing.Padding.screen
                )
                
                // Card padding examples
                PaddingPatternExample(
                    title = "Card Padding",
                    description = "Standard card content padding", 
                    paddingValues = NSSpacing.Padding.card
                )
                
                // Button padding examples
                PaddingPatternExample(
                    title = "Button Padding",
                    description = "Standard button padding",
                    paddingValues = NSSpacing.Padding.button
                )
                
                // List item padding
                PaddingPatternExample(
                    title = "List Item Padding", 
                    description = "Standard list item padding",
                    paddingValues = NSSpacing.Padding.listItem
                )
            }
        }
    }
}

@Composable
private fun PaddingPatternExample(
    title: String,
    description: String,
    paddingValues: PaddingValues
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(NSSpacing.xs)
    ) {
        Text(
            text = title,
            style = NSTypography.captionBold
        )
        
        Text(
            text = description,
            style = NSTypography.small,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    RoundedCornerShape(NSSpacing.CornerRadius.md)
                )
                .padding(paddingValues)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                        RoundedCornerShape(NSSpacing.CornerRadius.sm)
                    )
            ) {
                Text(
                    text = "Content",
                    style = NSTypography.caption,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun TypographySpacingDemoPreview() {
    NorthStarTheme {
        TypographySpacingDemo()
    }
}
