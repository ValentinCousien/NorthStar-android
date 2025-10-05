//
// AuthenticationView.kt
// NorthStar Android
//
// Created by Claude on 26/09/2025.
// Port of iOS AuthenticationView.swift to Android Compose

package com.vcousien.northstar.core.authentication

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vcousien.northstar.R
import com.vcousien.northstar.app.AppViewModel
import com.vcousien.northstar.core.designsystem.DesignTokens
import com.vcousien.northstar.core.designsystem.NSSpacing
import com.vcousien.northstar.core.designsystem.NSTypography
import com.vcousien.northstar.core.designsystem.components.NSPrimaryButton
import kotlinx.coroutines.delay
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Enhanced authentication view with biometric authentication flow
 * Android port of the iOS AuthenticationView
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthenticationView(
    appViewModel: AppViewModel,
    modifier: Modifier = Modifier,
    authManager: BiometricAuthManager = hiltViewModel()
) {
    val context = LocalContext.current
    val activity = context as? FragmentActivity
    
    // Collect authentication state
    val isAuthenticated by authManager.isAuthenticated.collectAsStateWithLifecycle()
    val authenticationError by authManager.authenticationError.collectAsStateWithLifecycle()
    val isAuthenticating by authManager.isAuthenticating.collectAsStateWithLifecycle()
    
    // Animation states
    var hasAttemptedAuth by remember { mutableStateOf(false) }
    val animationScale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )
    
    val pulseAnimation by rememberInfiniteTransition(label = "pulse").animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )
    
    val rotationAnimation by rememberInfiniteTransition(label = "rotation").animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )
    
    val contentOffset by animateFloatAsState(
        targetValue = 0f,
        animationSpec = tween(800, easing = EaseOut),
        label = "content_offset"
    )
    
    // Effect to handle authentication flow
    LaunchedEffect(Unit) {
        if (!hasAttemptedAuth && activity != null) {
            hasAttemptedAuth = true
            delay(800) // Allow entrance animation to complete
            performAuthentication(authManager, activity, appViewModel)
        }
    }
    
    // Handle successful authentication
    LaunchedEffect(isAuthenticated) {
        if (isAuthenticated) {
            delay(300) // Brief delay for success animation
            appViewModel.completeAuthentication()
        }
    }
    
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // Background gradient (similar to SkyColor from iOS)
        GradientBackground()
        
        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = NSSpacing.xl)
                .offset(y = contentOffset.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))
            
            // Header section with app branding
            AppBrandingSection(
                pulseScale = pulseAnimation,
                animationScale = animationScale
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Authentication section
            AuthenticationSection(
                isAuthenticating = isAuthenticating,
                authenticationError = authenticationError,
                biometricType = authManager.biometricType,
                rotationAnimation = rotationAnimation,
                animationScale = animationScale,
                onAuthenticateClick = {
                    activity?.let { fragmentActivity ->
                        performAuthentication(authManager, fragmentActivity, appViewModel)
                    }
                }
            )
            
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun GradientBackground() {
    val gradientColors = listOf(
        Color(0xFF1E3A8A), // Deep blue
        Color(0xFF3B82F6), // Blue
        Color(0xFF60A5FA), // Light blue
        Color(0xFF93C5FD)  // Very light blue
    )
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = gradientColors,
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY
                )
            )
    )
}

