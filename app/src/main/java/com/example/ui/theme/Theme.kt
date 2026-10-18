package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CyberColorScheme = darkColorScheme(
    primary = CyberCyan,
    onPrimary = Color.Black,
    primaryContainer = CyberSurfaceVariant,
    onPrimaryContainer = CyberCyan,
    secondary = CyberPurple,
    onSecondary = Color.White,
    secondaryContainer = CyberSurface,
    onSecondaryContainer = CyberPurple,
    tertiary = CyberGreen,
    onTertiary = Color.Black,
    background = CyberBackground,
    onBackground = CyberTextPrimary,
    surface = CyberSurface,
    onSurface = CyberTextPrimary,
    surfaceVariant = CyberSurfaceVariant,
    onSurfaceVariant = CyberTextSecondary,
    outline = CyberCyan.copy(alpha = 0.35f)
)

@Composable
fun MyApplicationTheme(
    accentColor: Color = CyberCyan,
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val dynamicScheme = CyberColorScheme.copy(
        primary = accentColor,
        onPrimaryContainer = accentColor,
        outline = accentColor.copy(alpha = 0.35f)
    )

    MaterialTheme(
        colorScheme = dynamicScheme,
        typography = Typography,
        content = content
    )
}
