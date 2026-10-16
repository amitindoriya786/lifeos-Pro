package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = EmeraldPrimary,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF064E3B),
    onPrimaryContainer = EmeraldLight,
    secondary = IndigoAccent,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF312E81),
    onSecondaryContainer = IndigoLight,
    tertiary = AmberAccent,
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFF78350F),
    onTertiaryContainer = AmberLight,
    error = RoseAccent,
    onError = Color.White,
    background = LifeOSBg,
    onBackground = TextPrimary,
    surface = LifeOSSurface,
    onSurface = TextPrimary,
    surfaceVariant = LifeOSSurfaceElevated,
    onSurfaceVariant = TextSecondary,
    outline = LifeOSBorder
)

@Composable
fun LifeOSProTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
