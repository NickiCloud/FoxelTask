package de.nickicloud.foxeltask.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
enum class DrawingTool {
    PEN,
    HIGHLIGHTER,
    ERASER,
    SHAPE_RECT,
    SHAPE_CIRCLE,
    SHAPE_LINE,
    SHAPE_ARROW,
    LASSO
}

@Serializable
data class DrawingPoint(
    val x: Float,
    val y: Float,
    val pressure: Float = 1.0f,
    val timestamp: Long = 0L
)

@Serializable
data class DrawingStroke(
    val id: String,
    val tool: DrawingTool = DrawingTool.PEN,
    val colorHex: String = "#FFFFFF",
    val strokeWidth: Float = 3.0f,
    val opacity: Float = 1.0f,
    val points: List<DrawingPoint> = emptyList(),
    val isEraser: Boolean = false
)

@Serializable
data class DrawingCanvasData(
    val version: Int = 1,
    val backgroundColorHex: String = "#0F172A",
    val strokes: List<DrawingStroke> = emptyList()
)

@Serializable
data class Drawing(
    val id: String,
    val workspaceId: String,
    val noteId: String? = null,
    val title: String,
    val canvasData: DrawingCanvasData,
    val thumbnailDataUrl: String? = null,
    val createdById: String,
    val createdAt: Instant,
    val updatedAt: Instant
)

@Serializable
data class SaveDrawingRequest(
    val workspaceId: String,
    val noteId: String? = null,
    val title: String,
    val canvasData: DrawingCanvasData,
    val thumbnailDataUrl: String? = null
)
