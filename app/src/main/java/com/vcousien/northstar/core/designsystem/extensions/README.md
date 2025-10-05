# UI Extensions & Utility Composables

This package contains common UI extensions and utility composables that extend the NorthStar design system with convenient helper functions and reusable patterns.

## Overview

The UI extensions provide convenient ways to apply consistent styling and behavior patterns throughout the NorthStar app. These extensions are designed to:

- Maintain consistency with the design system
- Reduce code duplication
- Provide convenient APIs for common use cases
- Support both light and dark themes
- Follow Material Design principles

## File Structure

```
extensions/
├── ModifierExtensions.kt      # Modifier extensions for styling
├── ComposeExtensions.kt       # Compose-specific utilities
├── LayoutExtensions.kt        # Layout and spacing utilities
├── AnimationExtensions.kt     # Animation patterns
├── UtilityComposables.kt      # Common UI components
└── UIExtensionsDemo.kt        # Demo/preview screen
```

## Components

### 1. Modifier Extensions (`ModifierExtensions.kt`)

Common modifier extensions that align with the iOS View+Extensions.swift patterns:

```kotlin
// Apply default background (equivalent to iOS withDefaultBackground())
Modifier.withDefaultBackground()

// Apply card-style background
Modifier.withCardBackground()

// Apply mood-specific background
Modifier.withMoodBackground(moodLevel: Int)

// Apply corner radius variations
Modifier.withSmallCorners()
Modifier.withStandardCorners()
Modifier.withLargeCorners()
Modifier.withRoundedCorners()

// Apply state-specific styling
Modifier.withErrorStyling()
Modifier.withSuccessStyling()
Modifier.withWarningStyling()
Modifier.withDisabledStyling()
```

### 2. Compose Extensions (`ComposeExtensions.kt`)

Compose-specific utilities for interactions and conditional styling:

```kotlin
// Clickable with ripple effect
Modifier.clickableWithRipple(onClick = { })

// Conditional modifiers
Modifier.conditional(condition) { /* modifier */ }

// Standard padding patterns
Modifier.withContentPadding()
Modifier.withVerticalSpacing()

// Interactive feedback
Modifier.withFocusIndicator(isFocused: Boolean)
Modifier.withSelectionIndicator(isSelected: Boolean)

// Accessibility helpers
Modifier.withMinTouchTarget()
```

### 3. Layout Extensions (`LayoutExtensions.kt`)

Layout utilities for consistent spacing and arrangement:

```kotlin
// Standard spacers
VerticalSpacer(height = NSSpacing.md)
HorizontalSpacer(width = NSSpacing.md)

// Convenience spacers
VerticalSpacerXS()  // Extra small
VerticalSpacerSM()  // Small
VerticalSpacerMD()  // Medium (default)
VerticalSpacerLG()  // Large
VerticalSpacerXL()  // Extra large

// Consistent dividers
StandardDivider()
SubtleDivider()
SectionDivider()

// Spaced layouts
SpacedColumn(spacing = NSSpacing.md) { /* content */ }
SpacedRow(spacing = NSSpacing.md) { /* content */ }

// Container patterns
ContentContainer { /* content */ }
SectionContainer { /* content */ }
```

### 4. Animation Extensions (`AnimationExtensions.kt`)

Animation patterns for smooth transitions:

```kotlin
// Entrance animations
Modifier.fadeIn(durationMs = 300)
Modifier.slideInFromBottom(offsetY = 50.dp)
Modifier.slideInFromLeft(offsetX = 50.dp)
Modifier.scaleIn(fromScale = 0.8f)

// Interactive feedback
Modifier.bounce(scale = 0.95f)

// Loading states
Modifier.shimmer()
Modifier.pulse()

// Continuous animations
Modifier.rotate(clockwise = true)

// Mood-specific animations
Modifier.animateMoodBackground(targetMoodLevel: Int)

// Transition composables
SlideTransition(targetState = isVisible) { /* content */ }
FadeTransition(targetState = isVisible) { /* content */ }
ScaleTransition(targetState = isVisible) { /* content */ }
```

### 5. Utility Composables (`UtilityComposables.kt`)

Reusable UI components for common patterns:

#### Status Indicators
```kotlin
StatusIndicator(
    status = StatusType.Success,
    size = IndicatorSize.Medium
)
```

#### Badges
```kotlin
NSBadge(
    text = "3",
    style = BadgeStyle.Default,
    backgroundColor = DesignTokens.Colors.primary
)
```

#### Chips
```kotlin
NSChip(
    label = "Mood",
    isSelected = true,
    onSelectionChanged = { /* handle selection */ },
    leadingIcon = Icons.Default.Mood
)
```

