package com.vcousien.northstar.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
 * Demo screen showcasing NSPrimaryButton component variants and configurations
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NSPrimaryButtonDemo() {
    var selectedIndex by remember { mutableStateOf(0) }
    
    val buttonDemoSections = remember {
        listOf(
            "Basic Primary Buttons",
            "Button Sizes",
            "Button States",
            "Button with Icons",
            "Button Width Options",
            "Secondary Buttons",
            "Destructive Buttons",
            "Text Buttons",
            "Loading States",
            "Real Use Cases"
        )
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("NSPrimaryButton Demo") },
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
            // Sidebar with demo sections
            LazyColumn(
                modifier = Modifier
                    .width(200.dp)
                    .fillMaxHeight()
                    .background(DesignTokens.Colors.surface)
                    .padding(NSSpacing.sm),
                verticalArrangement = Arrangement.spacedBy(NSSpacing.xs)
            ) {
                items(buttonDemoSections.size) { index ->
                    val title = buttonDemoSections[index]
                    Card(
                        onClick = { selectedIndex = index },
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedIndex == index) {
                                DesignTokens.Colors.primary.copy(alpha = 0.1f)
                            } else {
                                DesignTokens.Colors.cardBackground
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
            
            // Content area showing selected demo
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(DesignTokens.Colors.background)
                    .padding(NSSpacing.md),
                verticalArrangement = Arrangement.spacedBy(NSSpacing.md)
            ) {
                item {
                    when (selectedIndex) {
                        0 -> BasicPrimaryButtonsDemo()
                        1 -> ButtonSizesDemo()
                        2 -> ButtonStatesDemo()
                        3 -> ButtonIconsDemo()
                        4 -> ButtonWidthDemo()
                        5 -> SecondaryButtonsDemo()
                        6 -> DestructiveButtonsDemo()
                        7 -> TextButtonsDemo()
                        8 -> LoadingStatesDemo()
                        9 -> RealUseCasesDemo()
                        else -> BasicPrimaryButtonsDemo()
                    }
                }
            }
        }
    }
}

@Composable
private fun DemoSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = DesignTokens.Colors.cardBackground
        )
    ) {
        Column(
            modifier = Modifier.padding(NSSpacing.md),
            verticalArrangement = Arrangement.spacedBy(NSSpacing.sm)
        ) {
            Text(
                text = title,
                style = NSTypography.subtitle,
                color = DesignTokens.Colors.textPrimary
            )
            content()
        }
    }
}

@Composable
private fun BasicPrimaryButtonsDemo() {
    DemoSection("Basic Primary Buttons") {
        NSPrimaryButton(
            title = "Save Changes",
            onClick = { /* Handle save */ }
        )
        
        NSPrimaryButton(
            title = "Continue",
            onClick = { /* Handle continue */ }
        )
        
        NSPrimaryButton(
            title = "Add Entry",
            onClick = { /* Handle add */ }
        )
    }
}

@Composable
private fun ButtonSizesDemo() {
    Column(
        verticalArrangement = Arrangement.spacedBy(NSSpacing.md)
    ) {
        DemoSection("Compact Size") {
            NSPrimaryButton(
                title = "Compact Button",
                onClick = { },
                size = NSButtonSize.Compact
            )
        }
        
        DemoSection("Standard Size") {
            NSPrimaryButton(
                title = "Standard Button",
                onClick = { },
                size = NSButtonSize.Standard
            )
        }
        
        DemoSection("Large Size") {
            NSPrimaryButton(
                title = "Large Button",
                onClick = { },
                size = NSButtonSize.Large
            )
        }
    }
}

@Composable
private fun ButtonStatesDemo() {
    Column(
        verticalArrangement = Arrangement.spacedBy(NSSpacing.md)
    ) {
        DemoSection("Enabled Button") {
            NSPrimaryButton(
                title = "Enabled Button",
                onClick = { },
                enabled = true
            )
        }
        
        DemoSection("Disabled Button") {
            NSPrimaryButton(
                title = "Disabled Button",
                onClick = { },
                enabled = false
            )
        }
    }
}

@Composable
private fun ButtonIconsDemo() {
    DemoSection("Button with Icons") {
        NSPrimaryButton(
            title = "Add Mood",
            onClick = { },
            icon = Icons.Default.Add
        )
        
        NSPrimaryButton(
            title = "Save Entry",
            onClick = { },
            icon = Icons.Default.Check
        )
        
        NSPrimaryButton(
            title = "Start Tracking",
            onClick = { },
            icon = Icons.Default.PlayArrow
        )
    }
}

@Composable
private fun ButtonWidthDemo() {
    Column(
        verticalArrangement = Arrangement.spacedBy(NSSpacing.md)
    ) {
        DemoSection("Fill Maximum Width") {
            NSPrimaryButton(
                title = "Full Width Button",
                onClick = { },
                width = NSButtonWidth.FillMax
            )
        }
        
        DemoSection("Wrap Content") {
            NSPrimaryButton(
                title = "Wrap Content",
                onClick = { },
                width = NSButtonWidth.WrapContent
            )
        }
        
        DemoSection("Fixed Width") {
            NSPrimaryButton(
                title = "Fixed Width",
                onClick = { },
                width = NSButtonWidth.Fixed(200.dp)
            )
        }
    }
}

