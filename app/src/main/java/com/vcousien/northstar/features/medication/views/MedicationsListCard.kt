package com.vcousien.northstar.features.medication.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vcousien.northstar.R
import com.vcousien.northstar.core.designsystem.NSSpacing
import com.vcousien.northstar.core.designsystem.NSTypography
import com.vcousien.northstar.core.designsystem.DesignTokens
import com.vcousien.northstar.core.designsystem.components.NSCard
import com.vcousien.northstar.core.models.Medication
import com.vcousien.northstar.core.models.getColor
import com.vcousien.northstar.core.models.getTextColor
import com.vcousien.northstar.features.medication.viewmodels.MedicationViewModel

/**
 * MedicationsListCard - Card showing all medications with management options
 *
 * Features:
 * - Header with title and add button
 * - List of all medications with dosage and frequency
 * - Time of day indicators with color badges
 * - Three-dot menu for each medication (Edit, Delete)
 * - Delete confirmation dialog
 * - Empty state when no medications exist
 * - Matches iOS MedicationsListCard implementation
 *
 * @param viewModel MedicationViewModel for medication management
 * @param modifier Modifier to be applied to the card
 */
@Composable
fun MedicationsListCard(
    viewModel: MedicationViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val medications by viewModel.medications.collectAsState()
    var medicationToDelete by remember { mutableStateOf<Medication?>(null) }
    var showingDeleteAlert by remember { mutableStateOf(false) }

    NSCard(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(NSSpacing.md)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.medication_my_medications),
                    style = NSTypography.heading3,
                    color = DesignTokens.Colors.textPrimary
                )

                IconButton(onClick = { viewModel.prepareNewMedication() }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.medication_add),
                        tint = DesignTokens.Colors.textSecondary
                    )
                }
            }

            if (medications.isNotEmpty()) {
                // Medications list
                medications.forEachIndexed { index, medication ->
                    MedicationRow(
                        medication = medication,
                        viewModel = viewModel,
                        onDelete = {
                            medicationToDelete = medication
                            showingDeleteAlert = true
                        }
                    )

                    if (index < medications.size - 1) {
                        HorizontalDivider(color = DesignTokens.Colors.border)
                    }
                }
            } else {
                // Empty state
                Text(
                    text = stringResource(R.string.medication_no_medications),
                    style = NSTypography.body,
                    color = DesignTokens.Colors.textSecondary,
                    modifier = Modifier.padding(vertical = NSSpacing.md)
                )
            }
        }
    }

    // Delete confirmation dialog
    if (showingDeleteAlert && medicationToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                showingDeleteAlert = false
                medicationToDelete = null
            },
            title = {
                Text("Delete Medication", style = NSTypography.heading3)
            },
            text = {
                Text(
                    "Are you sure you want to delete \"${medicationToDelete?.name}\"? This action will also delete all associated data and cannot be undone."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        medicationToDelete?.let { viewModel.deleteMedication(it.id) }
                        showingDeleteAlert = false
                        medicationToDelete = null
                    }
                ) {
                    Text(
                        text = stringResource(R.string.common_delete),
                        color = DesignTokens.Colors.error
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showingDeleteAlert = false
                        medicationToDelete = null
                    }
                ) {
                    Text(stringResource(R.string.common_cancel))
                }
            }
        )
    }
}

/**
 * Medication row component
 */
@Composable
private fun MedicationRow(
    medication: Medication,
    viewModel: MedicationViewModel,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = NSSpacing.xs),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Medication details
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = medication.name,
                style = NSTypography.body,
                color = DesignTokens.Colors.textPrimary
            )

            Text(
                text = "${medication.displayDosage} - ${medication.frequency.displayName}",
                style = NSTypography.caption,
                color = DesignTokens.Colors.textSecondary
            )
        }

        // Time of day indicators
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(end = NSSpacing.sm)
        ) {
            medication.timeOfDay.forEach { time ->
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(
                            color = time.getColor(),
                            shape = RoundedCornerShape(NSSpacing.CornerRadius.sm)
                        ),
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

        // Three-dot menu
        Box {
            IconButton(onClick = { showMenu = true }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More options",
                    tint = DesignTokens.Colors.textSecondary
                )
            }

            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                // Edit option
                DropdownMenuItem(
                    text = {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(NSSpacing.sm),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                tint = DesignTokens.Colors.textPrimary
                            )
                            Text(
                                text = stringResource(R.string.common_edit),
                                style = NSTypography.body
                            )
                        }
                    },
                    onClick = {
                        viewModel.prepareMedicationForEditing(medication)
                        showMenu = false
                    }
                )

                // Delete option
                DropdownMenuItem(
                    text = {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(NSSpacing.sm),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null,
                                tint = DesignTokens.Colors.error
                            )
                            Text(
                                text = stringResource(R.string.common_delete),
                                style = NSTypography.body,
                                color = DesignTokens.Colors.error
                            )
                        }
                    },
                    onClick = {
                        showMenu = false
                        onDelete()
                    }
                )
            }
        }
    }
}
