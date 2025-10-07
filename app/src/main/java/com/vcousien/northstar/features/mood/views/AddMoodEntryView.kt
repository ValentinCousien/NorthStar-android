package com.vcousien.northstar.features.mood.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.vcousien.northstar.core.designsystem.NSSpacing
import com.vcousien.northstar.core.designsystem.NSTypography
import com.vcousien.northstar.core.designsystem.DesignTokens
import com.vcousien.northstar.core.designsystem.components.NSPrimaryButton
import com.vcousien.northstar.core.designsystem.components.NSSlider
import com.vcousien.northstar.core.models.MoodEntry
import com.vcousien.northstar.features.mood.models.MoodLevel
import com.vcousien.northstar.features.mood.viewmodels.MoodTrackingViewModel
import kotlinx.datetime.Clock

/**
 * AddMoodEntryView - View for adding or editing mood entries
 *
 * Features:
 * - Mood level selection with icons
 * - Energy level slider (0-10 scale)
 * - Anxiety level slider (0-10 scale)
 * - Optional sleep quality slider
 * - Notes text field
 * - Form validation
 * - Matches iOS AddMoodEntryView implementation
 *
 * @param viewModel MoodTrackingViewModel for mood data management
 * @param onDismiss Callback when the view is dismissed
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMoodEntryView(
    viewModel: MoodTrackingViewModel = viewModel(),
    onDismiss: () -> Unit = {}
) {
    val today = Clock.System.now()
    val existingEntry = viewModel.getEntry(today)

    // State variables
    var selectedMoodLevel by remember {
        mutableStateOf(existingEntry?.level ?: MoodLevel.NEUTRAL)
    }
    var energyLevel by remember {
        mutableStateOf(existingEntry?.energyLevel ?: 5.0)
    }
    var anxietyLevel by remember {
        mutableStateOf(existingEntry?.anxietyLevel ?: 0.0)
    }
    var sleepQuality by remember {
        mutableStateOf(existingEntry?.sleepQuality)
    }
    var notes by remember {
        mutableStateOf(existingEntry?.notes ?: "")
    }
    var trackSleepQuality by remember {
        mutableStateOf(existingEntry?.sleepQuality != null)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (existingEntry != null)
                            stringResource(R.string.mood_edit_entry)
                        else
                            stringResource(R.string.mood_add_entry),
                        style = NSTypography.heading2
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.common_cancel)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DesignTokens.Colors.cardBackground
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
                .padding(NSSpacing.screenEdge)
        ) {
            // Mood Level Selection
            Text(
                text = stringResource(R.string.mood_level_header),
                style = NSTypography.heading3,
                color = DesignTokens.Colors.textPrimary,
                modifier = Modifier.padding(bottom = NSSpacing.md)
            )

            Text(
                text = stringResource(R.string.mood_level_question),
                style = NSTypography.body,
                color = DesignTokens.Colors.textPrimary,
                modifier = Modifier.padding(bottom = NSSpacing.md)
            )

            // Mood Level Grid
            MoodLevelGrid(
                selectedLevel = selectedMoodLevel,
                onLevelSelected = { selectedMoodLevel = it }
            )

            Spacer(modifier = Modifier.height(NSSpacing.xl))

            // Energy Level Section
            Text(
                text = stringResource(R.string.mood_energy_header),
                style = NSTypography.heading3,
                color = DesignTokens.Colors.textPrimary,
                modifier = Modifier.padding(bottom = NSSpacing.md)
            )

            Text(
                text = stringResource(R.string.mood_energy_question),
                style = NSTypography.body,
                color = DesignTokens.Colors.textPrimary,
                modifier = Modifier.padding(bottom = NSSpacing.sm)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Low",
                    style = NSTypography.caption,
                    color = DesignTokens.Colors.textSecondary
                )
                Text(
                    text = "High",
                    style = NSTypography.caption,
                    color = DesignTokens.Colors.textSecondary
                )
            }

            NSSlider(
                title = "",
                value = energyLevel.toFloat(),
                onValueChange = { energyLevel = it.toDouble() },
                range = 0f..10f,
                steps = 9,
                modifier = Modifier.padding(vertical = NSSpacing.sm)
            )

            Text(
                text = "${energyLevel.toInt()}/10",
                style = NSTypography.bodyBold,
                color = DesignTokens.Colors.primary,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(NSSpacing.xl))

            // Anxiety Level Section
            Text(
                text = stringResource(R.string.mood_anxiety_header),
                style = NSTypography.heading3,
                color = DesignTokens.Colors.textPrimary,
                modifier = Modifier.padding(bottom = NSSpacing.md)
            )

            Text(
                text = stringResource(R.string.mood_anxiety_question),
                style = NSTypography.body,
                color = DesignTokens.Colors.textPrimary,
                modifier = Modifier.padding(bottom = NSSpacing.sm)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Low",
                    style = NSTypography.caption,
                    color = DesignTokens.Colors.textSecondary
                )
                Text(
                    text = "High",
                    style = NSTypography.caption,
                    color = DesignTokens.Colors.textSecondary
                )
            }

            NSSlider(
                title = "",
                value = anxietyLevel.toFloat(),
                onValueChange = { anxietyLevel = it.toDouble() },
                range = 0f..10f,
                steps = 9,
                modifier = Modifier.padding(vertical = NSSpacing.sm)
            )

            Text(
                text = "${anxietyLevel.toInt()}/10",
                style = NSTypography.bodyBold,
                color = if (anxietyLevel > 5.0) DesignTokens.Colors.error else DesignTokens.Colors.primary,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(NSSpacing.xl))

            // Sleep Quality Section (Optional)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.sleep_quality),
                    style = NSTypography.heading3,
                    color = DesignTokens.Colors.textPrimary
                )

                Switch(
                    checked = trackSleepQuality,
                    onCheckedChange = {
                        trackSleepQuality = it
                        if (!it) sleepQuality = null else sleepQuality = 5.0
                    }
                )
            }

            if (trackSleepQuality) {
                Spacer(modifier = Modifier.height(NSSpacing.md))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Poor",
                        style = NSTypography.caption,
                        color = DesignTokens.Colors.textSecondary
                    )
                    Text(
                        text = "Excellent",
                        style = NSTypography.caption,
                        color = DesignTokens.Colors.textSecondary
                    )
                }

                NSSlider(
                    title = "",
                    value = sleepQuality?.toFloat() ?: 5f,
                    onValueChange = { sleepQuality = it.toDouble() },
                    range = 0f..10f,
                    steps = 9,
                    modifier = Modifier.padding(vertical = NSSpacing.sm)
                )

                Text(
                    text = "${sleepQuality?.toInt() ?: 5}/10",
                    style = NSTypography.bodyBold,
                    color = DesignTokens.Colors.primary,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            Spacer(modifier = Modifier.height(NSSpacing.xl))

            // Notes Section
            Text(
                text = stringResource(R.string.mood_notes_header),
                style = NSTypography.heading3,
                color = DesignTokens.Colors.textPrimary,
                modifier = Modifier.padding(bottom = NSSpacing.md)
            )

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                textStyle = NSTypography.body,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = DesignTokens.Colors.primary,
                    unfocusedBorderColor = DesignTokens.Colors.border,
                    focusedTextColor = DesignTokens.Colors.textPrimary,
                    unfocusedTextColor = DesignTokens.Colors.textPrimary
                ),
                placeholder = {
                    Text(
                        text = stringResource(R.string.mood_notes_placeholder),
                        style = NSTypography.body,
                        color = DesignTokens.Colors.textSecondary
                    )
                }
            )

            Spacer(modifier = Modifier.height(NSSpacing.xl))

            // Save button
            NSPrimaryButton(
                title = stringResource(R.string.common_save),
                onClick = {
                    saveMoodEntry(
                        viewModel = viewModel,
                        existingEntry = existingEntry,
                        level = selectedMoodLevel,
                        energyLevel = energyLevel,
                        anxietyLevel = anxietyLevel,
                        sleepQuality = sleepQuality,
                        notes = notes.trim()
                    )
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(NSSpacing.lg))
        }
    }
}

/**
 * Mood level selection grid
 */
