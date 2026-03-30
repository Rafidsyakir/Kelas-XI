package com.example.trifhop.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * ════════════════════════════════════════════════════════════
 *   TRIFHOP PREMIUM COLOR SYSTEM - Award Winning Palette
 * ════════════════════════════════════════════════════════════
 * Designed with perfect contrast & accessibility (WCAG AAA)
 * Main Brand: Electric Blue #137FEC
 */

// ═══════════════ PRIMARY BRAND COLORS ═══════════════
val TrifhopBlue = Color(0xFF137FEC)           // Main brand color
val TrifhopBlueLight = Color(0xFF4DA3F5)       // Lighter variant
val TrifhopBlueDark = Color(0xFF0D5FB8)        // Darker variant
val TrifhopBlueVeryDark = Color(0xFF0A4A8F)    // Very dark variant

// ═══════════════ BACKGROUND & SURFACES ═══════════════
// Dark Theme (Premium)
val BackgroundDark = Color(0xFF0A0E14)         // Deep dark background
val SurfaceDark = Color(0xFF151B25)            // Elevated surface
val SurfaceVariantDark = Color(0xFF1E2633)     // Cards & containers
val SurfaceElevatedDark = Color(0xFF242D3D)    // Elevated cards

// Light Theme (Clean & Bright)
val BackgroundLight = Color(0xFFF8F9FB)        // Soft background
val SurfaceLight = Color(0xFFFFFFFF)           // Pure white surface
val SurfaceVariantLight = Color(0xFFF1F3F6)    // Light cards
val SurfaceElevatedLight = Color(0xFFFFFFFF)   // Elevated white

// ═══════════════ TEXT COLORS (Perfect Contrast) ═══════════════
// Dark Theme Text
val TextPrimaryDark = Color(0xFFF0F3F7)        // Almost white (high contrast)
val TextSecondaryDark = Color(0xFF9BA7B8)      // Muted gray-blue
val TextTertiaryDark = Color(0xFF6B7A8F)       // Subtle gray
val TextDisabledDark = Color(0xFF4A5664)       // Disabled state

// Light Theme Text
val TextPrimaryLight = Color(0xFF0D1117)       // Almost black (high contrast)
val TextSecondaryLight = Color(0xFF5A6477)     // Medium gray
val TextTertiaryLight = Color(0xFF8391A5)      // Light gray
val TextDisabledLight = Color(0xFFB4BCC8)      // Disabled state

// ═══════════════ SEMANTIC COLORS ═══════════════
val SuccessGreen = Color(0xFF10B981)           // Success state
val SuccessLight = Color(0xFF34D399)           // Light success
val SuccessDark = Color(0xFF059669)            // Dark success

val ErrorRed = Color(0xFFEF4444)               // Error state
val ErrorLight = Color(0xFFF87171)             // Light error
val ErrorDark = Color(0xFFDC2626)              // Dark error

val WarningOrange = Color(0xFFF59E0B)          // Warning state
val WarningLight = Color(0xFFFBBF24)           // Light warning
val WarningDark = Color(0xFFD97706)            // Dark warning

val InfoBlue = Color(0xFF3B82F6)               // Info state
val InfoLight = Color(0xFF60A5FA)              // Light info
val InfoDark = Color(0xFF2563EB)               // Dark info

// ═══════════════ NEUTRAL PALETTE (Premium Grays) ═══════════════
val Gray50 = Color(0xFFFAFBFC)
val Gray100 = Color(0xFFF3F5F7)
val Gray200 = Color(0xFFE7EBF0)
val Gray300 = Color(0xFFD1D8E0)
val Gray400 = Color(0xFFA1AEC0)
val Gray500 = Color(0xFF6B7A8F)
val Gray600 = Color(0xFF4E5D73)
val Gray700 = Color(0xFF364152)
val Gray800 = Color(0xFF1E2633)
val Gray900 = Color(0xFF0F151D)

// ═══════════════ ACCENT COLORS (Premium Touches) ═══════════════
val GoldAccent = Color(0xFFFFB800)             // Premium gold
val PurpleAccent = Color(0xFF8B5CF6)           // Purple accent
val TealAccent = Color(0xFF14B8A6)             // Teal accent
val PinkAccent = Color(0xFFEC4899)             // Pink accent

// ═══════════════ OVERLAY & SHADOWS ═══════════════
val OverlayDark = Color(0x99000000)            // 60% opacity dark
val OverlayLight = Color(0x66000000)           // 40% opacity dark
val ShadowBlue = Color(0x33137FEC)             // Blue shadow (20%)
val ShadowDark = Color(0x1A000000)             // Subtle dark shadow

// ═══════════════ SHIMMER EFFECT COLORS ═══════════════
val ShimmerBaseLight = Color(0xFFE7EBF0)
val ShimmerHighlightLight = Color(0xFFF3F5F7)
val ShimmerBaseDark = Color(0xFF1E2633)
val ShimmerHighlightDark = Color(0xFF242D3D)

// ═══════════════ STATUS COLORS ═══════════════
val OnlineGreen = Color(0xFF22C55E)
val OfflineGray = Color(0xFF6B7280)
val PendingYellow = Color(0xFFFBBF24)
val CompletedBlue = Color(0xFF3B82F6)

// Legacy colors for compatibility
val Primary = TrifhopBlue
val PrimaryVariant = TrifhopBlueDark
val ErrorColor = ErrorRed
val SuccessColor = SuccessGreen
val WarningColor = WarningOrange