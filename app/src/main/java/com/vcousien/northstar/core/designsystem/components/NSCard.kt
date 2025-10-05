package com.vcousien.northstar.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.vcousien.northstar.core.designsystem.DesignTokens
import com.vcousien.northstar.core.designsystem.NSSpacing
import com.vcousien.northstar.ui.theme.northStarColors

/**
 * NSCard - Standardized card container component
 * 
 * A reusable card component that provides consistent styling and behavior
 * across the NorthStar app. Matches the iOS NSCard implementation.
 * 
 * Features:
 * - Consistent Material Design card styling
 * - Optional shadow with customizable elevation
 * - Customizable padding
 * - Full width layout with proper content alignment
 * - Uses NorthStar design system colors and spacing
 * 
 * @param modifier Modifier to be applied to the card
 * @param hasShadow Whether to apply shadow/elevation to the card (default: true)
 * @param padding Internal padding for the card content (default: NSSpacing.md)
 * @param backgroundColor Background color override (default: cardBackground from theme)
 * @param content The content to be displayed inside the card
 */
@Composable
fun NSCard(
    modifier: Modifier = Modifier,
    hasShadow: Boolean = true,
    padding: Dp = NSSpacing.md,
    backgroundColor: Color? = null,
    content: @Composable () -> Unit
) {
    val cardBackgroundColor = backgroundColor ?: MaterialTheme.northStarColors.cardBackground
    val elevation = if (hasShadow) DesignTokens.Elevation.CARD.dp else 0.dp
    
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(NSSpacing.CornerRadius.md),
        colors = CardDefaults.cardColors(
            containerColor = cardBackgroundColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = elevation
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding),
            contentAlignment = Alignment.TopStart
        ) {
            content()
        }
    }
}

/**
 * Alternative NSCard implementation using Box + shadow for more control
 * This version more closely matches the iOS implementation's shadow behavior
 */
@Composable
fun NSCardWithCustomShadow(
    modifier: Modifier = Modifier,
    hasShadow: Boolean = true,
    padding: Dp = NSSpacing.md,
    backgroundColor: Color? = null,
    content: @Composable () -> Unit
) {
    val cardBackgroundColor = backgroundColor ?: MaterialTheme.northStarColors.cardBackground
    val shadowModifier = if (hasShadow) {
        Modifier.shadow(
            elevation = 8.dp,
            shape = RoundedCornerShape(NSSpacing.CornerRadius.md),
            ambientColor = Color.Black.copy(alpha = 0.1f),
            spotColor = Color.Black.copy(alpha = 0.1f)
        )
    } else {
        Modifier
    }
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .then(shadowModifier)
            .background(
                color = cardBackgroundColor,
                shape = RoundedCornerShape(NSSpacing.CornerRadius.md)
            )
            .padding(padding),
        contentAlignment = Alignment.TopStart
    ) {
        content()
    }
}

/**
 * Compact NSCard variant with smaller padding
 * Useful for list items or condensed layouts
 */
@Composable
fun NSCardCompact(
    modifier: Modifier = Modifier,
    hasShadow: Boolean = true,
    backgroundColor: Color? = null,
    content: @Composable () -> Unit
) {
    NSCard(
        modifier = modifier,
        hasShadow = hasShadow,
        padding = NSSpacing.sm,
        backgroundColor = backgroundColor,
        content = content
    )
}

/**
 * Large NSCard variant with larger padding
 * Useful for main content areas or feature sections
 */
@Composable
fun NSCardLarge(
    modifier: Modifier = Modifier,
    hasShadow: Boolean = true,
    backgroundColor: Color? = null,
    content: @Composable () -> Unit
) {
    NSCard(
        modifier = modifier,
        hasShadow = hasShadow,
        padding = NSSpacing.lg,
        backgroundColor = backgroundColor,
        content = content
    )
}

/**
 * NSCard without shadow/elevation
 * Useful for sections that need card styling but should appear flat
 */
@Composable
fun NSCardFlat(
    modifier: Modifier = Modifier,
    padding: Dp = NSSpacing.md,
    backgroundColor: Color? = null,
    content: @Composable () -> Unit
) {
    NSCard(
        modifier = modifier,
        hasShadow = false,
        padding = padding,
        backgroundColor = backgroundColor,
        content = content
    )
}
