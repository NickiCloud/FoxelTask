package de.nickicloud.foxeltask.database.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentTimestamp
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object BoardsTable : Table("boards") {
    val id = varchar("id", 36)
    val workspaceId = varchar("workspace_id", 36).references(WorkspacesTable.id, onDelete = ReferenceOption.CASCADE)
    val title = varchar("title", 128)
    val description = text("description").nullable()
    val type = varchar("type", 32).default("KANBAN") // KANBAN, LIST, CALENDAR
    val createdById = varchar("created_by_id", 36).references(UsersTable.id, onDelete = ReferenceOption.CASCADE)
    val isArchived = bool("is_archived").default(false)
    val position = integer("position").default(0)
    val colorTheme = varchar("color_theme", 32).nullable()
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp)
    val updatedAt = timestamp("updated_at").defaultExpression(CurrentTimestamp)

    override val primaryKey = PrimaryKey(id, name = "pk_boards_id")
}

object BoardColumnsTable : Table("board_columns") {
    val id = varchar("id", 36)
    val boardId = varchar("board_id", 36).references(BoardsTable.id, onDelete = ReferenceOption.CASCADE)
    val name = varchar("name", 128)
    val colorHex = varchar("color_hex", 16).default("#6366F1")
    val position = integer("position").default(0)
    val wipLimit = integer("wip_limit").nullable()
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp)
    val updatedAt = timestamp("updated_at").defaultExpression(CurrentTimestamp)

    override val primaryKey = PrimaryKey(id, name = "pk_board_columns_id")
}

object CardsTable : Table("cards") {
    val id = varchar("id", 36)
    val columnId = varchar("column_id", 36).references(BoardColumnsTable.id, onDelete = ReferenceOption.CASCADE)
    val boardId = varchar("board_id", 36).references(BoardsTable.id, onDelete = ReferenceOption.CASCADE)
    val title = varchar("title", 255)
    val description = text("description").nullable()
    val priority = varchar("priority", 32).default("MEDIUM") // LOW, MEDIUM, HIGH, URGENT
    val assigneeId = varchar("assignee_id", 36).references(UsersTable.id, onDelete = ReferenceOption.SET_NULL).nullable()
    val dueDate = timestamp("due_date").nullable()
    val position = integer("position").default(0)
    val coverColor = varchar("cover_color", 32).nullable()
    val isCompleted = bool("is_completed").default(false)
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp)
    val updatedAt = timestamp("updated_at").defaultExpression(CurrentTimestamp)

    override val primaryKey = PrimaryKey(id, name = "pk_cards_id")
}
