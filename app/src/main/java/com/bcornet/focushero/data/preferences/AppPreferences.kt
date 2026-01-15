package com.bcornet.focushero.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.bcornet.focushero.ui.screens.profile.AccentColorOption
import com.bcornet.focushero.ui.screens.profile.ThemePreference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

private const val DATASTORE_NAME = "focushero_prefs"

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = DATASTORE_NAME)

class AppPreferences(
    private val context: Context,
) {
    private object Keys {
        val THEME = stringPreferencesKey("theme_preference")
        val ACCENT = stringPreferencesKey("accent_color")
    }

    val themePreferenceFlow: Flow<ThemePreference> =
        context.dataStore.data
            .map { prefs ->
                val raw = prefs[Keys.THEME]
                raw.toThemePreferenceOrDefault()
            }
            .distinctUntilChanged()

    val accentColorFlow: Flow<AccentColorOption> =
        context.dataStore.data
            .map { prefs ->
                val raw = prefs[Keys.ACCENT]
                raw.toAccentColorOrDefault()
            }
            .distinctUntilChanged()

    suspend fun setThemePreference(value: ThemePreference) {
        context.dataStore.edit { prefs ->
            prefs[Keys.THEME] = value.name
        }
    }

    suspend fun setAccentColor(value: AccentColorOption) {
        context.dataStore.edit { prefs ->
            prefs[Keys.ACCENT] = value.name
        }
    }
}

private fun String?.toThemePreferenceOrDefault(): ThemePreference {
    return runCatching {
        if (this.isNullOrBlank()) ThemePreference.SYSTEM else ThemePreference.valueOf(this)
    }.getOrElse { ThemePreference.SYSTEM }
}

private fun String?.toAccentColorOrDefault(): AccentColorOption {
    return runCatching {
        if (this.isNullOrBlank()) AccentColorOption.DEFAULT else AccentColorOption.valueOf(this)
    }.getOrElse { AccentColorOption.DEFAULT }
}
