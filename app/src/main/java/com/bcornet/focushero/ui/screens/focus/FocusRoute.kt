package com.bcornet.focushero.ui.screens.focus

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
fun FocusRoute (
    contentPadding: PaddingValues
) {
    val context = LocalContext.current

    val db = remember { DatabaseProvider.getDatabase(context) }
    val repository = remember(db) { FocusSessionRepository(db.focusSessionDao()) }

    val vm: FocusViewModel = viewModel(
        factory = FocusViewModel.Factory(repository)
    )

    val uiState by vm.uiState.collectAsState()

    FocusScreen(
        contentPadding = contentPadding,
        uiState = uiState,

        onDecreaseMinutes = {
            val next = (uiState.selectedDurationMinutes - 5).coerceAtLeast(5)
            vm.setDurationMinutes(next)
        },

        onIncreaseMinutes = {
            val next = (uiState.selectedDurationMinutes + 5).coerceAtMost(240)
            vm.setDurationMinutes(next)
        },

        onStart = vm::start,
        onPause = vm::pause,
        onResume = vm::resume,
        onStop = vm::stopEarly,
        onDismissResult = vm::dismissResultBanner,
    )
}