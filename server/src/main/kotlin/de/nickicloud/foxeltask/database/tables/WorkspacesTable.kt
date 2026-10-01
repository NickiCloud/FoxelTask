package de.nickicloud.foxeltask.database.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentTimestamp
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object WorkspacesTable : Table("workspaces") {
    val id = varchar("id", 36)
    val name = varchar("name", 128)
    val slug = varchar("slug", 128).uniqueIndex("idx_workspaces_slug")
    val description = text("description").nullable()
    val icon = varchar("icon", 64).nullable()
    val ownerId = varchar("owner_id", 36).references(UsersTable.id, onDelete = ReferenceOption.CASCADE)
    val isArchived = bool("is_archived").default(false)
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp)
    val updatedAt = timestamp("updated_at").defaultExpression(CurrentTimestamp)

    override val primaryKey = PrimaryKey(id, name = "pk_workspaces_id")
}

object WorkspaceMembersTable : Table("workspace_members") {
    val id = varchar("id", 36)
    val workspaceId = varchar("workspace_id", 36).references(WorkspacesTable.id, onDelete = ReferenceOption.CASCADE)
    val userId = varchar("user_id", 36).references(UsersTable.id, onDelete = ReferenceOption.CASCADE)
    val role = varchar("role", 32).default("VIEWER") // GLOBAL_ADMIN, WORKSPACE_ADMIN, EDITOR, VIEWER
    val joinedAt = timestamp("joined_at").defaultExpression(CurrentTimestamp)

    override val primaryKey = PrimaryKey(id, name = "pk_workspace_members_id")

    init {
        uniqueIndex("idx_ws_member_unique", workspaceId, userId)
    }
}
