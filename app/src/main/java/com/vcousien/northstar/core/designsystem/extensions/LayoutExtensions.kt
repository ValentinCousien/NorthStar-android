package com.vcousien.northstar.core.designsystem.extensions

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.vcousien.northstar.core.designsystem.DesignTokens
import com.vcousien.northstar.core.designsystem.NSSpacing

/**
 * Layout utility functions and extensions for consistent spacing and arrangement
 * 
 * These utilities help maintain consistent layout patterns across the app
 * and provide convenient ways to handle common layout scenarios.
 */

/**
 * Standard vertical spacer using design system spacing
 */
@Composable
fun VerticalSpacer(height: Dp = NSSpacing.md) {
    Spacer(modifier = Modifier.height(height))
}

/**
 * Standard horizontal spacer using design system spacing
 */
@Composable
fun HorizontalSpacer(width: Dp = NSSpacing.md) {
    Spacer(modifier = Modifier.width(width))
}

/**
 * Extra small vertical spacer
 */
@Composable
fun VerticalSpacerXS() = VerticalSpacer(NSSpacing.xs)

/**
 * Small vertical spacer
 */
@Composable
fun VerticalSpacerSM() = VerticalSpacer(NSSpacing.sm)

/**
 * Medium vertical spacer (default)
 */
@Composable
fun VerticalSpacerMD() = VerticalSpacer(NSSpacing.md)

/**
 * Large vertical spacer
 */
@Composable
fun VerticalSpacerLG() = VerticalSpacer(NSSpacing.lg)

/**
 * Extra large vertical spacer
 */
@Composable
fun VerticalSpacerXL() = VerticalSpacer(NSSpacing.xl)

/**
 * Extra small horizontal spacer
 */
@Composable
fun HorizontalSpacerXS() = HorizontalSpacer(NSSpacing.xs)

/**
 * Small horizontal spacer
 */
@Composable
fun HorizontalSpacerSM() = HorizontalSpacer(NSSpacing.sm)

/**
 * Medium horizontal spacer (default)
 */
@Composable
fun HorizontalSpacerMD() = HorizontalSpacer(NSSpacing.md)

/**
 * Large horizontal spacer
 */
@Composable
fun HorizontalSpacerLG() = HorizontalSpacer(NSSpacing.lg)

/**
 * Extra large horizontal spacer
 */
@Composable
fun HorizontalSpacerXL() = HorizontalSpacer(NSSpacing.xl)

/**
 * Standard divider with design system styling
 */
@Composable
fun StandardDivider(
    modifier: Modifier = Modifier,
    color: Color = DesignTokens.Colors.divider,
    thickness: Dp = 1.dp
) {
    Divider(
        modifier = modifier,
        color = color,
        thickness = thickness
    )
}

/**
 * Subtle divider with reduced opacity
 */
@Composable
fun SubtleDivider(
    modifier: Modifier = Modifier,
    color: Color = DesignTokens.Colors.divider.copy(alpha = 0.5f)
) {
    StandardDivider(modifier = modifier, color = color)
}

/**
 * Section divider with extra spacing
 */
@Composable
fun SectionDivider(
    modifier: Modifier = Modifier,
    addVerticalPadding: Boolean = true
) {
    if (addVerticalPadding) {
        VerticalSpacerLG()
    }
    StandardDivider(modifier = modifier)
    if (addVerticalPadding) {
        VerticalSpacerLG()
    }
}

/**
 * Arrange children in a column with consistent spacing
 */
@Composable
fun SpacedColumn(
    modifier: Modifier = Modifier,
    spacing: Dp = NSSpacing.md,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(spacing),
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment,
        content = content
    )
}

/**
 * Arrange children in a row with consistent spacing
 */
@Composable
fun SpacedRow(
    modifier: Modifier = Modifier,
    spacing: Dp = NSSpacing.md,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(spacing),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = verticalAlignment,
        content = content
    )
}

/**
 * Content container with standard padding and background
 */
@Composable
fun ContentContainer(
    modifier: Modifier = Modifier,
    padding: PaddingValues = PaddingValues(NSSpacing.screenEdge),
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .withDefaultBackground()
            .padding(padding)
    ) {
        content()
    }
}

/**
 * Section container with title and content
 */
@Composable
fun SectionContainer(
    modifier: Modifier = Modifier,
    addDivider: Boolean = false,
    content: @Composable () -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        content()
        if (addDivider) {
            VerticalSpacerMD()
            StandardDivider()
        }
    }
}

/**
 * LazyListScope extension for adding items with separators
 */
fun <T> LazyListScope.itemsWithSeparator(
    items: List<T>,
    separator: @Composable () -> Unit = { StandardDivider() },
    key: ((item: T) -> Any)? = null,
    itemContent: @Composable (item: T) -> Unit
) {
    items(
        items = items,
        key = key
    ) { item ->
        itemContent(item)
        if (items.indexOf(item) < items.size - 1) {
            separator()
        }
    }
}

/**
 * LazyListScope extension for adding items with spacers
 */
fun <T> LazyListScope.itemsWithSpacing(
    items: List<T>,
    spacing: Dp = NSSpacing.md,
    key: ((item: T) -> Any)? = null,
    itemContent: @Composable (item: T) -> Unit
) {
    items(
        items = items,
        key = key
    ) { item ->
        itemContent(item)
        if (items.indexOf(item) < items.size - 1) {
            VerticalSpacer(spacing)
        }
    }
}

/**
 * Create a flex box layout with wrapping
 */
@Composable
fun FlexBox(
    modifier: Modifier = Modifier,
    horizontalSpacing: Dp = NSSpacing.sm,
    verticalSpacing: Dp = NSSpacing.sm,
    content: @Composable () -> Unit
) {
    // Note: This is a simplified version. For a full FlexBox implementation,
    // you would use FlowRow from androidx.compose.foundation.layout
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(verticalSpacing)
    ) {
        content()
    }
}
