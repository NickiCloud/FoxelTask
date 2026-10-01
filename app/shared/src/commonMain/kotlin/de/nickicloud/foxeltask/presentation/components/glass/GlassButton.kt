package de.nickicloud.foxeltask.presentation.components.glass

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.nickicloud.foxeltask.theme.FoxelColors
import de.nickicloud.foxeltask.theme.FoxelShapes
import de.nickicloud.foxeltask.theme.FoxelThemeTokens
import de.nickicloud.foxeltask.theme.neonGlow

enum class GlassButtonStyle {
    PRIMARY_NEON,
    SECONDARY_GLASS,
    OUTLINE_GLASS,
    DESTRUCTIVE
}

@Composable
fun GlassButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: GlassButtonStyle = GlassButtonStyle.PRIMARY_NEON,
    icon: (@Composable () -> Unit)? = null,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    shape: Shape = FoxelShapes.ShapeSm
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isPressed by interactionSource.collectIsPressedAsState()

    val glowColor = when (style) {
        GlassButtonStyle.PRIMARY_NEON -> FoxelColors.NeonCyan
        GlassButtonStyle.SECONDARY_GLASS -> FoxelColors.NeonPurple
        GlassButtonStyle.OUTLINE_GLASS -> null
        GlassButtonStyle.DESTRUCTIVE -> FoxelColors.NeonCoral
    }

    val baseBgColor = when (style) {
        GlassButtonStyle.PRIMARY_NEON -> FoxelColors.NeonCyan.copy(alpha = if (isHovered) 0.35f else 0.22f)
        GlassButtonStyle.SECONDARY_GLASS -> FoxelThemeTokens.glass.surfaceElevated
        GlassButtonStyle.OUTLINE_GLASS -> Color.Transparent
        GlassButtonStyle.DESTRUCTIVE -> FoxelColors.NeonCoral.copy(alpha = if (isHovered) 0.35f else 0.20f)
    }

    val borderColor = when (style) {
        GlassButtonStyle.PRIMARY_NEON -> FoxelColors.NeonCyan.copy(alpha = if (isHovered) 0.9f else 0.6f)
        GlassButtonStyle.SECONDARY_GLASS -> FoxelThemeTokens.glass.borderDefault
        GlassButtonStyle.OUTLINE_GLASS -> FoxelThemeTokens.glass.borderSubtle
        GlassButtonStyle.DESTRUCTIVE -> FoxelColors.NeonCoral.copy(alpha = if (isHovered) 0.9f else 0.6f)
    }

    val contentColor = when (style) {
        GlassButtonStyle.PRIMARY_NEON -> FoxelColors.NeonCyan
        GlassButtonStyle.SECONDARY_GLASS -> Color.White
        GlassButtonStyle.OUTLINE_GLASS -> FoxelColors.TextSecondaryDark
        GlassButtonStyle.DESTRUCTIVE -> FoxelColors.NeonCoral
    }

    Box(
        modifier = modifier
            .then(
                if (glowColor != null && isHovered && enabled) {
                    Modifier.neonGlow(color = glowColor, radius = 10.dp, shape = shape)
                } else Modifier
            )
            .clip(shape)
            .background(baseBgColor)
            .border(
                width = if (isHovered) 1.5.dp else 1.dp,
                brush = Brush.verticalGradient(
                    listOf(
                        borderColor,
                        borderColor.copy(alpha = borderColor.alpha * 0.4f)
                    )
                ),
                shape = shape
            )
            .clickable(
                enabled = enabled && !isLoading,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 20.dp, vertical = 11.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                color = contentColor,
                strokeWidth = 2.dp
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                icon?.invoke()
                Text(
                    text = text,
                    color = contentColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
