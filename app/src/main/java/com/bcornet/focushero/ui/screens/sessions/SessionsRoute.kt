package com.bcornet.focushero.ui.screens.sessions

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bcornet.focushero.data.DatabaseProvider
import com.bcornet.focushero.data.repo.FocusSessionRepository

@Composable
fun SessionsRoute(
    contentPadding= PaddingValues,
) {
    val context = LocalContext.current

    val db = remember { DatabaseProvider.getDatabase(context) }
    val repository = remember(db) { FocusSessionRepository(db.focusSessionDao()) }

    val vm: SessionsViewModel = viewModel(
        factory = SessionsViewModel.Factory(repository),
    )

    val uiState by vm.uiState.collectAsState()

    SessionsScreen(
        contentPadding = contentPadding,
        uiState = uiState,
    )
}