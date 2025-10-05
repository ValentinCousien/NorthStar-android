package com.vcousien.northstar.core.designsystem.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.vcousien.northstar.core.designsystem.DesignTokens
import com.vcousien.northstar.core.designsystem.NSSpacing
import com.vcousien.northstar.core.designsystem.NSTypography

/**
 * NSPrimaryButton - Standardized primary button component for key actions
 * 
 * A reusable primary button component that provides consistent styling and behavior
 * across the NorthStar app. Used for the most important actions in each screen.
 * 
 * Features:
 * - Consistent primary brand styling with elevation
 * - Optional leading icon support
 * - Disabled state handling with visual feedback
 * - Proper accessibility semantics
 * - Configurable sizing options (compact, standard, large)
 * - Full-width and fixed-width variants
 * - Loading state support
 * - Matches iOS NSPrimaryButton implementation
 * 
 * Design Guidelines:
 * - Use for primary actions (Save, Continue, Add, Submit)
 * - Limit to one primary button per screen/section
 * - Use secondary buttons for less important actions
 * - Provide clear, action-oriented button text
 * 
 * @param title The button text to display
 * @param onClick Callback invoked when the button is clicked
 * @param modifier Modifier to be applied to the button
 * @param icon Optional icon to display before the text
 * @param enabled Whether the button is enabled for interaction (default: true)
 * @param loading Whether to show loading indicator instead of content (default: false)
 * @param size The size variant of the button (default: Standard)
 * @param width The width configuration of the button (default: FillMax)
 * @param contentDescription Accessibility description for screen readers
 */
@Composable
fun NSPrimaryButton(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    loading: Boolean = false,
    size: NSButtonSize = NSButtonSize.Standard,
    width: NSButtonWidth = NSButtonWidth.FillMax,
    contentDescription: String? = null
) {
    val buttonColors = ButtonDefaults.buttonColors(
        containerColor = DesignTokens.Colors.primary,
        contentColor = DesignTokens.Colors.onPrimary,
        disabledContainerColor = DesignTokens.Colors.primary.copy(alpha = DesignTokens.Alpha.DISABLED),
        disabledContentColor = DesignTokens.Colors.onPrimary.copy(alpha = DesignTokens.Alpha.DISABLED)
    )
    
    val buttonModifier = modifier
        .then(
            when (width) {
                NSButtonWidth.FillMax -> Modifier.fillMaxWidth()
                NSButtonWidth.WrapContent -> Modifier.wrapContentWidth()
                is NSButtonWidth.Fixed -> Modifier.width(width.width)
            }
        )
        .then(
            when (size) {
                NSButtonSize.Compact -> Modifier.height(40.dp)
                NSButtonSize.Standard -> Modifier.height(48.dp)
                NSButtonSize.Large -> Modifier.height(56.dp)
            }
        )
        .let { mod ->
            contentDescription?.let { 
                mod.semantics { this.contentDescription = it }
            } ?: mod
        }
    
    Button(
        onClick = onClick,
        enabled = enabled && !loading,
        colors = buttonColors,
        shape = when (size) {
            NSButtonSize.Compact -> RoundedCornerShape(NSSpacing.CornerRadius.sm)
            NSButtonSize.Standard -> RoundedCornerShape(NSSpacing.CornerRadius.md)
            NSButtonSize.Large -> RoundedCornerShape(NSSpacing.CornerRadius.lg)
        },
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = if (enabled && !loading) 4.dp else 0.dp,
            pressedElevation = 2.dp,
            disabledElevation = 0.dp
        ),
        contentPadding = when (size) {
            NSButtonSize.Compact -> PaddingValues(
                horizontal = NSSpacing.md,
                vertical = NSSpacing.xs
            )
            NSButtonSize.Standard -> PaddingValues(
                horizontal = NSSpacing.md,
                vertical = NSSpacing.sm
            )
            NSButtonSize.Large -> PaddingValues(
                horizontal = NSSpacing.lg,
                vertical = NSSpacing.md
            )
        },
        modifier = buttonModifier
    ) {
        if (loading) {
            CircularProgressIndicator(
                color = DesignTokens.Colors.onPrimary,
                strokeWidth = 2.dp,
                modifier = Modifier.size(
                    when (size) {
                        NSButtonSize.Compact -> 16.dp
                        NSButtonSize.Standard -> 20.dp
                        NSButtonSize.Large -> 24.dp
                    }
                )
            )
        } else {
            Row(
                horizontalArrangement = Arrangement.spacedBy(NSSpacing.sm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null, // Decorative, text provides context
                        modifier = Modifier.size(
                            when (size) {
                                NSButtonSize.Compact -> 16.dp
                                NSButtonSize.Standard -> 20.dp
                                NSButtonSize.Large -> 24.dp
                            }
                        )
                    )
                }
                
                Text(
                    text = title,
                    style = when (size) {
                        NSButtonSize.Compact -> NSTypography.caption.copy(fontWeight = FontWeight.Bold)
                        NSButtonSize.Standard -> NSTypography.bodyBold
                        NSButtonSize.Large -> NSTypography.subtitle.copy(fontWeight = FontWeight.Bold)
                    }
                )
            }
        }
    }
}

