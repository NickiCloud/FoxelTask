package de.nickicloud.foxeltask.presentation.components.glass

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.nickicloud.foxeltask.theme.FoxelShapes
import de.nickicloud.foxeltask.theme.FoxelThemeTokens
import de.nickicloud.foxeltask.theme.glassmorphic

@Composable
fun GlassSurface(
    modifier: Modifier = Modifier,
    shape: Shape = FoxelShapes.ShapeMd,
    backgroundColor: Color = FoxelThemeTokens.glass.surfaceDefault,
    borderColor: Color = FoxelThemeTokens.glass.borderDefault,
    borderWidth: Dp = FoxelThemeTokens.glass.defaultBorderWidth,
    elevation: Dp = 0.dp,
    glowColor: Color? = null,
    glowRadius: Dp = 12.dp,
    contentPadding: Dp = 0.dp,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .glassmorphic(
                backgroundColor = backgroundColor,
                borderColor = borderColor,
                borderWidth = borderWidth,
                shape = shape,
                elevation = elevation,
                glowColor = glowColor,
                glowRadius = glowRadius
            )
            .padding(contentPadding),
        content = content
    )
}
