package de.nickicloud.foxeltask.database.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentTimestamp
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object TagsTable : Table("tags") {
    val id = varchar("id", 36)
    val workspaceId = varchar("workspace_id", 36).references(WorkspacesTable.id, onDelete = ReferenceOption.CASCADE)
    val name = varchar("name", 64)
    val colorHex = varchar("color_hex", 16).default("#3B82F6")

    override val primaryKey = PrimaryKey(id, name = "pk_tags_id")

    init {
        uniqueIndex("idx_ws_tag_name", workspaceId, name)
    }
}

object NotesTable : Table("notes") {
    val id = varchar("id", 36)
    val workspaceId = varchar("workspace_id", 36).references(WorkspacesTable.id, onDelete = ReferenceOption.CASCADE)
    val boardId = varchar("board_id", 36).references(BoardsTable.id, onDelete = ReferenceOption.SET_NULL).nullable()
    val cardId = varchar("card_id", 36).references(CardsTable.id, onDelete = ReferenceOption.SET_NULL).nullable()
    val parentFolderId = varchar("parent_folder_id", 36).references(id, onDelete = ReferenceOption.SET_NULL).nullable()
    val title = varchar("title", 255)
    val content = mediumText("content")
    val type = varchar("type", 32).default("MARKDOWN") // MARKDOWN, DRAWING_EMBEDDED, CANVAS
    val isPinned = bool("is_pinned").default(false)
    val isArchived = bool("is_archived").default(false)
    val createdById = varchar("created_by_id", 36).references(UsersTable.id, onDelete = ReferenceOption.CASCADE)
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp)
    val updatedAt = timestamp("updated_at").defaultExpression(CurrentTimestamp)

    override val primaryKey = PrimaryKey(id, name = "pk_notes_id")
}

object NoteTagsTable : Table("note_tags") {
    val noteId = varchar("note_id", 36).references(NotesTable.id, onDelete = ReferenceOption.CASCADE)
    val tagId = varchar("tag_id", 36).references(TagsTable.id, onDelete = ReferenceOption.CASCADE)

    override val primaryKey = PrimaryKey(noteId, tagId, name = "pk_note_tags")
}

object CardTagsTable : Table("card_tags") {
    val cardId = varchar("card_id", 36).references(CardsTable.id, onDelete = ReferenceOption.CASCADE)
    val tagId = varchar("tag_id", 36).references(TagsTable.id, onDelete = ReferenceOption.CASCADE)

    override val primaryKey = PrimaryKey(cardId, tagId, name = "pk_card_tags")
}