@Composable
private fun MoodLevelGrid(
    selectedLevel: MoodLevel,
    onLevelSelected: (MoodLevel) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(NSSpacing.md)
    ) {
        // Split mood levels into rows of 3
        val moodLevels = MoodLevel.values().toList()
        val rows = moodLevels.chunked(3)

        rows.forEach { rowLevels: List<MoodLevel> ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(NSSpacing.sm)
            ) {
                rowLevels.forEach { level: MoodLevel ->
                    MoodLevelButton(
                        level = level,
                        isSelected = level == selectedLevel,
                        onClick = { onLevelSelected(level) },
                        modifier = Modifier.weight(1f)
                    )
                }
                // Add empty spaces if row has less than 3 items
                repeat(3 - rowLevels.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

/**
 * Individual mood level button
 */
@Composable
private fun MoodLevelButton(
    level: MoodLevel,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(
                color = if (isSelected) DesignTokens.Colors.primary.copy(alpha = 0.1f) else DesignTokens.Colors.cardBackground,
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) DesignTokens.Colors.primary else DesignTokens.Colors.border,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(NSSpacing.md),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(NSSpacing.xs)
    ) {
        Icon(
            imageVector = level.getIcon(),
            contentDescription = null,
            modifier = Modifier.size(32.dp),
            tint = level.getColor()
        )

        Text(
            text = level.getName(),
            style = NSTypography.caption,
            color = DesignTokens.Colors.textPrimary
        )
    }
}

/**
 * Save the mood entry
 */
private fun saveMoodEntry(
    viewModel: MoodTrackingViewModel,
    existingEntry: MoodEntry?,
    level: MoodLevel,
    energyLevel: Double,
    anxietyLevel: Double,
    sleepQuality: Double?,
    notes: String
) {
    val entry = MoodEntry(
        date = Clock.System.now(),
        level = level,
        energyLevel = energyLevel,
        anxietyLevel = anxietyLevel,
        sleepQuality = sleepQuality,
        notes = notes
    )

    if (existingEntry != null) {
        // Update existing entry
        viewModel.updateEntry(entry.copy(id = existingEntry.id))
    } else {
        // Add new entry
        viewModel.addEntry(entry)
    }
}
