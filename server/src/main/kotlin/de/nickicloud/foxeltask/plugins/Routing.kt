package de.nickicloud.foxeltask.plugins

import de.nickicloud.foxeltask.routes.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("FoxelTask API Server v1.0.0 is running.")
        }
        authRoutes()
        workspaceRoutes()
        boardRoutes()
        noteRoutes()
        drawingRoutes()
        adminRoutes()
        syncRoutes()
    }
}
