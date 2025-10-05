package com.vcousien.northstar.features.insights.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.vcousien.northstar.R
import com.vcousien.northstar.core.designsystem.DesignTokens
import com.vcousien.northstar.core.designsystem.NSSpacing
import com.vcousien.northstar.core.designsystem.NSTypography
import com.vcousien.northstar.features.shared.views.ComingSoonCard

/**
 * InsightsView - Main insights and analytics screen
 *
 * Features:
 * - Correlation analysis between mood, sleep, and medications
 * - Trend visualization
 * - Pattern detection
 * - Coming soon section for future features
 * - Matches iOS InsightsView implementation
 *
 * This is a simplified POC version that will be enhanced in future iterations
 *
 * @param modifier Modifier to be applied to the view
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsightsView(
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.insights_title),
                        style = NSTypography.heading1,
                        color = DesignTokens.Colors.textPrimary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DesignTokens.Colors.background
                )
            )
        },
        containerColor = DesignTokens.Colors.background
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(NSSpacing.md))

            // Mood-Sleep Correlation Card
            MoodSleepCorrelationCard(
                modifier = Modifier.padding(horizontal = NSSpacing.screenEdge)
            )

            Spacer(modifier = Modifier.height(NSSpacing.lg))

            // Coming Soon Features
            ComingSoonCard(
                modifier = Modifier.padding(horizontal = NSSpacing.screenEdge)
            )

            Spacer(modifier = Modifier.height(NSSpacing.xxl))
        }
    }
}
