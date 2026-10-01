package de.nickicloud.foxeltask.theme

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Helper to draw any Shape Outline in DrawScope on all Compose Multiplatform targets.
 */
private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawShapeOutline(
    outline: Outline,
    brush: Brush,
    style: DrawStyle = Fill
) {
    when (outline) {
        is Outline.Rectangle -> drawRect(
            brush = brush,
            topLeft = outline.rect.topLeft,
            size = outline.rect.size,
            style = style
        )
        is Outline.Rounded -> drawRoundRect(
            brush = brush,
            topLeft = Offset(outline.roundRect.left, outline.roundRect.top),
            size = Size(outline.roundRect.width, outline.roundRect.height),
            cornerRadius = CornerRadius(
                outline.roundRect.bottomLeftCornerRadius.x,
                outline.roundRect.bottomLeftCornerRadius.y
            ),
            style = style
        )
        is Outline.Generic -> drawPath(
            path = outline.path,
            brush = brush,
            style = style
        )
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawShapeOutline(
    outline: Outline,
    color: Color,
    style: DrawStyle = Fill
) {
    when (outline) {
        is Outline.Rectangle -> drawRect(
            color = color,
            topLeft = outline.rect.topLeft,
            size = outline.rect.size,
            style = style
        )
        is Outline.Rounded -> drawRoundRect(
            color = color,
            topLeft = Offset(outline.roundRect.left, outline.roundRect.top),
            size = Size(outline.roundRect.width, outline.roundRect.height),
            cornerRadius = CornerRadius(
                outline.roundRect.bottomLeftCornerRadius.x,
                outline.roundRect.bottomLeftCornerRadius.y
            ),
            style = style
        )
        is Outline.Generic -> drawPath(
            path = outline.path,
            color = color,
            style = style
        )
    }
}

/**
 * Core Glassmorphism modifier applying a translucent background, frosted highlight border,
 * optional subtle elevation shadow, and smooth clipping.
 */
fun Modifier.glassmorphic(
    backgroundColor: Color = FoxelColors.GlassSurfaceDark,
    borderColor: Color = FoxelColors.GlassBorderDark,
    borderWidth: Dp = 1.dp,
    shape: Shape = FoxelShapes.ShapeMd,
    elevation: Dp = 0.dp,
    glowColor: Color? = null,
    glowRadius: Dp = 12.dp
): Modifier = this.then(
    Modifier
        .then(
            if (elevation > 0.dp) {
                Modifier.shadow(elevation = elevation, shape = shape, spotColor = Color.Black.copy(alpha = 0.5f))
            } else Modifier
        )
        .then(
            if (glowColor != null) {
                Modifier.neonGlow(color = glowColor, radius = glowRadius, shape = shape)
            } else Modifier
        )
        .clip(shape)
        .background(backgroundColor)
        .border(
            width = borderWidth,
            brush = Brush.verticalGradient(
                colors = listOf(
                    borderColor.copy(alpha = (borderColor.alpha * 1.5f).coerceAtMost(1f)),
                    borderColor.copy(alpha = borderColor.alpha * 0.4f),
                    borderColor.copy(alpha = borderColor.alpha * 0.15f)
                )
            ),
            shape = shape
        )
        .drawBehind {
            // Draw subtle top specular light highlight for hyper-realistic glass feel
            val highlightBrush = Brush.verticalGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.12f),
                    Color.White.copy(alpha = 0.01f),
                    Color.Transparent
                ),
                startY = 0f,
                endY = size.height * 0.35f
            )
            drawRect(brush = highlightBrush)
        }
)

/**
 * Frosted Glass modifier with layered translucency and gradient accent border.
 */
fun Modifier.frostedGlass(
    shape: Shape = FoxelShapes.ShapeMd,
    tint: Color = FoxelColors.GlassSurfaceDark,
    accentGradients: List<Color> = listOf(FoxelColors.GlassBorderHighlight, FoxelColors.GlassBorderDark)
): Modifier = this
    .clip(shape)
    .background(tint)
    .border(
        width = 1.dp,
        brush = Brush.linearGradient(colors = accentGradients),
        shape = shape
    )

/**
 * Neon Glow modifier creating a luminous soft outer aura around any shape.
 */
fun Modifier.neonGlow(
    color: Color = FoxelColors.NeonCyan,
    radius: Dp = 12.dp,
    shape: Shape = FoxelShapes.ShapeMd
): Modifier = this.drawBehind {
    val outline = shape.createOutline(size, layoutDirection, this)
    drawShapeOutline(
        outline = outline,
        brush = Brush.radialGradient(
            colors = listOf(
                color.copy(alpha = 0.30f),
                color.copy(alpha = 0.10f),
                Color.Transparent
            ),
            center = Offset(size.width / 2, size.height / 2),
            radius = (size.width.coerceAtLeast(size.height) / 2) + radius.toPx()
        )
    )
}

/**
 * Animated Pulse Glow modifier for active cards, live notifications, or selected boards.
 */
fun Modifier.pulsingGlow(
    color: Color = FoxelColors.NeonCyan,
    minAlpha: Float = 0.15f,
    maxAlpha: Float = 0.55f,
    durationMs: Int = 1800
): Modifier = composed {
    val transition = rememberInfiniteTransition()
    val animatedAlpha by transition.animateFloat(
        initialValue = minAlpha,
        targetValue = maxAlpha,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = durationMs, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    this.drawBehind {
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(
                    color.copy(alpha = animatedAlpha),
                    Color.Transparent
                ),
                center = Offset(size.width / 2, size.height / 2),
                radius = size.width.coerceAtLeast(size.height) * 0.75f
            )
        )
    }
}

/**
 * Subtle inner highlight stroke for pristine glass depth.
 */
fun Modifier.glassInnerHighlight(
    shape: Shape = FoxelShapes.ShapeMd,
    strokeWidth: Float = 1.5f,
    highlightColor: Color = Color.White.copy(alpha = 0.15f)
): Modifier = this.drawBehind {
    val outline = shape.createOutline(size, layoutDirection, this)
    drawShapeOutline(
        outline = outline,
        color = highlightColor,
        style = Stroke(width = strokeWidth)
    )
}
