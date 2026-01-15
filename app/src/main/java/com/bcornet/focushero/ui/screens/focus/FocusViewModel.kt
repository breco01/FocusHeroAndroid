package com.bcornet.focushero.ui.screens.focus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.bcornet.focushero.data.repo.FocusSessionRepository
import com.bcornet.focushero.domain.logic.LevelCalculator
import com.bcornet.focushero.domain.logic.PointsCalculator
import com.bcornet.focushero.domain.model.FocusSession
import com.bcornet.focushero.domain.model.SessionStatus
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.UUID

class FocusViewModel(
    private val repository: FocusSessionRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(FocusUiState())
    val uiState: StateFlow<FocusUiState> = _uiState.asStateFlow()

    private var tickerJob: Job? = null
    private var currentSessionStart: Instant? = null

    init {
        observeRecentSessions()
        observeProgress()
    }

    fun setDurationMinutes(minutes: Int) {
        // Only editable when no session is running
        if (_uiState.value.sessionStatus != FocusSessionRunState.IDLE) return
        val clamped = minutes.coerceIn(5, 240)

        _uiState.update {
            it.copy(
                selectedDurationMinutes = clamped,
                totalSeconds = clamped * 60,
                remainingSeconds = clamped * 60,
            )
        }
    }

    fun start() {
        if (_uiState.value.sessionStatus != FocusSessionRunState.IDLE) return

        currentSessionStart = Instant.now()

        _uiState.update {
            it.copy(
                sessionStatus = FocusSessionRunState.RUNNING,
                lastSessionResult = null,
                totalSeconds = it.selectedDurationMinutes * 60,
                remainingSeconds = it.selectedDurationMinutes * 60,
            )
        }

        startTicker()
    }

    fun pause() {
        if (_uiState.value.sessionStatus != FocusSessionRunState.RUNNING) return
        stopTicker()
        _uiState.update { it.copy(sessionStatus = FocusSessionRunState.PAUSED) }
    }

    fun resume() {
        if (_uiState.value.sessionStatus != FocusSessionRunState.PAUSED) return
        _uiState.update { it.copy(sessionStatus = FocusSessionRunState.RUNNING) }
        startTicker()
    }

    fun stopEarly() {
        val state = _uiState.value
        if (state.sessionStatus == FocusSessionRunState.IDLE) return

        stopTicker()

        val start = currentSessionStart ?: Instant.now()
        val end = Instant.now()
        val elapsed = (state.totalSeconds - state.remainingSeconds).coerceAtLeast(0)

        viewModelScope.launch {
            saveSession(
                id = UUID.randomUUID(),
                startTime = start,
                endTime = end,
                durationSeconds = elapsed,
                status = SessionStatus.STOPPED,
            )
        }

        resetToIdleWithBanner(SessionStatus.STOPPED, pointsEarned = 0)
    }

    fun dismissResultBanner() {
        _uiState.update { it.copy(lastSessionResult = null) }
    }

    override fun onCleared() {
        stopTicker()
        super.onCleared()
    }

    private fun observeRecentSessions() {
        viewModelScope.launch {
            repository.observeSessionsMostRecentFirst()
                .collectLatest { sessions ->
                    _uiState.update {
                        it.copy(
                            recentSessions = sessions.take(5)
                        )
                    }
                }
        }
    }

    private fun observeProgress() {
        viewModelScope.launch {
            repository.observeTotalPoints()
                .collectLatest { totalPoints ->
                    val level = LevelCalculator.levelForTotalPoints(totalPoints)
                    val progress = LevelCalculator.progressToNextLevel(totalPoints)
                    val remaining = LevelCalculator.pointsRemainingToNextLevel(totalPoints)

                    _uiState.update {
                        it.copy(
                            totalPoints = totalPoints,
                            currentLevel = level,
                            progressToNextLevel = progress,
                            pointsRemainingToNextLevel = remaining,
                        )
                    }
                }
        }
    }

    private fun startTicker() {
        stopTicker()

        tickerJob = viewModelScope.launch {
            while (true) {
                delay(1000)

                val state = _uiState.value
                if (state.sessionStatus != FocusSessionRunState.RUNNING) return@launch

                val nextRemaining = (state.remainingSeconds - 1).coerceAtLeast(0)

                _uiState.update { it.copy(remainingSeconds = nextRemaining) }

                if (nextRemaining == 0) {
                    stopTicker()

                    val start = currentSessionStart ?: Instant.now()
                    val end = Instant.now()

                    viewModelScope.launch {
                        saveSession(
                            id = UUID.randomUUID(),
                            startTime = start,
                            endTime = end,
                            durationSeconds = state.totalSeconds,
                            status = SessionStatus.COMPLETED,
                        )
                    }

                    val points = PointsCalculator.calculatePoints(
                        durationSeconds = state.totalSeconds,
                        status = SessionStatus.COMPLETED,
                    )

                    resetToIdleWithBanner(SessionStatus.COMPLETED, pointsEarned = points)
                    return@launch
                }
            }
        }
    }

    private fun stopTicker() {
        tickerJob?.cancel()
        tickerJob = null
    }

    private suspend fun saveSession(
        id: UUID,
        startTime: Instant,
        endTime: Instant,
        durationSeconds: Int,
        status: SessionStatus,
    ) {
        val points = PointsCalculator.calculatePoints(
            durationSeconds = durationSeconds,
            status = status,
        )

        repository.saveSession(
            FocusSession(
                id = id,
                startTime = startTime,
                endTime = endTime,
                durationSeconds = durationSeconds,
                pointsEarned = points,
                status = status,
            )
        )
    }

    private fun resetToIdleWithBanner(status: SessionStatus, pointsEarned: Int) {
        currentSessionStart = null

        _uiState.update {
            it.copy(
                sessionStatus = FocusSessionRunState.IDLE,
                remainingSeconds = it.selectedDurationMinutes * 60,
                totalSeconds = it.selectedDurationMinutes * 60,
                lastSessionResult = SessionResultBanner(
                    status = status,
                    pointsEarned = pointsEarned,
                ),
            )
        }
    }

    class Factory(
        private val repository: FocusSessionRepository,
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(
            modelClass: Class<T>,
            extras: CreationExtras,
        ): T {
            if (modelClass.isAssignableFrom(FocusViewModel::class.java)) {
                return FocusViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return create(modelClass, CreationExtras.Empty)
        }
    }
}