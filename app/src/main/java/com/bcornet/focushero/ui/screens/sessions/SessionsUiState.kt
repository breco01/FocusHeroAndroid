package com.bcornet.focushero.ui.screens.sessions

import com.bcornet.focushero.domain.model.FocusSession

data class SessionsUiState(
    val isLoading: Boolean = true,
    val sessions: List<FocusSession> = emptyList(),
    val errorMessage: String? = null,
)