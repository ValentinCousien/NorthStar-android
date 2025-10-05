package com.vcousien.northstar.core.storage

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger Hilt module for providing storage-related dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object StorageModule {
    
    /**
     * Provides the SecureStorageManager singleton
     */
    @Provides
    @Singleton
    fun provideSecureStorageManager(
        @ApplicationContext context: Context
    ): SecureStorageManager {
        return SecureStorageManager(context)
    }
    
    /**
     * Provides the SecureLocalStorage singleton
     */
    @Provides
    @Singleton
    fun provideSecureLocalStorage(
        @ApplicationContext context: Context,
        secureStorageManager: SecureStorageManager
    ): SecureLocalStorage {
        return SecureLocalStorage(context, secureStorageManager)
    }
    
    /**
     * Provides the LocalStorage singleton (abstraction layer)
     */
    @Provides
    @Singleton
    fun provideLocalStorage(
        secureLocalStorage: SecureLocalStorage
    ): LocalStorage {
        return LocalStorage(secureLocalStorage)
    }
    
    /**
     * Provides the UserSettingsManager singleton
     */
    @Provides
    @Singleton
    fun provideUserSettingsManager(
        secureLocalStorage: SecureLocalStorage
    ): UserSettingsManager {
        return UserSettingsManager(secureLocalStorage)
    }
}

/**
 * Dagger Hilt module for binding repository interfaces to their implementations
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    /**
     * Binds HealthDataRepository to DefaultHealthDataRepository
     */
    @Binds
    @Singleton
    abstract fun bindHealthDataRepository(
        defaultHealthDataRepository: DefaultHealthDataRepository
    ): HealthDataRepository
    
    /**
     * Binds UserSettingsRepository to DefaultUserSettingsRepository
     */
    @Binds
    @Singleton
    abstract fun bindUserSettingsRepository(
        defaultUserSettingsRepository: DefaultUserSettingsRepository
    ): UserSettingsRepository
}
