# NorthStar Android

<div align="center">
  <img src="https://img.shields.io/badge/Platform-Android-green.svg" alt="Platform">
  <img src="https://img.shields.io/badge/Kotlin-1.9+-purple.svg" alt="Kotlin">
  <img src="https://img.shields.io/badge/Compose-1.5+-blue.svg" alt="Compose">
  <img src="https://img.shields.io/badge/MinSDK-24-orange.svg" alt="MinSDK">
</div>

## Overview

NorthStar is a comprehensive mental health tracking application for Android, designed to help users monitor their mood, sleep patterns, and medication adherence. This is a port of the iOS SwiftUI application to Android using Jetpack Compose and modern Android development practices.

## Features

### 🎭 Mood Tracking
- **9-Level Mood Scale**: Track mood from severely depressed to manic
- **Energy & Anxiety Levels**: Monitor energy (0-10) and anxiety (0-10)
- **Sleep Quality**: Optional sleep quality tracking with mood entries
- **Notes**: Add contextual notes to each mood entry
- **Trend Visualization**: View mood patterns over time
- **History**: Browse and edit past mood entries

### 😴 Sleep Tracking
- **Sleep Duration**: Record sleep and wake times
- **Sleep Quality**: Rate sleep quality (0-10 scale)
- **Interruptions**: Track number of sleep interruptions
- **Statistics**: View average sleep duration and quality
- **Recent History**: Quick access to recent sleep entries
- **Date/Time Picker**: Material Design 3 time picker with past-date validation

### 💊 Medication Management
- **Medication Database**: Maintain list of medications with dosage info
- **Adherence Tracking**: Log when medications are taken
- **Reminders**: Schedule medication reminder notifications
- **Statistics**: View adherence rates and streaks
- **Side Effects**: Track medication side effects (severity-based)
- **Export**: Export medication data in multiple formats

### 📊 Insights & Analytics
- **Mood Trends**: Visualize mood patterns over time
- **Sleep Correlation**: Analyze mood-sleep relationships
- **Medication Impact**: Track how medications affect mood
- **Pattern Recognition**: Identify recurring patterns

### ⚙️ Settings & Customization
- **User Profile**: Customize display name
- **Notifications**: Configure daily reminder times
- **Tracking Preferences**: Enable/disable specific tracking features
  - Energy level tracking
  - Anxiety level tracking
  - Sleep tracking
  - Irritability tracking
- **Security**: Optional biometric authentication (fingerprint/face)
- **Data Management**: Export and clear data options

### 🎨 Design System
- **Material Design 3**: Modern Android UI components
- **Dark Theme Support**: Automatic theme switching
- **Custom Components**: NSCard, NSButton, NSSlider, NSEmptyState
- **Consistent Typography**: Standardized text styles throughout
- **Color-Coded Moods**: Visual mood indicators with distinct colors

## Architecture

### MVVM Pattern
The app follows Model-View-ViewModel architecture:
- **Models**: Data classes for entries and settings
- **ViewModels**: Business logic and state management
- **Views**: Jetpack Compose UI components

### Project Structure
```
app/src/main/java/com/vcousien/northstar/
├── app/                          # Main app container and navigation
│   ├── AppViewModel.kt          # Global app state management
│   ├── ContentView.kt           # Root app container with flow control
│   ├── MainTabView.kt           # Bottom navigation container
│   ├── HomeView.kt              # Home screen with today's summary
│   └── AppTab.kt                # Tab definitions
├── core/                         # Core functionality
│   ├── authentication/          # Biometric authentication
│   ├── designsystem/            # Design tokens, components, typography
│   ├── models/                  # Data models (MoodEntry, SleepEntry, etc.)
│   └── storage/                 # Local storage and persistence
├── features/                     # Feature modules
│   ├── mood/                    # Mood tracking feature
│   │   ├── models/              # Mood-specific models
│   │   ├── viewmodels/          # MoodTrackingViewModel
│   │   └── views/               # Mood UI components
│   ├── sleep/                   # Sleep tracking feature
│   │   ├── viewmodels/          # SleepTrackingViewModel
│   │   └── views/               # Sleep UI components
│   ├── medication/              # Medication management
│   │   ├── models/              # Medication models
│   │   ├── viewmodels/          # MedicationViewModel
│   │   ├── views/               # Medication UI components
│   │   └── reminders/           # Notification scheduling
│   ├── insights/                # Analytics and insights
│   │   └── views/               # Insights UI components
│   ├── onboarding/              # First-launch onboarding
│   │   └── views/               # Onboarding screens
│   └── settings/                # App settings
│       └── views/               # Settings UI components
└── MainActivity.kt              # Main activity entry point
```

## Tech Stack

### Core Technologies
- **Kotlin**: Modern, concise programming language
- **Jetpack Compose**: Declarative UI framework
- **Coroutines**: Asynchronous programming
- **Flow**: Reactive data streams

