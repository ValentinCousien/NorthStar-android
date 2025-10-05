package com.vcousien.northstar.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class for NorthStar - Mental Health Tracking Application
 * 
 * This class serves as the entry point for dependency injection and
 * application-wide initialization.
 */
@HiltAndroidApp
class NorthStarApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
    }
}