/**
 * Button size variants for different use cases
 */
enum class NSButtonSize {
    /** Compact button for toolbars and tight spaces (40dp height) */
    Compact,
    /** Standard button for most use cases (48dp height) */
    Standard,
    /** Large button for prominent actions (56dp height) */
    Large
}

/**
 * Button width configuration options
 */
sealed class NSButtonWidth {
    /** Button takes full available width */
    object FillMax : NSButtonWidth()
    /** Button wraps to content width */
    object WrapContent : NSButtonWidth()
    /** Button has fixed width */
    data class Fixed(val width: Dp) : NSButtonWidth()
}

/**
 * NSPrimaryButton variant for destructive actions (delete, remove, etc.)
 * Uses error color scheme to indicate dangerous operations
 * 
 * @param title The button text to display
 * @param onClick Callback invoked when the button is clicked
 * @param modifier Modifier to be applied to the button
 * @param icon Optional icon to display before the text
 * @param enabled Whether the button is enabled for interaction (default: true)
 * @param loading Whether to show loading indicator (default: false)
 * @param size The size variant of the button (default: Standard)
 * @param width The width configuration of the button (default: FillMax)
 * @param contentDescription Accessibility description for screen readers
 */
@Composable
fun NSDestructiveButton(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    loading: Boolean = false,
    size: NSButtonSize = NSButtonSize.Standard,
    width: NSButtonWidth = NSButtonWidth.FillMax,
    contentDescription: String? = null
) {
    val buttonColors = ButtonDefaults.buttonColors(
        containerColor = DesignTokens.Colors.error,
        contentColor = DesignTokens.Colors.onError,
        disabledContainerColor = DesignTokens.Colors.error.copy(alpha = DesignTokens.Alpha.DISABLED),
        disabledContentColor = DesignTokens.Colors.onError.copy(alpha = DesignTokens.Alpha.DISABLED)
    )
    
    val buttonModifier = modifier
        .then(
            when (width) {
                NSButtonWidth.FillMax -> Modifier.fillMaxWidth()
                NSButtonWidth.WrapContent -> Modifier.wrapContentWidth()
                is NSButtonWidth.Fixed -> Modifier.width(width.width)
            }
        )
        .then(
            when (size) {
                NSButtonSize.Compact -> Modifier.height(40.dp)
                NSButtonSize.Standard -> Modifier.height(48.dp)
                NSButtonSize.Large -> Modifier.height(56.dp)
            }
        )
        .let { mod ->
            contentDescription?.let { 
                mod.semantics { this.contentDescription = it }
            } ?: mod
        }
    
    Button(
        onClick = onClick,
        enabled = enabled && !loading,
        colors = buttonColors,
        shape = when (size) {
            NSButtonSize.Compact -> RoundedCornerShape(NSSpacing.CornerRadius.sm)
            NSButtonSize.Standard -> RoundedCornerShape(NSSpacing.CornerRadius.md)
            NSButtonSize.Large -> RoundedCornerShape(NSSpacing.CornerRadius.lg)
        },
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = if (enabled && !loading) 4.dp else 0.dp,
            pressedElevation = 2.dp,
            disabledElevation = 0.dp
        ),
        contentPadding = when (size) {
            NSButtonSize.Compact -> PaddingValues(
                horizontal = NSSpacing.md,
                vertical = NSSpacing.xs
            )
            NSButtonSize.Standard -> PaddingValues(
                horizontal = NSSpacing.md,
                vertical = NSSpacing.sm
            )
            NSButtonSize.Large -> PaddingValues(
                horizontal = NSSpacing.lg,
                vertical = NSSpacing.md
            )
        },
        modifier = buttonModifier
    ) {
        if (loading) {
            CircularProgressIndicator(
                color = DesignTokens.Colors.onError,
                strokeWidth = 2.dp,
                modifier = Modifier.size(
                    when (size) {
                        NSButtonSize.Compact -> 16.dp
                        NSButtonSize.Standard -> 20.dp
                        NSButtonSize.Large -> 24.dp
                    }
                )
            )
        } else {
            Row(
                horizontalArrangement = Arrangement.spacedBy(NSSpacing.sm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(
                            when (size) {
                                NSButtonSize.Compact -> 16.dp
                                NSButtonSize.Standard -> 20.dp
                                NSButtonSize.Large -> 24.dp
                            }
                        )
                    )
                }
                
                Text(
                    text = title,
                    style = when (size) {
                        NSButtonSize.Compact -> NSTypography.caption.copy(fontWeight = FontWeight.Bold)
                        NSButtonSize.Standard -> NSTypography.bodyBold
                        NSButtonSize.Large -> NSTypography.subtitle.copy(fontWeight = FontWeight.Bold)
                    }
                )
            }
        }
    }
}

