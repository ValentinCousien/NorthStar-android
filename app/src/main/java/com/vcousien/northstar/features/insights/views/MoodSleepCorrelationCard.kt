package com.vcousien.northstar.features.insights.views

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.vcousien.northstar.R
import com.vcousien.northstar.core.designsystem.DesignTokens
import com.vcousien.northstar.core.designsystem.NSSpacing
import com.vcousien.northstar.core.designsystem.NSTypography
import com.vcousien.northstar.core.designsystem.components.NSCard

/**
 * MoodSleepCorrelationCard - Card showing correlation between mood and sleep
 *
 * This is a POC/simplified version that displays a placeholder message.
 * In future versions, this will include:
 * - Correlation visualization
 * - Trend charts
 * - Statistical analysis
 *
 * Matches iOS MoodSleepCorrelationCard POC implementation
 *
 * @param modifier Modifier to be applied to the card
 */
@Composable
fun MoodSleepCorrelationCard(
    modifier: Modifier = Modifier
) {
    NSCard(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(NSSpacing.md)
        ) {
            // Header
            Text(
                text = stringResource(R.string.insights_sleep_correlation),
                style = NSTypography.heading3,
                color = DesignTokens.Colors.textPrimary,
                modifier = Modifier.fillMaxWidth()
            )

            // Description
            Text(
                text = stringResource(R.string.insights_correlation_continue_recording),
                style = NSTypography.body,
                color = DesignTokens.Colors.textSecondary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = NSSpacing.md)
            )

            // Simplified visualization placeholder
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(NSSpacing.md),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ShowChart,
                    contentDescription = null,
                    tint = DesignTokens.Colors.primary.copy(alpha = 0.7f),
                    modifier = Modifier.size(80.dp)
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(NSSpacing.xs)
                ) {
                    Text(
                        text = stringResource(R.string.insights_more_data_needed),
                        style = NSTypography.subtitle,
                        color = DesignTokens.Colors.textPrimary
                    )

                    Text(
                        text = stringResource(R.string.insights_minimum_data_required),
                        style = NSTypography.caption,
                        color = DesignTokens.Colors.textSecondary
                    )
                }
            }
        }
    }
}
