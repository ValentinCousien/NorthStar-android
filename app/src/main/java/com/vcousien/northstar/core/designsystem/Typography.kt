package com.vcousien.northstar.core.designsystem

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * NorthStar Typography System
 * Based on the iOS implementation with Material Design 3 integration
 */
object NSTypography {
    
    /**
     * Font size constants matching iOS implementation
     */
    object FontSize {
        val xs = 12.sp
        val sm = 14.sp
        val md = 16.sp
        val lg = 18.sp
        val xl = 20.sp
        val xxl = 24.sp
        val xxxl = 30.sp
    }
    
    /**
     * Font weight constants
     */
    object FontWeight {
        val regular = androidx.compose.ui.text.font.FontWeight.Normal
        val medium = androidx.compose.ui.text.font.FontWeight.Medium
        val semibold = androidx.compose.ui.text.font.FontWeight.SemiBold
        val bold = androidx.compose.ui.text.font.FontWeight.Bold
    }
    
    /**
     * Predefined text styles matching iOS implementation
     */
    val heading1 = TextStyle(
        fontSize = FontSize.xxxl,
        fontWeight = FontWeight.bold,
        lineHeight = (FontSize.xxxl.value * 1.2).sp,
        letterSpacing = (-0.5).sp
    )
    
    val heading2 = TextStyle(
        fontSize = FontSize.xxl,
        fontWeight = FontWeight.bold,
        lineHeight = (FontSize.xxl.value * 1.25).sp,
        letterSpacing = (-0.25).sp
    )
    
    val heading3 = TextStyle(
        fontSize = FontSize.xl,
        fontWeight = FontWeight.semibold,
        lineHeight = (FontSize.xl.value * 1.3).sp,
        letterSpacing = 0.sp
    )
    
    val subtitle = TextStyle(
        fontSize = FontSize.lg,
        fontWeight = FontWeight.medium,
        lineHeight = (FontSize.lg.value * 1.4).sp,
        letterSpacing = 0.15.sp
    )
    
    val body = TextStyle(
        fontSize = FontSize.md,
        fontWeight = FontWeight.regular,
        lineHeight = (FontSize.md.value * 1.5).sp,
        letterSpacing = 0.25.sp
    )
    
    val bodyBold = TextStyle(
        fontSize = FontSize.md,
        fontWeight = FontWeight.semibold,
        lineHeight = (FontSize.md.value * 1.5).sp,
        letterSpacing = 0.25.sp
    )
    
    val caption = TextStyle(
        fontSize = FontSize.sm,
        fontWeight = FontWeight.regular,
        lineHeight = (FontSize.sm.value * 1.4).sp,
        letterSpacing = 0.4.sp
    )
    
    val captionBold = TextStyle(
        fontSize = FontSize.sm,
        fontWeight = FontWeight.medium,
        lineHeight = (FontSize.sm.value * 1.4).sp,
        letterSpacing = 0.4.sp
    )
    
    val small = TextStyle(
        fontSize = FontSize.xs,
        fontWeight = FontWeight.regular,
        lineHeight = (FontSize.xs.value * 1.3).sp,
        letterSpacing = 0.4.sp
    )
    
    /**
     * Material Design 3 Typography theme integration
     * Maps our custom typography to Material Design roles
     */
    fun createMaterial3Typography(): Typography {
        return Typography(
            displayLarge = heading1,
            displayMedium = heading2,
            displaySmall = heading3,
            headlineLarge = heading1,
            headlineMedium = heading2,
            headlineSmall = heading3,
            titleLarge = subtitle,
            titleMedium = bodyBold,
            titleSmall = caption,
            bodyLarge = body,
            bodyMedium = body,
            bodySmall = caption,
            labelLarge = captionBold,
            labelMedium = caption,
            labelSmall = small
        )
    }
}

/**
 * Convenience functions for accessing typography styles
 */
@Composable
fun getTypographyStyle(style: String): TextStyle {
    return when (style.lowercase()) {
        "heading1", "h1" -> NSTypography.heading1
        "heading2", "h2" -> NSTypography.heading2
        "heading3", "h3" -> NSTypography.heading3
        "subtitle" -> NSTypography.subtitle
        "body" -> NSTypography.body
        "bodybold" -> NSTypography.bodyBold
        "caption" -> NSTypography.caption
        "captionbold" -> NSTypography.captionBold
        "small" -> NSTypography.small
        else -> NSTypography.body
    }
}

/**
 * Helper function to get font size for custom implementations
 */
fun getFontSize(size: String) = when (size.lowercase()) {
    "xs" -> NSTypography.FontSize.xs
    "sm" -> NSTypography.FontSize.sm
    "md" -> NSTypography.FontSize.md
    "lg" -> NSTypography.FontSize.lg
    "xl" -> NSTypography.FontSize.xl
    "xxl" -> NSTypography.FontSize.xxl
    "xxxl" -> NSTypography.FontSize.xxxl
    else -> NSTypography.FontSize.md
}

/**
 * Helper function to get font weight for custom implementations
 */
fun getFontWeight(weight: String) = when (weight.lowercase()) {
    "regular", "normal" -> NSTypography.FontWeight.regular
    "medium" -> NSTypography.FontWeight.medium
    "semibold" -> NSTypography.FontWeight.semibold
    "bold" -> NSTypography.FontWeight.bold
    else -> NSTypography.FontWeight.regular
}
