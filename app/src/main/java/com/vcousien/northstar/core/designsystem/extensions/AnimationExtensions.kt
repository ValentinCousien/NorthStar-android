package com.vcousien.northstar.core.designsystem.extensions

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.vcousien.northstar.core.designsystem.DesignTokens
import com.vcousien.northstar.core.designsystem.NSSpacing
import kotlinx.coroutines.delay

/**
 * Animation utilities and extensions for smooth transitions and effects
 * 
 * Provides common animation patterns used throughout the NorthStar app,
 * including fade animations, slide transitions, and interactive feedback.
 */

/**
 * Default animation specifications used throughout the app
 */
object NSAnimations {
    val fastEasing = tween<Float>(DesignTokens.Animation.SHORT, easing = FastOutSlowInEasing)
    val mediumEasing = tween<Float>(DesignTokens.Animation.MEDIUM, easing = FastOutSlowInEasing)
    val slowEasing = tween<Float>(DesignTokens.Animation.LONG, easing = FastOutSlowInEasing)
    
    val springAnimation = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMediumLow
    )
    
    val bounceAnimation = spring<Float>(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessMedium
    )
}

/**
 * Fade in animation modifier
 */
@Composable
fun Modifier.fadeIn(
    durationMs: Int = DesignTokens.Animation.MEDIUM,
    delayMs: Int = 0
): Modifier {
    var isVisible by remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMs, delayMs),
        label = "fadeIn"
    )
    
    LaunchedEffect(Unit) {
        delay(delayMs.toLong())
        isVisible = true
    }
    
    return this.alpha(alpha)
}

/**
 * Slide in from bottom animation modifier
 */
@Composable
fun Modifier.slideInFromBottom(
    durationMs: Int = DesignTokens.Animation.MEDIUM,
    delayMs: Int = 0,
    offsetY: Dp = 50.dp
): Modifier {
    val density = LocalDensity.current
    var isVisible by remember { mutableStateOf(false) }
    val animatedOffset by animateIntOffsetAsState(
        targetValue = if (isVisible) IntOffset.Zero else IntOffset(0, with(density) { offsetY.roundToPx() }),
        animationSpec = tween(durationMs, delayMs),
        label = "slideInFromBottom"
    )
    
    LaunchedEffect(Unit) {
        delay(delayMs.toLong())
        isVisible = true
    }
    
    return this.graphicsLayer {
        translationY = animatedOffset.y.toFloat()
    }
}

/**
 * Slide in from left animation modifier
 */
@Composable
fun Modifier.slideInFromLeft(
    durationMs: Int = DesignTokens.Animation.MEDIUM,
    delayMs: Int = 0,
    offsetX: Dp = 50.dp
): Modifier {
    val density = LocalDensity.current
    var isVisible by remember { mutableStateOf(false) }
    val animatedOffset by animateIntOffsetAsState(
        targetValue = if (isVisible) IntOffset.Zero else IntOffset(-with(density) { offsetX.roundToPx() }, 0),
        animationSpec = tween(durationMs, delayMs),
        label = "slideInFromLeft"
    )
    
    LaunchedEffect(Unit) {
        delay(delayMs.toLong())
        isVisible = true
    }
    
    return this.graphicsLayer {
        translationX = animatedOffset.x.toFloat()
    }
}

/**
 * Scale in animation modifier
 */
@Composable
fun Modifier.scaleIn(
    durationMs: Int = DesignTokens.Animation.MEDIUM,
    delayMs: Int = 0,
    fromScale: Float = 0.8f
): Modifier {
    var isVisible by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else fromScale,
        animationSpec = NSAnimations.springAnimation,
        label = "scaleIn"
    )
    
    LaunchedEffect(Unit) {
        delay(delayMs.toLong())
        isVisible = true
    }
    
    return this.scale(scale)
}

/**
 * Bounce animation modifier for interactive feedback
 */
@Composable
fun Modifier.bounce(
    scale: Float = 0.95f,
    durationMs: Int = DesignTokens.Animation.SHORT
): Modifier {
    var isPressed by remember { mutableStateOf(false) }
    val animatedScale by animateFloatAsState(
        targetValue = if (isPressed) scale else 1f,
        animationSpec = NSAnimations.bounceAnimation,
        label = "bounce"
    )
    
    return this.scale(animatedScale)
}