### Architecture Components
- **ViewModel**: UI state management
- **Hilt**: Dependency injection
- **Navigation**: Type-safe navigation
- **DataStore**: Settings persistence

### UI/UX
- **Material Design 3**: Modern design system
- **Accompanist**: Compose extensions (Pager, SystemUI)
- **Coil**: Image loading (if needed)

### Storage
- **Room**: Local database (planned)
- **SharedPreferences**: Simple key-value storage
- **Encrypted SharedPreferences**: Secure storage for sensitive data

### Notifications
- **WorkManager**: Background task scheduling
- **NotificationManager**: Medication reminders

## Getting Started

### Prerequisites
- **Android Studio**: Hedgehog (2023.1.1) or later
- **JDK**: 17 or later
- **Gradle**: 8.0+ (included in wrapper)
- **Minimum SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/vcousien/NorthStar-android.git
   cd NorthStar-android
   ```

2. **Open in Android Studio**
   - Launch Android Studio
   - Select "Open an existing project"
   - Navigate to the cloned directory
   - Wait for Gradle sync to complete

3. **Build and Run**
   ```bash
   ./gradlew assembleDebug
   ```
   Or click the "Run" button in Android Studio

### Configuration

No special configuration is needed for development. The app uses default settings and local storage.

## Development

### Code Style
- Follow [Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use meaningful variable and function names
- Document complex logic with comments
- Keep functions small and focused

### Adding New Features

1. **Create Feature Module**
   ```kotlin
   features/
   └── newfeature/
       ├── models/
       ├── viewmodels/
       └── views/
   ```

2. **Define Models**
   ```kotlin
   data class NewEntry(
       val id: String = UUID.randomUUID().toString(),
       val date: Instant,
       // ... other fields
   )
   ```

3. **Create ViewModel**
   ```kotlin
   @HiltViewModel
   class NewFeatureViewModel @Inject constructor(
       private val localStorage: LocalStorage
   ) : ViewModel() {
       // State and business logic
   }
   ```

4. **Build UI with Compose**
   ```kotlin
   @Composable
   fun NewFeatureView(
       viewModel: NewFeatureViewModel = viewModel()
   ) {
       // Composable UI
   }
   ```

### Testing

Run tests with:
```bash
./gradlew test                 # Unit tests
./gradlew connectedAndroidTest # Instrumentation tests
```

### Debug Mode

Access the debug console by tapping the NorthStar logo in Settings 7 times. The debug console provides:
- App state inspection
- Data entry counts
- User settings values
- Reset controls (authentication, onboarding)
- Clear all data option

## Implementation Status

### ✅ Completed (Prompts 1-53)
- [x] Project structure and MVVM setup
- [x] Design system (colors, typography, components)
- [x] Authentication (biometric)
- [x] Mood tracking (complete feature)
- [x] Sleep tracking (complete feature)
- [x] Medication management (complete feature)
- [x] Insights and analytics
- [x] Navigation and main app structure
- [x] Onboarding flow
- [x] Settings and user preferences
- [x] Debug console

### 🚧 In Progress (Prompts 54-66)
- [ ] NotificationManager implementation
- [ ] Data persistence layer (Room)
- [ ] Data backup and restore
- [ ] CSV/JSON export functionality
- [ ] Unit and integration tests
- [ ] Accessibility features
- [ ] Animations and transitions
- [ ] Performance optimization

See [IMPLEMENTATION_PROGRESS.md](IMPLEMENTATION_PROGRESS.md) for detailed progress tracking.

## Data Privacy & Security

NorthStar takes privacy seriously:

- **Local Storage**: All data stored locally on device
- **No Cloud Sync**: Data never leaves your device (currently)
- **Encrypted Storage**: Sensitive data encrypted at rest
- **Biometric Protection**: Optional fingerprint/face unlock
- **No Analytics**: No tracking or analytics services
- **No Ads**: Completely ad-free

## Contributing

Contributions are welcome! Please:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- **iOS Original**: Based on the NorthStar iOS app
- **Material Design**: Google's Material Design 3 guidelines
- **Jetpack Compose**: Modern Android UI toolkit
- **Community**: Thanks to all contributors and testers

## Contact

For questions, suggestions, or issues:
- **GitHub Issues**: [Create an issue](https://github.com/vcousien/NorthStar-android/issues)
- **Email**: vcousien@example.com

## Roadmap

### Short Term
- [ ] Complete notification system
- [ ] Add Room database integration
- [ ] Implement data export (CSV/JSON)
- [ ] Add unit tests

### Medium Term
- [ ] Cloud sync with encryption
- [ ] Widget support
- [ ] Wear OS companion app
- [ ] Accessibility improvements

### Long Term
- [ ] AI-powered insights
- [ ] Care provider sharing
- [ ] Multi-language support
- [ ] Tablet optimization

---

**Built with ❤️ using Jetpack Compose and Material Design 3**
