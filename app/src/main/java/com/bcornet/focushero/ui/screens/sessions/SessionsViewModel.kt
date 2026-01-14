package com.bcornet.focushero.ui.screens.sessions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewModelScope
import com.bcornet.focushero.data.repo.FocusSessionRepository
import com.bcornet.focushero.domain.logic.LevelCalculator
import com.bcornet.focushero.domain.model.FocusSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class SessionsViewModel(
    private val repository: FocusSessionRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SessionsUiState())
    val uiState: StateFlow<SessionsUiState> = _uiState.asStateFlow()

    init {
        observeSessions()
    }

    private fun observeSessions() {
        viewModelScope.launch {
            repository.observeSessionsMostRecentFirst()
                .collectLatest { sessionsMostRecentFirst ->
                    val items = buildInterleavedItemsWithLevelUps(
                        sessionsMostRecentFirst = sessionsMostRecentFirst
                    )

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            items = items,
                            errorMessage = null,
                        )
                    }
                }
        }
    }

    private fun buildInterleavedItemsWithLevelUps(
        sessionsMostRecentFirst: List<FocusSession>,
    ): List<SessionsListItem> {
        if (sessionsMostRecentFirst.isEmpty()) return emptyList()

        val sessionsOldestFirst = sessionsMostRecentFirst.asReversed()

        val levelUpsBySessionId: Map<UUID, List<Int>> =
            computeLevelUpsBySessionIdOldestFirst(sessionsOldestFirst)

        val result = ArrayList<SessionsListItem>(sessionsMostRecentFirst.size * 2)

        for (session in sessionsMostRecentFirst) {
            val reachedLevels = levelUpsBySessionId[session.id].orEmpty()

            for (level in reachedLevels.sortedDescending()) {
                result.add(SessionsListItem.LevelUpItem(level = level))
            }
            result.add(SessionsListItem.SessionItem(session = session))
        }

        return result
    }

    private fun computeLevelUpsBySessionIdOldestFirst(
        sessionsOldestFirst: List<FocusSession>,
    ): Map<UUID, List<Int>> {

        var cumulativePoints = 0
        val result = mutableMapOf<UUID, List<Int>>()

        for (session in sessionsOldestFirst) {
            val levelBefore = LevelCalculator.levelForTotalPoints(cumulativePoints)

            cumulativePoints += session.pointsEarned

            val levelAfter = LevelCalculator.levelForTotalPoints(cumulativePoints)

            if (levelAfter > levelBefore) {
                // All levels reached during this session
                val reachedLevels = (levelBefore + 1..levelAfter).toList()
                result[session.id] = reachedLevels
            }
        }

        return result
    }

    class Factory(
        private val repository: FocusSessionRepository,
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(
            modelClass: Class<T>,
            extras: CreationExtras,
        ): T {
            if (modelClass.isAssignableFrom(SessionsViewModel::class.java)) {
                return SessionsViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return create(modelClass, CreationExtras.Empty)
        }
    }
}