package com.vcousien.northstar.core.designsystem.extensions

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.vcousien.northstar.core.designsystem.DesignTokens
import com.vcousien.northstar.core.designsystem.NSSpacing
import com.vcousien.northstar.core.designsystem.NSTypography

/**
 * Common utility composables used throughout the NorthStar app
 * 
 * These composables provide reusable UI patterns and components that
 * complement the main design system components and help maintain consistency.
 */

/**
 * Enum classes for component configuration
 */
enum class StatusType { Success, Error, Warning, Info }
enum class IndicatorSize { Small, Medium, Large }
enum class BadgeStyle { Default, Rounded, Square }
enum class AvatarSize { Small, Medium, Large, ExtraLarge }

/**
 * Status indicator with icon and color
 */
@Composable
fun StatusIndicator(
    status: StatusType,
    modifier: Modifier = Modifier,
    size: IndicatorSize = IndicatorSize.Medium
) {
    val (icon, color) = when (status) {
        StatusType.Success -> Icons.Default.CheckCircle to DesignTokens.Colors.success
        StatusType.Error -> Icons.Default.Error to DesignTokens.Colors.error
        StatusType.Warning -> Icons.Default.Warning to DesignTokens.Colors.warning
        StatusType.Info -> Icons.Default.Info to DesignTokens.Colors.info
    }
    
    val iconSize = when (size) {
        IndicatorSize.Small -> 16.dp
        IndicatorSize.Medium -> 24.dp
        IndicatorSize.Large -> 32.dp
    }
    
    Icon(
        imageVector = icon,
        contentDescription = status.name,
        modifier = modifier.size(iconSize),
        tint = color
    )
}

/**
 * Badge component for displaying counts or status
 */
@Composable
fun NSBadge(
    text: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = DesignTokens.Colors.primary,
    textColor: Color = Color.White,
    style: BadgeStyle = BadgeStyle.Default
) {
    val shape = when (style) {
        BadgeStyle.Default -> CircleShape
        BadgeStyle.Rounded -> RoundedCornerShape(NSSpacing.CornerRadius.sm)
        BadgeStyle.Square -> RoundedCornerShape(2.dp)
    }
    
    Box(
        modifier = modifier
            .clip(shape)
            .background(backgroundColor)
            .padding(
                horizontal = if (style == BadgeStyle.Default) NSSpacing.xs else NSSpacing.sm,
                vertical = NSSpacing.xxs
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = NSTypography.caption,
            color = textColor,
            fontWeight = FontWeight.Medium,
            maxLines = 1
        )
    }
}

/**
 * Chip component for tags or selectable items
 */
