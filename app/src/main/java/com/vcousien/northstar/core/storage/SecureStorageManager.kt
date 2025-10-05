package com.vcousien.northstar.core.storage

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manager for securely storing sensitive data using Android Keystore
 *
 * This class provides methods to save, load, and delete data using encrypted storage.
 * It handles both raw strings and serializable objects.
 */
@Singleton
class SecureStorageManager @Inject constructor(
    private val context: Context
) {
    
    // MARK: - Error Definitions
    
    /**
     * Errors that can occur during secure storage operations
     */
    sealed class SecureStorageError : Exception() {
        /** Error during data serialization/deserialization */
        object DataConversionError : SecureStorageError()
        
        /** The requested item was not found in storage */
        object ItemNotFound : SecureStorageError()
        
        /** Error during encryption/decryption operation */
        data class EncryptionError(override val message: String) : SecureStorageError()
        
        /** An unknown error occurred */
        data class UnknownError(override val message: String) : SecureStorageError()
    }
    
    // MARK: - Private Properties
    
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
    
    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
    
    private val sharedPreferences by lazy {
        try {
            EncryptedSharedPreferences.create(
                "northstar_secure_prefs",
                masterKeyAlias,
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            throw SecureStorageError.EncryptionError("Failed to initialize encrypted storage: ${e.message}")
        }
    }
    
    // MARK: - Save Methods
    
    /**
     * Saves a string value to secure storage
     * @param key The key to store the value under
     * @param value The string value to store
     * @throws SecureStorageError if the operation fails
     */
    @Throws(SecureStorageError::class)
    suspend fun saveString(key: String, value: String) = withContext(Dispatchers.IO) {
        try {
            sharedPreferences.edit()
                .putString(key, value)
                .apply()
        } catch (e: Exception) {
            throw SecureStorageError.UnknownError("Failed to save string: ${e.message}")
        }
    }
    
    /**
     * Saves a serializable object to secure storage
     * @param key The key to store the object under
     * @param value The serializable object to store
     * @throws SecureStorageError if the operation fails
     */
    @Throws(SecureStorageError::class)
    internal suspend inline fun <reified T> saveObject(key: String, value: T) = withContext(Dispatchers.IO) {
        try {
            val jsonString = json.encodeToString(value)
            sharedPreferences.edit()
                .putString(key, jsonString)
                .apply()
        } catch (e: Exception) {
            when (e) {
                is kotlinx.serialization.SerializationException -> 
                    throw SecureStorageError.DataConversionError
                else -> 
                    throw SecureStorageError.UnknownError("Failed to save object: ${e.message}")
            }
        }
    }
    
    /**
     * Saves an array of serializable objects to secure storage
     * @param key The key to store the array under
     * @param values The array of serializable objects to store
     * @throws SecureStorageError if the operation fails
     */
    @Throws(SecureStorageError::class)
    internal suspend inline fun <reified T> saveArray(key: String, values: List<T>) = withContext(Dispatchers.IO) {
        saveObject(key, values)
    }
    
    // MARK: - Load Methods
    
    /**
     * Loads a string value from secure storage
     * @param key The key to load the value from
     * @return The stored string value
     * @throws SecureStorageError if the operation fails or item not found
     */
    @Throws(SecureStorageError::class)
    suspend fun loadString(key: String): String = withContext(Dispatchers.IO) {
        try {
            sharedPreferences.getString(key, null)
                ?: throw SecureStorageError.ItemNotFound
        } catch (e: SecureStorageError) {
            throw e
        } catch (e: Exception) {
            throw SecureStorageError.UnknownError("Failed to load string: ${e.message}")
        }
    }
    
    /**
     * Loads a serializable object from secure storage
     * @param key The key to load the object from
     * @return The deserialized object
     * @throws SecureStorageError if the operation fails or item not found
     */
    @Throws(SecureStorageError::class)
    internal suspend inline fun <reified T> loadObject(key: String): T = withContext(Dispatchers.IO) {
        try {
            val jsonString = sharedPreferences.getString(key, null)
                ?: throw SecureStorageError.ItemNotFound
            
            json.decodeFromString<T>(jsonString)
        } catch (e: SecureStorageError) {
            throw e
        } catch (e: Exception) {
            when (e) {
                is kotlinx.serialization.SerializationException -> 
                    throw SecureStorageError.DataConversionError
                else -> 
                    throw SecureStorageError.UnknownError("Failed to load object: ${e.message}")
            }
        }
    }
    
    /**
     * Loads an array of serializable objects from secure storage
     * @param key The key to load the array from
     * @return The array of deserialized objects
     * @throws SecureStorageError if the operation fails or item not found
     */
    @Throws(SecureStorageError::class)
    internal suspend inline fun <reified T> loadArray(key: String): List<T> = withContext(Dispatchers.IO) {
        loadObject<List<T>>(key)
    }
    
    // MARK: - Optional Load Methods (return null if not found)
    
    /**
     * Loads a string value from secure storage, returning null if not found
     * @param key The key to load the value from
     * @return The stored string value, or null if not found
     */
    suspend fun loadStringOrNull(key: String): String? = withContext(Dispatchers.IO) {
        try {
            loadString(key)
        } catch (e: SecureStorageError.ItemNotFound) {
            null
        }
    }
    
    /**
     * Loads a serializable object from secure storage, returning null if not found
     * @param key The key to load the object from
     * @return The deserialized object, or null if not found
     */
    suspend internal inline fun <reified T> loadObjectOrNull(key: String): T? = withContext(Dispatchers.IO) {
        try {
            loadObject<T>(key)
        } catch (e: SecureStorageError.ItemNotFound) {
            null
        }
    }
    
    /**
     * Loads an array of serializable objects from secure storage, returning empty list if not found
     * @param key The key to load the array from
     * @return The array of deserialized objects, or empty list if not found
     */
    suspend internal inline fun <reified T> loadArrayOrEmpty(key: String): List<T> = withContext(Dispatchers.IO) {
        try {
            loadArray<T>(key)
        } catch (e: SecureStorageError.ItemNotFound) {
            emptyList()
        }
    }
    
    // MARK: - Delete Methods
    
    /**
     * Deletes an item from secure storage
     * @param key The key of the item to delete
     * @throws SecureStorageError if the operation fails
     */
    @Throws(SecureStorageError::class)
    suspend fun delete(key: String) = withContext(Dispatchers.IO) {
        try {
            sharedPreferences.edit()
                .remove(key)
                .apply()
        } catch (e: Exception) {
            throw SecureStorageError.UnknownError("Failed to delete item: ${e.message}")
        }
    }
    
    /**
     * Checks if a key exists in secure storage
     * @param key The key to check
     * @return true if the key exists, false otherwise
     */
    suspend fun contains(key: String): Boolean = withContext(Dispatchers.IO) {
        try {
            sharedPreferences.contains(key)
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Clears all data from secure storage
     * This is a destructive operation that cannot be undone
     */
    suspend fun clearAll() = withContext(Dispatchers.IO) {
        try {
            sharedPreferences.edit()
                .clear()
                .apply()
        } catch (e: Exception) {
            throw SecureStorageError.UnknownError("Failed to clear all data: ${e.message}")
        }
    }
}
