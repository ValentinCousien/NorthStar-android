package com.vcousien.northstar.features.mood.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vcousien.northstar.R
import com.vcousien.northstar.core.designsystem.NSSpacing
import com.vcousien.northstar.core.designsystem.NSTypography
import com.vcousien.northstar.core.designsystem.components.NSCard
import com.vcousien.northstar.core.models.MoodEntry
import com.vcousien.northstar.features.mood.models.MoodLevel
import com.vcousien.northstar.features.mood.viewmodels.MoodTrackingViewModel
import com.vcousien.northstar.ui.theme.NorthStarTheme
import com.vcousien.northstar.ui.theme.northStarColors
import kotlinx.datetime.Instant
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.round

/**
 * MoodEntryDetailView - A composable that displays detailed information about a mood entry
 * 
 * This view allows users to view all details of a mood entry including:
 * - Date and time of entry
 * - Mood level with visual representation
 * - Energy and anxiety metrics
 * - Sleep quality (if available)
 * - Notes (if any)
 * - Action menu for editing/deleting
 * 
 * @param entry The mood entry to display
 * @param viewModel The mood tracking view model for handling actions
 * @param onDismiss Callback when the view should be dismissed
 * @param onEditEntry Callback when the user wants to edit the entry (optional)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodEntryDetailView(
    entry: MoodEntry,
    viewModel: MoodTrackingViewModel,
    onDismiss: () -> Unit,
    onEditEntry: ((MoodEntry) -> Unit)? = null
) {
    var showDeleteAlert by remember { mutableStateOf(false) }
    var showActionsMenu by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Text(
                    text = stringResource(R.string.mood_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            },
            navigationIcon = {
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.common_cancel),
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            },
            actions = {
                Box {
                    IconButton(onClick = { showActionsMenu = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Actions",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    DropdownMenu(
                        expanded = showActionsMenu,
                        onDismissRequest = { showActionsMenu = false }
                    ) {
                        if (onEditEntry != null) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.common_edit)) },
                                onClick = {
                                    showActionsMenu = false
                                    onEditEntry(entry)
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = null
                                    )
                                }
                            )
                        }

                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.common_delete)) },
                            onClick = {
                                showActionsMenu = false
                                showDeleteAlert = true
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background
            )
        )

        // Scrollable Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = NSSpacing.screenEdge)
        ) {
            Spacer(modifier = Modifier.height(NSSpacing.lg))

            // Date and Time Header
            EntryDateHeader(entry = entry)

            Spacer(modifier = Modifier.height(NSSpacing.xl))

            // Mood Level Display
            MoodLevelDisplay(entry = entry)

            Spacer(modifier = Modifier.height(NSSpacing.xl))

            // Metrics Card
            EntryMetricsCard(entry = entry)

            // Notes (if present)
            if (entry.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(NSSpacing.lg))
                EntryNotesView(notes = entry.notes)
            }

            Spacer(modifier = Modifier.height(NSSpacing.xl))
        }
    }

    // Delete Confirmation Dialog
    if (showDeleteAlert) {
        AlertDialog(
            onDismissRequest = { showDeleteAlert = false },
            title = {
                Text("Delete this mood entry?")
            },
            text = {
                Text("This action cannot be undone.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteEntry(entry.id)
                        showDeleteAlert = false
                        onDismiss()
                    }
                ) {
                    Text(
                        text = stringResource(R.string.common_delete),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteAlert = false }
                ) {
                    Text(stringResource(R.string.common_cancel))
                }
            }
        )
    }
}

/**
 * Header component displaying the entry date and time
 */