/**
 * Shimmer loading animation modifier
 */
@Composable
fun Modifier.shimmer(
    colors: List<Color> = listOf(
        Color.LightGray.copy(alpha = 0.6f),
        Color.LightGray.copy(alpha = 0.2f),
        Color.LightGray.copy(alpha = 0.6f)
    ),
    durationMs: Int = 1000
): Modifier {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnimation = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMs, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslate"
    )
    
    return this.background(
        brush = androidx.compose.ui.graphics.Brush.linearGradient(
            colors = colors,
            start = androidx.compose.ui.geometry.Offset(translateAnimation.value, 0f),
            end = androidx.compose.ui.geometry.Offset(translateAnimation.value + 200f, 200f)
        )
    )
}

/**
 * Pulsing animation modifier
 */
@Composable
fun Modifier.pulse(
    minAlpha: Float = 0.3f,
    maxAlpha: Float = 1f,
    durationMs: Int = 1000
): Modifier {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = minAlpha,
        targetValue = maxAlpha,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMs, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )
    
    return this.alpha(alpha)
}

/**
 * Rotate animation modifier
 */
@Composable
fun Modifier.rotate(
    durationMs: Int = 1000,
    clockwise: Boolean = true
): Modifier {
    val infiniteTransition = rememberInfiniteTransition(label = "rotate")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (clockwise) 360f else -360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMs, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotateAngle"
    )
    
    return this.graphicsLayer {
        rotationZ = angle
    }
}

/**
 * Mood color transition animation
 */
@Composable
fun Modifier.animateMoodBackground(
    targetMoodLevel: Int,
    durationMs: Int = DesignTokens.Animation.MEDIUM
): Modifier {
    val targetColor = DesignTokens.Colors.moodColor(targetMoodLevel)
    val animatedColor by animateColorAsState(
        targetValue = targetColor,
        animationSpec = tween(durationMs),
        label = "moodBackground"
    )
    
    return this.background(
        color = animatedColor,
        shape = RoundedCornerShape(NSSpacing.CornerRadius.md)
    )
}

/**
 * Slide transition for screen changes
 */
@Composable
fun SlideTransition(
    targetState: Boolean,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = targetState,
        enter = slideInHorizontally(
            initialOffsetX = { it },
            animationSpec = tween(DesignTokens.Animation.MEDIUM, easing = FastOutSlowInEasing)
        ) + fadeIn(animationSpec = tween(DesignTokens.Animation.MEDIUM, easing = FastOutSlowInEasing)),
        exit = slideOutHorizontally(
            targetOffsetX = { -it },
            animationSpec = tween(DesignTokens.Animation.MEDIUM, easing = FastOutSlowInEasing)
        ) + fadeOut(animationSpec = tween(DesignTokens.Animation.MEDIUM, easing = FastOutSlowInEasing)),
        content = content
    )
}

/**
 * Fade transition for content changes
 */
@Composable
fun FadeTransition(
    targetState: Boolean,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = targetState,
        enter = fadeIn(animationSpec = tween(DesignTokens.Animation.MEDIUM, easing = FastOutSlowInEasing)),
        exit = fadeOut(animationSpec = tween(DesignTokens.Animation.MEDIUM, easing = FastOutSlowInEasing)),
        content = content
    )
}

/**
 * Scale transition for modal content
 */
@Composable
fun ScaleTransition(
    targetState: Boolean,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = targetState,
        enter = scaleIn(
            initialScale = 0.8f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMediumLow
            )
        ) + fadeIn(animationSpec = tween(DesignTokens.Animation.MEDIUM, easing = FastOutSlowInEasing)),
        exit = scaleOut(
            targetScale = 0.8f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMediumLow
            )
        ) + fadeOut(animationSpec = tween(DesignTokens.Animation.MEDIUM, easing = FastOutSlowInEasing)),
        content = content
    )
}

/**
 * Loading overlay with animated background
 */
@Composable
fun LoadingOverlay(
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.Black.copy(alpha = 0.5f),
    content: @Composable () -> Unit = {}
) {
    AnimatedVisibility(
        visible = isLoading,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(backgroundColor),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}
