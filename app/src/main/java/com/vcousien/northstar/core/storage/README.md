# NorthStar Android - Secure Storage Implementation

## Overview

This document describes the secure storage implementation for the NorthStar Android app, which provides encrypted storage for sensitive health data using Android Keystore and secure preferences management.

## Architecture

### Core Components

#### 1. SecureStorageManager
- **Purpose**: Low-level secure storage using Android Keystore
- **Features**: 
  - AES-256 encryption for all stored data
  - Serialization support for complex objects
  - Thread-safe operations using coroutines
  - Comprehensive error handling

#### 2. SecureLocalStorage
- **Purpose**: High-level storage abstraction matching iOS implementation
- **Features**:
  - Encrypted storage for sensitive health data (mood, sleep, medication records)
  - DataStore for non-sensitive app preferences
  - Flow-based reactive data access
  - Consistent API with iOS version

#### 3. LocalStorage (UserDefaults Equivalent)
- **Purpose**: Abstraction layer providing iOS LocalStorage compatibility
- **Features**:
  - Simple facade over SecureLocalStorage
  - Direct method mapping to iOS implementation
  - Maintains same API signatures where possible
  - Suspension-based methods for async operations

#### 4. UserSettingsManager (iOS UserSettings Equivalent)
- **Purpose**: Reactive user settings management matching iOS @Published behavior
- **Features**:
  - StateFlow properties for reactive UI updates
  - Individual setting update methods
  - Automatic persistence to secure storage
  - Synchronous getters for immediate access

#### 5. Repository Pattern
- **HealthDataRepository**: Interface for health data operations
- **UserSettingsRepository**: Interface for user preferences
- **DefaultHealthDataRepository**: Implementation using SecureLocalStorage
- **DefaultUserSettingsRepository**: Implementation using SecureLocalStorage

## Security Features

### Data Classification

**Sensitive Data (Encrypted Storage)**:
- Mood entries with levels, energy, anxiety ratings
- Sleep tracking data with quality metrics
- Medication definitions and usage logs
- Personal health notes and observations

**Non-Sensitive Data (DataStore)**:
- User interface preferences
- Notification settings
- Feature toggles (tracking enabled/disabled)
- Onboarding completion status

### Encryption Details

- **Algorithm**: AES-256-GCM encryption
- **Key Management**: Android Keystore hardware-backed keys
- **Key Derivation**: MasterKeys with AES256_GCM_SPEC
- **Storage**: EncryptedSharedPreferences with dual encryption:
  - Keys encrypted with AES256_SIV
  - Values encrypted with AES256_GCM

## Data Models

### MoodEntry
```kotlin
data class MoodEntry(
    val id: String,
    val date: Long, // Epoch milliseconds
    val level: MoodLevel, // 0-8 scale enum
    val notes: String,
    val energyLevel: Double, // 0-10 scale
    val anxietyLevel: Double, // 0-10 scale
    val sleepQuality: Double?, // Optional 0-10 scale
    val irritabilityLevel: Double? // Optional 0-10 scale
)
```

### SleepEntry
```kotlin
data class SleepEntry(
    val id: String,
    val date: Long,
    val durationSeconds: Long,
    val quality: Double, // 0-10 scale
    val startTime: Long,
    val endTime: Long,
    val interruptions: Int,
    val notes: String
)
```

### Medication & MedicationEntry
```kotlin
data class Medication(
    val id: String,
    val name: String,
    val type: MedicationType,
    val dosage: String,
    val frequency: String,
    val instructions: String,
    val isActive: Boolean,
    val dateAdded: Long
)

data class MedicationEntry(
    val id: String,
    val medicationId: String,
    val dateTaken: Long,
    val dosageTaken: String,
    val notes: String,
    val takenAsPresc: Boolean,
    val sideEffects: String
)
```

### UserSettings
```kotlin
data class UserSettings(
    val userName: String,
    val notificationsEnabled: Boolean,
    val dailyReminderTime: LocalTime,
    val securityEnabled: Boolean,
    val energyTrackingEnabled: Boolean,
    val anxietyTrackingEnabled: Boolean,
    val sleepTrackingEnabled: Boolean,
    val irritabilityTrackingEnabled: Boolean,
    val hasCompletedOnboarding: Boolean
)
```

## Usage Examples

### Dependency Injection Setup
```kotlin
@HiltAndroidApp
class NorthStarApplication : Application()

// In your Activity/Fragment/ViewModel
@Inject
lateinit var localStorage: LocalStorage

@Inject
lateinit var userSettingsManager: UserSettingsManager

@Inject
lateinit var healthDataRepository: HealthDataRepository

@Inject 
lateinit var userSettingsRepository: UserSettingsRepository
```

