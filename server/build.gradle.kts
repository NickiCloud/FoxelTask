plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.ktor)
}

group = "de.nickicloud.foxeltask"
version = "1.0.0"
application {
    mainClass = "de.nickicloud.foxeltask.ApplicationKt"
}

dependencies {
    api(project(":core"))
    implementation(libs.logback)
    implementation(libs.ktor.serverCore)
    implementation(libs.ktor.serverNetty)
    implementation(libs.ktor.server.contentNegotiation)
    implementation(libs.ktor.server.websockets)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.authJwt)
    implementation(libs.ktor.server.cors)
    implementation(libs.ktor.server.statusPages)
    implementation(libs.ktor.serialization.kotlinxJson)

    // Database & Security
    implementation(libs.exposed.core)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.kotlin.datetime)
    implementation(libs.hikaricp)
    implementation(libs.mysql.connector)
    implementation(libs.bcrypt)

    testImplementation(libs.ktor.serverTestHost)
    testImplementation(libs.kotlin.testJunit)
}