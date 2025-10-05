package com.vcousien.northstar.core.designsystem.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.vcousien.northstar.core.designsystem.DesignTokens
import com.vcousien.northstar.core.designsystem.NSSpacing
import com.vcousien.northstar.core.designsystem.NSTypography

/**
 * NSEmptyState - Standardized empty state component for placeholder screens
 * 
 * A reusable empty state component that provides consistent styling and behavior
 * across the NorthStar app. Used to display meaningful messages when no data
 * is available, guiding users on next actions.
 * 
 * Features:
 * - Configurable title and message text
 * - Optional icon display with consistent styling
 * - Optional action button with callback
 * - Proper spacing and typography alignment
 * - Accessible design with proper content description
 * - Matches iOS NSEmptyState implementation
 * 
 * Common use cases:
 * - No mood entries recorded
 * - Empty medication list
 * - No sleep data available
 * - Search results not found
 * - Network error states
 * 
 * @param title The main heading text displayed prominently
 * @param message The descriptive text explaining the empty state
 * @param modifier Modifier to be applied to the empty state container
 * @param icon Optional icon to display above the text content
 * @param action Optional callback for the action button
 * @param actionTitle Text to display on the action button (required if action is provided)
 * @param maxWidth Maximum width for the content to maintain readability
 */
@Composable
fun NSEmptyState(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    action: (() -> Unit)? = null,
    actionTitle: String = "",
    maxWidth: Dp = 300.dp
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(NSSpacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            modifier = Modifier.widthIn(max = maxWidth),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(NSSpacing.md)
        ) {
            // Optional icon
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null, // Decorative icon
                    modifier = Modifier.size(60.dp),
                    tint = DesignTokens.Colors.primary.copy(alpha = 0.7f)
                )
            }
            
            // Title text
            Text(
                text = title,
                style = NSTypography.heading3,
                color = DesignTokens.Colors.textPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            
            // Message text
            Text(
                text = message,
                style = NSTypography.body,
                color = DesignTokens.Colors.textSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            
            // Optional action button
            if (action != null && actionTitle.isNotEmpty()) {
                Spacer(modifier = Modifier.height(NSSpacing.md))
                
                Button(
                    onClick = action,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DesignTokens.Colors.primary,
                        contentColor = DesignTokens.Colors.onPrimary
                    ),
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.wrapContentWidth()
                ) {
                    Text(
                        text = actionTitle,
                        style = NSTypography.bodyBold
                    )
                }
            }
        }
    }
}

/**
 * NSEmptyState variant specifically for mood tracking empty states
 * Pre-configured with mood-related messaging and styling
 * 
 * @param modifier Modifier to be applied to the empty state container
 * @param onAddMoodEntry Callback for when user wants to add their first mood entry
 */
@Composable
fun NSMoodEmptyState(
    modifier: Modifier = Modifier,
    onAddMoodEntry: (() -> Unit)? = null
) {
    NSEmptyState(
        title = "No Mood Entries Yet",
        message = "Start tracking your mood to see patterns and insights over time. Your first entry helps build your mental health journey.",
        action = onAddMoodEntry,
        actionTitle = "Add Mood Entry",
        modifier = modifier
    )
}

/**
 * NSEmptyState variant specifically for sleep tracking empty states
 * Pre-configured with sleep-related messaging and styling
 * 
 * @param modifier Modifier to be applied to the empty state container
 * @param onAddSleepEntry Callback for when user wants to log their first sleep
 */
@Composable
fun NSSleepEmptyState(
    modifier: Modifier = Modifier,
    onAddSleepEntry: (() -> Unit)? = null
) {
    NSEmptyState(
        title = "No Sleep Data Recorded",
        message = "Track your sleep patterns to understand how rest affects your mood and well-being. Better sleep leads to better days.",
        action = onAddSleepEntry,
        actionTitle = "Log Sleep",
        modifier = modifier
    )
}

