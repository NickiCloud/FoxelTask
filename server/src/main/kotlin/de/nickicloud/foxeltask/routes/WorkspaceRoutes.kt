package de.nickicloud.foxeltask.routes

import de.nickicloud.foxeltask.database.DatabaseFactory.dbQuery
import de.nickicloud.foxeltask.database.tables.BoardsTable
import de.nickicloud.foxeltask.database.tables.NotesTable
import de.nickicloud.foxeltask.database.tables.WorkspaceMembersTable
import de.nickicloud.foxeltask.database.tables.WorkspacesTable
import de.nickicloud.foxeltask.model.*
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import java.util.*

fun Route.workspaceRoutes() {
    authenticate("auth-jwt") {
        route("/api/workspaces") {
            get {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asString() ?: return@get

                val workspaces = dbQuery {
                    val memberships = WorkspaceMembersTable.selectAll()
                        .where { WorkspaceMembersTable.userId eq userId }
                        .toList()

                    val wsIds = memberships.map { it[WorkspaceMembersTable.workspaceId] }
                    val roleMap = memberships.associate { it[WorkspaceMembersTable.workspaceId] to Role.valueOf(it[WorkspaceMembersTable.role]) }

                    WorkspacesTable.selectAll()
                        .where { WorkspacesTable.id inList wsIds }
                        .map { wsRow ->
                            val wsId = wsRow[WorkspacesTable.id]
                            val bCount = BoardsTable.selectAll().where { BoardsTable.workspaceId eq wsId }.count().toInt()
                            val nCount = NotesTable.selectAll().where { NotesTable.workspaceId eq wsId }.count().toInt()
                            val mCount = WorkspaceMembersTable.selectAll().where { WorkspaceMembersTable.workspaceId eq wsId }.count().toInt()

                            WorkspaceSummary(
                                id = wsId,
                                name = wsRow[WorkspacesTable.name],
                                slug = wsRow[WorkspacesTable.slug],
                                description = wsRow[WorkspacesTable.description],
                                icon = wsRow[WorkspacesTable.icon],
                                role = roleMap[wsId] ?: Role.VIEWER,
                                boardsCount = bCount,
                                notesCount = nCount,
                                membersCount = mCount
                            )
                        }
                }

                call.respond(ApiResponse.success(workspaces))
            }

            post {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asString() ?: return@post
                val req = call.receive<CreateWorkspaceRequest>()

                val wsId = UUID.randomUUID().toString()
                val slug = req.name.lowercase().replace("\\s+".toRegex(), "-") + "-" + UUID.randomUUID().toString().take(6)

                dbQuery {
                    WorkspacesTable.insert {
                        it[WorkspacesTable.id] = wsId
                        it[WorkspacesTable.name] = req.name
                        it[WorkspacesTable.slug] = slug
                        it[WorkspacesTable.description] = req.description
                        it[WorkspacesTable.icon] = req.icon ?: "folder"
                        it[WorkspacesTable.ownerId] = userId
                        it[WorkspacesTable.isArchived] = false
                    }

                    WorkspaceMembersTable.insert {
                        it[WorkspaceMembersTable.id] = UUID.randomUUID().toString()
                        it[WorkspaceMembersTable.workspaceId] = wsId
                        it[WorkspaceMembersTable.userId] = userId
                        it[WorkspaceMembersTable.role] = Role.WORKSPACE_ADMIN.name
                    }
                }

                val workspace = WorkspaceSummary(
                    id = wsId,
                    name = req.name,
                    slug = slug,
                    description = req.description,
                    icon = req.icon ?: "folder",
                    role = Role.WORKSPACE_ADMIN,
                    boardsCount = 0,
                    notesCount = 0,
                    membersCount = 1
                )

                call.respond(HttpStatusCode.Created, ApiResponse.success(workspace))
            }
        }
    }
}
