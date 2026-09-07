package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme =
  darkColorScheme(
    primary = TurquoiseLight,
    onPrimary = NightCanvas,
    primaryContainer = TurquoiseDark,
    onPrimaryContainer = TurquoiseContainer,
    secondary = GoldAccent,
    onSecondary = NightCanvas,
    secondaryContainer = GoldDark,
    onSecondaryContainer = GoldLight,
    tertiary = GoldLight,
    background = NightCanvas,
    surface = NightSurface,
    surfaceVariant = NightSurfaceVariant,
    onBackground = TextPrimaryDark,
    onSurface = TextPrimaryDark,
    onSurfaceVariant = TextSecondaryDark,
    outline = NightBorder
  )

private val LightColorScheme =
  lightColorScheme(
    primary = TurquoisePrimary,
    onPrimary = Color.White,
    primaryContainer = TurquoiseContainer,
    onPrimaryContainer = OnTurquoiseContainer,
    secondary = GoldDark,
    onSecondary = Color.White,
    secondaryContainer = GoldContainer,
    onSecondaryContainer = OnGoldContainer,
    tertiary = GoldAccent,
    background = SpiritualIvory,
    surface = SpiritualSurface,
    surfaceVariant = SpiritualSurfaceVariant,
    onBackground = TextPrimaryLight,
    onSurface = TextPrimaryLight,
    onSurfaceVariant = TextSecondaryLight,
    outline = SpiritualBorder
  )

@Composable
fun RowzehClockTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

