package de.nickicloud.foxeltask.routes

import de.nickicloud.foxeltask.database.DatabaseFactory.dbQuery
import de.nickicloud.foxeltask.database.tables.UsersTable
import de.nickicloud.foxeltask.model.*
import de.nickicloud.foxeltask.security.JwtConfig
import de.nickicloud.foxeltask.security.PasswordHasher
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.selectAll
import java.util.*

fun Route.authRoutes() {
    route("/api/auth") {
        post("/register") {
            val req = call.receive<RegisterRequest>()
            val existing = dbQuery {
                UsersTable.selectAll()
                    .where { (UsersTable.username eq req.username) or (UsersTable.email eq req.email) }
                    .count()
            }

            if (existing > 0) {
                call.respond(HttpStatusCode.Conflict, ApiResponse.error<Unit>("Benutzername oder E-Mail existiert bereits", "USER_EXISTS"))
                return@post
            }

            val userId = UUID.randomUUID().toString()
            val hashedPassword = PasswordHasher.hash(req.password)

            dbQuery {
                UsersTable.insert {
                    it[UsersTable.id] = userId
                    it[UsersTable.username] = req.username
                    it[UsersTable.email] = req.email
                    it[UsersTable.passwordHash] = hashedPassword
                    it[UsersTable.fullName] = req.fullName
                    it[UsersTable.isGlobalAdmin] = false
                    it[UsersTable.isActive] = true
                }
            }

            val token = JwtConfig.generateToken(userId, req.username, false)
            val profile = UserProfile(
                id = userId,
                username = req.username,
                email = req.email,
                fullName = req.fullName,
                isGlobalAdmin = false
            )

            call.respond(HttpStatusCode.Created, ApiResponse.success(AuthResponse(token = token, expiresIn = 86400, user = profile)))
        }

        post("/login") {
            val req = call.receive<LoginRequest>()
            val row = dbQuery {
                UsersTable.selectAll()
                    .where { (UsersTable.username eq req.usernameOrEmail) or (UsersTable.email eq req.usernameOrEmail) }
                    .singleOrNull()
            }

            if (row == null) {
                call.respond(HttpStatusCode.Unauthorized, ApiResponse.error<Unit>("Ungültige Zugangsdaten", "INVALID_CREDENTIALS"))
                return@post
            }

            val storedHash = row[UsersTable.passwordHash]
            if (!PasswordHasher.verify(req.password, storedHash)) {
                call.respond(HttpStatusCode.Unauthorized, ApiResponse.error<Unit>("Ungültige Zugangsdaten", "INVALID_CREDENTIALS"))
                return@post
            }

            val userId = row[UsersTable.id]
            val username = row[UsersTable.username]
            val isAdmin = row[UsersTable.isGlobalAdmin]

            val token = JwtConfig.generateToken(userId, username, isAdmin)
            val profile = UserProfile(
                id = userId,
                username = username,
                email = row[UsersTable.email],
                fullName = row[UsersTable.fullName],
                avatarUrl = row[UsersTable.avatarUrl],
                isGlobalAdmin = isAdmin
            )

            call.respond(ApiResponse.success(AuthResponse(token = token, expiresIn = 86400, user = profile)))
        }

        authenticate("auth-jwt") {
            get("/me") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asString()

                if (userId == null) {
                    call.respond(HttpStatusCode.Unauthorized, ApiResponse.error<Unit>("Nicht authentifiziert"))
                    return@get
                }

                val user = dbQuery {
                    UsersTable.selectAll()
                        .where { UsersTable.id eq userId }
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
                        .singleOrNull()
                }

                if (user != null) {
                    call.respond(ApiResponse.success(user))
                } else {
                    call.respond(HttpStatusCode.NotFound, ApiResponse.error<Unit>("Benutzer nicht gefunden"))
                }
            }
        }
    }
}
