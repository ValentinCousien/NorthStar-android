package com.vcousien.northstar.features.shared.views

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.vcousien.northstar.R
import com.vcousien.northstar.core.designsystem.DesignTokens
import com.vcousien.northstar.core.designsystem.NSSpacing
import com.vcousien.northstar.core.designsystem.NSTypography
import com.vcousien.northstar.core.designsystem.components.NSCard

/**
 * ComingSoonCard - A card showing upcoming features
 *
 * Features:
 * - "Coming Soon" header
 * - List of planned features with icons and descriptions
 * - Used in Insights view to show future functionality
 * - Matches iOS ComingSoonCard implementation
 *
 * @param modifier Modifier to be applied to the card
 */
@Composable
fun ComingSoonCard(modifier: Modifier = Modifier) {
    NSCard(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(NSSpacing.md)
        ) {
            // Header
            Text(
                text = stringResource(R.string.insights_coming_soon),
                style = NSTypography.heading3,
                color = DesignTokens.Colors.textPrimary,
                modifier = Modifier.fillMaxWidth()
            )

            // Features list
            Column(
                modifier = Modifier.padding(vertical = NSSpacing.xs),
                verticalArrangement = Arrangement.spacedBy(NSSpacing.md)
            ) {
                FeatureRow(
                    icon = Icons.Default.Notifications,
                    title = stringResource(R.string.insights_coming_soon_alerts_title),
                    description = stringResource(R.string.insights_coming_soon_alerts_description)
                )

                HorizontalDivider(color = DesignTokens.Colors.border)

                FeatureRow(
                    icon = Icons.Default.Person,
                    title = stringResource(R.string.insights_coming_soon_reports_title),
                    description = stringResource(R.string.insights_coming_soon_reports_description)
                )

                HorizontalDivider(color = DesignTokens.Colors.border)

                FeatureRow(
                    icon = Icons.Outlined.BarChart,
                    title = stringResource(R.string.insights_coming_soon_analytics_title),
                    description = stringResource(R.string.insights_coming_soon_analytics_description)
                )
            }
        }
    }
}

/**
 * Individual feature row component
 */
@Composable
private fun FeatureRow(
    icon: ImageVector,
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(NSSpacing.md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = DesignTokens.Colors.primary,
            modifier = Modifier.size(30.dp)
        )

        // Text content
        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = title,
                style = NSTypography.bodyBold,
                color = DesignTokens.Colors.textPrimary
            )

            Text(
                text = description,
                style = NSTypography.caption,
                color = DesignTokens.Colors.textSecondary
            )
        }
    }
}
