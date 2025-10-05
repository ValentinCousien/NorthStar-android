package com.vcousien.northstar.core.designsystem

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * NorthStar Spacing System
 * Based on the iOS implementation with Android-specific adaptations
 */
object NSSpacing {
    
    /**
     * Base spacing units matching iOS implementation
     */
    val xxs: Dp = 2.dp
    val xs: Dp = 4.dp
    val sm: Dp = 8.dp
    val md: Dp = 16.dp
    val lg: Dp = 24.dp
    val xl: Dp = 32.dp
    val xxl: Dp = 48.dp
    val xxxl: Dp = 64.dp
    
    /**
     * Screen edge spacing - standard margin for screen content
     */
    val screenEdge: Dp = 16.dp
    
    /**
     * Default spacing between elements in stacks/lists
     */
    val stackDefault: Dp = 12.dp
    
    /**
     * Corner radius definitions for different component types
     */
    object CornerRadius {
        val sm: Dp = 4.dp
        val md: Dp = 8.dp
        val lg: Dp = 16.dp
        val xl: Dp = 24.dp
        val pill: Dp = 999.dp // For pill-shaped components
        
        /**
         * Get RoundedCornerShape for the given radius
         */
        fun shape(radius: Dp): Shape = RoundedCornerShape(radius)
        
        /**
         * Predefined shapes for common use cases
         */
        val smallShape: Shape = RoundedCornerShape(sm)
        val mediumShape: Shape = RoundedCornerShape(md)
        val largeShape: Shape = RoundedCornerShape(lg)
        val extraLargeShape: Shape = RoundedCornerShape(xl)
        val pillShape: Shape = RoundedCornerShape(pill)
    }
    
    /**
     * Common padding patterns
     */
    object Padding {
        // Screen-level padding
        val screen = PaddingValues(screenEdge)
        val screenHorizontal = PaddingValues(horizontal = screenEdge)
        val screenVertical = PaddingValues(vertical = screenEdge)
        
        // Card padding
        val card = PaddingValues(md)
        val cardSmall = PaddingValues(sm)
        val cardLarge = PaddingValues(lg)
        
        // Button padding
        val button = PaddingValues(horizontal = lg, vertical = sm)
        val buttonSmall = PaddingValues(horizontal = md, vertical = xs)
        val buttonLarge = PaddingValues(horizontal = xl, vertical = md)
        
        // List item padding
        val listItem = PaddingValues(horizontal = md, vertical = sm)
        val listItemLarge = PaddingValues(horizontal = md, vertical = md)
        
        // Dialog padding
        val dialog = PaddingValues(lg)
        val dialogContent = PaddingValues(horizontal = lg, vertical = md)
    }
    
    /**
     * Component-specific spacing constants
     */
    object Component {
        // Icon spacing
        val iconPadding: Dp = xs
        val iconMargin: Dp = sm
        
        // Form field spacing
        val fieldSpacing: Dp = md
        val fieldPadding: Dp = sm
        
        // Divider spacing
        val dividerMargin: Dp = md
        
        // Tab spacing
        val tabPadding: Dp = md
        val tabSpacing: Dp = lg
        
        // Bottom sheet spacing
        val bottomSheetHandle: Dp = xs
        val bottomSheetContent: Dp = md
        
        // FAB margin from edges
        val fabMargin: Dp = md
        
        // Snackbar spacing
        val snackbarMargin: Dp = md
        
        // Chip spacing
        val chipSpacing: Dp = xs
        val chipPadding: Dp = sm
    }
    
    /**
     * Layout-specific spacing
     */
    object Layout {
        // Section spacing in scrollable content
        val sectionSpacing: Dp = lg
        val sectionPadding: Dp = md
        
        // Header spacing
        val headerHeight: Dp = 56.dp
        val headerPadding: Dp = md
        
        // Toolbar height
        val toolbarHeight: Dp = 56.dp
        
        // Navigation bar height
        val navigationBarHeight: Dp = 80.dp
        
        // Status bar spacing consideration
        val statusBarPadding: Dp = 24.dp
        
        // Safe area insets (for content that needs to avoid system UI)
        val safeAreaTop: Dp = 44.dp
        val safeAreaBottom: Dp = 34.dp
        val safeAreaHorizontal: Dp = 0.dp
    }
}

/**
 * Convenience functions for common spacing operations
 */

/**
 * Get spacing value by name
 */
fun getSpacing(size: String): Dp = when (size.lowercase()) {
    "xxs" -> NSSpacing.xxs
    "xs" -> NSSpacing.xs
    "sm" -> NSSpacing.sm
    "md" -> NSSpacing.md
    "lg" -> NSSpacing.lg
    "xl" -> NSSpacing.xl
    "xxl" -> NSSpacing.xxl
    "xxxl" -> NSSpacing.xxxl
    "screen" -> NSSpacing.screenEdge
    "stack" -> NSSpacing.stackDefault
    else -> NSSpacing.md
}

/**
 * Get corner radius by name
 */
fun getCornerRadius(size: String): Dp = when (size.lowercase()) {
    "sm", "small" -> NSSpacing.CornerRadius.sm
    "md", "medium" -> NSSpacing.CornerRadius.md
    "lg", "large" -> NSSpacing.CornerRadius.lg
    "xl", "extralarge" -> NSSpacing.CornerRadius.xl
    "pill" -> NSSpacing.CornerRadius.pill
    else -> NSSpacing.CornerRadius.md
}

/**
 * Get shape by name
 */
fun getShape(size: String): Shape = when (size.lowercase()) {
    "sm", "small" -> NSSpacing.CornerRadius.smallShape
    "md", "medium" -> NSSpacing.CornerRadius.mediumShape
    "lg", "large" -> NSSpacing.CornerRadius.largeShape
    "xl", "extralarge" -> NSSpacing.CornerRadius.extraLargeShape
    "pill" -> NSSpacing.CornerRadius.pillShape
    else -> NSSpacing.CornerRadius.mediumShape
}

/**
 * Create custom padding with symmetric values
 */
fun symmetricPadding(horizontal: Dp = 0.dp, vertical: Dp = 0.dp): PaddingValues =
    PaddingValues(horizontal = horizontal, vertical = vertical)

/**
 * Create custom padding with all sides
 */
fun allSidesPadding(all: Dp): PaddingValues = PaddingValues(all)

/**
 * Create custom padding with individual sides
 */
fun customPadding(
    start: Dp = 0.dp,
    top: Dp = 0.dp,
    end: Dp = 0.dp,
    bottom: Dp = 0.dp
): PaddingValues = PaddingValues(start = start, top = top, end = end, bottom = bottom)

/**
 * Extension functions for Dp calculations
 */
operator fun Dp.times(multiplier: Float): Dp = Dp(value * multiplier)
operator fun Dp.times(multiplier: Int): Dp = Dp(value * multiplier)
operator fun Dp.div(divisor: Float): Dp = Dp(value / divisor)
operator fun Dp.div(divisor: Int): Dp = Dp(value / divisor)
