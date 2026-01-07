package com.bcornet.focushero.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.bcornet.focushero.PlaceholderScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: String = Routes.Focus,
){
    NavHost(
        navController = navController,
        startDestination = startDestination,
    ){
        composable(Routes.Focus){
            PlaceholderScreen(title = "Focus")
        }

        composable(Routes.Sessions){
            PlaceholderScreen(title = "Sessions")
        }

        composable(Routes.Stats){
            PlaceholderScreen(title = "Stats")
        }

        composable(Routes.Settings){
            PlaceholderScreen(title = "Settings")
        }
    }
}

object Routes{
    const val Focus = "focus"
    const val Sessions = "sessions"
    const val Stats = "stats"
    const val Settings = "settings"
}
