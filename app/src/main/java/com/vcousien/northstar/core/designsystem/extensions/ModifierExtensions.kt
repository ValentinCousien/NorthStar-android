package com.vcousien.northstar.core.designsystem.extensions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.vcousien.northstar.core.designsystem.DesignTokens
import com.vcousien.northstar.core.designsystem.NSSpacing
import com.vcousien.northstar.ui.theme.northStarColors

/**
 * Modifier Extensions for NorthStar Design System
 * 
 * Provides convenient modifier extensions that match the iOS View+Extensions.swift
 * functionality while leveraging Android/Compose patterns.
 * 
 * These extensions help maintain consistency across the app by providing
 * reusable styling patterns that align with the NorthStar design system.
 */

/**
 * Apply the default background styling used throughout the app
 * Equivalent to iOS View.withDefaultBackground()
 * 
 * Sets the background color to the theme's background color and fills max size
 * with proper system bars padding handling
 */
@Composable
fun Modifier.withDefaultBackground(): Modifier = this
    .fillMaxSize()
    .background(DesignTokens.Colors.background)
    .systemBarsPadding()

/**
 * Apply card-style background with rounded corners and shadow
 * Common pattern for card-like components
 */
@Composable
fun Modifier.withCardBackground(
    backgroundColor: Color? = null
): Modifier = this
    .clip(RoundedCornerShape(NSSpacing.CornerRadius.md))
    .background(backgroundColor ?: DesignTokens.Colors.cardBackground)

/**
 * Apply surface-style background
 * Used for elevated surfaces and containers
 */
@Composable
fun Modifier.withSurfaceBackground(): Modifier = this
    .background(DesignTokens.Colors.surface)

/**
 * Apply standard corner radius based on design system
 */
fun Modifier.withStandardCorners(): Modifier = this
    .clip(RoundedCornerShape(NSSpacing.CornerRadius.md))

/**
 * Apply small corner radius
 */
fun Modifier.withSmallCorners(): Modifier = this
    .clip(RoundedCornerShape(NSSpacing.CornerRadius.sm))

/**
 * Apply large corner radius
 */
fun Modifier.withLargeCorners(): Modifier = this
    .clip(RoundedCornerShape(NSSpacing.CornerRadius.lg))

/**
 * Apply rounded (circular) corners
 */
fun Modifier.withRoundedCorners(): Modifier = this
    .clip(RoundedCornerShape(50))

/**
 * Apply mood-specific background color
 */
@Composable
fun Modifier.withMoodBackground(moodLevel: Int): Modifier = this
    .background(DesignTokens.Colors.moodColor(moodLevel))

/**
 * Apply disabled styling with reduced opacity
 */
@Composable
fun Modifier.withDisabledStyling(): Modifier = this
    .background(
        DesignTokens.Colors.surface.copy(alpha = DesignTokens.Alpha.DISABLED)
    )

/**
 * Apply error state styling
 */
@Composable
fun Modifier.withErrorStyling(): Modifier = this
    .background(DesignTokens.Colors.error.copy(alpha = 0.1f))
    .clip(RoundedCornerShape(NSSpacing.CornerRadius.sm))

/**
 * Apply success state styling
 */
@Composable
fun Modifier.withSuccessStyling(): Modifier = this
    .background(DesignTokens.Colors.success.copy(alpha = 0.1f))
    .clip(RoundedCornerShape(NSSpacing.CornerRadius.sm))

/**
 * Apply warning state styling
 */
@Composable
fun Modifier.withWarningStyling(): Modifier = this
    .background(DesignTokens.Colors.warning.copy(alpha = 0.1f))
    .clip(RoundedCornerShape(NSSpacing.CornerRadius.sm))

/**
 * Apply info state styling
 */
@Composable
fun Modifier.withInfoStyling(): Modifier = this
    .background(DesignTokens.Colors.info.copy(alpha = 0.1f))
    .clip(RoundedCornerShape(NSSpacing.CornerRadius.sm))
