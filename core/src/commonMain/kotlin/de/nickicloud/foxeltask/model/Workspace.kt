package de.nickicloud.foxeltask.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Workspace(
    val id: String,
    val name: String,
    val slug: String,
    val description: String? = null,
    val icon: String? = null,
    val ownerId: String,
    val isArchived: Boolean = false,
    val createdAt: Instant,
    val updatedAt: Instant
)

@Serializable
data class WorkspaceMember(
    val id: String,
    val workspaceId: String,
    val userId: String,
    val user: UserProfile? = null,
    val role: Role,
    val joinedAt: Instant
)

@Serializable
data class WorkspaceSummary(
    val id: String,
    val name: String,
    val slug: String,
    val description: String? = null,
    val icon: String? = null,
    val role: Role,
    val boardsCount: Int = 0,
    val notesCount: Int = 0,
    val membersCount: Int = 0
)

@Serializable
data class CreateWorkspaceRequest(
    val name: String,
    val description: String? = null,
    val icon: String? = null
)

@Serializable
data class UpdateWorkspaceRequest(
    val name: String? = null,
    val description: String? = null,
    val icon: String? = null,
    val isArchived: Boolean? = null
)

@Serializable
data class AddMemberRequest(
    val userIdOrEmail: String,
    val role: Role
)
