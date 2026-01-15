package com.bcornet.focushero.ui.screens.profile

data class ProfileUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,

    // Overview
    val totalPoints: Int = 0,
    val currentLevel: Int = 1,

    // Sessions
    val completedSessionsCount: Int = 0,
    val stoppedSessionsCount: Int = 0,
    val totalSessionsCount: Int = 0,

    // Focus time
    val totalCompletedFocusMinutes: Int = 0,

    // Progress
    val progressToNextLevel: Float = 0f,
    val pointsRemainingToNextLevel: Int = 0,

    // Completion rate
    val completionRatePercent: Int = 0,

    // Achievements
    val achievementsUnlockedCount: Int = 0,
    val achievementsTotalCount: Int = 0,
    val unlockedAchievements: List<AchievementUi> = emptyList(),
    val lockedAchievements: List<AchievementUi> = emptyList(),

    // Appearance
    val themePreference: ThemePreference = ThemePreference.SYSTEM,
    val accentColor: AccentColorOption = AccentColorOption.DEFAULT,
)

enum class ThemePreference {
    SYSTEM,
    LIGHT,
    DARK,
}

enum class AccentColorOption {
    DEFAULT,
    BLUE,
    GREEN,
    ORANGE,
    PINK,
    YELLOW,
    RED,
}

data class AchievementUi(
    val id: String,
    val title: String,
    val description: String,
    val isUnlocked: Boolean,
)
