package com.bcornet.focushero.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.bcornet.focushero.ui.screens.focus.FocusRoute
import com.bcornet.focushero.ui.screens.profile.ProfileRoute
import com.bcornet.focushero.ui.screens.sessions.SessionsRoute
import com.bcornet.focushero.ui.screens.stats.StatsRoute

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
            SessionsRoute(contentPadding = contentPadding)
        }

        composable(Routes.Stats) {
            StatsRoute(contentPadding = contentPadding)
        }

        composable(Routes.Profile) {
            ProfileRoute(contentPadding = contentPadding)
        }
    }
}

object Routes {
    const val Focus = "focus"
    const val Sessions = "sessions"
    const val Stats = "stats"
    const val Profile = "profile"
}
