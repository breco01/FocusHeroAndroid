package com.bcornet.focushero.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.bcornet.focushero.ui.navigation.AppNavHost
import com.bcornet.focushero.ui.navigation.Routes

private data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: @Composable () -> Unit,
)

@Composable
fun MainScaffold(
    navController: NavHostController = rememberNavController(),
) {
    val items = listOf(
        BottomNavItem(
            route = Routes.Focus,
            label = "Focus",
            icon = {androidx.compose.material3.Icon(Icons.Filled.Home, contentDescription = "Focus")}
        ),
        BottomNavItem(
            route = Routes.Sessions,
            label = "Sessions",
            icon = {androidx.compose.material3.Icon(Icons.Filled.List, contentDescription = "Sessions")}
        ),
        BottomNavItem(
            route = Routes.Stats,
            label = "Stats",
            icon = {androidx.compose.material3.Icon(Icons.Filled.BarChart, contentDescription = "Stats")}
        ),
        BottomNavItem(
            route = Routes.Settings,
            label = "Settings",
            icon = {androidx.compose.material3.Icon(Icons.Filled.Settings, contentDescription = "Settings")}
        ),
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar{
                items.forEach { item ->
                    val selected = currentRoute == item.route

                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            if (!selected) {
                                navController.navigate(item.route) {
                                    popUpTo(Routes.Focus) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = item.icon,
                        label = { androidx.compose.material3.Text(item.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        AppNavHost(
            navController = navController,
            startDestination = Routes.Focus,
        )
    }
}