package de.nickicloud.foxeltask.model

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val message: String? = null,
    val errorCode: String? = null
) {
    companion object {
        fun <T> success(data: T, message: String? = null): ApiResponse<T> =
            ApiResponse(success = true, data = data, message = message)

        fun <T> error(message: String, errorCode: String? = null): ApiResponse<T> =
            ApiResponse(success = false, message = message, errorCode = errorCode)
    }
}

@Serializable
data class PaginatedResponse<T>(
    val items: List<T>,
    val totalCount: Long,
    val page: Int,
    val pageSize: Int,
    val totalPages: Int
)
