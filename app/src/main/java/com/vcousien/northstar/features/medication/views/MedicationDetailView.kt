package com.vcousien.northstar.features.medication.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vcousien.northstar.core.designsystem.NSSpacing
import com.vcousien.northstar.core.designsystem.NSTypography
import com.vcousien.northstar.core.designsystem.DesignTokens
import com.vcousien.northstar.core.designsystem.components.NSCard
import com.vcousien.northstar.core.models.Medication
import com.vcousien.northstar.core.models.getColor
import com.vcousien.northstar.features.medication.viewmodels.MedicationViewModel
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import java.text.SimpleDateFormat
import java.util.*

/**
 * MedicationDetailView - View displaying detailed information about a medication
 *
 * Features:
 * - Medication information card
 * - Adherence statistics
 * - Common side effects
 * - Recent entries
 * - Edit and delete actions
 * - Matches iOS MedicationDetailView implementation
 *
 * @param medication The medication to display
 * @param viewModel MedicationViewModel for medication management
 * @param onNavigateBack Callback when navigation back is requested
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationDetailView(
    medication: Medication,
    viewModel: MedicationViewModel = viewModel(),
    onNavigateBack: () -> Unit = {}
) {
    var showEditSheet by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var selectedTimeframe by remember { mutableStateOf(30) }
    val timeframeOptions = listOf(7, 14, 30, 90)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = medication.name,
                        style = NSTypography.heading2
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Text("<", style = NSTypography.heading2)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DesignTokens.Colors.background
                )
            )
        },
        containerColor = DesignTokens.Colors.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Medication Info Card
            MedicationInfoCard(
                medication = medication,
                modifier = Modifier.padding(horizontal = NSSpacing.screenEdge, vertical = NSSpacing.md)
            )

            // Adherence Card
            AdherenceStatsCard(
                medication = medication,
                viewModel = viewModel,
                selectedTimeframe = selectedTimeframe,
                timeframeOptions = timeframeOptions,
                onTimeframeChange = { selectedTimeframe = it },
                modifier = Modifier.padding(horizontal = NSSpacing.screenEdge, vertical = NSSpacing.md)
            )

            // Side Effects Card
            SideEffectsStatsCard(
                medication = medication,
                viewModel = viewModel,
                selectedTimeframe = selectedTimeframe,
                modifier = Modifier.padding(horizontal = NSSpacing.screenEdge, vertical = NSSpacing.md)
            )

            // Recent Entries Card
            RecentEntriesCard(
                medication = medication,
                viewModel = viewModel,
                modifier = Modifier.padding(horizontal = NSSpacing.screenEdge, vertical = NSSpacing.md)
            )

            // Action Buttons
            ActionButtons(
                onEdit = {
                    viewModel.prepareMedicationForEditing(medication)
                    showEditSheet = true
                },
                onDelete = { showDeleteConfirmation = true },
                modifier = Modifier.padding(horizontal = NSSpacing.screenEdge, vertical = NSSpacing.md)
            )

            Spacer(modifier = Modifier.height(NSSpacing.xxl))
        }
    }

    // Edit sheet
    if (showEditSheet) {
        ModalBottomSheet(
            onDismissRequest = { showEditSheet = false },
            containerColor = DesignTokens.Colors.background
        ) {
            AddMedicationView(
                viewModel = viewModel,
                onDismiss = { showEditSheet = false }
            )
        }
    }

    // Delete confirmation dialog
    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = {
                Text("Delete Medication", style = NSTypography.heading3)
            },
            text = {
                Text("Are you sure you want to delete ${medication.name}? This action cannot be undone.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteMedication(medication.id)
                        showDeleteConfirmation = false
                        onNavigateBack()
                    }
                ) {
                    Text("Delete", color = DesignTokens.Colors.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

/**
 * Medication information card
 */