/**
 * NSEmptyState variant specifically for medication tracking empty states
 * Pre-configured with medication-related messaging and styling
 * 
 * @param modifier Modifier to be applied to the empty state container
 * @param onAddMedication Callback for when user wants to add their first medication
 */
@Composable
fun NSMedicationEmptyState(
    modifier: Modifier = Modifier,
    onAddMedication: (() -> Unit)? = null
) {
    NSEmptyState(
        title = "No Medications Added",
        message = "Add your medications to track adherence and monitor how they affect your mood and overall well-being.",
        action = onAddMedication,
        actionTitle = "Add Medication",
        modifier = modifier
    )
}

/**
 * NSEmptyState variant for search results
 * Pre-configured with search-related messaging
 * 
 * @param searchQuery The search term that produced no results
 * @param modifier Modifier to be applied to the empty state container
 * @param onClearSearch Optional callback to clear the search
 */
@Composable
fun NSSearchEmptyState(
    searchQuery: String,
    modifier: Modifier = Modifier,
    onClearSearch: (() -> Unit)? = null
) {
    NSEmptyState(
        title = "No Results Found",
        message = "We couldn't find anything matching \"$searchQuery\". Try adjusting your search terms or check the spelling.",
        action = onClearSearch,
        actionTitle = "Clear Search",
        modifier = modifier
    )
}

/**
 * NSEmptyState variant for network errors
 * Pre-configured with network-related messaging and retry functionality
 * 
 * @param modifier Modifier to be applied to the empty state container
 * @param onRetry Callback for when user wants to retry the failed action
 */
@Composable
fun NSNetworkErrorState(
    modifier: Modifier = Modifier,
    onRetry: (() -> Unit)? = null
) {
    NSEmptyState(
        title = "Connection Problem",
        message = "Unable to load your data. Please check your internet connection and try again.",
        action = onRetry,
        actionTitle = "Retry",
        modifier = modifier
    )
}

/**
 * NSEmptyState variant for loading states
 * Shows a minimal message without action button during data loading
 * 
 * @param message Custom loading message (optional)
 * @param modifier Modifier to be applied to the empty state container
 */
@Composable
fun NSLoadingState(
    modifier: Modifier = Modifier,
    message: String = "Loading your data..."
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(NSSpacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            color = DesignTokens.Colors.primary,
            modifier = Modifier.size(40.dp)
        )
        
        Spacer(modifier = Modifier.height(NSSpacing.md))
        
        Text(
            text = message,
            style = NSTypography.body,
            color = DesignTokens.Colors.textSecondary,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * NSEmptyState variant for general error states
 * Pre-configured with error messaging and retry functionality
 * 
 * @param title Custom error title (optional)
 * @param message Custom error message (optional)
 * @param modifier Modifier to be applied to the empty state container
 * @param onRetry Optional callback for retry action
 */
@Composable
fun NSErrorState(
    modifier: Modifier = Modifier,
    title: String = "Something Went Wrong",
    message: String = "We encountered an unexpected error. Please try again or contact support if the problem persists.",
    onRetry: (() -> Unit)? = null
) {
    NSEmptyState(
        title = title,
        message = message,
        action = onRetry,
        actionTitle = "Try Again",
        modifier = modifier
    )
}

/**
 * NSEmptyState variant for insights when insufficient data exists
 * Pre-configured for analytics and insights empty states
 * 
 * @param dataType The type of data needed (e.g., "mood entries", "sleep data")
 * @param minDataRequired Minimum amount of data needed for insights
 * @param modifier Modifier to be applied to the empty state container
 * @param onAddData Optional callback to encourage data entry
 */
@Composable
fun NSInsufficientDataState(
    dataType: String,
    minDataRequired: String,
    modifier: Modifier = Modifier,
    onAddData: (() -> Unit)? = null
) {
    NSEmptyState(
        title = "Not Enough Data",
        message = "We need at least $minDataRequired of $dataType to generate meaningful insights for your mental health journey.",
        action = onAddData,
        actionTitle = "Add Data",
        modifier = modifier
    )
}