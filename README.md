
# FocusHero

FocusHero is een Android productivity applicatie ontwikkeld in het kader van het vak **Android Development**.
De app helpt gebruikers hun focusgewoontes te verbeteren via tijdsgebonden focussessies, visuele feedback,
statistieken en een lichte gamification-laag.

De applicatie is volledig **offline-first**: alle data wordt lokaal opgeslagen,
waardoor de app zonder netwerkverbinding correct en volledig blijft functioneren.

---

## Features

-  **Focus sessions** met start, pauze, hervat en stop
-  **Gamification** met punten, levels, achievements en een visuele focus companion
-  **Statistieken dashboard**
  - 7 / 14 / 30 dagen bereikselectie
  - dagelijkse en wekelijkse aggregaties
  - grafieken voor focus tijd en punten
  - vergelijking completed vs stopped sessions
-  **Visuele focus companion** (Lottie)
  - reageert op focus state (idle / focusing / paused)
  - tijdelijke feedback bij completion (victory) of vroegtijdig stoppen (tired)
-  **Theme & accent customization**
  - System / Light / Dark
  - Accentkleur met onmiddellijke app-wide impact
-  **Offline-first dataopslag**
  - alle data lokaal via Room
  - geen backend of account vereist
-  **Deterministische demo data**
  - voor ontwikkeling en statistische visualisaties

---

## Architecture Overview

De app volgt een **Jetpack Compose + MVVM-gebaseerde architectuur** met een duidelijke scheiding van verantwoordelijkheden.

### UI layer
- Jetpack Compose screens
- Herbruikbare composables
- Stateless waar mogelijk

### ViewModels
- State management via `StateFlow`
- Timerlogica en gebruikersinteracties

### Domain
- Pure business logic (punten- en levelberekening)
- Geen Android-afhankelijkheden

### Data
- Room database
- Repositories
- DataStore Preferences

De architectuur is bewust eenvoudig gehouden,
met focus op leesbaarheid, voorspelbaarheid en correcte state handling.

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
Alle focus sessions, statistieken en instellingen worden lokaal opgeslagen.
De app blijft volledig functioneel zonder internetverbinding.

### Gamification zonder druk
Levels en punten zijn bedoeld om consistent gedrag te stimuleren,
niet om competitieve druk te creëren.

### Visuele feedback
De focus companion fungeert als state-indicator en motivatie-feedback,
waardoor tekstuele uitleg tot een minimum beperkt blijft.

---

## UX & Design Principles

- Minimalistische layout
- Duidelijke visuele hiërarchie
- Weinig tekst, veel betekenisvolle visuals
- Consistente spacing en typografie
- Accentkleur benadrukt acties en voortgang

---

## Build & Run

1. Open het project in Android Studio
2. Sync Gradle
3. Selecteer een emulator of fysiek toestel (API 26+)
4. Run de app

Demo data wordt automatisch toegevoegd in debug builds.

---

## Notes for Evaluation

- Focus op codekwaliteit, structuur en UX
- Bewuste architecturale keuzes
- Geen backend of authenticatie (by design)
- Volledig lokaal en deterministisch

---

## Author

Brent Cornet  
Applied Computer Science – Software Engineering  
Erasmushogeschool Brussel