### iOS-Style LocalStorage Usage (Direct Equivalent)
```kotlin
// Using LocalStorage abstraction (matches iOS LocalStorage exactly)
class SomeViewModel @Inject constructor(
    private val localStorage: LocalStorage
) : ViewModel() {
    
    fun saveMoodData(entries: List<MoodEntry>) {
        viewModelScope.launch {
            localStorage.saveMoodEntries(entries)
        }
    }
    
    fun loadMoodData(): List<MoodEntry> {
        return runBlocking {
            localStorage.loadMoodEntries()
        }
    }
    
    fun clearAllAppData() {
        viewModelScope.launch {
            localStorage.clearAllData() // Equivalent to iOS LocalStorage.clearAllData()
        }
    }
}
```

### iOS-Style UserSettings Management (Reactive Equivalent)
```kotlin
// Using UserSettingsManager (matches iOS UserSettings @Published behavior)
class SettingsViewModel @Inject constructor(
    private val userSettingsManager: UserSettingsManager
) : ViewModel() {
    
    // Reactive properties (equivalent to iOS @Published)
    val userName = userSettingsManager.userName
    val notificationsEnabled = userSettingsManager.notificationsEnabled
    val hasCompletedOnboarding = userSettingsManager.hasCompletedOnboarding
    
    // Update methods (equivalent to iOS UserSettings methods)
    fun updateUserName(newName: String) {
        userSettingsManager.updateUserName(newName) // Auto-persists like iOS
    }
    
    fun completeOnboarding() {
        userSettingsManager.completeOnboarding() // Equivalent to iOS completeOnboarding()
    }
    
    fun resetToDefaults() {
        userSettingsManager.resetToDefaults() // Equivalent to iOS resetToDefaults()
    }
    
    // Immediate access (equivalent to iOS direct property access)
    fun getCurrentUserName(): String {
        return userSettingsManager.getCurrentUserName()
    }
}
```

### Compose UI Integration
```kotlin
@Composable
fun SettingsScreen(
    userSettingsManager: UserSettingsManager = hiltViewModel<SomeViewModel>().userSettingsManager
) {
    // Collect StateFlow as Compose State (equivalent to iOS @Published observation)
    val userName by userSettingsManager.userName.collectAsState()
    val notificationsEnabled by userSettingsManager.notificationsEnabled.collectAsState()
    val hasCompletedOnboarding by userSettingsManager.hasCompletedOnboarding.collectAsState()
    
    Column {
        TextField(
            value = userName,
            onValueChange = { userSettingsManager.updateUserName(it) },
            label = { Text("User Name") }
        )
        
        Switch(
            checked = notificationsEnabled,
            onCheckedChange = { userSettingsManager.updateNotificationsEnabled(it) }
        )
        
        if (!hasCompletedOnboarding) {
            Button(
                onClick = { userSettingsManager.completeOnboarding() }
            ) {
                Text("Complete Onboarding")
            }
        }
    }
}
```

### Storing Health Data
```kotlin
// Add a mood entry
val moodEntry = MoodEntry(
    date = Clock.System.now(),
    level = MoodLevel.ELEVATED,
    notes = "Feeling great today!",
    energyLevel = 8.0,
    anxietyLevel = 2.0
)
healthDataRepository.addMoodEntry(moodEntry)

// Add a sleep entry
val sleepEntry = SleepEntry(
    date = Clock.System.now(),
    startTime = Clock.System.now().minus(8.hours),
    endTime = Clock.System.now(),
    quality = 7.5,
    interruptions = 1,
    notes = "Woke up once during the night"
)
healthDataRepository.addSleepEntry(sleepEntry)
```

### Managing User Settings
```kotlin
// Update user preferences
userSettingsRepository.updateUserName("Jane Doe")
userSettingsRepository.updateNotificationsEnabled(true)

// Get reactive updates
userSettingsRepository.getUserSettingsFlow()
    .collect { settings ->
        // UI updates based on settings changes
        updateUI(settings)
    }
```

### Loading Data
```kotlin
// Load all mood entries
val moodEntries = healthDataRepository.loadMoodEntries()

// Load medications
val medications = healthDataRepository.loadMedications()

// Load user settings
val userSettings = userSettingsRepository.loadUserSettings()
```

## Error Handling

### SecureStorageError Types
- `DataConversionError`: Serialization/deserialization issues
- `ItemNotFound`: Requested data doesn't exist
- `EncryptionError`: Keystore or encryption problems  
- `UnknownError`: Unexpected storage failures