@Composable
fun NSChip(
    label: String,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    onSelectionChanged: ((Boolean) -> Unit)? = null,
    leadingIcon: ImageVector? = null,
    backgroundColor: Color? = null,
    textColor: Color? = null
) {
    val chipColors = if (isSelected) {
        AssistChipDefaults.assistChipColors(
            containerColor = backgroundColor ?: DesignTokens.Colors.primary,
            labelColor = textColor ?: Color.White
        )
    } else {
        AssistChipDefaults.assistChipColors(
            containerColor = backgroundColor ?: DesignTokens.Colors.surface,
            labelColor = textColor ?: DesignTokens.Colors.textPrimary
        )
    }
    
    AssistChip(
        onClick = { onSelectionChanged?.invoke(!isSelected) },
        label = {
            Text(
                text = label,
                style = NSTypography.caption,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        leadingIcon = leadingIcon?.let { icon ->
            {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
        },
        modifier = modifier,
        colors = chipColors,
        border = if (!isSelected) {
            BorderStroke(
                width = 1.dp,
                color = DesignTokens.Colors.divider
            )
        } else null
    )
}

/**
 * Avatar component for user profiles or entities
 */
@Composable
fun NSAvatar(
    initials: String,
    modifier: Modifier = Modifier,
    size: AvatarSize = AvatarSize.Medium,
    backgroundColor: Color = DesignTokens.Colors.primary,
    textColor: Color = Color.White
) {
    val avatarSize = when (size) {
        AvatarSize.Small -> 32.dp
        AvatarSize.Medium -> 48.dp
        AvatarSize.Large -> 64.dp
        AvatarSize.ExtraLarge -> 80.dp
    }
    
    val textStyle = when (size) {
        AvatarSize.Small -> NSTypography.caption
        AvatarSize.Medium -> NSTypography.body
        AvatarSize.Large -> NSTypography.heading3
        AvatarSize.ExtraLarge -> NSTypography.heading2
    }
    
    Box(
        modifier = modifier
            .size(avatarSize)
            .clip(CircleShape)
            .withCardBackground(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials.take(2).uppercase(),
            style = textStyle,
            color = textColor,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Progress indicator with label
 */
@Composable
fun NSProgressIndicator(
    progress: Float,
    modifier: Modifier = Modifier,
    label: String? = null,
    showPercentage: Boolean = false,
    color: Color = DesignTokens.Colors.primary
) {
    Column(modifier = modifier.fillMaxWidth()) {
        if (label != null || showPercentage) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (label != null) {
                    Text(
                        text = label,
                        style = NSTypography.caption,
                        color = DesignTokens.Colors.textSecondary
                    )
                }
                if (showPercentage) {
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        style = NSTypography.caption,
                        color = DesignTokens.Colors.textSecondary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            VerticalSpacerXS()
        }
        
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = color.copy(alpha = 0.2f)
        )
    }
}

/**
 * Empty state placeholder
 */
@Composable
fun EmptyStatePlaceholder(
    title: String,
    subtitle: String? = null,
    icon: ImageVector? = null,
    modifier: Modifier = Modifier,
    action: (@Composable () -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(NSSpacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = DesignTokens.Colors.textSecondary.copy(alpha = 0.6f)
            )
            VerticalSpacerMD()
        }
        
        Text(
            text = title,
            style = NSTypography.heading3,
            color = DesignTokens.Colors.textPrimary,
            textAlign = TextAlign.Center
        )
        
        if (subtitle != null) {
            VerticalSpacerXS()
            Text(
                text = subtitle,
                style = NSTypography.body,
                color = DesignTokens.Colors.textSecondary,
                textAlign = TextAlign.Center
            )
        }
        
        if (action != null) {
            VerticalSpacerLG()
            action()
        }
    }
}

/**
 * Loading skeleton for placeholder content
 */
@Composable
fun SkeletonBox(
    modifier: Modifier = Modifier,
    shape: androidx.compose.ui.graphics.Shape = RoundedCornerShape(NSSpacing.CornerRadius.sm)
) {
    Box(
        modifier = modifier
            .clip(shape)
            .shimmer()
    )
}

/**
 * Mood level indicator
 */
@Composable
fun MoodIndicator(
    level: Int,
    modifier: Modifier = Modifier,
    size: IndicatorSize = IndicatorSize.Medium,
    showLabel: Boolean = false
) {
    val indicatorSize = when (size) {
        IndicatorSize.Small -> 12.dp
        IndicatorSize.Medium -> 16.dp
        IndicatorSize.Large -> 24.dp
    }
    
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(NSSpacing.xs)
    ) {
        Box(
            modifier = Modifier
                .size(indicatorSize)
                .clip(CircleShape)
                .withMoodBackground(level)
        )
        
        if (showLabel) {
            Text(
                text = DesignTokens.Mood.getCategoryName(level),
                style = NSTypography.caption,
                color = DesignTokens.Colors.textSecondary,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/**
 * Notification dot for indicating unread items
 */
@Composable
fun NotificationDot(
    modifier: Modifier = Modifier,
    size: Dp = 8.dp,
    color: Color = DesignTokens.Colors.error
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(color)
    )
}

/**
 * Section header with optional action
 */
@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    action: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = NSTypography.heading3,
                color = DesignTokens.Colors.textPrimary
            )
            if (subtitle != null) {
                VerticalSpacerXS()
                Text(
                    text = subtitle,
                    style = NSTypography.body,
                    color = DesignTokens.Colors.textSecondary
                )
            }
        }
        
        if (action != null) {
            HorizontalSpacerMD()
            action()
        }
    }
}

/**
 * Info card for displaying tips or information
 */
@Composable
fun InfoCard(
    message: String,
    modifier: Modifier = Modifier,
    type: StatusType = StatusType.Info,
    onDismiss: (() -> Unit)? = null
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when (type) {
                StatusType.Success -> DesignTokens.Colors.success.copy(alpha = 0.1f)
                StatusType.Error -> DesignTokens.Colors.error.copy(alpha = 0.1f)
                StatusType.Warning -> DesignTokens.Colors.warning.copy(alpha = 0.1f)
                StatusType.Info -> DesignTokens.Colors.info.copy(alpha = 0.1f)
            }
        )
    ) {
        Row(
            modifier = Modifier.padding(NSSpacing.md),
            horizontalArrangement = Arrangement.spacedBy(NSSpacing.sm),
            verticalAlignment = Alignment.Top
        ) {
            StatusIndicator(
                status = type,
                size = IndicatorSize.Small
            )
            
            Text(
                text = message,
                style = NSTypography.body,
                color = DesignTokens.Colors.textPrimary,
                modifier = Modifier.weight(1f)
            )
            
            if (onDismiss != null) {
                TextButton(
                    onClick = onDismiss,
                    contentPadding = PaddingValues(NSSpacing.xs)
                ) {
                    Text(
                        text = "Dismiss",
                        style = NSTypography.caption
                    )
                }
            }
        }
    }
}

/**
 * Expandable section with collapsible content
 */
@Composable
fun ExpandableSection(
    title: String,
    modifier: Modifier = Modifier,
    initiallyExpanded: Boolean = false,
    content: @Composable () -> Unit
) {
    var isExpanded by remember { mutableStateOf(initiallyExpanded) }
    
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickableWithRipple { isExpanded = !isExpanded }
                .padding(vertical = NSSpacing.sm),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = NSTypography.subtitle,
                color = DesignTokens.Colors.textPrimary
            )
            
            Icon(
                imageVector = if (isExpanded) {
                    Icons.Default.ExpandLess
                } else {
                    Icons.Default.ExpandMore
                },
                contentDescription = if (isExpanded) "Collapse" else "Expand",
                tint = DesignTokens.Colors.textSecondary
            )
        }
        
        AnimatedVisibility(visible = isExpanded) {
            Column {
                VerticalSpacerSM()
                content()
            }
        }
    }
}
