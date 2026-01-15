package com.bcornet.focushero.ui.theme

import androidx.compose.ui.graphics.Color
import com.bcornet.focushero.ui.screens.profile.AccentColorOption

// Default template colors
val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

// Accent palette for user selection
val AccentBlue = Color(0xFF3B82F6)
val AccentGreen = Color(0xFF22C55E)
val AccentOrange = Color(0xFFF97316)
val AccentPink = Color(0xFFEC4899)
val AccentYellow = Color(0xFFEAB308)
val AccentRed = Color(0xFFEF4444)

fun accentColorFor(option: AccentColorOption): Color {
    return when (option) {
        AccentColorOption.DEFAULT -> Purple40
        AccentColorOption.BLUE -> AccentBlue
        AccentColorOption.GREEN -> AccentGreen
        AccentColorOption.ORANGE -> AccentOrange
        AccentColorOption.PINK -> AccentPink
        AccentColorOption.YELLOW -> AccentYellow
        AccentColorOption.RED -> AccentRed
    }
}
