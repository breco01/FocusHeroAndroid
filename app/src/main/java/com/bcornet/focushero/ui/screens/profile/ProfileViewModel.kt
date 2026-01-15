package com.bcornet.focushero.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewModelScope
import com.bcornet.focushero.data.repo.FocusSessionRepository
import com.bcornet.focushero.domain.logic.LevelCalculator
import com.bcornet.focushero.domain.model.SessionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val repository: FocusSessionRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        observeProfileStats()
    }

    private fun observeProfileStats() {
        viewModelScope.launch {
            val sessionsFlow = repository.observeSessionsMostRecentFirst()
            val totalPointsFlow = repository.observeTotalPoints()

            sessionsFlow
                .combine(totalPointsFlow) { sessions, totalPoints ->
                    val totalSessionsCount = sessions.size
                    val completedSessionsCount = sessions.count { it.status == SessionStatus.COMPLETED }
                    val stoppedSessionsCount = sessions.count { it.status == SessionStatus.STOPPED }

                    val totalFocusSecondsCompleted = sessions
                        .asSequence()
                        .filter { it.status == SessionStatus.COMPLETED }
                        .sumOf { it.durationSeconds.toLong().coerceAtLeast(0L) }

                    val totalCompletedFocusMinutes = (totalFocusSecondsCompleted / 60L).toInt()

                    val completionRatePercent = run {
                        val total = totalSessionsCount.coerceAtLeast(0)
                        if (total == 0) 0 else ((completedSessionsCount.toFloat() / total.toFloat()) * 100f).toInt()
                    }

                    val level = LevelCalculator.levelForTotalPoints(totalPoints)
                    val progress = LevelCalculator.progressToNextLevel(totalPoints)
                    val remaining = LevelCalculator.pointsRemainingToNextLevel(totalPoints)

                    val eval = evaluateAchievements(
                        completedSessionsCount = completedSessionsCount,
                        totalPoints = totalPoints,
                        currentLevel = level,
                        totalCompletedFocusSeconds = totalFocusSecondsCompleted,
                    )

                    ProfileUiState(
                        isLoading = false,
                        errorMessage = null,

                        totalPoints = totalPoints,
                        currentLevel = level,

                        completedSessionsCount = completedSessionsCount,
                        stoppedSessionsCount = stoppedSessionsCount,
                        totalSessionsCount = totalSessionsCount,

                        totalCompletedFocusMinutes = totalCompletedFocusMinutes,

                        progressToNextLevel = progress,
                        pointsRemainingToNextLevel = remaining,

                        completionRatePercent = completionRatePercent,

                        achievementsUnlockedCount = eval.unlocked.size,
                        achievementsTotalCount = eval.all.size,
                        unlockedAchievements = eval.unlocked,
                        lockedAchievements = eval.locked,

                        // Route overschrijft deze met DataStore waarden
                        themePreference = ThemePreference.SYSTEM,
                        accentColor = AccentColorOption.DEFAULT,
                    )
                }
                .catch { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = e.message ?: "Failed to load profile data.",
                        )
                    }
                }
                .collectLatest { state ->
                    _uiState.value = state
                }
        }
    }

    private data class AchievementEvaluation(
        val all: List<AchievementUi>,
        val unlocked: List<AchievementUi>,
        val locked: List<AchievementUi>,
    )

    private fun evaluateAchievements(
        completedSessionsCount: Int,
        totalPoints: Int,
        currentLevel: Int,
        totalCompletedFocusSeconds: Long,
    ): AchievementEvaluation {
        val totalCompletedHours = totalCompletedFocusSeconds / 3600.0

        val all = buildList {
            // Quick Wins
            add(
                achievement(
                    id = "first_step",
                    title = "First Step",
                    description = "Complete your first focus session.",
                    unlocked = completedSessionsCount >= 1
                )
            )
            add(
                achievement(
                    id = "getting_consistent",
                    title = "Getting Consistent",
                    description = "Complete 3 focus sessions.",
                    unlocked = completedSessionsCount >= 3
                )
            )
            add(
                achievement(
                    id = "on_a_roll",
                    title = "On a Roll",
                    description = "Complete 7 focus sessions.",
                    unlocked = completedSessionsCount >= 7
                )
            )
            add(
                achievement(
                    id = "habit_builder",
                    title = "Habit Builder",
                    description = "Complete 10 focus sessions.",
                    unlocked = completedSessionsCount >= 10
                )
            )
            add(
                achievement(
                    id = "one_hour_in",
                    title = "One Hour In",
                    description = "Complete 1 hour of focused work.",
                    unlocked = totalCompletedHours >= 1.0
                )
            )
            add(
                achievement(
                    id = "level_up",
                    title = "Level Up",
                    description = "Reach level 2.",
                    unlocked = currentLevel >= 2
                )
            )

            // Mid-term Goals
            add(
                achievement(
                    id = "routine_master",
                    title = "Routine Master",
                    description = "Complete 20 focus sessions.",
                    unlocked = completedSessionsCount >= 20
                )
            )
            add(
                achievement(
                    id = "point_collector",
                    title = "Point Collector",
                    description = "Earn 50 total points.",
                    unlocked = totalPoints >= 50
                )
            )
            add(
                achievement(
                    id = "centurion",
                    title = "Centurion",
                    description = "Earn 100 total points.",
                    unlocked = totalPoints >= 100
                )
            )
            add(
                achievement(
                    id = "deep_work",
                    title = "Deep Work",
                    description = "Complete 2 hours of focused work.",
                    unlocked = totalCompletedHours >= 2.0
                )
            )
            add(
                achievement(
                    id = "serious_session",
                    title = "Serious Session",
                    description = "Complete 5 hours of focused work.",
                    unlocked = totalCompletedHours >= 5.0
                )
            )
            add(
                achievement(
                    id = "climbing_up",
                    title = "Climbing Up",
                    description = "Reach level 5.",
                    unlocked = currentLevel >= 5
                )
            )

            // Long-term Goals
            add(
                achievement(
                    id = "focus_veteran",
                    title = "Focus Veteran",
                    description = "Complete 50 focus sessions.",
                    unlocked = completedSessionsCount >= 50
                )
            )
            add(
                achievement(
                    id = "legendary_focus",
                    title = "Legendary Focus",
                    description = "Complete 100 focus sessions.",
                    unlocked = completedSessionsCount >= 100
                )
            )
            add(
                achievement(
                    id = "point_hoarder",
                    title = "Point Hoarder",
                    description = "Earn 200 total points.",
                    unlocked = totalPoints >= 200
                )
            )
            add(
                achievement(
                    id = "elite_grinder",
                    title = "Elite Grinder",
                    description = "Earn 500 total points.",
                    unlocked = totalPoints >= 500
                )
            )
            add(
                achievement(
                    id = "deep_work_engine",
                    title = "Deep Work Engine",
                    description = "Complete 10 hours of focused work.",
                    unlocked = totalCompletedHours >= 10.0
                )
            )
            add(
                achievement(
                    id = "double_digits",
                    title = "Double Digits",
                    description = "Reach level 10.",
                    unlocked = currentLevel >= 10
                )
            )
        }

        val unlocked = all.filter { it.isUnlocked }
        val locked = all.filterNot { it.isUnlocked }

        return AchievementEvaluation(
            all = all,
            unlocked = unlocked,
            locked = locked,
        )
    }

    private fun achievement(
        id: String,
        title: String,
        description: String,
        unlocked: Boolean,
    ): AchievementUi {
        return AchievementUi(
            id = id,
            title = title,
            description = description,
            isUnlocked = unlocked,
        )
    }

    class Factory(
        private val repository: FocusSessionRepository,
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(
            modelClass: Class<T>,
            extras: CreationExtras,
        ): T {
            if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
                return ProfileViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return create(modelClass, CreationExtras.Empty)
        }
    }
}
