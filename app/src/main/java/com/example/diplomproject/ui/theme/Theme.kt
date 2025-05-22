package com.example.diplomproject.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val PrimaryColor = Color(0xFF1A212F)
private val SecondaryColor = Color(0xFF5F88A4)
private val TertiaryColor = Color(0xFFB6CCFE)
private val BackgroundColor = Color(0xFFFFFFFF)
private val SurfaceColor = Color(0xFFF8F9FF)
private val TextOnPrimary = Color(0xFFFFFFFF)
private val TextOnBackground = Color(0xFF1A1A2E)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryColor,
    secondary = SecondaryColor,
    tertiary = TertiaryColor,
    background = BackgroundColor,
    surface = SurfaceColor,
    onPrimary = TextOnPrimary,
    onBackground = TextOnBackground,
    onSurface = TextOnBackground
)

private val LightColorScheme = lightColorScheme(
    primary = SecondaryColor,
    secondary = PrimaryColor,
    tertiary = TertiaryColor,
    background = Color(0xFF1A1A1A), // Темный фон
    surface = Color(0xFF2B2B2B), // Темно-серый
    onPrimary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun EmotionTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}