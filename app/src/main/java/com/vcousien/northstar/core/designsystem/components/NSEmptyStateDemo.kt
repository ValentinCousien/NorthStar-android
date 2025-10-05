package com.vcousien.northstar.core.designsystem.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vcousien.northstar.core.designsystem.DesignTokens
import com.vcousien.northstar.core.designsystem.NSSpacing
import com.vcousien.northstar.core.designsystem.NSTypography

/**
 * Demo screen showcasing NSEmptyState component variants
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NSEmptyStateDemo() {
    var selectedIndex by remember { mutableStateOf(0) }
    
    val emptyStateVariants = remember {
        listOf(
            "Basic Empty State",
            "With Icon",
            "With Action",
            "Mood Empty State",
            "Sleep Empty State",
            "Medication Empty State",
            "Search Empty State",
            "Network Error State",
            "Loading State",
            "Error State",
            "Insufficient Data State"
        )
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("NSEmptyState Demo") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DesignTokens.Colors.surface,
                    titleContentColor = DesignTokens.Colors.textPrimary
                )
            )
        }
    ) { paddingValues ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Sidebar with variant options
            LazyColumn(
                modifier = Modifier
                    .width(200.dp)
                    .fillMaxHeight()
                    .padding(NSSpacing.md),
                verticalArrangement = Arrangement.spacedBy(NSSpacing.xs)
            ) {
                items(emptyStateVariants.size) { index ->
                    val title = emptyStateVariants[index]
                    Card(
                        onClick = { selectedIndex = index },
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedIndex == index) {
                                DesignTokens.Colors.primary.copy(alpha = 0.1f)
                            } else {
                                DesignTokens.Colors.surface
                            }
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = title,
                            style = NSTypography.caption,
                            color = if (selectedIndex == index) {
                                DesignTokens.Colors.primary
                            } else {
                                DesignTokens.Colors.textSecondary
                            },
                            modifier = Modifier.padding(NSSpacing.sm)
                        )
                    }
                }
            }
            
            // Divider
            VerticalDivider(
                color = DesignTokens.Colors.outline,
                thickness = 1.dp
            )
            
            // Content area showing selected variant
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(NSSpacing.md),
                contentAlignment = Alignment.Center
            ) {
                when (selectedIndex) {
                    0 -> BasicEmptyStateExample()
                    1 -> EmptyStateWithIconExample()
                    2 -> EmptyStateWithActionExample()
                    3 -> MoodEmptyStateExample()
                    4 -> SleepEmptyStateExample()
                    5 -> MedicationEmptyStateExample()
                    6 -> SearchEmptyStateExample()
                    7 -> NetworkErrorStateExample()
                    8 -> LoadingStateExample()
                    9 -> ErrorStateExample()
                    10 -> InsufficientDataStateExample()
                    else -> BasicEmptyStateExample()
                }
            }
        }
    }
}

@Composable
private fun BasicEmptyStateExample() {
    NSEmptyState(
        title = "No Items Found",
        message = "There are currently no items to display. This is a basic empty state without any additional elements."
    )
}

@Composable
private fun EmptyStateWithIconExample() {
    NSEmptyState(
        title = "Inbox Empty",
        message = "You're all caught up! No new messages to display at this time.",
        icon = Icons.Outlined.Email
    )
}

@Composable
private fun EmptyStateWithActionExample() {
    NSEmptyState(
        title = "Get Started",
        message = "Welcome to NorthStar! Start your mental health journey by adding your first entry.",
        icon = Icons.Outlined.Add,
        action = { /* Handle action */ },
        actionTitle = "Add Entry"
    )
}

@Composable
private fun MoodEmptyStateExample() {
    NSMoodEmptyState(
        onAddMoodEntry = { /* Handle add mood entry */ }
    )
}

@Composable
private fun SleepEmptyStateExample() {
    NSSleepEmptyState(
        onAddSleepEntry = { /* Handle add sleep entry */ }
    )
}

@Composable
private fun MedicationEmptyStateExample() {
    NSMedicationEmptyState(
        onAddMedication = { /* Handle add medication */ }
    )
}

@Composable
private fun SearchEmptyStateExample() {
    NSSearchEmptyState(
        searchQuery = "anxiety",
        onClearSearch = { /* Handle clear search */ }
    )
}

@Composable
private fun NetworkErrorStateExample() {
    NSNetworkErrorState(
        onRetry = { /* Handle retry */ }
    )
}

@Composable
private fun LoadingStateExample() {
    NSLoadingState(
        message = "Loading your mood data..."
    )
}

@Composable
private fun ErrorStateExample() {
    NSErrorState(
        onRetry = { /* Handle retry */ }
    )
}

@Composable
private fun InsufficientDataStateExample() {
    NSInsufficientDataState(
        dataType = "mood entries",
        minDataRequired = "7 days",
        onAddData = { /* Handle add data */ }
    )
}

@Preview(showBackground = true, widthDp = 800, heightDp = 600)
@Composable
private fun NSEmptyStateDemoPreview() {
    NSEmptyStateDemo()
}

@Preview(showBackground = true)
@Composable
private fun BasicEmptyStatePreview() {
    BasicEmptyStateExample()
}

@Preview(showBackground = true)
@Composable
private fun EmptyStateWithIconPreview() {
    EmptyStateWithIconExample()
}

@Preview(showBackground = true)
@Composable
private fun MoodEmptyStatePreview() {
    MoodEmptyStateExample()
}

@Preview(showBackground = true)
@Composable
private fun LoadingStatePreview() {
    LoadingStateExample()
}