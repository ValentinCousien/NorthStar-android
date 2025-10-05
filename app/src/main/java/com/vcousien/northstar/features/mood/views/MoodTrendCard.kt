package com.vcousien.northstar.features.mood.views

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vcousien.northstar.R
import com.vcousien.northstar.core.designsystem.DesignTokens
import com.vcousien.northstar.core.designsystem.NSSpacing
import com.vcousien.northstar.core.designsystem.NSTypography
import com.vcousien.northstar.core.designsystem.components.NSCard
import com.vcousien.northstar.features.mood.models.MoodLevel
import com.vcousien.northstar.features.mood.viewmodels.MoodTrackingViewModel
import com.vcousien.northstar.ui.theme.NorthStarTheme
import com.vcousien.northstar.ui.theme.northStarColors
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max

/**
 * MoodTrendCard - A composable that displays a 7-day mood trend visualization
 * 
 * This card shows mood trends over the past 7 days using a simple bar chart.
 * Each bar represents one day and is colored according to the mood level recorded.
 * Days with no entries show neutral mood level.
 * 
 * Features:
 * - 7-day bar chart visualization
 * - Color-coded bars based on mood levels
 * - Day labels (Mon, Tue, Wed, etc.)
 * - Automatic normalization of values for display
 * - Responsive height adjustment based on mood values
 * 
 * @param viewModel The mood tracking view model to get trend data from
 * @param modifier Modifier to be applied to the card
 */
@Composable
fun MoodTrendCard(
    viewModel: MoodTrackingViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val moodTrend by remember {
        derivedStateOf { viewModel.getMoodTrend() }
    }

    NSCard(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Title
            Text(
                text = stringResource(R.string.mood_trends),
                style = NSTypography.heading3,
                color = DesignTokens.Colors.textPrimary,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(NSSpacing.md))

            // Chart
            if (moodTrend.isNotEmpty()) {
                MoodTrendChart(
                    trendData = moodTrend,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                )
            } else {
                // Empty state
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.mood_no_entries_description),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.northStarColors.textSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

/**
 * The actual chart component that renders the mood trend visualization
 */
@Composable
private fun MoodTrendChart(
    trendData: List<Double>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        // Chart bars
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = NSSpacing.xs),
            horizontalArrangement = Arrangement.spacedBy(
                space = NSSpacing.xs,
                alignment = Alignment.CenterHorizontally
            ),
            verticalAlignment = Alignment.Bottom
        ) {
            trendData.forEachIndexed { index, value ->
                MoodTrendBar(
                    moodValue = value,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(NSSpacing.xs))

        // Day labels
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = NSSpacing.xs),
            horizontalArrangement = Arrangement.spacedBy(
                space = NSSpacing.xs,
                alignment = Alignment.CenterHorizontally
            )
        ) {
            repeat(7) { dayOffset ->
                Text(
                    text = getDayLabel(dayOffset),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.northStarColors.textSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

/**
 * Individual bar component for the mood trend chart
 */
@Composable
private fun MoodTrendBar(
    moodValue: Double,
    modifier: Modifier = Modifier
) {
    val moodLevel = moodValue.toInt().coerceIn(0, 8)
    val barColor = DesignTokens.Colors.moodColor(moodLevel)
    val normalizedHeight = normalizeValue(moodValue)
    
    Box(
        modifier = modifier
            .fillMaxHeight(normalizedHeight.toFloat())
            .clip(RoundedCornerShape(NSSpacing.CornerRadius.sm))
            .background(barColor)
            .defaultMinSize(minWidth = 24.dp, minHeight = 8.dp)
    )
}

/**
 * Normalize mood values for display (0-8 scale to 0.1-1.0 scale)
 * Ensures all bars have a minimum visible height
 */
private fun normalizeValue(value: Double): Double {
    val minHeight = 0.1 // Minimum height for visibility
    val maxValue = 8.0  // Maximum mood level
    return minHeight + ((value / maxValue) * (1.0 - minHeight))
}

/**
 * Get day label for the given day offset (0 = today, 6 = 6 days ago)
 */
private fun getDayLabel(dayOffset: Int): String {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DAY_OF_YEAR, -(6 - dayOffset))
    
    val formatter = SimpleDateFormat("EE", Locale.getDefault())
    return formatter.format(calendar.time)
}

// Preview Components

@Preview(showBackground = true)
@Composable
private fun MoodTrendCardPreview() {
    NorthStarTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Mock trend data for preview
            MoodTrendCardWithData(
                trendData = listOf(2.0, 1.0, 4.0, 6.0, 7.0, 5.0, 3.0)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MoodTrendCardEmptyPreview() {
    NorthStarTheme {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            MoodTrendCardWithData(
                trendData = emptyList()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MoodTrendCardVariousLevelsPreview() {
    NorthStarTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Preview with various mood levels
            MoodTrendCardWithData(
                trendData = listOf(0.0, 2.0, 4.0, 6.0, 8.0, 1.0, 7.0)
            )
        }
    }
}

/**
 * Preview-only version that accepts trend data directly
 */
@Composable
private fun MoodTrendCardWithData(
    trendData: List<Double>
) {
    NSCard {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Title
            Text(
                text = stringResource(R.string.mood_trends),
                style = NSTypography.heading3,
                color = DesignTokens.Colors.textPrimary,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(NSSpacing.md))

            // Chart
            if (trendData.isNotEmpty()) {
                MoodTrendChart(
                    trendData = trendData,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                )
            } else {
                // Empty state
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.mood_no_entries_description),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.northStarColors.textSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MoodTrendChartPreview() {
    NorthStarTheme {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .background(
                    color = DesignTokens.Colors.background,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Chart Only - Various Levels",
                style = NSTypography.heading3
            )
            
            MoodTrendChart(
                trendData = listOf(0.0, 2.0, 4.0, 6.0, 8.0, 1.0, 7.0),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            )
            
            Text(
                text = "Chart Only - Mixed Data",
                style = NSTypography.heading3
            )
            
            MoodTrendChart(
                trendData = listOf(3.0, 7.0, 2.0, 8.0, 1.0, 6.0, 4.0),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MoodTrendBarPreview() {
    NorthStarTheme {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .height(120.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            // Show bars for each mood level
            repeat(9) { level ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    MoodTrendBar(
                        moodValue = level.toDouble(),
                        modifier = Modifier
                            .width(24.dp)
                            .weight(1f)
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = level.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.northStarColors.textSecondary
                    )
                }
            }
        }
    }
}