/**
 * NSSecondaryButton - Secondary button variant with outlined style
 * Used for less important actions alongside primary buttons
 * 
 * @param title The button text to display
 * @param onClick Callback invoked when the button is clicked
 * @param modifier Modifier to be applied to the button
 * @param icon Optional icon to display before the text
 * @param enabled Whether the button is enabled for interaction (default: true)
 * @param loading Whether to show loading indicator (default: false)
 * @param size The size variant of the button (default: Standard)
 * @param width The width configuration of the button (default: WrapContent)
 * @param contentDescription Accessibility description for screen readers
 */
@Composable
fun NSSecondaryButton(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    loading: Boolean = false,
    size: NSButtonSize = NSButtonSize.Standard,
    width: NSButtonWidth = NSButtonWidth.WrapContent,
    contentDescription: String? = null
) {
    val buttonColors = ButtonDefaults.outlinedButtonColors(
        containerColor = Color.Transparent,
        contentColor = DesignTokens.Colors.primary,
        disabledContainerColor = Color.Transparent,
        disabledContentColor = DesignTokens.Colors.primary.copy(alpha = DesignTokens.Alpha.DISABLED)
    )
    
    val buttonModifier = modifier
        .then(
            when (width) {
                NSButtonWidth.FillMax -> Modifier.fillMaxWidth()
                NSButtonWidth.WrapContent -> Modifier.wrapContentWidth()
                is NSButtonWidth.Fixed -> Modifier.width(width.width)
            }
        )
        .then(
            when (size) {
                NSButtonSize.Compact -> Modifier.height(40.dp)
                NSButtonSize.Standard -> Modifier.height(48.dp)
                NSButtonSize.Large -> Modifier.height(56.dp)
            }
        )
        .let { mod ->
            contentDescription?.let { 
                mod.semantics { this.contentDescription = it }
            } ?: mod
        }
    
    OutlinedButton(
        onClick = onClick,
        enabled = enabled && !loading,
        colors = buttonColors,
        border = BorderStroke(
            width = 1.5.dp,
            color = if (enabled) DesignTokens.Colors.primary else DesignTokens.Colors.primary.copy(alpha = DesignTokens.Alpha.DISABLED)
        ),
        shape = when (size) {
            NSButtonSize.Compact -> RoundedCornerShape(NSSpacing.CornerRadius.sm)
            NSButtonSize.Standard -> RoundedCornerShape(NSSpacing.CornerRadius.md)
            NSButtonSize.Large -> RoundedCornerShape(NSSpacing.CornerRadius.lg)
        },
        contentPadding = when (size) {
            NSButtonSize.Compact -> PaddingValues(
                horizontal = NSSpacing.md,
                vertical = NSSpacing.xs
            )
            NSButtonSize.Standard -> PaddingValues(
                horizontal = NSSpacing.md,
                vertical = NSSpacing.sm
            )
            NSButtonSize.Large -> PaddingValues(
                horizontal = NSSpacing.lg,
                vertical = NSSpacing.md
            )
        },
        modifier = buttonModifier
    ) {
        if (loading) {
            CircularProgressIndicator(
                color = DesignTokens.Colors.primary,
                strokeWidth = 2.dp,
                modifier = Modifier.size(
                    when (size) {
                        NSButtonSize.Compact -> 16.dp
                        NSButtonSize.Standard -> 20.dp
                        NSButtonSize.Large -> 24.dp
                    }
                )
            )
        } else {
            Row(
                horizontalArrangement = Arrangement.spacedBy(NSSpacing.sm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(
                            when (size) {
                                NSButtonSize.Compact -> 16.dp
                                NSButtonSize.Standard -> 20.dp
                                NSButtonSize.Large -> 24.dp
                            }
                        )
                    )
                }
                
                Text(
                    text = title,
                    style = when (size) {
                        NSButtonSize.Compact -> NSTypography.caption.copy(fontWeight = FontWeight.Bold)
                        NSButtonSize.Standard -> NSTypography.bodyBold
                        NSButtonSize.Large -> NSTypography.subtitle.copy(fontWeight = FontWeight.Bold)
                    }
                )
            }
        }
    }
}

