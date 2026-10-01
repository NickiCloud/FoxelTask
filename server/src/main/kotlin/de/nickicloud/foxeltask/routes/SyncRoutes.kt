package de.nickicloud.foxeltask.routes

import de.nickicloud.foxeltask.model.WebSocketClientMessage
import de.nickicloud.foxeltask.model.WebSocketServerMessage
import de.nickicloud.foxeltask.security.JwtConfig
import de.nickicloud.foxeltask.sync.ConnectionManager
import de.nickicloud.foxeltask.sync.UserSession
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.serialization.json.Json

fun Route.syncRoutes() {
    route("/ws/sync") {
        webSocket {
            var currentUserSession: UserSession? = null

            try {
                incoming.consumeEach { frame ->
                    if (frame is Frame.Text) {
                        val text = frame.readText()
                        try {
                            when (val clientMsg = Json.decodeFromString<WebSocketClientMessage>(text)) {
                                is WebSocketClientMessage.JoinWorkspace -> {
                                    val jwt = try {
                                        JwtConfig.verifier.verify(clientMsg.token)
                                    } catch (_: Exception) {
                                        send(Frame.Text(Json.encodeToString(WebSocketServerMessage.Error("AUTH_ERROR", "Token invalid"))))
                                        return@consumeEach
                                    }
                                    val userId = jwt.getClaim("userId").asString()
                                    val username = jwt.getClaim("username").asString()

                                    val session = UserSession(
                                        userId = userId,
                                        username = username,
                                        workspaceId = clientMsg.workspaceId,
                                        session = this
                                    )
                                    currentUserSession = session
                                    ConnectionManager.register(session)
                                }

                                is WebSocketClientMessage.LeaveWorkspace -> {
                                    currentUserSession?.let { ConnectionManager.unregister(it) }
                                    currentUserSession = null
                                }

                                is WebSocketClientMessage.DispatchSync -> {
                                    currentUserSession?.let { session ->
                                        ConnectionManager.broadcastToWorkspace(
                                            workspaceId = session.workspaceId,
                                            message = WebSocketServerMessage.SyncEvent(clientMsg.message),
                                            excludeUserId = session.userId
                                        )
                                    }
                                }

                                is WebSocketClientMessage.SendCursor -> {
                                    currentUserSession?.let { session ->
                                        ConnectionManager.broadcastToWorkspace(
                                            workspaceId = session.workspaceId,
                                            message = WebSocketServerMessage.CursorUpdate(clientMsg.cursor),
                                            excludeUserId = session.userId
                                        )
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            send(Frame.Text(Json.encodeToString(WebSocketServerMessage.Error("PARSE_ERROR", e.message ?: "Unknown error"))))
                        }
                    }
                }
            } finally {
                currentUserSession?.let {
                    ConnectionManager.unregister(it)
                }
            }
        }
    }
}
