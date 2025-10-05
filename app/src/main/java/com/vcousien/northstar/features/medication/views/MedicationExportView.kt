package com.vcousien.northstar.features.medication.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vcousien.northstar.R
import com.vcousien.northstar.core.designsystem.DesignTokens
import com.vcousien.northstar.core.designsystem.NSSpacing
import com.vcousien.northstar.core.designsystem.NSTypography
import com.vcousien.northstar.core.designsystem.components.NSPrimaryButton
import com.vcousien.northstar.core.models.MedicationEntry
import com.vcousien.northstar.features.medication.viewmodels.MedicationViewModel
import kotlinx.datetime.*

/**
 * MedicationExportView - View for exporting medication data
 *
 * Features:
 * - Period selection (7, 14, 30, 90, 180, 365 days)
 * - Format selection (PDF, CSV)
 * - Content options (include side effects, include notes)
 * - Export preview showing data summary
 * - Export button
 * - Matches iOS MedicationExportView implementation
 *
 * @param onDismiss Callback when the view is dismissed
 * @param viewModel MedicationViewModel for accessing medication data
 * @param modifier Modifier to be applied to the view
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationExportView(
    onDismiss: () -> Unit,
    viewModel: MedicationViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    var selectedTimeframe by remember { mutableStateOf(30) }
    var selectedFormat by remember { mutableStateOf(ExportFormat.PDF) }
    var includeSideEffects by remember { mutableStateOf(true) }
    var includeNotes by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.medication_export_title),
                        style = NSTypography.heading2,
                        color = DesignTokens.Colors.textPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.common_cancel),
                            tint = DesignTokens.Colors.textSecondary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DesignTokens.Colors.surface
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
                .padding(horizontal = NSSpacing.screenEdge),
            verticalArrangement = Arrangement.spacedBy(NSSpacing.lg)
        ) {
            Spacer(modifier = Modifier.height(NSSpacing.md))

            // Period Section
            PeriodSection(
                selectedTimeframe = selectedTimeframe,
                onTimeframeChange = { selectedTimeframe = it }
            )

            // Format Section
            FormatSection(
                selectedFormat = selectedFormat,
                onFormatChange = { selectedFormat = it }
            )

            // Content Options Section
            ContentOptionsSection(
                includeSideEffects = includeSideEffects,
                includeNotes = includeNotes,
                onIncludeSideEffectsChange = { includeSideEffects = it },
                onIncludeNotesChange = { includeNotes = it }
            )

            // Preview Section
            PreviewSection(
                viewModel = viewModel,
                selectedTimeframe = selectedTimeframe,
                includeSideEffects = includeSideEffects
            )

            // Export Button
            NSPrimaryButton(
                title = stringResource(R.string.medication_export_button),
                onClick = {
                    exportData(
                        viewModel = viewModel,
                        timeframe = selectedTimeframe,
                        format = selectedFormat,
                        includeSideEffects = includeSideEffects,
                        includeNotes = includeNotes,
                        onDismiss = onDismiss
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(NSSpacing.xxl))
        }
    }
}

/**
 * Period selection section
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PeriodSection(
    selectedTimeframe: Int,
    onTimeframeChange: (Int) -> Unit
) {
    val timeframeOptions = listOf(7, 14, 30, 90, 180, 365)
    var expanded by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = DesignTokens.Colors.surface,
        shape = RoundedCornerShape(NSSpacing.CornerRadius.md)
    ) {
        Column(
            modifier = Modifier.padding(NSSpacing.md),
            verticalArrangement = Arrangement.spacedBy(NSSpacing.md)
        ) {
            Text(
                text = stringResource(R.string.medication_export_period),
                style = NSTypography.caption,
                color = DesignTokens.Colors.textSecondary
            )

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = getTimeframeLabel(selectedTimeframe),
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = DesignTokens.Colors.border,
                        focusedBorderColor = DesignTokens.Colors.primary
                    )
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    timeframeOptions.forEach { days ->
                        DropdownMenuItem(
                            text = { Text(getTimeframeLabel(days)) },
                            onClick = {
                                onTimeframeChange(days)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Format selection section
 */