@Composable
private fun SecondaryButtonsDemo() {
    Column(
        verticalArrangement = Arrangement.spacedBy(NSSpacing.md)
    ) {
        DemoSection("Secondary Buttons") {
            NSSecondaryButton(
                title = "Cancel",
                onClick = { }
            )
            
            NSSecondaryButton(
                title = "View Details",
                onClick = { },
                icon = Icons.Default.Info
            )
            
            NSSecondaryButton(
                title = "Edit",
                onClick = { },
                icon = Icons.Default.Edit
            )
        }
        
        DemoSection("Primary + Secondary Combination") {
            Row(
                horizontalArrangement = Arrangement.spacedBy(NSSpacing.md)
            ) {
                NSSecondaryButton(
                    title = "Cancel",
                    onClick = { },
                    modifier = Modifier.weight(1f),
                    width = NSButtonWidth.FillMax
                )
                
                NSPrimaryButton(
                    title = "Save",
                    onClick = { },
                    modifier = Modifier.weight(1f),
                    width = NSButtonWidth.FillMax
                )
            }
        }
    }
}

@Composable
private fun DestructiveButtonsDemo() {
    DemoSection("Destructive Actions") {
        NSDestructiveButton(
            title = "Delete Entry",
            onClick = { },
            icon = Icons.Default.Delete
        )
        
        NSDestructiveButton(
            title = "Clear All Data",
            onClick = { },
            icon = Icons.Default.Clear
        )
        
        NSDestructiveButton(
            title = "Remove Medication",
            onClick = { }
        )
    }
}

@Composable
private fun TextButtonsDemo() {
    DemoSection("Text Buttons") {
        NSTextButton(
            title = "Skip",
            onClick = { }
        )
        
        NSTextButton(
            title = "Learn More",
            onClick = { },
            icon = Icons.Default.Info
        )
        
        NSTextButton(
            title = "Go Back",
            onClick = { },
            icon = Icons.Default.ArrowBack
        )
    }
}

@Composable
private fun LoadingStatesDemo() {
    DemoSection("Loading States") {
        NSPrimaryButton(
            title = "Saving...",
            onClick = { },
            loading = true
        )
        
        NSSecondaryButton(
            title = "Loading...",
            onClick = { },
            loading = true
        )
        
        NSDestructiveButton(
            title = "Deleting...",
            onClick = { },
            loading = true
        )
    }
}

@Composable
private fun RealUseCasesDemo() {
    Column(
        verticalArrangement = Arrangement.spacedBy(NSSpacing.lg)
    ) {
        DemoSection("Mood Entry Form") {
            Column(
                verticalArrangement = Arrangement.spacedBy(NSSpacing.sm)
            ) {
                Text(
                    text = "How are you feeling today?",
                    style = NSTypography.body
                )
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(NSSpacing.sm)
                ) {
                    NSTextButton(
                        title = "Cancel",
                        onClick = { },
                        modifier = Modifier.weight(1f)
                    )
                    
                    NSPrimaryButton(
                        title = "Save Mood",
                        onClick = { },
                        icon = Icons.Default.Check,
                        modifier = Modifier.weight(2f),
                        width = NSButtonWidth.FillMax
                    )
                }
            }
        }
        
        DemoSection("Medication Management") {
            Column(
                verticalArrangement = Arrangement.spacedBy(NSSpacing.sm)
            ) {
                NSPrimaryButton(
                    title = "Add New Medication",
                    onClick = { },
                    icon = Icons.Default.Add
                )
                
                NSSecondaryButton(
                    title = "View Medication History",
                    onClick = { },
                    icon = Icons.Default.History,
                    width = NSButtonWidth.FillMax
                )
            }
        }
        
        DemoSection("Settings & Actions") {
            Column(
                verticalArrangement = Arrangement.spacedBy(NSSpacing.sm)
            ) {
                NSSecondaryButton(
                    title = "Export Data",
                    onClick = { },
                    icon = Icons.Default.Download,
                    width = NSButtonWidth.FillMax
                )
                
                NSSecondaryButton(
                    title = "App Settings",
                    onClick = { },
                    icon = Icons.Default.Settings,
                    width = NSButtonWidth.FillMax
                )
                
                Spacer(modifier = Modifier.height(NSSpacing.sm))
                
                NSDestructiveButton(
                    title = "Clear All Data",
                    onClick = { },
                    icon = Icons.Default.Warning,
                    width = NSButtonWidth.FillMax
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 800, heightDp = 600)
@Composable
private fun NSPrimaryButtonDemoPreview() {
    NSPrimaryButtonDemo()
}

@Preview(showBackground = true)
@Composable
private fun BasicPrimaryButtonsPreview() {
    BasicPrimaryButtonsDemo()
}

@Preview(showBackground = true)
@Composable
private fun ButtonSizesPreview() {
    ButtonSizesDemo()
}

@Preview(showBackground = true)
@Composable
private fun RealUseCasesPreview() {
    RealUseCasesDemo()
}