@Composable
private fun MedicationInfoCard(
    medication: Medication,
    modifier: Modifier = Modifier
) {
    NSCard(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(NSSpacing.md)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = medication.name,
                        style = NSTypography.heading2,
                        color = DesignTokens.Colors.textPrimary
                    )

                    Text(
                        text = medication.dosage,
                        style = NSTypography.subtitle,
                        color = DesignTokens.Colors.textSecondary
                    )
                }

                Icon(
                    imageVector = Icons.Default.Edit, // Use pill icon in real app
                    contentDescription = null,
                    tint = DesignTokens.Colors.primary,
                    modifier = Modifier.size(30.dp)
                )
            }

            HorizontalDivider(color = DesignTokens.Colors.border)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Frequency",
                        style = NSTypography.captionBold,
                        color = DesignTokens.Colors.textSecondary
                    )

                    Text(
                        text = medication.frequency.displayName,
                        style = NSTypography.body,
                        color = DesignTokens.Colors.textPrimary
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Times",
                        style = NSTypography.captionBold,
                        color = DesignTokens.Colors.textSecondary
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(NSSpacing.xs)) {
                        medication.timeOfDay.forEach { time ->
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(time.getColor()),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = time.displayName.first().toString(),
                                    style = NSTypography.small,
                                    color = time.getTextColor()
                                )
                            }
                        }
                    }
                }
            }

            if (medication.notes.isNotEmpty()) {
                HorizontalDivider(color = DesignTokens.Colors.border)

                Column {
                    Text(
                        text = "Notes",
                        style = NSTypography.captionBold,
                        color = DesignTokens.Colors.textSecondary
                    )

                    Text(
                        text = medication.notes,
                        style = NSTypography.body,
                        color = DesignTokens.Colors.textPrimary
                    )
                }
            }
        }
    }
}

/**
 * Adherence statistics card
 */
@Composable
private fun AdherenceStatsCard(
    medication: Medication,
    viewModel: MedicationViewModel,
    selectedTimeframe: Int,
    timeframeOptions: List<Int>,
    onTimeframeChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val stats = remember(selectedTimeframe) {
        viewModel.getMedicationStats(medication.id, selectedTimeframe)
    }
    val adherenceRate = stats["adherenceRate"] as? Double ?: 0.0

    NSCard(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(NSSpacing.md)
        ) {
            Text(
                text = "Adherence Rate",
                style = NSTypography.heading3,
                color = DesignTokens.Colors.textPrimary
            )

            // Timeframe selector
            Row(
                horizontalArrangement = Arrangement.spacedBy(NSSpacing.sm)
            ) {
                Text(
                    text = "Period:",
                    style = NSTypography.caption,
                    color = DesignTokens.Colors.textSecondary,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )

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
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("$days days", style = NSTypography.caption)
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = String.format("%.0f%%", adherenceRate),
                    style = NSTypography.heading1,
                    color = getAdherenceColor(adherenceRate)
                )

                Column(horizontalAlignment = Alignment.End) {
                    val totalEntries = stats["totalEntries"] as? Int ?: 0
                    val takenEntries = stats["takenEntries"] as? Int ?: 0

                    Text(
                        text = "$takenEntries/$totalEntries doses",
                        style = NSTypography.caption,
                        color = DesignTokens.Colors.textSecondary
                    )

                    Text(
                        text = "over $selectedTimeframe days",
                        style = NSTypography.caption,
                        color = DesignTokens.Colors.textSecondary
                    )
                }
            }

            LinearProgressIndicator(
                progress = { (adherenceRate / 100.0).toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = getAdherenceColor(adherenceRate),
                trackColor = DesignTokens.Colors.primaryLight
            )
        }
    }
}

/**
 * Side effects statistics card
 */
