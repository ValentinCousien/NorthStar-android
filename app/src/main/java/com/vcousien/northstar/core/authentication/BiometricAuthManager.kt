//
// BiometricAuthManager.kt
// NorthStar Android
//
// Created by Claude on 26/09/2025.
// Port of iOS BiometricAuthManager.swift to Android

package com.vcousien.northstar.core.authentication

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

/**
 * Enhanced manager for handling biometric authentication with improved user experience
 * Android port of the iOS BiometricAuthManager
 */
@Singleton
class BiometricAuthManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()
    
    private val _authenticationError = MutableStateFlow<String?>(null)
    val authenticationError: StateFlow<String?> = _authenticationError.asStateFlow()
    
    private val _isAuthenticating = MutableStateFlow(false)
    val isAuthenticating: StateFlow<Boolean> = _isAuthenticating.asStateFlow()
    
    private val _authenticationAttempts = MutableStateFlow(0)
    val authenticationAttempts: StateFlow<Int> = _authenticationAttempts.asStateFlow()
    
    private val biometricManager = BiometricManager.from(context)
    private val maxAttempts = 3
    
    /**
     * Check if biometric authentication is available on the device
     */
    val biometricType: BiometricType
        get() {
            return when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
                BiometricManager.BIOMETRIC_SUCCESS -> {
                    // Android doesn't provide specific biometric type info like iOS
                    // We'll assume fingerprint as the most common type
                    BiometricType.FINGERPRINT
                }
                else -> BiometricType.NONE
            }
        }
    
    /**
     * Check if any form of device authentication is available (including PIN/password)
     */
    val canAuthenticate: Boolean
        get() {
            val result = biometricManager.canAuthenticate(
                BiometricManager.Authenticators.BIOMETRIC_STRONG or 
                BiometricManager.Authenticators.DEVICE_CREDENTIAL
            )
            return result == BiometricManager.BIOMETRIC_SUCCESS
        }
    
    /**
     * Check if maximum authentication attempts have been reached
     */
    val hasReachedMaxAttempts: Boolean
        get() = authenticationAttempts.value >= maxAttempts
    
    /**
     * Get user-friendly authentication reason based on biometric type
     */
    private val authenticationReason: String
        get() = when (biometricType) {
            BiometricType.FINGERPRINT -> "Use your fingerprint to access your data securely"
            BiometricType.FACE -> "Use facial recognition to access your data securely"
            BiometricType.IRIS -> "Use iris scanning to access your data securely"
            BiometricType.NONE -> "Authenticate to access your data securely"
        }
    
    /**
     * Perform biometric authentication with enhanced error handling
     */
    suspend fun authenticate(activity: FragmentActivity): Boolean {
        if (!canAuthenticate) {
            _authenticationError.value = getAuthenticationUnavailableMessage()
            return false
        }
        
        if (hasReachedMaxAttempts) {
            _authenticationError.value = "Too many failed attempts. Please try again later or use your device PIN/password."
            return false
        }
        
        _isAuthenticating.value = true
        _authenticationError.value = null
        
        return try {
            val result = performBiometricAuthentication(activity)
            
            _isAuthenticated.value = result
            _isAuthenticating.value = false
            
            if (result) {
                // Reset attempts on successful authentication
                _authenticationAttempts.value = 0
            } else {
                _authenticationAttempts.value = authenticationAttempts.value + 1
            }
            
            result
        } catch (e: Exception) {
            _isAuthenticated.value = false
            _isAuthenticating.value = false
            _authenticationAttempts.value = authenticationAttempts.value + 1
            _authenticationError.value = getFormattedError(e)
            false
        }
    }
    
    /**
     * Perform the actual biometric authentication using BiometricPrompt
     */
    private suspend fun performBiometricAuthentication(activity: FragmentActivity): Boolean = 
        suspendCancellableCoroutine { continuation ->
            
            val executor = ContextCompat.getMainExecutor(activity)
            
            val biometricPrompt = BiometricPrompt(activity, executor,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        super.onAuthenticationError(errorCode, errString)
                        if (continuation.isActive) {
                            continuation.resume(false)
                        }
                    }
                    
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
                        if (continuation.isActive) {
                            continuation.resume(true)
                        }
                    }
                    
                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        // Don't resolve here - let the user try again
                        // Only resolve on error or success
                    }
                }
            )
            
            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle("Authenticate")
                .setSubtitle(authenticationReason)
                .setAllowedAuthenticators(
                    BiometricManager.Authenticators.BIOMETRIC_STRONG or 
                    BiometricManager.Authenticators.DEVICE_CREDENTIAL
                )
                .build()
            
            biometricPrompt.authenticate(promptInfo)
            
            continuation.invokeOnCancellation {
                biometricPrompt.cancelAuthentication()
            }
        }
    
    /**
     * Reset authentication state and attempts
     */
    fun reset() {
        _isAuthenticated.value = false
        _authenticationError.value = null
        _isAuthenticating.value = false
        _authenticationAttempts.value = 0
    }
    
    /**
     * Get formatted error message based on exception type
     */
    private fun getFormattedError(error: Throwable): String {
        return when {
            error.message?.contains("cancel", ignoreCase = true) == true -> 
                "Authentication was cancelled."
            error.message?.contains("lockout", ignoreCase = true) == true -> 
                "${biometricType.displayName} is temporarily locked. Please use your device PIN/password to unlock."
            error.message?.contains("not available", ignoreCase = true) == true -> 
                "${biometricType.displayName} is not available on this device."
            error.message?.contains("not enrolled", ignoreCase = true) == true -> 
                "${biometricType.displayName} is not set up. Please set it up in Settings."
            else -> 
                "Authentication failed. Please try again or use your device PIN/password."
        }
    }
    
    /**
     * Get message when authentication is unavailable
     */
    private fun getAuthenticationUnavailableMessage(): String {
        return when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> 
                "No biometric authentication hardware is available on this device."
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> 
                "Biometric authentication hardware is currently unavailable."
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> 
                "No biometric credentials are enrolled. Please set up biometric authentication in Settings."
            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> 
                "A security update is required for biometric authentication."
            BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED -> 
                "Biometric authentication is not supported on this device."
            BiometricManager.BIOMETRIC_STATUS_UNKNOWN -> 
                "Biometric authentication status is unknown."
            else -> 
                "Biometric authentication is not available. Please set up a PIN, password, or biometric authentication in Settings."
        }
    }
}

/**
 * Enhanced enum representing different biometric authentication types
 * Android port of the iOS BiometricType enum
 */
enum class BiometricType {
    NONE,
    FINGERPRINT,
    FACE,
    IRIS;
    
    val displayName: String
        get() = when (this) {
            NONE -> "PIN/Password"
            FINGERPRINT -> "Fingerprint"
            FACE -> "Face Authentication"
            IRIS -> "Iris Authentication"
        }
    
    val icon: String
        get() = when (this) {
            NONE -> "lock"
            FINGERPRINT -> "fingerprint"
            FACE -> "face"
            IRIS -> "visibility"
        }
    
    val description: String
        get() = when (this) {
            NONE -> "Secure authentication using your device PIN or password"
            FINGERPRINT -> "Quick and secure authentication using your fingerprint"
            FACE -> "Fast and secure authentication using facial recognition"
            IRIS -> "Secure authentication using iris scanning"
        }
    
    val instructionText: String
        get() = when (this) {
            NONE -> "Enter your device PIN or password to continue"
            FINGERPRINT -> "Place your finger on the fingerprint sensor"
            FACE -> "Look at your device to authenticate"
            IRIS -> "Look at your device to authenticate"
        }
}
