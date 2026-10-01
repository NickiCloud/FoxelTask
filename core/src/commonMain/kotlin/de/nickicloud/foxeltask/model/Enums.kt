package de.nickicloud.foxeltask.model

import kotlinx.serialization.Serializable

@Serializable
enum class Role {
    GLOBAL_ADMIN,
    WORKSPACE_ADMIN,
    EDITOR,
    VIEWER;

    val canEdit: Boolean
        get() = this == GLOBAL_ADMIN || this == WORKSPACE_ADMIN || this == EDITOR

    val canManageWorkspace: Boolean
        get() = this == GLOBAL_ADMIN || this == WORKSPACE_ADMIN
}

@Serializable
enum class Priority {
    LOW,
    MEDIUM,
    HIGH,
    URGENT
}

@Serializable
enum class BoardType {
    KANBAN,
    LIST,
    CALENDAR
}

@Serializable
enum class NoteType {
    MARKDOWN,
    DRAWING_EMBEDDED,
    CANVAS
}

@Serializable
enum class SyncAction {
    CREATE,
    UPDATE,
    DELETE,
    MOVE,
    CURSOR_MOVE
}

@Serializable
enum class SyncEntityType {
    WORKSPACE,
    BOARD,
    COLUMN,
    CARD,
    NOTE,
    DRAWING,
    CANVAS_STROKE
}
