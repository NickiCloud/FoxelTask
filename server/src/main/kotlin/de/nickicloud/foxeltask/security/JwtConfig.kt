package de.nickicloud.foxeltask.security

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import java.util.Date

object JwtConfig {
    val secret: String = System.getenv("JWT_SECRET") ?: "foxeltask-super-secure-production-jwt-secret-key-32-chars-min"
    val issuer: String = System.getenv("JWT_ISSUER") ?: "https://foxeltask.nickicloud.de"
    val audience: String = System.getenv("JWT_AUDIENCE") ?: "foxeltask-app"
    val realm: String = "FoxelTask Server"
    private const val VALIDITY_IN_MS = 36_000_000 * 24L // 24 hours

    private val algorithm = Algorithm.HMAC256(secret)

    val verifier: JWTVerifier = JWT
        .require(algorithm)
        .withAudience(audience)
        .withIssuer(issuer)
        .build()

    fun generateToken(userId: String, username: String, isGlobalAdmin: Boolean): String {
        return JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim("userId", userId)
            .withClaim("username", username)
            .withClaim("isGlobalAdmin", isGlobalAdmin)
            .withExpiresAt(Date(System.currentTimeMillis() + VALIDITY_IN_MS))
            .sign(algorithm)
    }
}
