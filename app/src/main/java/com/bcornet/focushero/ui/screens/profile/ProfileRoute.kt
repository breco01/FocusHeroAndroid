package com.bcornet.focushero.ui.screens.profile

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bcornet.focushero.data.DatabaseProvider
import com.bcornet.focushero.data.repo.FocusSessionRepository

@Composable
fun ProfileRoute(
    contentPadding: PaddingValues,
) {
    val context = LocalContext.current

    val db = remember { DatabaseProvider.getDatabase(context) }
    val repository = remember { FocusSessionRepository(db.focusSessionDao()) }

    val vm: ProfileViewModel = viewModel(
        factory = ProfileViewModel.Factory(repository)
    )

    val uiState by vm.uiState.collectAsState()

    ProfileScreen(
        contentPadding = contentPadding,
        uiState = uiState,
    )
}