@Composable
private fun AppBrandingSection(
    pulseScale: Float,
    animationScale: Float
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(NSSpacing.lg)
    ) {
        // App icon with pulse animation
        Box(
            modifier = Modifier
                .size(120.dp)
                .scale(pulseScale)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = stringResource(R.string.app_name),
                modifier = Modifier
                    .size(50.dp)
                    .scale(animationScale),
                tint = Color.White
            )
        }
        
        // App title
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(NSSpacing.sm)
        ) {
            Text(
                text = stringResource(R.string.app_name),
                style = NSTypography.heading1.copy(
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                ),
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun AuthenticationSection(
    isAuthenticating: Boolean,
    authenticationError: String?,
    biometricType: BiometricType,
    rotationAnimation: Float,
    animationScale: Float,
    onAuthenticateClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(NSSpacing.xl)
    ) {
        // Authentication content
        if (isAuthenticating) {
            AuthenticatingView(
                biometricType = biometricType,
                rotationAnimation = rotationAnimation
            )
        } else {
            AuthenticationPromptView(
                biometricType = biometricType,
                authenticationError = authenticationError,
                animationScale = animationScale,
                onAuthenticateClick = onAuthenticateClick
            )
        }
        
        // Security message
        SecurityMessage()
    }
}

@Composable
private fun AuthenticatingView(
    biometricType: BiometricType,
    rotationAnimation: Float
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(NSSpacing.lg)
    ) {
        // Animated loading indicator
        Box(
            modifier = Modifier.size(80.dp),
            contentAlignment = Alignment.Center
        ) {
            // Background circle
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.1f))
            )
            
            // Rotating progress indicator
            CircularProgressIndicator(
                modifier = Modifier
                    .size(80.dp)
                    .rotate(rotationAnimation),
                strokeWidth = 3.dp,
                color = Color.White,
                trackColor = Color.White.copy(alpha = 0.3f)
            )
            
            // Center icon
            Icon(
                imageVector = getBiometricIcon(biometricType),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = Color.White
            )
        }
        
        // Loading text
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(NSSpacing.sm)
        ) {
            Text(
                text = stringResource(R.string.auth_authenticating),
                style = NSTypography.subtitle.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                ),
                color = Color.White,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = biometricType.instructionText,
                style = NSTypography.body.copy(fontSize = 14.sp),
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun AuthenticationPromptView(
    biometricType: BiometricType,
    authenticationError: String?,
    animationScale: Float,
    onAuthenticateClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(NSSpacing.lg)
    ) {
        // Biometric icon
        Box(
            modifier = Modifier
                .size(120.dp)
                .scale(animationScale)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = getBiometricIcon(biometricType),
                contentDescription = null,
                modifier = Modifier.size(45.dp),
                tint = Color.White
            )
        }
        
        // Instruction text
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(NSSpacing.md)
        ) {
            Text(
                text = stringResource(R.string.auth_subtitle, biometricType.displayName),
                style = NSTypography.subtitle.copy(fontSize = 16.sp),
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center
            )
            
            // Error message
            authenticationError?.let { error ->
                Card(
                    modifier = Modifier.padding(horizontal = NSSpacing.md),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Red.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(NSSpacing.md)
                ) {
                    Row(
                        modifier = Modifier.padding(NSSpacing.md),
                        horizontalArrangement = Arrangement.spacedBy(NSSpacing.sm),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color.Red.copy(alpha = 0.8f)
                        )
                        
                        Text(
                            text = error,
                            style = NSTypography.body.copy(fontSize = 14.sp),
                            color = Color.Red.copy(alpha = 0.8f),
                            textAlign = TextAlign.Start
                        )
                    }
                }
            }
        }
        
        // Authentication button
        AuthenticateButton(
            biometricType = biometricType,
            animationScale = animationScale,
            onClick = onAuthenticateClick
        )
    }
}

@Composable
private fun AuthenticateButton(
    biometricType: BiometricType,
    animationScale: Float,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .scale(animationScale)
            .defaultMinSize(minWidth = 280.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = DesignTokens.Colors.primary
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 8.dp,
            pressedElevation = 4.dp
        ),
        shape = RoundedCornerShape(NSSpacing.lg),
        contentPadding = PaddingValues(
            horizontal = NSSpacing.xl,
            vertical = NSSpacing.md
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(NSSpacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = getBiometricIcon(biometricType),
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            
            Text(
                text = stringResource(R.string.auth_button_authenticate),
                style = NSTypography.bodyBold.copy(
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}

@Composable
private fun SecurityMessage() {
    Row(
        modifier = Modifier.padding(horizontal = NSSpacing.xl),
        horizontalArrangement = Arrangement.spacedBy(NSSpacing.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Security,
            contentDescription = null,
            modifier = Modifier.size(14.dp),
            tint = Color.White.copy(alpha = 0.7f)
        )
        
        Text(
            text = stringResource(R.string.auth_security_message),
            style = NSTypography.caption.copy(fontSize = 14.sp),
            color = Color.White.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}

private fun getBiometricIcon(biometricType: BiometricType): ImageVector {
    return when (biometricType) {
        BiometricType.FINGERPRINT -> Icons.Default.Fingerprint
        BiometricType.FACE -> Icons.Default.Face
        BiometricType.IRIS -> Icons.Default.Visibility
        BiometricType.NONE -> Icons.Default.Lock
    }
}

private fun performAuthentication(
    authManager: BiometricAuthManager,
    activity: FragmentActivity,
    appViewModel: AppViewModel
) {
    CoroutineScope(Dispatchers.Main).launch {
        val success = authManager.authenticate(activity)
        if (success) {
            // Add a small delay for visual feedback
            delay(300)
            appViewModel.completeAuthentication()
        }
    }
}