/**
 * NSTextButton - Text-only button variant for subtle actions
 * Used for tertiary actions and navigation
 * 
 * @param title The button text to display
 * @param onClick Callback invoked when the button is clicked
 * @param modifier Modifier to be applied to the button
 * @param icon Optional icon to display before the text
 * @param enabled Whether the button is enabled for interaction (default: true)
 * @param loading Whether to show loading indicator (default: false)
 * @param size The size variant of the button (default: Standard)
 * @param contentDescription Accessibility description for screen readers
 */
@Composable
fun NSTextButton(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    loading: Boolean = false,
    size: NSButtonSize = NSButtonSize.Standard,
    contentDescription: String? = null
) {
    val buttonColors = ButtonDefaults.textButtonColors(
        contentColor = DesignTokens.Colors.primary,
        disabledContentColor = DesignTokens.Colors.primary.copy(alpha = DesignTokens.Alpha.DISABLED)
    )
    
    val buttonModifier = modifier
        .wrapContentWidth()
        .then(
            when (size) {
                NSButtonSize.Compact -> Modifier.height(32.dp)
                NSButtonSize.Standard -> Modifier.height(40.dp)
                NSButtonSize.Large -> Modifier.height(48.dp)
            }
        )
        .let { mod ->
            contentDescription?.let { 
                mod.semantics { this.contentDescription = it }
            } ?: mod
        }
    
    TextButton(
        onClick = onClick,
        enabled = enabled && !loading,
        colors = buttonColors,
        contentPadding = when (size) {
            NSButtonSize.Compact -> PaddingValues(horizontal = NSSpacing.sm)
            NSButtonSize.Standard -> PaddingValues(horizontal = NSSpacing.md)
            NSButtonSize.Large -> PaddingValues(horizontal = NSSpacing.lg)
        },
        modifier = buttonModifier
    ) {
        if (loading) {
            CircularProgressIndicator(
                color = DesignTokens.Colors.primary,
                strokeWidth = 2.dp,
                modifier = Modifier.size(
                    when (size) {
                        NSButtonSize.Compact -> 14.dp
                        NSButtonSize.Standard -> 16.dp
                        NSButtonSize.Large -> 20.dp
                    }
                )
            )
        } else {
            Row(
                horizontalArrangement = Arrangement.spacedBy(NSSpacing.xs),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(
                            when (size) {
                                NSButtonSize.Compact -> 14.dp
                                NSButtonSize.Standard -> 16.dp
                                NSButtonSize.Large -> 20.dp
                            }
                        )
                    )
                }
                
                Text(
                    text = title,
                    style = when (size) {
                        NSButtonSize.Compact -> NSTypography.caption.copy(fontWeight = FontWeight.Medium)
                        NSButtonSize.Standard -> NSTypography.body.copy(fontWeight = FontWeight.Medium)
                        NSButtonSize.Large -> NSTypography.subtitle.copy(fontWeight = FontWeight.Medium)
                    }
                )
            }
        }
    }
}