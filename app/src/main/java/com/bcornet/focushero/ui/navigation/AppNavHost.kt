package com.bcornet.focushero.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.bcornet.focushero.PlaceholderScreen
import com.bcornet.focushero.ui.screens.focus.FocusRoute
import com.bcornet.focushero.ui.screens.focus.FocusScreen
import com.bcornet.focushero.ui.screens.sessions.SessionsScreen
import com.bcornet.focushero.ui.screens.settings.SettingsScreen
import com.bcornet.focushero.ui.screens.stats.StatsScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: String = Routes.Focus,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        composable(Routes.Focus) {
            FocusRoute(contentPadding = contentPadding)
        }

        composable(Routes.Sessions) {
            SessionsScreen(contentPadding = contentPadding)
        }

        composable(Routes.Stats) {
            StatsScreen(contentPadding = contentPadding)
        }

        composable(Routes.Settings) {
            SettingsScreen(contentPadding = contentPadding)
        }
    }
}

object Routes {
    const val Focus = "focus"
    const val Sessions = "sessions"
    const val Stats = "stats"
    const val Settings = "settings"
}
