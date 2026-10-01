package de.nickicloud.foxeltask.database.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentTimestamp
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object UsersTable : Table("users") {
    val id = varchar("id", 36)
    val username = varchar("username", 64).uniqueIndex("idx_users_username")
    val email = varchar("email", 128).uniqueIndex("idx_users_email")
    val passwordHash = varchar("password_hash", 255)
    val fullName = varchar("full_name", 128)
    val avatarUrl = varchar("avatar_url", 512).nullable()
    val isGlobalAdmin = bool("is_global_admin").default(false)
    val isActive = bool("is_active").default(true)
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp)
    val updatedAt = timestamp("updated_at").defaultExpression(CurrentTimestamp)

    override val primaryKey = PrimaryKey(id, name = "pk_users_id")
}
