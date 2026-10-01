package de.nickicloud.foxeltask.plugins

import de.nickicloud.foxeltask.security.JwtConfig
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

fun Application.configureSecurity() {
    authentication {
        jwt("auth-jwt") {
            realm = JwtConfig.realm
            verifier(JwtConfig.verifier)
            validate { credential ->
                val userId = credential.payload.getClaim("userId").asString()
                if (!userId.isNullOrBlank()) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }
    }
}
