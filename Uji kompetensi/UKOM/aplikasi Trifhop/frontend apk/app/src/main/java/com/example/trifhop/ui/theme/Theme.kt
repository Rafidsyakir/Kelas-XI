package com.example.trifhop.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

/**
 * ════════════════════════════════════════════════════════════
 *   TRIFHOP PREMIUM SHAPE SYSTEM
 * ════════════════════════════════════════════════════════════
 * Modern corner radii for clean, premium feel
 */
private val TrifhopShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),    // Tiny elements
    small = RoundedCornerShape(8.dp),         // Chips, badges
    medium = RoundedCornerShape(12.dp),       // Cards, inputs
    large = RoundedCornerShape(16.dp),        // Large cards
    extraLarge = RoundedCornerShape(28.dp)    // Bottom sheets, modals
)

/**
 * ════════════════════════════════════════════════════════════
 *   DARK COLOR SCHEME (Premium & Modern)
 * ════════════════════════════════════════════════════════════
 */
private val DarkColorScheme = darkColorScheme(
    // Primary Brand
    primary = TrifhopBlue,
    onPrimary = Color.White,
    primaryContainer = TrifhopBlueDark,
    onPrimaryContainer = TrifhopBlueLight,
    
    // Secondary
    secondary = TrifhopBlueLight,
    onSecondary = Color.White,
    secondaryContainer = TrifhopBlueDark,
    onSecondaryContainer = TrifhopBlueLight,
    
    // Tertiary (Accent)
    tertiary = TealAccent,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF115E59),
    onTertiaryContainer = Color(0xFF5EEAD4),
    
    // Background & Surface
    background = BackgroundDark,
    onBackground = TextPrimaryDark,
    surface = SurfaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = TextSecondaryDark,
    surfaceTint = TrifhopBlue,
    
    // Inverse
    inverseSurface = SurfaceLight,
    inverseOnSurface = TextPrimaryLight,
    inversePrimary = TrifhopBlueDark,
    
    // Borders & Outlines
    outline = Gray600,
    outlineVariant = Gray700,
    
    // Semantic Colors
    error = ErrorRed,
    onError = Color.White,
    errorContainer = ErrorDark,
    onErrorContainer = ErrorLight,
    
    // Scrim
    scrim = Color(0xCC000000)
)

/**
 * ════════════════════════════════════════════════════════════
 *   LIGHT COLOR SCHEME (Clean & Bright)
 * ════════════════════════════════════════════════════════════
 */
private val LightColorScheme = lightColorScheme(
    // Primary Brand
    primary = TrifhopBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD6EDFF),
    onPrimaryContainer = TrifhopBlueVeryDark,
    
    // Secondary
    secondary = TrifhopBlueDark,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD6EDFF),
    onSecondaryContainer = TrifhopBlueVeryDark,
    
    // Tertiary (Accent)
    tertiary = TealAccent,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFA7F3D0),
    onTertiaryContainer = Color(0xFF064E3B),
    
    // Background & Surface
    background = BackgroundLight,
    onBackground = TextPrimaryLight,
    surface = SurfaceLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = TextSecondaryLight,
    surfaceTint = TrifhopBlue,
    
    // Inverse
    inverseSurface = SurfaceDark,
    inverseOnSurface = TextPrimaryDark,
    inversePrimary = TrifhopBlueLight,
    
    // Borders & Outlines
    outline = Gray400,
    outlineVariant = Gray200,
    
    // Semantic Colors
    error = ErrorRed,
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = ErrorDark,
    
    // Scrim
    scrim = Color(0x99000000)
)

/**
 * ════════════════════════════════════════════════════════════
 *   MAIN THEME COMPOSABLE
 * ════════════════════════════════════════════════════════════
 */
@Composable
fun TrifhopTheme(
    darkTheme: Boolean = true, // Default to dark for premium look
    dynamicColor: Boolean = false, // Disable for brand consistency
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
        shapes = TrifhopShapes,
        content = content
    )
}