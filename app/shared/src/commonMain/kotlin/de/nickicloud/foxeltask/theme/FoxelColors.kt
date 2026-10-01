package de.nickicloud.foxeltask.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

object FoxelColors {
    // Brand & Accent Colors
    val NeonCyan = Color(0xFF00F0FF)
    val NeonPurple = Color(0xFFA855F7)
    val NeonViolet = Color(0xFF8B5CF6)
    val NeonBlue = Color(0xFF3B82F6)
    val NeonEmerald = Color(0xFF10B981)
    val NeonAmber = Color(0xFFF59E0B)
    val NeonCoral = Color(0xFFFF5252)
    val NeonRose = Color(0xFFEC4899)

    // Deep Dark Backgrounds (for Dark Glassmorphism)
    val VoidDark = Color(0xFF070B14)
    val DeepSpace = Color(0xFF0B1120)
    val SlateDark = Color(0xFF111827)
    val MidnightSurface = Color(0xFF1E293B)

    // Frosted Glass Layer Colors - Dark Mode
    val GlassSurfaceDarkUltraSubtle = Color(0x0AFFFFFF) // 4% white
    val GlassSurfaceDarkSubtle = Color(0x14FFFFFF)      // 8% white
    val GlassSurfaceDark = Color(0x24FFFFFF)            // 14% white
    val GlassSurfaceDarkElevated = Color(0x38FFFFFF)    // 22% white
    val GlassSurfaceDarkFocused = Color(0x4DFFFFFF)     // 30% white

    // Frosted Glass Layer Colors - Light Mode
    val GlassSurfaceLightUltraSubtle = Color(0x29FFFFFF) // 16% white
    val GlassSurfaceLightSubtle = Color(0x66FFFFFF)      // 40% white
    val GlassSurfaceLight = Color(0x99FFFFFF)            // 60% white
    val GlassSurfaceLightElevated = Color(0xCCFFFFFF)    // 80% white

    // Glass Border Tints
    val GlassBorderDark = Color(0x33FFFFFF)             // 20% white border
    val GlassBorderLight = Color(0x66FFFFFF)            // 40% white border
    val GlassBorderHighlight = Color(0x80FFFFFF)        // 50% white top-highlight
    val GlassBorderNeonCyan = Color(0x8000F0FF)
    val GlassBorderNeonPurple = Color(0x80A855F7)

    // Text & Foreground Colors
    val TextPrimaryDark = Color(0xFFF8FAFC)
    val TextSecondaryDark = Color(0xFF94A3B8)
    val TextMutedDark = Color(0xFF64748B)

    val TextPrimaryLight = Color(0xFF0F172A)
    val TextSecondaryLight = Color(0xFF475569)
    val TextMutedLight = Color(0xFF94A3B8)

    // Gradients
    val AuroraGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFF0F172A),
            Color(0xFF1E1B4B),
            Color(0xFF0F172A)
        )
    )

    val CyberpunkGradient = Brush.linearGradient(
        colors = listOf(
            Color(0x3300F0FF),
            Color(0x33A855F7)
        )
    )

    val AccentGlassGradient = Brush.horizontalGradient(
        colors = listOf(
            NeonCyan.copy(alpha = 0.8f),
            NeonViolet.copy(alpha = 0.8f)
        )
    )

    val SurfaceHighlightGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0x2EFFFFFF),
            Color(0x05FFFFFF)
        )
    )

    val CardBorderGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0x66FFFFFF),
            Color(0x14FFFFFF),
            Color(0x0AFFFFFF)
        )
    )

    fun parseHexColor(hex: String, defaultColor: Color = NeonCyan): Color {
        val clean = hex.removePrefix("#").trim()
        return try {
            when (clean.length) {
                6 -> {
                    val r = clean.substring(0, 2).toInt(16)
                    val g = clean.substring(2, 4).toInt(16)
                    val b = clean.substring(4, 6).toInt(16)
                    Color(r, g, b)
                }
                8 -> {
                    val a = clean.substring(0, 2).toInt(16)
                    val r = clean.substring(2, 4).toInt(16)
                    val g = clean.substring(4, 6).toInt(16)
                    val b = clean.substring(6, 8).toInt(16)
                    Color(r, g, b, a)
                }
                3 -> {
                    val r = clean.substring(0, 1).repeat(2).toInt(16)
                    val g = clean.substring(1, 2).repeat(2).toInt(16)
                    val b = clean.substring(2, 3).repeat(2).toInt(16)
                    Color(r, g, b)
                }
                else -> defaultColor
            }
        } catch (_: Throwable) {
            defaultColor
        }
    }
}