@Composable
private fun EntryDateHeader(
    entry: MoodEntry
) {
    val dateFormat = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault())
    val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
    val date = Date(entry.date)

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = dateFormat.format(date),
            style = NSTypography.heading2,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(NSSpacing.xs))

        Text(
            text = timeFormat.format(date),
            style = NSTypography.subtitle,
            color = MaterialTheme.northStarColors.textSecondary,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Component displaying the mood level with icon and name
 */
@Composable
private fun MoodLevelDisplay(
    entry: MoodEntry
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Mood Icon
        Icon(
            imageVector = entry.level.getIcon(),
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = entry.level.getColor()
        )
        
        Spacer(modifier = Modifier.height(NSSpacing.md))
        
        // Mood Name
        Text(
            text = entry.level.getName(),
            style = NSTypography.heading3,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Card displaying entry metrics (energy, anxiety, sleep quality)
 */
@Composable
private fun EntryMetricsCard(
    entry: MoodEntry
) {
    NSCard {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Energy and Anxiety Levels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Energy Level
                MetricView(
                    title = stringResource(R.string.mood_energy_level),
                    value = "${round(entry.energyLevel).toInt()}/10",
                    isHighlighted = false
                )
                
                // Anxiety Level
                MetricView(
                    title = stringResource(R.string.mood_anxiety_level),
                    value = "${round(entry.anxietyLevel).toInt()}/10",
                    isHighlighted = entry.anxietyLevel > 5.0
                )
            }
            
            // Sleep Quality (if available)
            entry.sleepQuality?.let { sleepQuality ->
                Spacer(modifier = Modifier.height(NSSpacing.md))

                HorizontalDivider(
                    color = MaterialTheme.northStarColors.divider,
                    thickness = 1.dp
                )
                
                Spacer(modifier = Modifier.height(NSSpacing.md))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.sleep_quality),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.northStarColors.textSecondary
                    )
                    
                    SleepQualityStars(quality = sleepQuality)
                }
            }
        }
    }
}

/**
 * Component for displaying individual metric values
 */
@Composable
private fun MetricView(
    title: String,
    value: String,
    isHighlighted: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.northStarColors.textSecondary,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(NSSpacing.xs))
        
        Text(
            text = value,
            style = NSTypography.heading3,
            color = if (isHighlighted) {
                MaterialTheme.colorScheme.error
            } else {
                MaterialTheme.colorScheme.primary
            },
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Component for displaying sleep quality as stars
 */
@Composable
private fun SleepQualityStars(
    quality: Double
) {
    val stars = (quality / 2.0).toInt().coerceIn(0, 5)
    
    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
        repeat(5) { index ->
            Icon(
                imageVector = if (index < stars) Icons.Default.Star else Icons.Default.StarBorder,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

/**
 * Component for displaying entry notes
 */
@Composable
private fun EntryNotesView(
    notes: String
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.mood_notes_label),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(NSSpacing.xs))

        NSCard(
            backgroundColor = MaterialTheme.colorScheme.surface
        ) {
            Text(
                text = notes,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// Preview Components
// Note: Full previews disabled as they require ViewModel dependency injection

// Preview for individual components:

@Preview(showBackground = true)
@Composable
private fun EntryDateHeaderPreview() {
    NorthStarTheme {
        val sampleEntry = MoodEntry(
            id = "preview",
            date = Instant.fromEpochMilliseconds(System.currentTimeMillis()),
            level = MoodLevel.NEUTRAL
        )
        
        Surface {
            Column(modifier = Modifier.padding(16.dp)) {
                EntryDateHeader(entry = sampleEntry)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MoodLevelDisplayPreview() {
    NorthStarTheme {
        val sampleEntry = MoodEntry(
            id = "preview",
            date = Instant.fromEpochMilliseconds(System.currentTimeMillis()),
            level = MoodLevel.ELEVATED
        )
        
        Surface {
            Column(modifier = Modifier.padding(16.dp)) {
                MoodLevelDisplay(entry = sampleEntry)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EntryMetricsCardPreview() {
    NorthStarTheme {
        val sampleEntry = MoodEntry(
            id = "preview",
            date = Instant.fromEpochMilliseconds(System.currentTimeMillis()),
            level = MoodLevel.NEUTRAL,
            energyLevel = 6.5,
            anxietyLevel = 3.0,
            sleepQuality = 7.0
        )
        
        Surface {
            Column(modifier = Modifier.padding(16.dp)) {
                EntryMetricsCard(entry = sampleEntry)
            }
        }
    }
}
