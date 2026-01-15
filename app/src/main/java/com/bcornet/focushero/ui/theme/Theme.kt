package com.bcornet.focushero.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.bcornet.focushero.ui.screens.profile.AccentColorOption
import com.bcornet.focushero.ui.screens.profile.ThemePreference

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80,
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40,
)

@Composable
fun FocusHeroTheme(
    themePreference: ThemePreference = ThemePreference.SYSTEM,
    accentColorOption: AccentColorOption = AccentColorOption.DEFAULT,
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val isDark = when (themePreference) {
        ThemePreference.SYSTEM -> isSystemInDarkTheme()
        ThemePreference.LIGHT -> false
        ThemePreference.DARK -> true
    }

    val context = LocalContext.current

    val useDynamic = dynamicColor &&
            accentColorOption == AccentColorOption.DEFAULT &&
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    val baseScheme = when {
        useDynamic -> if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        isDark -> DarkColorScheme
        else -> LightColorScheme
    }

    val accent = accentColorFor(accentColorOption)
    val finalScheme = baseScheme.withAccent(accent)

    MaterialTheme(
        colorScheme = finalScheme,
        typography = Typography,
        content = content,
    )
}

private fun ColorScheme.withAccent(accent: Color): ColorScheme {
    return copy(
        primary = accent,
        secondary = accent,
        tertiary = accent,
    )
}
