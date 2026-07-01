package com.piplog.app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.graphics.Brush
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = Color.White,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = OnPrimaryContainer,
    secondary = PrimaryLight,
    onSecondary = Color.White,
    secondaryContainer = SurfaceVariant,
    onSecondaryContainer = OnSurface,
    tertiary = Warning,
    background = Background,
    onBackground = OnBackground,
    surface = Surface,
    onSurface = OnSurface,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = OnSurfaceVariant,
    outline = BorderColor,
    outlineVariant = MutedText,
    error = Loss,
    onError = Color.White,
    errorContainer = LossBackground,
    onErrorContainer = Loss,
    scrim = Color.Black
)

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = Color.White,
    primaryContainer = OnPrimaryContainer,
    onPrimaryContainer = PrimaryContainer,
    secondary = PrimaryDark,
    onSecondary = Color.White,
    secondaryContainer = LightSurfaceVariant,
    onSecondaryContainer = OnLightSurface,
    tertiary = Warning,
    background = LightBackgroundEnd,
    onBackground = OnLightBackground,
    surface = LightSurface,
    onSurface = OnLightSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightMutedText,
    outline = BorderColor,
    outlineVariant = LightMutedText,
    error = Loss,
    onError = Color.White,
    errorContainer = LossBackground,
    onErrorContainer = Loss,
    scrim = Color.Black.copy(alpha = 0.5f)
)

@Composable
fun PipLogTheme(
    themeViewModel: ThemeViewModel = viewModel(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val isDarkMode by themeViewModel.isDarkMode.collectAsState()
    val colorScheme = if (isDarkMode) DarkColorScheme else LightColorScheme

    val backgroundBrush = if (isDarkMode) {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFF0D1B2A), // Dark blue top
                Color(0xFF010203)  // Black bottom
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                LightBackgroundStart, // Light blue top
                LightBackgroundEnd    // White bottom
            )
        )
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDarkMode
        }
    }

    CompositionLocalProvider(
        LocalBackgroundBrush provides backgroundBrush,
        LocalThemeViewModel provides themeViewModel
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

@Composable
fun ThemedBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val backgroundBrush = LocalBackgroundBrush.current ?: Brush.verticalGradient(
        colors = listOf(MaterialTheme.colorScheme.background, MaterialTheme.colorScheme.background)
    )
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundBrush),
        content = content
    )
}

val LocalBackgroundBrush = staticCompositionLocalOf<Brush?> { null }
val LocalThemeViewModel = staticCompositionLocalOf<ThemeViewModel> { error("No ThemeViewModel provided") }
