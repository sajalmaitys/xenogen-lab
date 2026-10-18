package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val XenoGenColorScheme =
  darkColorScheme(
    primary = LabPrimary,
    onPrimary = LabOnPrimary,
    primaryContainer = LabPrimaryContainer,
    secondary = LabSecondary,
    onSecondary = LabOnSecondary,
    secondaryContainer = LabSecondaryContainer,
    background = LabBackground,
    surface = LabSurface,
    onBackground = LabOnSurface,
    onSurface = LabOnSurface,
    surfaceVariant = LabSurfaceContainer,
    onSurfaceVariant = LabOnSurfaceVariant,
    outline = LabOutline,
    outlineVariant = LabOutlineVariant,
    error = LabError,
    errorContainer = LabErrorContainer,
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = XenoGenColorScheme,
    typography = Typography,
    content = content
  )
}

