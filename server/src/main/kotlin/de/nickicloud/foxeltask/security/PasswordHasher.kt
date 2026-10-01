package de.nickicloud.foxeltask.security

import at.favre.lib.crypto.bcrypt.BCrypt

object PasswordHasher {
    private const val COST = 12

    fun hash(password: String): String {
        return BCrypt.withDefaults().hashToString(COST, password.toCharArray())
    }

    fun verify(password: String, hashed: String): Boolean {
        return BCrypt.verifyer().verify(password.toCharArray(), hashed.toCharArray()).verified
    }
}
