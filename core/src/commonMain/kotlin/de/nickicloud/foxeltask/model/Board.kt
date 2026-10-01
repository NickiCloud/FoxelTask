package de.nickicloud.foxeltask.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Board(
    val id: String,
    val workspaceId: String,
    val title: String,
    val description: String? = null,
    val type: BoardType = BoardType.KANBAN,
    val createdById: String,
    val isArchived: Boolean = false,
    val position: Int = 0,
    val colorTheme: String? = null,
    val columns: List<BoardColumn> = emptyList(),
    val createdAt: Instant,
    val updatedAt: Instant
)

@Serializable
data class BoardColumn(
    val id: String,
    val boardId: String,
    val name: String,
    val colorHex: String = "#6366F1",
    val position: Int = 0,
    val wipLimit: Int? = null,
    val cards: List<KanbanCard> = emptyList(),
    val createdAt: Instant,
    val updatedAt: Instant
)

@Serializable
data class KanbanCard(
    val id: String,
    val columnId: String,
    val boardId: String,
    val title: String,
    val description: String? = null,
    val priority: Priority = Priority.MEDIUM,
    val assigneeId: String? = null,
    val assignee: UserProfile? = null,
    val dueDate: Instant? = null,
    val position: Int = 0,
    val coverColor: String? = null,
    val isCompleted: Boolean = false,
    val tags: List<Tag> = emptyList(),
    val attachmentsCount: Int = 0,
    val notesCount: Int = 0,
    val createdAt: Instant,
    val updatedAt: Instant
)

@Serializable
data class CreateBoardRequest(
    val workspaceId: String,
    val title: String,
    val description: String? = null,
    val type: BoardType = BoardType.KANBAN,
    val colorTheme: String? = null
)

@Serializable
data class CreateColumnRequest(
    val boardId: String,
    val name: String,
    val colorHex: String? = null,
    val position: Int? = null,
    val wipLimit: Int? = null
)

@Serializable
data class CreateCardRequest(
    val columnId: String,
    val boardId: String,
    val title: String,
    val description: String? = null,
    val priority: Priority = Priority.MEDIUM,
    val assigneeId: String? = null,
    val dueDate: Instant? = null,
    val coverColor: String? = null,
    val tagIds: List<String> = emptyList()
)

@Serializable
data class MoveCardRequest(
    val cardId: String,
    val targetColumnId: String,
    val targetPosition: Int
)

@Serializable
data class MoveColumnRequest(
    val columnId: String,
    val targetPosition: Int
)
