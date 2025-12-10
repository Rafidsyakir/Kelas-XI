package com.example.aplikasimonitoringkelas3.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Aurora Night Dark Theme - Premium Look
private val DarkColorScheme = darkColorScheme(
    primary = GradientBlue,
    secondary = AuroraPurple,
    tertiary = AuroraPink,
    background = SpaceBlack,
    surface = CardDark,
    surfaceVariant = CardElevated,
    onPrimary = TextBright,
    onSecondary = TextBright,
    onTertiary = TextBright,
    onBackground = TextBright,
    onSurface = TextLight,
    onSurfaceVariant = TextMuted,
    primaryContainer = SpaceBlue,
    secondaryContainer = Color(0xFF2D1B4E),
    tertiaryContainer = Color(0xFF4A1942),
    error = NeonError,
    errorContainer = Color(0xFF3D1419),
    onErrorContainer = NeonError,
    outline = GlassBorder,
    outlineVariant = GlassLight
)

// Aurora Light Theme - Clean Modern
private val LightColorScheme = lightColorScheme(
    primary = GradientBlue,
    secondary = AuroraPurple,
    tertiary = AuroraPink,
    background = Color(0xFFF8FAFC),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFF1F5F9),
    onPrimary = TextBright,
    onSecondary = TextBright,
    onTertiary = TextBright,
    onBackground = SpaceBlack,
    onSurface = SpaceNavy,
    onSurfaceVariant = TextDark,
    primaryContainer = Color(0xFFE0F2FE),
    secondaryContainer = Color(0xFFF3E8FF),
    tertiaryContainer = Color(0xFFFCE7F3),
    error = NeonError,
    errorContainer = Color(0xFFFEE2E2),
    onErrorContainer = Color(0xFFB91C1C)
)

@Composable
fun AplikasiMonitoringKelas3Theme(
    darkTheme: Boolean = true, // Default to dark theme for Aurora look
    dynamicColor: Boolean = false,
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

    // Make status bar transparent for immersive look
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}