### Best Practices
```kotlin
try {
    val data = secureStorageManager.loadObject<MoodEntry>("mood_entry_key")
    // Process data
} catch (e: SecureStorageError.ItemNotFound) {
    // Handle missing data gracefully
} catch (e: SecureStorageError.DataConversionError) {
    // Handle corruption or format changes
} catch (e: SecureStorageError.EncryptionError) {
    // Handle security/keystore issues
}
```

## Performance Considerations

### Optimizations
- **Batch Operations**: Save/load entire lists rather than individual items
- **Background Threading**: All storage operations use `Dispatchers.IO`
- **Caching**: Repository layer provides in-memory caching opportunities
- **Lazy Loading**: Encrypted preferences initialized only when needed

### Monitoring
- Log storage operation timings in debug builds
- Monitor for encryption/decryption performance issues
- Track data size growth over time

## Testing

### Unit Tests
- Mock `SecureLocalStorage` for repository tests
- Test data serialization/deserialization
- Verify proper error handling
- Test repository business logic (add/update/delete operations)

### Integration Tests
- Test actual encryption/decryption flows
- Verify data persistence across app restarts
- Test migration scenarios

## Migration from iOS

### Key Equivalences

| iOS Component | Android Equivalent | Notes |
|---------------|-------------------|-------|
| `UserSettings` (ObservableObject) | `UserSettingsManager` | StateFlow properties instead of @Published |
| `LocalStorage` | `LocalStorage` | Direct API mapping, suspension-based |
| `SecureLocalStorage` | `SecureLocalStorage` | Core implementation, similar architecture |
| `@Published var userName` | `val userName: StateFlow<String>` | Reactive property observation |
| `userSettings.userName = "John"` | `userSettingsManager.updateUserName("John")` | Method-based updates with auto-persistence |
| `userSettings.completeOnboarding()` | `userSettingsManager.completeOnboarding()` | Direct method mapping |
| `localStorage.clearAllData()` | `localStorage.clearAllData()` | Exact same method signature |

### Usage Pattern Comparison

**iOS SwiftUI:**
```swift
struct SettingsView: View {
    @StateObject private var userSettings = UserSettings()
    @StateObject private var storage = LocalStorage()
    
    var body: some View {
        VStack {
            TextField("Name", text: $userSettings.userName)
            Toggle("Notifications", isOn: $userSettings.notificationsEnabled)
        }
        .onAppear {
            storage.clearAllData() // If needed
        }
    }
}
```

**Android Compose:**
```kotlin
@Composable
fun SettingsScreen(
    userSettingsManager: UserSettingsManager,
    localStorage: LocalStorage
) {
    val userName by userSettingsManager.userName.collectAsState()
    val notificationsEnabled by userSettingsManager.notificationsEnabled.collectAsState()
    
    Column {
        TextField(
            value = userName,
            onValueChange = { userSettingsManager.updateUserName(it) }
        )
        Switch(
            checked = notificationsEnabled,
            onCheckedChange = { userSettingsManager.updateNotificationsEnabled(it) }
        )
    }
    
    LaunchedEffect(Unit) {
        localStorage.clearAllData() // If needed
    }
}
```

### Key Differences
1. **Serialization**: Uses Kotlinx.serialization instead of Codable
2. **Date Handling**: Uses kotlinx-datetime with epoch milliseconds storage
3. **Threading**: Coroutines instead of Swift async/await
4. **Storage Backend**: EncryptedSharedPreferences + DataStore vs Keychain + UserDefaults

### Data Compatibility
- JSON serialization format maintains cross-platform compatibility
- Date values stored as epoch milliseconds for universal parsing
- Enum values use consistent string representations

## Security Compliance

### Standards Met
- **AES-256 encryption** for sensitive data
- **Hardware-backed keystore** when available
- **Key rotation** support through MasterKeys
- **Secure key derivation** using Android Security library

### Audit Trail
- All storage operations are logged (debug builds only)
- Clear data classification and handling
- Separation of sensitive vs non-sensitive data storage

## Future Enhancements

### Planned Features
1. **Biometric Authentication**: Lock access behind fingerprint/face unlock
2. **Cloud Sync**: Encrypted synchronization across devices
3. **Data Export**: Secure export functionality for data portability
4. **Backup/Restore**: Encrypted backup with user-controlled keys

### Scalability
- Repository pattern allows easy addition of new data types
- Modular storage backends enable future storage optimizations
- Clean separation allows integration testing of different storage strategies

## Conclusion

The secure storage implementation provides a robust, encrypted foundation for NorthStar's sensitive health data while maintaining performance and usability. The architecture closely mirrors the iOS implementation to ensure consistency across platforms while leveraging Android-specific security features.
