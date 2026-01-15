package com.bcornet.focushero.ui.screens.focus

import com.bcornet.focushero.domain.logic.LevelCalculator
import com.bcornet.focushero.domain.model.FocusSession
import com.bcornet.focushero.domain.model.SessionStatus

data class FocusUiState(
    val sessionStatus: FocusSessionRunState = FocusSessionRunState.IDLE,

    val selectedDurationMinutes: Int = 25,
    val remainingSeconds: Int = 25 * 60,
    val totalSeconds: Int = 25 * 60,

    val lastSessionResult: SessionResultBanner? = null,

    val recentSessions: List<FocusSession> = emptyList(),

    val totalPoints: Int = 0,
    val currentLevel: Int = 1,
    val progressToNextLevel: Float = 0f,
    val pointsRemainingToNextLevel: Int = LevelCalculator.DEFAULT_POINTS_PER_LEVEL,
)

enum class FocusSessionRunState {
    IDLE,
    RUNNING,
    PAUSED,
}

data class SessionResultBanner(
    val status: SessionStatus,
    val pointsEarned: Int,
)