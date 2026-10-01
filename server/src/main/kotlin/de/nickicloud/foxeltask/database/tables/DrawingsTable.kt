package de.nickicloud.foxeltask.database.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentTimestamp
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object DrawingsTable : Table("drawings") {
    val id = varchar("id", 36)
    val workspaceId = varchar("workspace_id", 36).references(WorkspacesTable.id, onDelete = ReferenceOption.CASCADE)
    val noteId = varchar("note_id", 36).references(NotesTable.id, onDelete = ReferenceOption.SET_NULL).nullable()
    val title = varchar("title", 255)
    val canvasData = mediumText("canvas_data")
    val thumbnailDataUrl = mediumText("thumbnail_data_url").nullable()
    val createdById = varchar("created_by_id", 36).references(UsersTable.id, onDelete = ReferenceOption.CASCADE)
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp)
    val updatedAt = timestamp("updated_at").defaultExpression(CurrentTimestamp)

    override val primaryKey = PrimaryKey(id, name = "pk_drawings_id")
}
