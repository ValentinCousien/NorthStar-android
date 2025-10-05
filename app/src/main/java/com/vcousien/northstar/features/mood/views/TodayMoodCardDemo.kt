package com.vcousien.northstar.features.mood.views

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.vcousien.northstar.core.designsystem.NSSpacing
import com.vcousien.northstar.core.designsystem.NSTypography
import com.vcousien.northstar.core.designsystem.DesignTokens
import com.vcousien.northstar.ui.theme.NorthStarTheme

/**
 * Demo composable for TodayMoodCard
 * Shows different states of the mood card component
 */
@Composable
fun TodayMoodCardDemo() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(NSSpacing.md),
        verticalArrangement = Arrangement.spacedBy(NSSpacing.lg)
    ) {
        Text(
            text = "TodayMoodCard Demo",
            style = NSTypography.heading2,
            color = DesignTokens.Colors.textPrimary
        )
        
        // Regular TodayMoodCard
        Text(
            text = "Standard Card",
            style = NSTypography.subtitle,
            color = DesignTokens.Colors.textSecondary
        )
        TodayMoodCard(
            onMoodEntryClick = { /* Handle click */ }
        )
        
        // Compact version
        Text(
            text = "Compact Card",
            style = NSTypography.subtitle,
            color = DesignTokens.Colors.textSecondary
        )
        TodayMoodCardCompact(
            onMoodEntryClick = { /* Handle click */ }
        )
        
        Spacer(modifier = Modifier.height(NSSpacing.xl))
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TodayMoodCardDemoPreview() {
    NorthStarTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = DesignTokens.Colors.background
        ) {
            TodayMoodCardDemo()
        }
    }
}

@Preview(name = "Dark Theme", showBackground = true, showSystemUi = true)
@Composable
fun TodayMoodCardDemoDarkPreview() {
    NorthStarTheme(darkTheme = true) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = DesignTokens.Colors.background
        ) {
            TodayMoodCardDemo()
        }
    }
}