@Composable
private fun SideEffectsStatsCard(
    medication: Medication,
    viewModel: MedicationViewModel,
    selectedTimeframe: Int,
    modifier: Modifier = Modifier
) {
    val stats = remember(selectedTimeframe) {
        viewModel.getMedicationStats(medication.id, selectedTimeframe)
    }
    val commonSideEffects = stats["commonSideEffects"] as? List<*> ?: emptyList<Any>()

    NSCard(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(NSSpacing.md)
        ) {
            Text(
                text = "Common Side Effects",
                style = NSTypography.heading3,
                color = DesignTokens.Colors.textPrimary
            )

            if (commonSideEffects.isNotEmpty()) {
                commonSideEffects.forEach { effect ->
                    Row(
                        modifier = Modifier.padding(vertical = NSSpacing.xxs)
                    ) {
                        Text(
                            text = "⚠️ ",
                            style = NSTypography.body
                        )

                        Text(
                            text = effect.toString(),
                            style = NSTypography.body,
                            color = DesignTokens.Colors.textPrimary
                        )
                    }
                }
            } else {
                Text(
                    text = "No side effects reported",
                    style = NSTypography.body,
                    color = DesignTokens.Colors.textSecondary
                )
            }
        }
    }
}

/**
 * Recent entries card
 */
@Composable
private fun RecentEntriesCard(
    medication: Medication,
    viewModel: MedicationViewModel,
    modifier: Modifier = Modifier
) {
    val today = Clock.System.now()
    val timeZone = TimeZone.currentSystemDefault()
    val startDate = today.minus(7, DateTimeUnit.DAY, timeZone)

    val recentEntries = remember {
        viewModel.getEntries(startDate, today)
            .filter { it.medicationId == medication.id }
            .sortedByDescending { it.date }
            .take(5)
    }

    NSCard(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(NSSpacing.md)
        ) {
            Text(
                text = "Recent Entries",
                style = NSTypography.heading3,
                color = DesignTokens.Colors.textPrimary
            )

            if (recentEntries.isNotEmpty()) {
                recentEntries.forEach { entry ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = NSSpacing.xs),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = formatDate(entry.dateAsInstant),
                                style = NSTypography.body,
                                color = DesignTokens.Colors.textPrimary
                            )

                            entry.actualTimeAsInstant?.let { actualTime ->
                                Text(
                                    text = "Taken at ${formatTime(actualTime)}",
                                    style = NSTypography.caption,
                                    color = DesignTokens.Colors.textSecondary
                                )
                            }
                        }

                        Icon(
                            imageVector = if (entry.taken) Icons.Default.Edit else Icons.Default.Delete,
                            contentDescription = null,
                            tint = if (entry.taken) DesignTokens.Colors.success else DesignTokens.Colors.error,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            } else {
                Text(
                    text = "No recent entries",
                    style = NSTypography.body,
                    color = DesignTokens.Colors.textSecondary
                )
            }
        }
    }
}

/**
 * Action buttons
 */
@Composable
private fun ActionButtons(
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(NSSpacing.md)
    ) {
        Button(
            onClick = onEdit,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = DesignTokens.Colors.primary)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(NSSpacing.sm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Edit, contentDescription = null)
                Text("Edit", style = NSTypography.bodyBold)
            }
        }

        Button(
            onClick = onDelete,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = DesignTokens.Colors.error)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(NSSpacing.sm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Delete, contentDescription = null)
                Text("Delete", style = NSTypography.bodyBold)
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

private fun formatDate(instant: kotlinx.datetime.Instant): String {
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    val calendar = Calendar.getInstance().apply {
        set(Calendar.YEAR, localDateTime.year)
        set(Calendar.MONTH, localDateTime.monthNumber - 1)
        set(Calendar.DAY_OF_MONTH, localDateTime.dayOfMonth)
    }

    val formatter = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
    return formatter.format(calendar.time)
}

private fun formatTime(instant: kotlinx.datetime.Instant): String {
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, localDateTime.hour)
        set(Calendar.MINUTE, localDateTime.minute)
    }

    val formatter = SimpleDateFormat("h:mm a", Locale.getDefault())
    return formatter.format(calendar.time)
}
