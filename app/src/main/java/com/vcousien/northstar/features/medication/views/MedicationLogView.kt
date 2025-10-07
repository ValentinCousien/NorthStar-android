package com.vcousien.northstar.features.medication.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vcousien.northstar.R
import com.vcousien.northstar.core.designsystem.NSSpacing
import com.vcousien.northstar.core.designsystem.NSTypography
import com.vcousien.northstar.core.designsystem.DesignTokens
import com.vcousien.northstar.features.medication.viewmodels.MedicationViewModel

/**
 * MedicationLogView - Main medication screen
 *
 * Features:
 * - Today's medications card showing scheduled and as-needed medications
 * - List of all medications with edit/delete actions
 * - FAB for adding new medications
 * - Modal sheet for adding/editing medications
 * - Matches iOS MedicationLogView implementation
 *
 * @param viewModel MedicationViewModel for medication management
 * @param modifier Modifier to be applied to the view
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationLogView(
    viewModel: MedicationViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val shouldShowAddSheet = viewModel.shouldShowAddSheet

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.medication_title),
                        style = NSTypography.heading1,
                        color = DesignTokens.Colors.textPrimary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DesignTokens.Colors.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.prepareNewMedication() },
                containerColor = DesignTokens.Colors.primary,
                contentColor = DesignTokens.Colors.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.medication_add)
                )
            }
        },
        containerColor = DesignTokens.Colors.background
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Today's medications card
            TodayMedicationCard(
                viewModel = viewModel,
                onAddMedicationClick = { viewModel.prepareNewMedication() },
                modifier = Modifier.padding(horizontal = NSSpacing.screenEdge, vertical = NSSpacing.md)
            )

            // Medications list card
            MedicationsListCard(
                viewModel = viewModel,
                modifier = Modifier.padding(horizontal = NSSpacing.screenEdge, vertical = NSSpacing.md)
            )

            // Bottom padding for FAB clearance
            Spacer(modifier = Modifier.height(NSSpacing.xxl))
        }
    }

    // Add/Edit Medication Sheet
    if (shouldShowAddSheet) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.cancelEditing() },
            containerColor = DesignTokens.Colors.background
        ) {
            AddMedicationView(
                viewModel = viewModel,
                onDismiss = { viewModel.cancelEditing() }
            )
        }
    }
}
