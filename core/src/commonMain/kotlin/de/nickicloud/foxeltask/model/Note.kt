package de.nickicloud.foxeltask.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Tag(
    val id: String,
    val workspaceId: String,
    val name: String,
    val colorHex: String = "#3B82F6"
)

@Serializable
data class NoteFolder(
    val id: String,
    val workspaceId: String,
    val parentFolderId: String? = null,
    val name: String,
    val icon: String? = null,
    val createdAt: Instant
)

@Serializable
data class Note(
    val id: String,
    val workspaceId: String,
    val boardId: String? = null,
    val cardId: String? = null,
    val parentFolderId: String? = null,
    val title: String,
    val content: String,
    val type: NoteType = NoteType.MARKDOWN,
    val isPinned: Boolean = false,
    val isArchived: Boolean = false,
    val createdById: String,
    val creator: UserProfile? = null,
    val tags: List<Tag> = emptyList(),
    val createdAt: Instant,
    val updatedAt: Instant
)

@Serializable
data class CreateNoteRequest(
    val workspaceId: String,
    val boardId: String? = null,
    val cardId: String? = null,
    val parentFolderId: String? = null,
    val title: String,
    val content: String = "",
    val type: NoteType = NoteType.MARKDOWN,
    val isPinned: Boolean = false,
    val tagIds: List<String> = emptyList()
)

@Serializable
data class UpdateNoteRequest(
    val title: String? = null,
    val content: String? = null,
    val parentFolderId: String? = null,
    val isPinned: Boolean? = null,
    val isArchived: Boolean? = null,
    val tagIds: List<String>? = null
)
