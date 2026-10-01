package de.nickicloud.foxeltask.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class FoxelGlassTokens(
    val surfaceSubtle: Color,
    val surfaceDefault: Color,
    val surfaceElevated: Color,
    val surfaceFocused: Color,
    val borderSubtle: Color,
    val borderDefault: Color,
    val borderHighlight: Color,
    val glowPrimary: Color,
    val glowSecondary: Color,
    val glowAccent: Color,
    val defaultBorderWidth: Dp = 1.dp,
    val defaultShape: Shape = FoxelShapes.ShapeMd
)

val DarkGlassTokens = FoxelGlassTokens(
    surfaceSubtle = FoxelColors.GlassSurfaceDarkSubtle,
    surfaceDefault = FoxelColors.GlassSurfaceDark,
    surfaceElevated = FoxelColors.GlassSurfaceDarkElevated,
    surfaceFocused = FoxelColors.GlassSurfaceDarkFocused,
    borderSubtle = FoxelColors.GlassBorderDark.copy(alpha = 0.12f),
    borderDefault = FoxelColors.GlassBorderDark,
    borderHighlight = FoxelColors.GlassBorderHighlight,
    glowPrimary = FoxelColors.NeonCyan,
    glowSecondary = FoxelColors.NeonPurple,
    glowAccent = FoxelColors.NeonViolet
)

val LightGlassTokens = FoxelGlassTokens(
    surfaceSubtle = FoxelColors.GlassSurfaceLightUltraSubtle,
    surfaceDefault = FoxelColors.GlassSurfaceLightSubtle,
    surfaceElevated = FoxelColors.GlassSurfaceLight,
    surfaceFocused = FoxelColors.GlassSurfaceLightElevated,
    borderSubtle = FoxelColors.GlassBorderLight.copy(alpha = 0.2f),
    borderDefault = FoxelColors.GlassBorderLight,
    borderHighlight = Color.White.copy(alpha = 0.8f),
    glowPrimary = FoxelColors.NeonBlue,
    glowSecondary = FoxelColors.NeonPurple,
    glowAccent = FoxelColors.NeonEmerald
)

val LocalFoxelGlassTheme = staticCompositionLocalOf { DarkGlassTokens }

private val DarkColorScheme = darkColorScheme(
    primary = FoxelColors.NeonCyan,
    onPrimary = FoxelColors.VoidDark,
    primaryContainer = FoxelColors.NeonCyan.copy(alpha = 0.2f),
    onPrimaryContainer = FoxelColors.NeonCyan,
    secondary = FoxelColors.NeonPurple,
    onSecondary = Color.White,
    secondaryContainer = FoxelColors.NeonPurple.copy(alpha = 0.2f),
    onSecondaryContainer = FoxelColors.NeonPurple,
    tertiary = FoxelColors.NeonEmerald,
    onTertiary = Color.White,
    background = FoxelColors.VoidDark,
    onBackground = FoxelColors.TextPrimaryDark,
    surface = FoxelColors.DeepSpace,
    onSurface = FoxelColors.TextPrimaryDark,
    surfaceVariant = FoxelColors.MidnightSurface,
    onSurfaceVariant = FoxelColors.TextSecondaryDark,
    outline = FoxelColors.GlassBorderDark,
    error = FoxelColors.NeonCoral,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = FoxelColors.NeonBlue,
    onPrimary = Color.White,
    primaryContainer = FoxelColors.NeonBlue.copy(alpha = 0.15f),
    onPrimaryContainer = FoxelColors.NeonBlue,
    secondary = FoxelColors.NeonPurple,
    onSecondary = Color.White,
    secondaryContainer = FoxelColors.NeonPurple.copy(alpha = 0.15f),
    onSecondaryContainer = FoxelColors.NeonPurple,
    tertiary = FoxelColors.NeonEmerald,
    onTertiary = Color.White,
    background = Color(0xFFF1F5F9),
    onBackground = FoxelColors.TextPrimaryLight,
    surface = Color(0xFFFFFFFF),
    onSurface = FoxelColors.TextPrimaryLight,
    surfaceVariant = Color(0xFFE2E8F0),
    onSurfaceVariant = FoxelColors.TextSecondaryLight,
    outline = FoxelColors.GlassBorderLight,
    error = FoxelColors.NeonCoral,
    onError = Color.White
)

@Composable
fun FoxelTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val glassTokens = if (darkTheme) DarkGlassTokens else LightGlassTokens

    CompositionLocalProvider(
        LocalFoxelGlassTheme provides glassTokens
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = FoxelTypography,
            shapes = Shapes(
                small = FoxelShapes.ShapeSm,
                medium = FoxelShapes.ShapeMd,
                large = FoxelShapes.ShapeLg,
                extraLarge = FoxelShapes.ShapeXl
            ),
            content = content
        )
    }
}

object FoxelThemeTokens {
    val glass: FoxelGlassTokens
        @Composable
        @ReadOnlyComposable
        get() = LocalFoxelGlassTheme.current
}
