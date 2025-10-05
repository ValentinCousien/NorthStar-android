# NorthStar Design System - Typography & Spacing

This document outlines the typography and spacing systems implemented for the NorthStar Android app, based on the iOS SwiftUI implementation.

## Typography System (`NSTypography`)

The typography system provides consistent text styling across the app with predefined text styles that match the iOS implementation.

### Font Sizes

- `xs` = 12sp
- `sm` = 14sp  
- `md` = 16sp
- `lg` = 18sp
- `xl` = 20sp
- `xxl` = 24sp
- `xxxl` = 30sp

### Font Weights

- `regular` = FontWeight.Normal
- `medium` = FontWeight.Medium
- `semibold` = FontWeight.SemiBold
- `bold` = FontWeight.Bold

### Predefined Text Styles

#### Headings
- `heading1` - 30sp, Bold (for main titles)
- `heading2` - 24sp, Bold (for section titles)  
- `heading3` - 20sp, SemiBold (for subsection titles)

#### Body Text
- `subtitle` - 18sp, Medium (for important secondary text)
- `body` - 16sp, Regular (for main body content)
- `bodyBold` - 16sp, SemiBold (for emphasized body text)

#### Small Text
- `caption` - 14sp, Regular (for captions and labels)
- `captionBold` - 14sp, Medium (for emphasized captions)
- `small` - 12sp, Regular (for fine print)

### Usage Examples

```kotlin
// Direct usage
Text(
    text = "Mood Tracker",
    style = NSTypography.heading1
)

// Through DesignTokens
Text(
    text = "Today's Mood", 
    style = DesignTokens.Typography.heading2
)

// Helper function
Text(
    text = "Sleep Quality",
    style = getTypographyStyle("heading3")
)

// Material Design 3 integration in theme
MaterialTheme(
    typography = NSTypography.createMaterial3Typography()
) {
    // Your content
}
```

## Spacing System (`NSSpacing`)

The spacing system provides consistent spacing values, corner radii, and padding patterns.

### Base Spacing Units

- `xxs` = 2dp
- `xs` = 4dp
- `sm` = 8dp  
- `md` = 16dp
- `lg` = 24dp
- `xl` = 32dp
- `xxl` = 48dp
- `xxxl` = 64dp

### Special Spacing Values

- `screenEdge` = 16dp (standard margin for screen content)
- `stackDefault` = 12dp (spacing between elements in lists/stacks)

### Corner Radius

- `sm` = 4dp (small buttons, chips)
- `md` = 8dp (cards, text fields)
- `lg` = 16dp (dialogs, large cards)
- `xl` = 24dp (modals, sheets)
- `pill` = 999dp (fully rounded shapes)

### Predefined Padding Patterns

#### Screen Padding
```kotlin
NSSpacing.Padding.screen           // All sides: 16dp
NSSpacing.Padding.screenHorizontal // Horizontal: 16dp
NSSpacing.Padding.screenVertical   // Vertical: 16dp
```

#### Component Padding
```kotlin
NSSpacing.Padding.card      // 16dp all sides
NSSpacing.Padding.cardSmall // 8dp all sides  
NSSpacing.Padding.cardLarge // 24dp all sides

NSSpacing.Padding.button      // Horizontal: 24dp, Vertical: 8dp
NSSpacing.Padding.buttonSmall // Horizontal: 16dp, Vertical: 4dp
NSSpacing.Padding.buttonLarge // Horizontal: 32dp, Vertical: 16dp

NSSpacing.Padding.listItem      // Horizontal: 16dp, Vertical: 8dp
NSSpacing.Padding.listItemLarge // Horizontal: 16dp, Vertical: 16dp
```

### Shapes

Predefined rounded corner shapes:
```kotlin
NSSpacing.CornerRadius.smallShape     // 4dp corners
NSSpacing.CornerRadius.mediumShape    // 8dp corners
NSSpacing.CornerRadius.largeShape     // 16dp corners
NSSpacing.CornerRadius.extraLargeShape // 24dp corners
NSSpacing.CornerRadius.pillShape      // Fully rounded
```

### Usage Examples

```kotlin
// Basic spacing
Column(
    verticalArrangement = Arrangement.spacedBy(NSSpacing.md)
) {
    // Content with 16dp spacing
}

// Through DesignTokens
Row(
    horizontalArrangement = Arrangement.spacedBy(DesignTokens.Spacing.sm)
) {
    // Content with 8dp spacing
}

// Card with padding and shape
Card(
    modifier = Modifier.padding(NSSpacing.Padding.card),
    shape = NSSpacing.CornerRadius.mediumShape
) {
    // Card content
}

// Helper functions
Box(
    modifier = Modifier.padding(getSpacing("lg"))
) {
    // Content with 24dp padding
}

// Custom padding
Column(
    modifier = Modifier.padding(
        symmetricPadding(
            horizontal = NSSpacing.md,
            vertical = NSSpacing.lg
        )
    )
) {
    // Content
}
```

## Integration with Existing Systems

### DesignTokens Integration

Both typography and spacing are integrated into the existing `DesignTokens` object:

```kotlin
// Typography access
DesignTokens.Typography.heading1
DesignTokens.Typography.body

// Spacing access  
DesignTokens.Spacing.md
DesignTokens.Spacing.screenEdge
```

### Material Design 3 Integration

The typography system integrates with Material Design 3:

```kotlin
@Composable
fun NorthStarTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        typography = NSTypography.createMaterial3Typography(),
        // ... other theme parameters
    ) {
        content()
    }
}
```

## Component-Specific Guidelines

### Mood Tracking Components
- Use `heading2` for mood level displays
- Use `body` for mood descriptions  
- Use `caption` for timestamps
- Apply `NSSpacing.md` between mood entries

### Sleep Tracking Components
- Use `heading3` for sleep duration
- Use `subtitle` for sleep quality labels
- Use `NSSpacing.Padding.card` for sleep cards

### Medication Components  
- Use `bodyBold` for medication names
- Use `caption` for dosage information
- Use `NSSpacing.sm` between medication items

### Form Components
- Use `NSSpacing.Component.fieldSpacing` between form fields
- Use `NSSpacing.Component.fieldPadding` inside text fields
- Use `subtitle` for form section headers

## Best Practices

1. **Consistency**: Always use the predefined spacing and typography values rather than hardcoding dimensions
2. **Semantic Usage**: Use typography styles for their intended purpose (e.g., `heading1` for main titles)
3. **Spacing Hierarchy**: Maintain visual hierarchy using consistent spacing patterns
4. **Accessibility**: The typography system includes appropriate line heights and letter spacing for readability
5. **Material Design**: Leverage the Material Design 3 integration for standard components

## Migration from Hardcoded Values

When updating existing components, replace hardcoded values:

```kotlin
// Before
Text(
    text = "Title",
    fontSize = 24.sp,
    fontWeight = FontWeight.Bold
)

// After  
Text(
    text = "Title",
    style = NSTypography.heading2
)

// Before
modifier = Modifier.padding(16.dp)

// After
modifier = Modifier.padding(NSSpacing.md)
```

This ensures consistency with the design system and makes future updates easier to manage.
