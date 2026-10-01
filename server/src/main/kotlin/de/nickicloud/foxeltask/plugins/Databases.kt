package de.nickicloud.foxeltask.plugins

import de.nickicloud.foxeltask.database.DatabaseFactory
import io.ktor.server.application.*

fun Application.configureDatabases() {
    DatabaseFactory.init()
}
