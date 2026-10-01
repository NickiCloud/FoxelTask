package de.nickicloud.foxeltask.presentation.components.glass

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.nickicloud.foxeltask.theme.FoxelShapes
import de.nickicloud.foxeltask.theme.FoxelThemeTokens
import de.nickicloud.foxeltask.theme.glassmorphic

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    shape: Shape = FoxelShapes.ShapeMd,
    baseColor: Color = FoxelThemeTokens.glass.surfaceDefault,
    borderColor: Color = FoxelThemeTokens.glass.borderDefault,
    accentGlowColor: Color? = null,
    elevation: Dp = 4.dp,
    contentPadding: Dp = 16.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isPressed by interactionSource.collectIsPressedAsState()

    val animatedBgColor by animateColorAsState(
        targetValue = when {
            isPressed -> FoxelThemeTokens.glass.surfaceFocused
            isHovered -> FoxelThemeTokens.glass.surfaceElevated
            else -> baseColor
        },
        animationSpec = tween(durationMillis = 200)
    )

    val animatedBorderColor by animateColorAsState(
        targetValue = when {
            isPressed || isHovered -> accentGlowColor ?: FoxelThemeTokens.glass.borderHighlight
            else -> borderColor
        },
        animationSpec = tween(durationMillis = 200)
    )

    val activeGlow = if (isHovered || isPressed) accentGlowColor ?: FoxelThemeTokens.glass.glowPrimary else null

    Box(
        modifier = modifier
            .glassmorphic(
                backgroundColor = animatedBgColor,
                borderColor = animatedBorderColor,
                borderWidth = if (isHovered) 1.5.dp else 1.dp,
                shape = shape,
                elevation = if (isHovered) elevation + 4.dp else elevation,
                glowColor = activeGlow,
                glowRadius = if (isHovered) 16.dp else 8.dp
            )
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onClick
                    )
                } else Modifier
            )
            .padding(contentPadding)
    ) {
        Column(content = content)
    }
}
