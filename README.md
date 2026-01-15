
# FocusHero

FocusHero is an Android productivity application developed as part of the **Android Development** course.
The app helps users improve their focus habits through timed focus sessions, visual feedback,
statistics, and a lightweight gamification layer.

The application is fully **offline-first**: all data is stored locally,
allowing the app to function correctly and completely without a network connection.

---

## Features

-  **Focus sessions** with start, pause, resume, and stop support
-  **Gamification** with points, levels, achievements, and a visual focus companion
-  **Statistics dashboard**
  - 7 / 14 / 30 day range selection
  - daily and weekly aggregations
  - charts for focused time and earned points
  - comparison of completed vs stopped sessions
-  **Visual focus companion** (Lottie animations)
  - reacts to focus state (idle / focusing / paused)
  - temporary feedback on completion (victory) or early stop (tired)
-  **Theme & accent customization**
  - System / Light / Dark
  - Accent color applied app-wide in real time
-  **Offline-first data storage**
  - all data stored locally using Room
  - no backend or account required
-  **Deterministic demo data**
  - used for development and meaningful statistics

---

## Architecture Overview

The app follows a **Jetpack Compose + MVVM-based architecture** with a clear separation of responsibilities.

### UI Layer
- Jetpack Compose screens
- Reusable composables
- Stateless where possible

### ViewModels
- State management using `StateFlow`
- Timer logic and user interaction handling

### Domain
- Pure business logic (points and level calculations)
- No Android framework dependencies

### Data
- Room database
- Repositories
- DataStore Preferences

The architecture is intentionally kept simple,
focusing on readability, predictability, and correct state handling.

---

## Technologies Used

- Kotlin 2.x
- Jetpack Compose
- Material 3
- Navigation Compose
- ViewModel + StateFlow
- Room (KSP)
- DataStore Preferences
- Lottie
- Vico Charts
- Android Studio

---

## File Structure

```text
app/src/main/java/com/bcornet/focushero/
│
├── data/
│   ├── db/
│   │   ├── AppDatabase.kt
│   │   ├── FocusSessionDao.kt
│   │   └── FocusSessionEntity.kt
│   │
│   ├── mappers/
│   │   └── FocusSessionMappers.kt
│   │
│   ├── preferences/
│   │   └── AppPreferences.kt
│   │
│   ├── repo/
│   │   └── FocusSessionRepository.kt
│   │
│   ├── DatabaseProvider.kt
│   └── DemoDataSeeder.kt
│
├── domain/
│   ├── logic/
│   │   ├── LevelCalculator.kt
│   │   └── PointsCalculator.kt
│   │
│   └── model/
│       └── FocusSession.kt
│
├── ui/
│   ├── components/
│   │   ├── FocusCompanion.kt
│   │   └── FocusSessionCard.kt
│   │
│   ├── navigation/
│   │   └── AppNavHost.kt
│   │
│   ├── screens/
│   │   ├── focus/
│   │   │   ├── FocusRoute.kt
│   │   │   ├── FocusScreen.kt
│   │   │   ├── FocusUiState.kt
│   │   │   └── FocusViewModel.kt
│   │   │
│   │   ├── sessions/
│   │   │   ├── SessionsRoute.kt
│   │   │   ├── SessionsScreen.kt
│   │   │   ├── SessionsUiState.kt
│   │   │   └── SessionsViewModel.kt
│   │   │
│   │   ├── stats/
│   │   │   ├── StatsRoute.kt
│   │   │   ├── StatsScreen.kt
│   │   │   ├── StatsUiState.kt
│   │   │   └── StatsViewModel.kt
│   │   │
│   │   └── profile/
│   │       ├── ProfileRoute.kt
│   │       ├── ProfileScreen.kt
│   │       ├── ProfileUiState.kt
│   │       └── ProfileViewModel.kt
│   │
│   ├── MainScaffold.kt
│   └── theme/
│       ├── Color.kt
│       ├── Theme.kt
│       └── Type.kt
│
├── MainActivity.kt
│
└── res/
    ├── raw/            # Lottie animations
    ├── values/         # strings, themes, colors
    └── mipmap-*        # adaptive launcher icons
```

---

## Core Design Decisions

### Offline-first
All focus sessions, statistics, and settings are stored locally.
The app remains fully functional without an internet connection.

### Gamification without pressure
Levels and points are designed to encourage consistency,
not competition or pressure.

### Visual feedback
The focus companion acts as a state indicator and motivational feedback,
keeping textual explanations to a minimum.

---

## UX & Design Principles

- Minimalistic layout
- Clear visual hierarchy
- Limited text, meaningful visuals
- Consistent spacing and typography
- Accent color highlights actions and progress

---

## Build & Run

1. Open the project in **Android Studio**
2. Sync Gradle dependencies
3. Select an emulator or physical device (API 26+)
4. Run the application

Demo data is automatically seeded in **debug builds**.

---

## Notes for Evaluation

- Strong focus on code quality, structure, and UX
- All architectural decisions are intentional and documented
- No backend or authentication by design
- Fully local and deterministic behavior

---

## Author

Brent Cornet  
Applied Computer Science – Software Engineering  
Erasmushogeschool Brussel
