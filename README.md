# PanicButton Android App

A simulated Android application demonstrating a Panic Button/SOS flow with nearby helper discovery, real-time status updates, and a mock location map. 

## Approach

The app is built entirely with Jetpack Compose using modern Android development practices (MVI-ish state management with StateFlow in a ViewModel). It includes:
- **Splash Screen & Preloader**: A native splash screen followed by a Compose-based animated preloader with a pulsing SOS icon.
- **Panic State Machine**: A robust `PanicViewModel` handles states like `Idle`, `Confirming` (countdown), `Searching`, `RequestSent`, `Accepted`, and `Confirmed`.
- **Mock Data Layer**: A `MockUserDataSource` simulates nearby users with hardcoded Delhi landmark coordinates. A simple `DistanceCalculator` computes the distance and ETA using the Haversine formula.
- **Glassmorphism UI**: Uses a custom dark theme with deep navy colors and semi-transparent "glass" cards for a modern, sleek aesthetic.
- **Local Database**: Room is used to persist a history of all panic requests.
- **Notifications**: Uses `NotificationCompat` with `PendingIntent` action buttons to simulate an incoming help request, allowing the helper to accept or decline directly from the notification shade.
- **Mini-Map**: A custom Canvas-based mini-map component that plots relative locations without needing a heavy Maps SDK.

## Libraries Used

- **Jetpack Compose**: For the entire UI layer (`androidx.compose.ui`, `androidx.compose.material3`, `androidx.compose.animation`).
- **ViewModel & Lifecycle**: For state management (`androidx.lifecycle.viewmodel.compose`, `androidx.lifecycle.runtime.compose`).
- **Room**: For local persistence of request history (`androidx.room`).
- **Coroutines & Flow**: For asynchronous programming and reactive UI updates (`kotlinx.coroutines`).
- **Core Splashscreen**: For the native Android 12+ splash screen experience (`androidx.core:core-splashscreen`).

## How to Run

1. Clone the repository.
2. Open the project in Android Studio (Koala or later recommended).
3. Let Gradle sync and download dependencies.
4. Build and run on an Android device or emulator (API 26+).
5. **Testing the flow**:
    - Tap the SOS button.
    - Wait for the 5-second countdown (or cancel it).
    - The app will search for the nearest online mock user.
    - A notification will appear simulating the helper's phone.
    - You can accept/decline either in the app (simulated response buttons) or via the notification.
    - Once accepted, the confirmed screen will show the helper's details, ETA, and a mini-map with location names.
