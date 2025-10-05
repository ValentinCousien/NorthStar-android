package com.vcousien.northstar.core.storage

import com.vcousien.northstar.core.models.*
import io.mockk.*
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalTime
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for SecureStorageManager
 */
class SecureStorageManagerTest {
    
    private lateinit var mockContext: android.content.Context
    private lateinit var secureStorageManager: SecureStorageManager
    
    @Before
    fun setup() {
        mockContext = mockk(relaxed = true)
        // Note: In real tests, you'd need to mock EncryptedSharedPreferences
        // For this example, we're showing the test structure
    }
    
    @Test
    fun `saveString should store string value`() = runTest {
        // This test would require proper mocking of EncryptedSharedPreferences
        // Implementation would test saving and retrieving string values
    }
    
    @Test
    fun `saveObject should serialize and store object`() = runTest {
        // Test serialization and storage of objects
    }
    
    @Test
    fun `loadObject should deserialize stored object`() = runTest {
        // Test loading and deserialization of stored objects
    }
    
    @Test
    fun `delete should remove stored item`() = runTest {
        // Test deletion of stored items
    }
}

/**
 * Unit tests for DefaultHealthDataRepository
 */
class DefaultHealthDataRepositoryTest {
    
    private lateinit var mockSecureLocalStorage: SecureLocalStorage
    private lateinit var healthDataRepository: DefaultHealthDataRepository
    
    @Before
    fun setup() {
        mockSecureLocalStorage = mockk(relaxed = true)
        healthDataRepository = DefaultHealthDataRepository(mockSecureLocalStorage)
    }
    
    @Test
    fun `addMoodEntry should add entry to existing list`() = runTest {
        // Arrange
        val existingEntries = listOf(
            MoodEntry(
                id = "1",
                date = Clock.System.now(),
                level = MoodLevel.NEUTRAL,
                notes = "Test entry 1"
            )
        )
        val newEntry = MoodEntry(
            id = "2", 
            date = Clock.System.now(),
            level = MoodLevel.ELEVATED,
            notes = "Test entry 2"
        )
        
        coEvery { mockSecureLocalStorage.loadMoodEntries() } returns existingEntries
        coEvery { mockSecureLocalStorage.saveMoodEntries(any()) } just Runs
        
        // Act
        healthDataRepository.addMoodEntry(newEntry)
        
        // Assert
        coVerify { 
            mockSecureLocalStorage.saveMoodEntries(
                match { entries ->
                    entries.size == 2 && 
                    entries.any { it.id == "1" } &&
                    entries.any { it.id == "2" }
                }
            ) 
        }
    }
    
    @Test
    fun `deleteMoodEntry should remove entry from list`() = runTest {
        // Arrange
        val existingEntries = listOf(
            MoodEntry(id = "1", date = Clock.System.now(), level = MoodLevel.NEUTRAL),
            MoodEntry(id = "2", date = Clock.System.now(), level = MoodLevel.ELEVATED)
        )
        
        coEvery { mockSecureLocalStorage.loadMoodEntries() } returns existingEntries
        coEvery { mockSecureLocalStorage.saveMoodEntries(any()) } just Runs
        
        // Act
        healthDataRepository.deleteMoodEntry("1")
        
        // Assert
        coVerify { 
            mockSecureLocalStorage.saveMoodEntries(
                match { entries ->
                    entries.size == 1 && 
                    entries.none { it.id == "1" } &&
                    entries.any { it.id == "2" }
                }
            ) 
        }
    }
    
    @Test
    fun `addSleepEntry should add entry to existing list`() = runTest {
        // Arrange
        val existingEntries = listOf(
            SleepEntry(
                id = "1",
                date = Clock.System.now(),
                startTime = Clock.System.now(),
                endTime = Clock.System.now(),
                quality = 7.5
            )
        )
        val newEntry = SleepEntry(
            id = "2",
            date = Clock.System.now(),
            startTime = Clock.System.now(),
            endTime = Clock.System.now(),
            quality = 8.0
        )
        
        coEvery { mockSecureLocalStorage.loadSleepEntries() } returns existingEntries
        coEvery { mockSecureLocalStorage.saveSleepEntries(any()) } just Runs
        
        // Act
        healthDataRepository.addSleepEntry(newEntry)
        
        // Assert
        coVerify { 
            mockSecureLocalStorage.saveSleepEntries(
                match { entries ->
                    entries.size == 2 && 
                    entries.any { it.id == "1" } &&
                    entries.any { it.id == "2" }
                }
            ) 
        }
    }
    
