package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF9ECAFF),
    onPrimary = Color(0xFF003258),
    primaryContainer = Color(0xFF00497D),
    onPrimaryContainer = Color(0xFFD1E4FF),
    secondary = Color(0xFFBBC7DB),
    background = Color(0xFF111318),
    surface = Color(0xFF111318),
    onBackground = Color(0xFFE2E2E9),
    onSurface = Color(0xFFE2E2E9)
)

private val LightColorScheme = lightColorScheme(
    primary = DzTealPrimary,
    onPrimary = Color.White,
    primaryContainer = DzTealLight,
    onPrimaryContainer = DzTealDark,
    secondary = DzGoldAccent,
    background = DzBackground,
    surface = DzSurface,
    onBackground = DzTextPrimary,
    onSurface = DzTextPrimary,
    surfaceVariant = DzCardBorder
)

@Composable
fun FindMeDZTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

