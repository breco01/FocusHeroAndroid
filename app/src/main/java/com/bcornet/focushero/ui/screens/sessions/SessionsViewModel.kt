package com.bcornet.focushero.ui.screens.sessions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewModelScope
import com.bcornet.focushero.data.repo.FocusSessionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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
                .collectLatest { sessions ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            sessions = sessions,
                            errorMessage = null,
                        )
                    }
                }
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