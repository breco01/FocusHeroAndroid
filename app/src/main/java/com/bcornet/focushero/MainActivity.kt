package com.bcornet.focushero

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.lifecycleScope
import com.bcornet.focushero.data.DatabaseProvider
import com.bcornet.focushero.data.DemoDataSeeder
import com.bcornet.focushero.data.preferences.AppPreferences
import com.bcornet.focushero.data.repo.FocusSessionRepository
import com.bcornet.focushero.ui.theme.FocusHeroTheme
import kotlinx.coroutines.launch

private const val DEMO_MODE = true

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val prefs = remember { AppPreferences(applicationContext) }

            val themePreference by prefs.themePreferenceFlow.collectAsState(initial = com.bcornet.focushero.ui.screens.profile.ThemePreference.SYSTEM)
            val accentColorOption by prefs.accentColorFlow.collectAsState(initial = com.bcornet.focushero.ui.screens.profile.AccentColorOption.DEFAULT)

            FocusHeroTheme(
                themePreference = themePreference,
                accentColorOption = accentColorOption,
                dynamicColor = true,
            ) {
                com.bcornet.focushero.ui.screens.MainScaffold()
            }
        }

        if (DEMO_MODE) {
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