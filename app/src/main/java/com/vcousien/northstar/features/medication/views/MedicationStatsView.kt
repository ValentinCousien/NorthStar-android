package com.vcousien.northstar.features.medication.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vcousien.northstar.R
import com.vcousien.northstar.core.designsystem.NSSpacing
import com.vcousien.northstar.core.designsystem.NSTypography
import com.vcousien.northstar.core.designsystem.DesignTokens
import com.vcousien.northstar.core.designsystem.components.NSCard
import com.vcousien.northstar.core.designsystem.components.NSEmptyState
import com.vcousien.northstar.features.medication.viewmodels.MedicationViewModel

/**
 * MedicationStatsView - Statistics and insights view for medication tracking
 *
 * Features:
 * - Timeframe selector (7, 14, 30, 90 days)
 * - Overall adherence rate with visualization
 * - Per-medication adherence breakdown
 * - Side effects summary
 * - Empty state when no medications exist
 * - Matches iOS MedicationStatsView implementation
 *
 * @param viewModel MedicationViewModel for medication management
 * @param modifier Modifier to be applied to the view
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationStatsView(
    viewModel: MedicationViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val medications by viewModel.medications.collectAsState()
    var selectedTimeframe by remember { mutableStateOf(30) }
    val timeframeOptions = listOf(7, 14, 30, 90)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.medication_stats),
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
            // Timeframe Selector
            TimeframeSelector(
                selectedTimeframe = selectedTimeframe,
                timeframeOptions = timeframeOptions,
                onTimeframeChange = { selectedTimeframe = it },
                modifier = Modifier.padding(horizontal = NSSpacing.screenEdge, vertical = NSSpacing.md)
            )

            // Overall Adherence Card
            OverallAdherenceCard(
                viewModel = viewModel,
                selectedTimeframe = selectedTimeframe,
                modifier = Modifier.padding(horizontal = NSSpacing.screenEdge, vertical = NSSpacing.md)
            )

            // Medication-Specific Adherence Card
            MedicationAdherenceCard(
                viewModel = viewModel,
                selectedTimeframe = selectedTimeframe,
                modifier = Modifier.padding(horizontal = NSSpacing.screenEdge, vertical = NSSpacing.md)
            )

            // Side Effects Summary Card
            SideEffectsCard(
                viewModel = viewModel,
                selectedTimeframe = selectedTimeframe,
                modifier = Modifier.padding(horizontal = NSSpacing.screenEdge, vertical = NSSpacing.md)
            )

            // Empty state
            if (medications.isEmpty()) {
                NSEmptyState(
                    title = stringResource(R.string.medication_no_medications),
                    message = stringResource(R.string.medication_add_medications_stats),
                    icon = Icons.Default.Add, // Use chart icon in real app
                    action = { viewModel.prepareNewMedication() },
                    actionTitle = stringResource(R.string.medication_add),
                    modifier = Modifier
                        .padding(horizontal = NSSpacing.screenEdge)
                        .padding(top = NSSpacing.xl)
                )
            }

            Spacer(modifier = Modifier.height(NSSpacing.xxl))
        }
    }
}

/**
 * Timeframe selector component
 */
@Composable
private fun TimeframeSelector(
    selectedTimeframe: Int,
    timeframeOptions: List<Int>,
    onTimeframeChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = DesignTokens.Colors.surface,
        shape = RoundedCornerShape(NSSpacing.CornerRadius.md)
    ) {
        Row(
            modifier = Modifier.padding(NSSpacing.md),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.medication_analysis_period),
                style = NSTypography.bodyBold,
                color = DesignTokens.Colors.textPrimary
            )

            Row(horizontalArrangement = Arrangement.spacedBy(NSSpacing.sm)) {
                timeframeOptions.forEach { days ->
                    Button(
                        onClick = { onTimeframeChange(days) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedTimeframe == days) {
                                DesignTokens.Colors.primary
                            } else {
                                DesignTokens.Colors.surface
                            },
                            contentColor = if (selectedTimeframe == days) {
                                androidx.compose.ui.graphics.Color.White
                            } else {
                                DesignTokens.Colors.textPrimary
                            }
                        ),
                        shape = RoundedCornerShape(NSSpacing.CornerRadius.sm),
                        contentPadding = PaddingValues(horizontal = NSSpacing.sm, vertical = NSSpacing.xs)
                    ) {
                        Text(
                            text = "$days ${stringResource(R.string.medication_days_suffix)}",
                            style = NSTypography.caption
                        )
                    }
                }
            }
        }
    }
}

/**
 * Overall adherence card component
 */
@Composable
private fun OverallAdherenceCard(
    viewModel: MedicationViewModel,
    selectedTimeframe: Int,
    modifier: Modifier = Modifier
) {
    val adherenceRate = remember(selectedTimeframe) {
        viewModel.calculateAdherence(days = selectedTimeframe)
    }

    NSCard(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(NSSpacing.md)
        ) {
            Text(
                text = stringResource(R.string.medication_overall_adherence),
                style = NSTypography.heading3,
                color = DesignTokens.Colors.textPrimary
            )

            // Adherence rate visualization
            Column(verticalArrangement = Arrangement.spacedBy(NSSpacing.sm)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = String.format("%.0f%%", adherenceRate),
                        style = NSTypography.heading1,
                        color = getAdherenceColor(adherenceRate)
                    )

                    Icon(
                        imageVector = getAdherenceIcon(adherenceRate),
                        contentDescription = null,
                        tint = getAdherenceColor(adherenceRate),
                        modifier = Modifier.size(40.dp)
                    )
                }

                // Progress bar
                LinearProgressIndicator(
                    progress = { (adherenceRate / 100.0).toFloat() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(NSSpacing.CornerRadius.sm)),
                    color = getAdherenceColor(adherenceRate),
                    trackColor = DesignTokens.Colors.primaryLight
                )

                // Adherence feedback
                Text(
                    text = getAdherenceFeedback(adherenceRate),
                    style = NSTypography.caption,
                    color = DesignTokens.Colors.textSecondary,
                    modifier = Modifier.padding(top = NSSpacing.xs)
                )
            }
        }
    }
}

