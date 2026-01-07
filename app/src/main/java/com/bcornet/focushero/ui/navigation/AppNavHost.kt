package com.bcornet.focushero.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

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
            // Placeholder
        }

        composable(Routes.Sessions){
            // Placeholder
        }

        composable(Routes.Stats){
            // Placeholder
        }

        composable(Routes.Settings){
            // Placeholder
        }
    }
}

object Routes{
    const val Focus = "focus"
    const val Sessions = "sessions"
    const val Stats = "stats"
    const val Settings = "settings"
}
