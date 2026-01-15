package com.bcornet.focushero.ui.screens.profile

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bcornet.focushero.data.DatabaseProvider
import com.bcornet.focushero.data.preferences.AppPreferences
import com.bcornet.focushero.data.repo.FocusSessionRepository
import kotlinx.coroutines.launch

@Composable
fun ProfileRoute(
    contentPadding: PaddingValues,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val db = remember { DatabaseProvider.getDatabase(context.applicationContext) }
    val repository = remember { FocusSessionRepository(db.focusSessionDao()) }

    val prefs = remember { AppPreferences(context.applicationContext) }
    val themePreference by prefs.themePreferenceFlow.collectAsState(initial = ThemePreference.SYSTEM)
    val accentColorOption by prefs.accentColorFlow.collectAsState(initial = AccentColorOption.DEFAULT)

    val vm: ProfileViewModel = viewModel(
        factory = ProfileViewModel.Factory(repository)
    )

    val vmState by vm.uiState.collectAsState()

    ProfileScreen(
        contentPadding = contentPadding,
        uiState = vmState.copy(
            themePreference = themePreference,
            accentColor = accentColorOption,
        ),
        onThemeSelected = { selected ->
            scope.launch { prefs.setThemePreference(selected) }
        },
        onAccentSelected = { selected ->
            scope.launch { prefs.setAccentColor(selected) }
        },
    )
}
