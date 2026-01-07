package com.bcornet.focushero

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.bcornet.focushero.ui.navigation.AppNavHost
import com.bcornet.focushero.ui.theme.FocusHeroTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FocusHeroTheme {
                val navController = rememberNavController()
                AppNavHost(navController = navController)
            }
        }
    }
}
@Composable
fun PlaceholderScreen(title: String){
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium
        )
    }
}