@Composable
private fun FormatSection(
    selectedFormat: ExportFormat,
    onFormatChange: (ExportFormat) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = DesignTokens.Colors.surface,
        shape = RoundedCornerShape(NSSpacing.CornerRadius.md)
    ) {
        Column(
            modifier = Modifier.padding(NSSpacing.md),
            verticalArrangement = Arrangement.spacedBy(NSSpacing.md)
        ) {
            Text(
                text = stringResource(R.string.medication_export_format),
                style = NSTypography.caption,
                color = DesignTokens.Colors.textSecondary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(NSSpacing.sm)
            ) {
                ExportFormat.values().forEach { format ->
                    OutlinedButton(
                        onClick = { onFormatChange(format) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (selectedFormat == format) {
                                DesignTokens.Colors.primary
                            } else {
                                androidx.compose.ui.graphics.Color.Transparent
                            },
                            contentColor = if (selectedFormat == format) {
                                androidx.compose.ui.graphics.Color.White
                            } else {
                                DesignTokens.Colors.textPrimary
                            }
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            width = if (selectedFormat == format) 0.dp else 1.dp
                        )
                    ) {
                        Text(
                            text = format.displayName,
                            style = NSTypography.body
                        )
                    }
                }
            }
        }
    }
}

/**
 * Content options section
 */
@Composable
private fun ContentOptionsSection(
    includeSideEffects: Boolean,
    includeNotes: Boolean,
    onIncludeSideEffectsChange: (Boolean) -> Unit,
    onIncludeNotesChange: (Boolean) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = DesignTokens.Colors.surface,
        shape = RoundedCornerShape(NSSpacing.CornerRadius.md)
    ) {
        Column(
            modifier = Modifier.padding(NSSpacing.md),
            verticalArrangement = Arrangement.spacedBy(NSSpacing.md)
        ) {
            Text(
                text = stringResource(R.string.medication_export_content),
                style = NSTypography.caption,
                color = DesignTokens.Colors.textSecondary
            )

            // Include Side Effects Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.medication_export_include_side_effects),
                    style = NSTypography.body,
                    color = DesignTokens.Colors.textPrimary
                )

                Switch(
                    checked = includeSideEffects,
                    onCheckedChange = onIncludeSideEffectsChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = androidx.compose.ui.graphics.Color.White,
                        checkedTrackColor = DesignTokens.Colors.primary
                    )
                )
            }

            HorizontalDivider(color = DesignTokens.Colors.border)

            // Include Notes Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.medication_export_include_notes),
                    style = NSTypography.body,
                    color = DesignTokens.Colors.textPrimary
                )

                Switch(
                    checked = includeNotes,
                    onCheckedChange = onIncludeNotesChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = androidx.compose.ui.graphics.Color.White,
                        checkedTrackColor = DesignTokens.Colors.primary
                    )
                )
            }
        }
    }
}

/**
 * Preview section showing export summary
 */
