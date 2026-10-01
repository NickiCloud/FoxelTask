package de.nickicloud.foxeltask.routes

import de.nickicloud.foxeltask.database.DatabaseFactory.dbQuery
import de.nickicloud.foxeltask.database.tables.BoardsTable
import de.nickicloud.foxeltask.database.tables.NotesTable
import de.nickicloud.foxeltask.database.tables.UsersTable
import de.nickicloud.foxeltask.database.tables.WorkspacesTable
import de.nickicloud.foxeltask.model.ApiResponse
import de.nickicloud.foxeltask.model.UserProfile
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update

@Serializable
data class AdminStatsResponse(
    val totalUsers: Long,
    val totalWorkspaces: Long,
    val totalBoards: Long,
    val totalNotes: Long,
    val systemStatus: String = "HEALTHY"
)

fun Route.adminRoutes() {
    authenticate("auth-jwt") {
        route("/api/admin") {
            get("/stats") {
                val principal = call.principal<JWTPrincipal>()
                val isAdmin = principal?.payload?.getClaim("isGlobalAdmin")?.asBoolean() ?: false

                if (!isAdmin) {
                    call.respond(HttpStatusCode.Forbidden, ApiResponse.error<Unit>("Admin-Rechte erforderlich", "FORBIDDEN"))
                    return@get
                }

                val stats = dbQuery {
                    val userCount = UsersTable.selectAll().count()
                    val wsCount = WorkspacesTable.selectAll().count()
                    val boardCount = BoardsTable.selectAll().count()
                    val noteCount = NotesTable.selectAll().count()

                    AdminStatsResponse(
                        totalUsers = userCount,
                        totalWorkspaces = wsCount,
                        totalBoards = boardCount,
                        totalNotes = noteCount
                    )
                }

                call.respond(ApiResponse.success(stats))
            }

            get("/users") {
                val principal = call.principal<JWTPrincipal>()
                val isAdmin = principal?.payload?.getClaim("isGlobalAdmin")?.asBoolean() ?: false

                if (!isAdmin) {
                    call.respond(HttpStatusCode.Forbidden, ApiResponse.error<Unit>("Admin-Rechte erforderlich", "FORBIDDEN"))
                    return@get
                }

                val users = dbQuery {
                    UsersTable.selectAll()
                        .map {
                            UserProfile(
                                id = it[UsersTable.id],
                                username = it[UsersTable.username],
                                email = it[UsersTable.email],
                                fullName = it[UsersTable.fullName],
                                avatarUrl = it[UsersTable.avatarUrl],
                                isGlobalAdmin = it[UsersTable.isGlobalAdmin]
                            )
                        }
                }

                call.respond(ApiResponse.success(users))
            }

            post("/users/{id}/toggle-admin") {
                val principal = call.principal<JWTPrincipal>()
                val isAdmin = principal?.payload?.getClaim("isGlobalAdmin")?.asBoolean() ?: false

                if (!isAdmin) {
                    call.respond(HttpStatusCode.Forbidden, ApiResponse.error<Unit>("Admin-Rechte erforderlich", "FORBIDDEN"))
                    return@post
                }

                val targetUserId = call.parameters["id"] ?: return@post

                dbQuery {
                    val currentAdmin = UsersTable.selectAll()
                        .where { UsersTable.id eq targetUserId }
                        .map { it[UsersTable.isGlobalAdmin] }
                        .singleOrNull() ?: false

                    UsersTable.update({ UsersTable.id eq targetUserId }) {
                        it[UsersTable.isGlobalAdmin] = !currentAdmin
                    }
                }

                call.respond(ApiResponse.success(true))
            }
        }
    }
}
