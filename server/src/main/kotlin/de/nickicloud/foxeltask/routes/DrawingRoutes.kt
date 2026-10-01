package de.nickicloud.foxeltask.routes

import de.nickicloud.foxeltask.database.DatabaseFactory.dbQuery
import de.nickicloud.foxeltask.database.tables.DrawingsTable
import de.nickicloud.foxeltask.model.*
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import java.util.*

fun Route.drawingRoutes() {
    authenticate("auth-jwt") {
        route("/api/drawings") {
            get("/workspace/{workspaceId}") {
                val wsId = call.parameters["workspaceId"] ?: return@get
                val drawings = dbQuery {
                    DrawingsTable.selectAll()
                        .where { DrawingsTable.workspaceId eq wsId }
                        .orderBy(DrawingsTable.updatedAt to org.jetbrains.exposed.sql.SortOrder.DESC)
                        .map { row ->
                            val canvasJson = row[DrawingsTable.canvasData]
                            val parsedData = try {
                                Json.decodeFromString<DrawingCanvasData>(canvasJson)
                            } catch (_: Exception) {
                                DrawingCanvasData()
                            }

                            Drawing(
                                id = row[DrawingsTable.id],
                                workspaceId = row[DrawingsTable.workspaceId],
                                noteId = row[DrawingsTable.noteId],
                                title = row[DrawingsTable.title],
                                canvasData = parsedData,
                                thumbnailDataUrl = row[DrawingsTable.thumbnailDataUrl],
                                createdById = row[DrawingsTable.createdById],
                                createdAt = row[DrawingsTable.createdAt],
                                updatedAt = row[DrawingsTable.updatedAt]
                            )
                        }
                }
                call.respond(ApiResponse.success(drawings))
            }

            post {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asString() ?: return@post
                val req = call.receive<SaveDrawingRequest>()

                val drawingId = UUID.randomUUID().toString()
                val serializedCanvas = Json.encodeToString(req.canvasData)

                dbQuery {
                    DrawingsTable.insert {
                        it[id] = drawingId
                        it[workspaceId] = req.workspaceId
                        it[noteId] = req.noteId
                        it[title] = req.title
                        it[canvasData] = serializedCanvas
                        it[thumbnailDataUrl] = req.thumbnailDataUrl
                        it[createdById] = userId
                    }
                }

                call.respond(HttpStatusCode.Created, ApiResponse.success(drawingId))
            }

            put("/{id}") {
                val drawingId = call.parameters["id"] ?: return@put
                val req = call.receive<SaveDrawingRequest>()
                val serializedCanvas = Json.encodeToString(req.canvasData)

                dbQuery {
                    DrawingsTable.update({ DrawingsTable.id eq drawingId }) {
                        it[DrawingsTable.title] = req.title
                        it[DrawingsTable.canvasData] = serializedCanvas
                        it[DrawingsTable.thumbnailDataUrl] = req.thumbnailDataUrl
                        it[DrawingsTable.noteId] = req.noteId
                    }
                }

                call.respond(ApiResponse.success(true))
            }
        }
    }
}
