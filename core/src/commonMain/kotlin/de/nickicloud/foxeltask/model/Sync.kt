package de.nickicloud.foxeltask.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class SyncMessage(
    val id: String,
    val workspaceId: String,
    val senderUserId: String,
    val entityType: SyncEntityType,
    val entityId: String,
    val action: SyncAction,
    val payload: JsonObject? = null,
    val timestamp: Instant
)

@Serializable
data class UserCursorPosition(
    val userId: String,
    val userName: String,
    val userColor: String,
    val boardId: String? = null,
    val noteId: String? = null,
    val x: Float,
    val y: Float
)

@Serializable
sealed interface WebSocketClientMessage {
    @Serializable
    data class JoinWorkspace(val workspaceId: String, val token: String) : WebSocketClientMessage

    @Serializable
    data class LeaveWorkspace(val workspaceId: String) : WebSocketClientMessage

    @Serializable
    data class DispatchSync(val message: SyncMessage) : WebSocketClientMessage

    @Serializable
    data class SendCursor(val cursor: UserCursorPosition) : WebSocketClientMessage
}

@Serializable
sealed interface WebSocketServerMessage {
    @Serializable
    data class SyncEvent(val message: SyncMessage) : WebSocketServerMessage

    @Serializable
    data class CursorUpdate(val cursor: UserCursorPosition) : WebSocketServerMessage

    @Serializable
    data class UserJoined(val userId: String, val username: String) : WebSocketServerMessage

    @Serializable
    data class UserLeft(val userId: String) : WebSocketServerMessage

    @Serializable
    data class Error(val code: String, val message: String) : WebSocketServerMessage
}
