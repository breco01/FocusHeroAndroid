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
import androidx.lifecycle.lifecycleScope
import com.bcornet.focushero.data.DatabaseProvider
import com.bcornet.focushero.data.DemoDataSeeder
import com.bcornet.focushero.data.repo.FocusSessionRepository
import com.bcornet.focushero.ui.theme.FocusHeroTheme
import kotlinx.coroutines.launch
private const val DEMO_MODE = true
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FocusHeroTheme {
                com.bcornet.focushero.ui.screens.MainScaffold()
            }
        }

        if(DEMO_MODE){
            val db = DatabaseProvider.getDatabase(applicationContext)
            val repository = FocusSessionRepository(db.focusSessionDao())

            lifecycleScope.launch {
                DemoDataSeeder.seedIfNeeded(
                    context = applicationContext,
                    repository = repository,
                )
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