/**
 * Medication-specific adherence card component
 */
@Composable
private fun MedicationAdherenceCard(
    viewModel: MedicationViewModel,
    selectedTimeframe: Int,
    modifier: Modifier = Modifier
) {
    val medications by viewModel.medications.collectAsState()

    NSCard(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(NSSpacing.md)
        ) {
            Text(
                text = stringResource(R.string.medication_adherence_by_medication),
                style = NSTypography.heading3,
                color = DesignTokens.Colors.textPrimary
            )

            if (medications.isNotEmpty()) {
                medications.forEachIndexed { index, medication ->
                    val stats = remember(selectedTimeframe, medication.id) {
                        viewModel.getMedicationStats(medication.id, selectedTimeframe)
                    }
                    val adherenceRate = stats["adherenceRate"] as? Double ?: 0.0

                    Column(
                        modifier = Modifier.padding(vertical = NSSpacing.xs),
                        verticalArrangement = Arrangement.spacedBy(NSSpacing.xs)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = medication.name,
                                style = NSTypography.bodyBold,
                                color = DesignTokens.Colors.textPrimary
                            )

                            Text(
                                text = String.format("%.0f%%", adherenceRate),
                                style = NSTypography.body,
                                color = getAdherenceColor(adherenceRate)
                            )
                        }

                        // Progress bar
                        LinearProgressIndicator(
                            progress = { (adherenceRate / 100.0).toFloat() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(NSSpacing.CornerRadius.sm)),
                            color = getAdherenceColor(adherenceRate),
                            trackColor = DesignTokens.Colors.primaryLight
                        )
                    }

                    if (index < medications.size - 1) {
                        HorizontalDivider(color = DesignTokens.Colors.border)
                    }
                }
            } else {
                Text(
                    text = stringResource(R.string.medication_no_medications),
                    style = NSTypography.body,
                    color = DesignTokens.Colors.textSecondary,
                    modifier = Modifier.padding(vertical = NSSpacing.md)
                )
            }
        }
    }
}

/**
 * Side effects summary card component
 */
@Composable
private fun SideEffectsCard(
    viewModel: MedicationViewModel,
    selectedTimeframe: Int,
    modifier: Modifier = Modifier
) {
    val medications by viewModel.medications.collectAsState()

    val medicationsWithSideEffects = remember(medications, selectedTimeframe) {
        medications.mapNotNull { medication ->
            val stats = viewModel.getMedicationStats(medication.id, selectedTimeframe)
            val sideEffects = (stats["commonSideEffects"] as? List<*>)?.filterIsInstance<String>() ?: emptyList()
            if (sideEffects.isNotEmpty()) {
                medication to sideEffects
            } else {
                null
            }
        }
    }

    NSCard(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(NSSpacing.md)
        ) {
            Text(
                text = stringResource(R.string.medication_side_effects_summary),
                style = NSTypography.heading3,
                color = DesignTokens.Colors.textPrimary
            )

            if (medicationsWithSideEffects.isNotEmpty()) {
                medicationsWithSideEffects.forEachIndexed { index, (medication, sideEffects) ->
                    Column(
                        modifier = Modifier.padding(vertical = NSSpacing.xs),
                        verticalArrangement = Arrangement.spacedBy(NSSpacing.xs)
                    ) {
                        Text(
                            text = medication.name,
                            style = NSTypography.bodyBold,
                            color = DesignTokens.Colors.textPrimary
                        )

                        sideEffects.forEach { effect ->
                            Row(
                                modifier = Modifier.padding(vertical = NSSpacing.xxs),
                                horizontalArrangement = Arrangement.spacedBy(NSSpacing.xs),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(
                                            color = DesignTokens.Colors.warning,
                                            shape = androidx.compose.foundation.shape.CircleShape
                                        )
                                )

                                Text(
                                    text = effect,
                                    style = NSTypography.body,
                                    color = DesignTokens.Colors.textPrimary
                                )
                            }
                        }
                    }

                    if (index < medicationsWithSideEffects.size - 1) {
                        HorizontalDivider(color = DesignTokens.Colors.border)
                    }
                }
            } else {
                Text(
                    text = stringResource(R.string.medication_no_side_effects_reported),
                    style = NSTypography.body,
                    color = DesignTokens.Colors.textSecondary,
                    modifier = Modifier.padding(vertical = NSSpacing.md)
                )
            }
        }
    }
}

// Helper functions

@Composable
private fun getAdherenceColor(rate: Double): androidx.compose.ui.graphics.Color {
    return when {
        rate >= 90 -> DesignTokens.Colors.success
        rate >= 70 -> DesignTokens.Colors.warning
        else -> DesignTokens.Colors.error
    }
}

private fun getAdherenceIcon(rate: Double): ImageVector {
    return when {
        rate >= 90 -> Icons.Default.Check // Use checkmark.circle.fill in real app
        rate >= 70 -> Icons.Default.Add // Use exclamationmark.circle.fill in real app
        else -> Icons.Default.Add // Use xmark.circle.fill in real app
    }
}

private fun getAdherenceFeedback(rate: Double): String {
    return when {
        rate >= 90 -> "Excellent adherence! Keep it up!"
        rate >= 70 -> "Good adherence. Try to improve consistency."
        rate >= 50 -> "Average adherence. Consider using reminders."
        else -> "Low adherence. Talk to your doctor."
    }
}