    @Test
    fun `addMedication should add medication to existing list`() = runTest {
        // Arrange
        val existingMedications = listOf(
            Medication(
                id = "1",
                name = "Medication 1",
                type = MedicationType.ANTIDEPRESSANT,
                dosage = "10mg",
                frequency = "Daily",
                dateAdded = Clock.System.now()
            )
        )
        val newMedication = Medication(
            id = "2",
            name = "Medication 2",
            type = MedicationType.MOOD_STABILIZER,
            dosage = "5mg",
            frequency = "Twice daily",
            dateAdded = Clock.System.now()
        )
        
        coEvery { mockSecureLocalStorage.loadMedications() } returns existingMedications
        coEvery { mockSecureLocalStorage.saveMedications(any()) } just Runs
        
        // Act
        healthDataRepository.addMedication(newMedication)
        
        // Assert
        coVerify { 
            mockSecureLocalStorage.saveMedications(
                match { medications ->
                    medications.size == 2 && 
                    medications.any { it.id == "1" } &&
                    medications.any { it.id == "2" }
                }
            ) 
        }
    }
    
    @Test
    fun `clearAllHealthData should clear all data types`() = runTest {
        // Act
        healthDataRepository.clearAllHealthData()
        
        // Assert
        coVerify { 
            mockSecureLocalStorage.saveMoodEntries(emptyList())
            mockSecureLocalStorage.saveSleepEntries(emptyList())
            mockSecureLocalStorage.saveMedications(emptyList())
            mockSecureLocalStorage.saveMedicationEntries(emptyList())
        }
    }
}

/**
 * Unit tests for DefaultUserSettingsRepository
 */
class DefaultUserSettingsRepositoryTest {
    
    private lateinit var mockSecureLocalStorage: SecureLocalStorage
    private lateinit var userSettingsRepository: DefaultUserSettingsRepository
    
    @Before
    fun setup() {
        mockSecureLocalStorage = mockk(relaxed = true)
        userSettingsRepository = DefaultUserSettingsRepository(mockSecureLocalStorage)
    }
    
    @Test
    fun `updateUserName should update only user name`() = runTest {
        // Arrange
        val currentSettings = UserSettings(
            userName = "Old Name",
            notificationsEnabled = true,
            securityEnabled = false
        )
        val expectedSettings = currentSettings.copy(userName = "New Name")
        
        coEvery { mockSecureLocalStorage.loadUserSettings() } returns currentSettings
        coEvery { mockSecureLocalStorage.saveUserSettings(any()) } just Runs
        
        // Act
        userSettingsRepository.updateUserName("New Name")
        
        // Assert
        coVerify { 
            mockSecureLocalStorage.saveUserSettings(expectedSettings)
        }
    }
    
    @Test
    fun `updateNotificationsEnabled should update only notifications setting`() = runTest {
        // Arrange
        val currentSettings = UserSettings(
            userName = "Test User",
            notificationsEnabled = false,
            securityEnabled = false
        )
        val expectedSettings = currentSettings.copy(notificationsEnabled = true)
        
        coEvery { mockSecureLocalStorage.loadUserSettings() } returns currentSettings
        coEvery { mockSecureLocalStorage.saveUserSettings(any()) } just Runs
        
        // Act
        userSettingsRepository.updateNotificationsEnabled(true)
        
        // Assert
        coVerify { 
            mockSecureLocalStorage.saveUserSettings(expectedSettings)
        }
    }
    
    @Test
    fun `updateSecurityEnabled should update only security setting`() = runTest {
        // Arrange
        val currentSettings = UserSettings(
            userName = "Test User",
            notificationsEnabled = true,
            securityEnabled = false
        )
        val expectedSettings = currentSettings.copy(securityEnabled = true)
        
        coEvery { mockSecureLocalStorage.loadUserSettings() } returns currentSettings
        coEvery { mockSecureLocalStorage.saveUserSettings(any()) } just Runs
        
        // Act
        userSettingsRepository.updateSecurityEnabled(true)
        
        // Assert
        coVerify { 
            mockSecureLocalStorage.saveUserSettings(expectedSettings)
        }
    }
}
