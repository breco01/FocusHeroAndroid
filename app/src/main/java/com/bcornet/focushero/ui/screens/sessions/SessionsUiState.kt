package com.bcornet.focushero.ui.screens.sessions

import com.bcornet.focushero.domain.model.FocusSession

sealed interface SessionsListItem {
    data class SessionItem(val session: FocusSession) : SessionsListItem
    data class LevelUpItem(val level: Int) : SessionsListItem
}

data class SessionsUiState(
    val isLoading: Boolean = true,
    val items: List<SessionsListItem> = emptyList(),
    val errorMessage: String? = null,
)