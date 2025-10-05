package com.vcousien.northstar.core.designsystem.extensions

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ripple
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.vcousien.northstar.core.designsystem.DesignTokens
import com.vcousien.northstar.core.designsystem.NSSpacing

/**
 * Compose-specific utility extensions for common UI patterns
 * 
 * These extensions provide convenient ways to apply common styling
 * and interaction patterns that are frequently used throughout
 * the NorthStar app.
 */

/**
 * Make a composable clickable with ripple effect and proper interaction handling
 */
@Composable
fun Modifier.clickableWithRipple(
    enabled: Boolean = true,
    bounded: Boolean = true,
    rippleColor: Color = MaterialTheme.colorScheme.primary,
    onClick: () -> Unit
): Modifier = this.clickable(
    enabled = enabled,
    interactionSource = remember { MutableInteractionSource() },
    indication = ripple(
        bounded = bounded,
        color = rippleColor
    ),
    onClick = onClick
)

/**
 * Apply conditional modifier based on a boolean condition
 */
inline fun Modifier.conditional(
    condition: Boolean,
    modifier: Modifier.() -> Modifier
): Modifier = if (condition) then(modifier()) else this

/**
 * Apply different modifiers based on a boolean condition
 */
inline fun Modifier.conditional(
    condition: Boolean,
    ifTrue: Modifier.() -> Modifier,
    ifFalse: Modifier.() -> Modifier = { this }
): Modifier = if (condition) then(ifTrue()) else then(ifFalse())

/**
 * Apply standard content padding (screen edge padding)
 */
fun Modifier.withContentPadding(): Modifier = this
    .padding(horizontal = NSSpacing.screenEdge)

/**
 * Apply standard vertical spacing between elements
 */
fun Modifier.withVerticalSpacing(): Modifier = this
    .padding(vertical = NSSpacing.stackDefault)

/**
 * Apply standard horizontal spacing between elements
 */
fun Modifier.withHorizontalSpacing(): Modifier = this
    .padding(horizontal = NSSpacing.md)

/**
 * Make a container that centers its content
 */
fun Modifier.centerContent(): Modifier = this
    .fillMaxSize()
    .wrapContentSize(Alignment.Center)

/**
 * Apply a subtle border for containers
 */
@Composable
fun Modifier.withSubtleBorder(
    color: Color = DesignTokens.Colors.divider,
    width: Dp = 1.dp,
    shape: Shape = RoundedCornerShape(NSSpacing.CornerRadius.md)
): Modifier = this.border(width, color, shape)

/**
 * Apply focus indicator styling
 */
@Composable
fun Modifier.withFocusIndicator(
    isFocused: Boolean,
    color: Color = DesignTokens.Colors.primary
): Modifier = conditional(isFocused) {
    border(
        width = 2.dp,
        color = color,
        shape = RoundedCornerShape(NSSpacing.CornerRadius.md)
    )
}

/**
 * Apply selection indicator styling
 */
@Composable
fun Modifier.withSelectionIndicator(
    isSelected: Boolean,
    selectedColor: Color = DesignTokens.Colors.primary,
    unselectedColor: Color = DesignTokens.Colors.divider
): Modifier = this.border(
    width = if (isSelected) 2.dp else 1.dp,
    color = if (isSelected) selectedColor else unselectedColor,
    shape = RoundedCornerShape(NSSpacing.CornerRadius.md)
)

/**
 * Apply error indicator styling
 */
@Composable
fun Modifier.withErrorIndicator(
    hasError: Boolean
): Modifier = conditional(hasError) {
    border(
        width = 1.dp,
        color = DesignTokens.Colors.error,
        shape = RoundedCornerShape(NSSpacing.CornerRadius.md)
    )
}

/**
 * Apply a circular shape with optional background
 */
fun Modifier.asCircle(
    backgroundColor: Color? = null
): Modifier = this
    .clip(CircleShape)
    .conditional(backgroundColor != null) {
        background(backgroundColor!!, CircleShape)
    }

/**
 * Apply minimum touch target size for accessibility
 */
fun Modifier.withMinTouchTarget(
    minSize: Dp = 48.dp
): Modifier = this.sizeIn(minWidth = minSize, minHeight = minSize)

/**
 * Apply standard elevation shadow
 */
fun Modifier.withElevation(
    elevation: Dp = DesignTokens.Elevation.CARD.dp
): Modifier = this
    // Note: In Material3, elevation is typically handled by the component itself
    // This is mainly for custom components that need elevation styling
    .background(
        Color.Black.copy(alpha = 0.1f),
        RoundedCornerShape(NSSpacing.CornerRadius.md)
    )

/**
 * Apply glass morphism effect
 */
fun Modifier.withGlassMorphism(
    alpha: Float = 0.8f,
    blurRadius: Dp = 20.dp
): Modifier = this
    .background(
        Color.White.copy(alpha = alpha),
        RoundedCornerShape(NSSpacing.CornerRadius.md)
    )
    .border(
        1.dp,
        Color.White.copy(alpha = 0.2f),
        RoundedCornerShape(NSSpacing.CornerRadius.md)
    )

/**
 * Convert dp to px for cases where pixel values are needed
 */
@Composable
fun Dp.toPx(): Float {
    return with(LocalDensity.current) { this@toPx.toPx() }
}

/**
 * Convert px to dp for cases where dp values are needed from pixel measurements
 */
@Composable
fun Float.toDp(): Dp {
    return with(LocalDensity.current) { this@toDp.toDp() }
}

/**
 * Apply animated background color change
 */
@Composable
fun Modifier.withAnimatedBackground(
    targetColor: Color,
    // Note: Animation would be implemented with AnimatedVisibility or animateColorAsState
    // This is a placeholder for the modifier structure
): Modifier = this.background(targetColor)