@Composable
private fun PreviewSection(
    viewModel: MedicationViewModel,
    selectedTimeframe: Int,
    includeSideEffects: Boolean
) {
    val medications by viewModel.medications.collectAsState()
    val entries = remember(selectedTimeframe) {
        getEntriesForTimeframe(viewModel, selectedTimeframe)
    }
    val adherenceRate = remember(selectedTimeframe) {
        viewModel.calculateAdherence(days = selectedTimeframe)
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = DesignTokens.Colors.surface,
        shape = RoundedCornerShape(NSSpacing.CornerRadius.md)
    ) {
        Column(
            modifier = Modifier.padding(NSSpacing.md),
            verticalArrangement = Arrangement.spacedBy(NSSpacing.md)
        ) {
            Text(
                text = stringResource(R.string.medication_export_preview),
                style = NSTypography.caption,
                color = DesignTokens.Colors.textSecondary
            )

            Text(
                text = stringResource(R.string.medication_export_preview_title),
                style = NSTypography.bodyBold,
                color = DesignTokens.Colors.textPrimary
            )

            Column(verticalArrangement = Arrangement.spacedBy(NSSpacing.xs)) {
                val medicationsCount = medications.size
                val medicationsText = if (medicationsCount > 1) {
                    stringResource(R.string.medication_export_medications_plural, medicationsCount)
                } else {
                    stringResource(R.string.medication_export_medications_singular, medicationsCount)
                }
                Text(
                    text = "• $medicationsText",
                    style = NSTypography.body,
                    color = DesignTokens.Colors.textSecondary
                )

                val entriesCount = entries.size
                val entriesText = if (entriesCount > 1) {
                    stringResource(R.string.medication_export_entries_plural, entriesCount)
                } else {
                    stringResource(R.string.medication_export_entries_singular, entriesCount)
                }
                Text(
                    text = "• $entriesText",
                    style = NSTypography.body,
                    color = DesignTokens.Colors.textSecondary
                )

                if (includeSideEffects) {
                    val sideEffectsCount = countSideEffects(entries)
                    val sideEffectsText = if (sideEffectsCount > 1) {
                        stringResource(R.string.medication_export_side_effects_plural, sideEffectsCount)
                    } else {
                        stringResource(R.string.medication_export_side_effects_singular, sideEffectsCount)
                    }
                    Text(
                        text = "• $sideEffectsText",
                        style = NSTypography.body,
                        color = DesignTokens.Colors.textSecondary
                    )
                }

                Text(
                    text = "• ${stringResource(R.string.medication_export_adherence_rate)}: ${String.format("%.0f%%", adherenceRate)}",
                    style = NSTypography.body,
                    color = DesignTokens.Colors.textSecondary
                )
            }
        }
    }
}

// Helper Functions

/**
 * Export format enum
 */
enum class ExportFormat(val displayName: String) {
    PDF("PDF"),
    CSV("CSV")
}

/**
 * Gets a label for a timeframe
 */
private fun getTimeframeLabel(days: Int): String {
    return when (days) {
        7 -> "7 days"
        14 -> "2 weeks"
        30 -> "1 month"
        90 -> "3 months"
        180 -> "6 months"
        365 -> "1 year"
        else -> "$days days"
    }
}

/**
 * Gets entries for a timeframe
 */
private fun getEntriesForTimeframe(viewModel: MedicationViewModel, days: Int): List<MedicationEntry> {
    val today = Clock.System.now()
    val timeZone = TimeZone.currentSystemDefault()
    val startDate = today.minus(days - 1, DateTimeUnit.DAY, timeZone)

    return viewModel.getEntries(startDate, today)
}

/**
 * Counts side effects in entries
 */
private fun countSideEffects(entries: List<MedicationEntry>): Int {
    return entries.sumOf { it.sideEffects.size }
}

/**
 * Exports the medication data
 * In a real implementation, this would generate a file and present a share dialog
 */
private fun exportData(
    viewModel: MedicationViewModel,
    timeframe: Int,
    format: ExportFormat,
    includeSideEffects: Boolean,
    includeNotes: Boolean,
    onDismiss: () -> Unit
) {
    // TODO: In a real implementation:
    // 1. Generate a file in the selected format (PDF or CSV)
    // 2. Include the selected data (side effects, notes)
    // 3. Present Android's share dialog or save the file

    println("📤 Exporting medication data:")
    println("   Timeframe: $timeframe days")
    println("   Format: $format")
    println("   Include side effects: $includeSideEffects")
    println("   Include notes: $includeNotes")

    // For now, just close the view
    onDismiss()
}