#### Avatars
```kotlin
NSAvatar(
    initials = "JD",
    size = AvatarSize.Medium,
    backgroundColor = DesignTokens.Colors.primary
)
```

#### Progress Indicators
```kotlin
NSProgressIndicator(
    progress = 0.75f,
    label = "Mood tracking progress",
    showPercentage = true,
    color = DesignTokens.Colors.success
)
```

#### Mood Indicators
```kotlin
MoodIndicator(
    level = 7,
    size = IndicatorSize.Medium,
    showLabel = true
)
```

#### Info Cards
```kotlin
InfoCard(
    message = "This is important information",
    type = StatusType.Info,
    onDismiss = { /* handle dismiss */ }
)
```

#### Expandable Sections
```kotlin
ExpandableSection(
    title = "Advanced Options",
    initiallyExpanded = false
) {
    // Collapsible content
}
```

#### Empty State
```kotlin
EmptyStatePlaceholder(
    title = "No entries yet",
    subtitle = "Start tracking your mood",
    icon = Icons.Default.Mood,
    action = {
        NSPrimaryButton("Add Entry") { /* handle action */ }
    }
)
```

## Usage Examples

### Basic Styling
```kotlin
@Composable
fun MyScreen() {
    Column(
        modifier = Modifier.withDefaultBackground()
    ) {
        NSCard(
            modifier = Modifier.withContentPadding()
        ) {
            Text("Content with standard styling")
        }
    }
}
```

### Interactive Elements
```kotlin
@Composable
fun InteractiveCard() {
    var isSelected by remember { mutableStateOf(false) }
    
    Box(
        modifier = Modifier
            .withCardBackground()
            .withSelectionIndicator(isSelected)
            .clickableWithRipple { isSelected = !isSelected }
    ) {
        Text("Tap to select")
    }
}
```

### Animated Content
```kotlin
@Composable
fun AnimatedContent() {
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        isVisible = true
    }
    
    FadeTransition(targetState = isVisible) {
        NSCard(
            modifier = Modifier
                .slideInFromBottom()
                .scaleIn(delayMs = 200)
        ) {
            Text("Animated card content")
        }
    }
}
```

### Mood-Specific UI
```kotlin
@Composable
fun MoodCard(moodLevel: Int) {
    NSCard(
        modifier = Modifier.animateMoodBackground(moodLevel)
    ) {
        SpacedRow {
            MoodIndicator(level = moodLevel, showLabel = true)
            Text(
                text = DesignTokens.Mood.getCategoryName(moodLevel),
                color = getContrastingTextColor(
                    DesignTokens.Colors.moodColor(moodLevel)
                )
            )
        }
    }
}
```

## Design Patterns

### Consistent Spacing
Always use the spacing utilities to maintain consistent layouts:
```kotlin
SpacedColumn {
    Text("Title")
    VerticalSpacerMD()
    Text("Content")
    VerticalSpacerLG()
    NSPrimaryButton("Action") { }
}
```

### Status Communication
Use consistent status indicators across the app:
```kotlin
SpacedRow {
    StatusIndicator(StatusType.Success)
    Text("Operation completed successfully")
}
```

### Loading States
Provide feedback during loading with consistent patterns:
```kotlin
if (isLoading) {
    SkeletonBox(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
    )
} else {
    // Actual content
}
```

## Accessibility

All utility composables include proper accessibility support:
- Semantic descriptions for screen readers
- Minimum touch target sizes
- High contrast color support
- Proper focus handling

## Testing

Use the `UIExtensionsDemo` composable to preview and test all extensions:
```kotlin
@Preview
@Composable
fun PreviewExtensions() {
    NorthStarTheme {
        UIExtensionsDemo()
    }
}
```

## iOS Parity

These extensions provide Android equivalents for iOS View extensions:

| iOS | Android |
|-----|---------|
| `View.withDefaultBackground()` | `Modifier.withDefaultBackground()` |
| SwiftUI modifiers | Compose Modifier extensions |
| iOS animations | Compose animations with similar timing |
| iOS layout patterns | Compose layout utilities |

## Best Practices

1. **Use semantic extensions**: Prefer `withCardBackground()` over manually setting background colors
2. **Maintain consistency**: Always use design system spacing and colors
3. **Consider performance**: Animations and effects should be smooth and efficient
4. **Test accessibility**: Ensure all interactive elements meet accessibility guidelines
5. **Follow Material Design**: Extensions should complement Material Design principles

## Contributing

When adding new extensions:
1. Follow existing naming conventions
2. Add proper documentation
3. Include usage examples
4. Add preview/demo components
5. Ensure accessibility compliance
6. Test with both light and dark themes
