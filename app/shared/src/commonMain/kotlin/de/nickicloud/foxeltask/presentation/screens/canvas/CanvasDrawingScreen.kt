package de.nickicloud.foxeltask.presentation.screens.canvas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.nickicloud.foxeltask.model.DrawingTool
import de.nickicloud.foxeltask.presentation.components.glass.*
import de.nickicloud.foxeltask.theme.FoxelColors
import de.nickicloud.foxeltask.theme.FoxelShapes

data class ColoredPath(
    val points: List<Offset>,
    val color: Color,
    val strokeWidth: Float,
    val isEraser: Boolean = false
)

@Composable
fun CanvasDrawingScreen(modifier: Modifier = Modifier) {
    val paths = remember { mutableStateListOf<ColoredPath>() }
    val undonePaths = remember { mutableStateListOf<ColoredPath>() }

    var currentTool by remember { mutableStateOf(DrawingTool.PEN) }
    var selectedColor by remember { mutableStateOf(FoxelColors.NeonCyan) }
    var strokeWidth by remember { mutableStateOf(4f) }
    var activePoints by remember { mutableStateOf<List<Offset>>(emptyList()) }

    val colors = listOf(
        FoxelColors.NeonCyan,
        FoxelColors.NeonPurple,
        FoxelColors.NeonEmerald,
        FoxelColors.NeonAmber,
        FoxelColors.NeonCoral,
        Color.White
    )

    Column(modifier = modifier.fillMaxSize()) {
        // Stylus Toolbar
        GlassSurface(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = FoxelColors.GlassSurfaceDarkSubtle,
            shape = FoxelShapes.ShapeMd,
            contentPadding = 10.dp
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tool Selectors
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf(
                        DrawingTool.PEN to "🖊️ Stift",
                        DrawingTool.HIGHLIGHTER to "🖍️ Marker",
                        DrawingTool.ERASER to "🧹 Radierer"
                    ).forEach { (tool, label) ->
                        val isSelected = currentTool == tool
                        Box(
                            modifier = Modifier
                                .clip(FoxelShapes.ShapeSm)
                                .background(
                                    if (isSelected) FoxelColors.NeonCyan.copy(alpha = 0.25f) else Color.Transparent
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) FoxelColors.NeonCyan else FoxelColors.GlassBorderDark,
                                    FoxelShapes.ShapeSm
                                )
                                .clickable {
                                    currentTool = tool
                                    strokeWidth = when (tool) {
                                        DrawingTool.PEN -> 4f
                                        DrawingTool.HIGHLIGHTER -> 18f
                                        DrawingTool.ERASER -> 24f
                                        else -> 4f
                                    }
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(label, color = if (isSelected) FoxelColors.NeonCyan else FoxelColors.TextSecondaryDark, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Color Palette
                    if (currentTool != DrawingTool.ERASER) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            colors.forEach { col ->
                                val isSelected = selectedColor == col
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(col)
                                        .border(
                                            if (isSelected) 2.dp else 1.dp,
                                            if (isSelected) Color.White else Color.Transparent,
                                            CircleShape
                                        )
                                        .clickable { selectedColor = col }
                                )
                            }
                        }
                    }
                }

                // Actions: Undo, Redo, Clear
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GlassButton(
                        text = "↩ Rückgängig",
                        onClick = {
                            if (paths.isNotEmpty()) {
                                val last = paths.removeLast()
                                undonePaths.add(last)
                            }
                        },
                        style = GlassButtonStyle.OUTLINE_GLASS,
                        enabled = paths.isNotEmpty()
                    )

                    GlassButton(
                        text = "↪ Wiederholen",
                        onClick = {
                            if (undonePaths.isNotEmpty()) {
                                val restored = undonePaths.removeLast()
                                paths.add(restored)
                            }
                        },
                        style = GlassButtonStyle.OUTLINE_GLASS,
                        enabled = undonePaths.isNotEmpty()
                    )

                    GlassButton(
                        text = "Canvas leeren",
                        onClick = {
                            paths.clear()
                            undonePaths.clear()
                            activePoints = emptyList()
                        },
                        style = GlassButtonStyle.DESTRUCTIVE
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Vector Canvas
        GlassSurface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            backgroundColor = FoxelColors.DeepSpace,
            shape = FoxelShapes.ShapeMd
        ) {
            androidx.compose.foundation.Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(currentTool, selectedColor, strokeWidth) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                activePoints = listOf(offset)
                            },
                            onDrag = { change, _ ->
                                activePoints = activePoints + change.position
                            },
                            onDragEnd = {
                                if (activePoints.isNotEmpty()) {
                                    val isEraser = currentTool == DrawingTool.ERASER
                                    val color = if (currentTool == DrawingTool.HIGHLIGHTER) {
                                        selectedColor.copy(alpha = 0.35f)
                                    } else if (isEraser) {
                                        FoxelColors.DeepSpace
                                    } else {
                                        selectedColor
                                    }

                                    paths.add(ColoredPath(activePoints, color, strokeWidth, isEraser))
                                    activePoints = emptyList()
                                    undonePaths.clear()
                                }
                            }
                        )
                    }
            ) {
                // Draw existing strokes
                paths.forEach { coloredPath ->
                    if (coloredPath.points.size > 1) {
                        val path = Path().apply {
                            moveTo(coloredPath.points.first().x, coloredPath.points.first().y)
                            for (i in 1 until coloredPath.points.size) {
                                lineTo(coloredPath.points[i].x, coloredPath.points[i].y)
                            }
                        }
                        drawPath(path, color = coloredPath.color, style = Stroke(width = coloredPath.strokeWidth))
                    }
                }

                // Draw active stroke
                if (activePoints.size > 1) {
                    val activePath = Path().apply {
                        moveTo(activePoints.first().x, activePoints.first().y)
                        for (i in 1 until activePoints.size) {
                            lineTo(activePoints[i].x, activePoints[i].y)
                        }
                    }
                    val activeColor = if (currentTool == DrawingTool.HIGHLIGHTER) {
                        selectedColor.copy(alpha = 0.35f)
                    } else if (currentTool == DrawingTool.ERASER) {
                        FoxelColors.DeepSpace
                    } else {
                        selectedColor
                    }
                    drawPath(activePath, color = activeColor, style = Stroke(width = strokeWidth))
                }
            }
        }
    }
}
