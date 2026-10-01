package de.nickicloud.foxeltask.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val username: String,
    val email: String,
    val fullName: String,
    val avatarUrl: String? = null,
    val isGlobalAdmin: Boolean = false,
    val isActive: Boolean = true,
    val createdAt: Instant,
    val updatedAt: Instant
)

@Serializable
data class UserProfile(
    val id: String,
    val username: String,
    val email: String,
    val fullName: String,
    val avatarUrl: String? = null,
    val isGlobalAdmin: Boolean = false
)

@Serializable
data class AuthResponse(
    val token: String,
    val refreshToken: String? = null,
    val expiresIn: Long,
    val user: UserProfile
)

@Serializable
data class LoginRequest(
    val usernameOrEmail: String,
    val password: String
)

@Serializable
data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    val fullName: String
)

@Serializable
data class UpdateUserRequest(
    val fullName: String? = null,
    val email: String? = null,
    val avatarUrl: String? = null,
    val currentPassword: String? = null,
    val newPassword: String? = null
)
