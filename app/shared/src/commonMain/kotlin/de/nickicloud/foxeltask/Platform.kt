package de.nickicloud.foxeltask

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform