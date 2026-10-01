package de.nickicloud.foxeltask.sync

import de.nickicloud.foxeltask.model.WebSocketServerMessage
import io.ktor.websocket.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.concurrent.ConcurrentHashMap

data class UserSession(
    val userId: String,
    val username: String,
    val workspaceId: String,
    val session: WebSocketSession
)

object ConnectionManager {
    // WorkspaceId -> Set of UserSessions
    private val workspaceSessions = ConcurrentHashMap<String, MutableSet<UserSession>>()
    private val mutex = Mutex()

    suspend fun register(userSession: UserSession) {
        mutex.withLock {
            val sessions = workspaceSessions.getOrPut(userSession.workspaceId) { mutableSetOf() }
            sessions.add(userSession)
        }
        broadcastToWorkspace(
            workspaceId = userSession.workspaceId,
            message = WebSocketServerMessage.UserJoined(userSession.userId, userSession.username),
            excludeUserId = userSession.userId
        )
    }

    suspend fun unregister(userSession: UserSession) {
        mutex.withLock {
            workspaceSessions[userSession.workspaceId]?.remove(userSession)
            if (workspaceSessions[userSession.workspaceId]?.isEmpty() == true) {
                workspaceSessions.remove(userSession.workspaceId)
            }
        }
        broadcastToWorkspace(
            workspaceId = userSession.workspaceId,
            message = WebSocketServerMessage.UserLeft(userSession.userId)
        )
    }

    suspend fun broadcastToWorkspace(
        workspaceId: String,
        message: WebSocketServerMessage,
        excludeUserId: String? = null
    ) {
        val payload = Json.encodeToString(message)
        val sessions = workspaceSessions[workspaceId]?.toList() ?: return

        for (userSession in sessions) {
            if (excludeUserId != null && userSession.userId == excludeUserId) continue
            try {
                userSession.session.send(Frame.Text(payload))
            } catch (_: Throwable) {
                // Session may have abruptly closed
            }
        }
    }
}
