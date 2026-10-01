package de.nickicloud.foxeltask.routes

import de.nickicloud.foxeltask.database.DatabaseFactory.dbQuery
import de.nickicloud.foxeltask.database.tables.NotesTable
import de.nickicloud.foxeltask.model.*
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import java.util.*

fun Route.noteRoutes() {
    authenticate("auth-jwt") {
        route("/api/notes") {
            get("/workspace/{workspaceId}") {
                val wsId = call.parameters["workspaceId"] ?: return@get
                val notes = dbQuery {
                    NotesTable.selectAll()
                        .where { (NotesTable.workspaceId eq wsId) and (NotesTable.isArchived eq false) }
                        .orderBy(NotesTable.isPinned to org.jetbrains.exposed.sql.SortOrder.DESC, NotesTable.updatedAt to org.jetbrains.exposed.sql.SortOrder.DESC)
                        .map {
                            Note(
                                id = it[NotesTable.id],
                                workspaceId = it[NotesTable.workspaceId],
                                boardId = it[NotesTable.boardId],
                                cardId = it[NotesTable.cardId],
                                parentFolderId = it[NotesTable.parentFolderId],
                                title = it[NotesTable.title],
                                content = it[NotesTable.content],
                                type = NoteType.valueOf(it[NotesTable.type]),
                                isPinned = it[NotesTable.isPinned],
                                isArchived = it[NotesTable.isArchived],
                                createdById = it[NotesTable.createdById],
                                createdAt = it[NotesTable.createdAt],
                                updatedAt = it[NotesTable.updatedAt]
                            )
                        }
                }
                call.respond(ApiResponse.success(notes))
            }

            post {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asString() ?: return@post
                val req = call.receive<CreateNoteRequest>()

                val noteId = UUID.randomUUID().toString()
                dbQuery {
                    NotesTable.insert {
                        it[NotesTable.id] = noteId
                        it[NotesTable.workspaceId] = req.workspaceId
                        it[NotesTable.boardId] = req.boardId
                        it[NotesTable.cardId] = req.cardId
                        it[NotesTable.parentFolderId] = req.parentFolderId
                        it[NotesTable.title] = req.title
                        it[NotesTable.content] = req.content
                        it[NotesTable.type] = req.type.name
                        it[NotesTable.isPinned] = req.isPinned
                        it[NotesTable.isArchived] = false
                        it[NotesTable.createdById] = userId
                    }
                }

                call.respond(HttpStatusCode.Created, ApiResponse.success(noteId))
            }

            put("/{id}") {
                val noteId = call.parameters["id"] ?: return@put
                val req = call.receive<UpdateNoteRequest>()

                dbQuery {
                    NotesTable.update({ NotesTable.id eq noteId }) {
                        req.title?.let { t -> it[NotesTable.title] = t }
                        req.content?.let { c -> it[NotesTable.content] = c }
                        req.parentFolderId?.let { pf -> it[NotesTable.parentFolderId] = pf }
                        req.isPinned?.let { p -> it[NotesTable.isPinned] = p }
                        req.isArchived?.let { a -> it[NotesTable.isArchived] = a }
                    }
                }

                call.respond(ApiResponse.success(true))
            }
        }
    }
}
