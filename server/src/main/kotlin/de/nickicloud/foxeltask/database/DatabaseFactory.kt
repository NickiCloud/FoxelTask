package de.nickicloud.foxeltask.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import de.nickicloud.foxeltask.database.tables.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory

object DatabaseFactory {
    private val logger = LoggerFactory.getLogger(DatabaseFactory::class.java)

    fun init() {
        val host = System.getenv("MYSQL_HOST") ?: "localhost"
        val port = System.getenv("MYSQL_PORT") ?: "3306"
        val databaseName = System.getenv("MYSQL_DATABASE") ?: "foxeltask"
        val user = System.getenv("MYSQL_USER") ?: "foxeluser"
        val password = System.getenv("MYSQL_PASSWORD") ?: "foxelpass"
        val maxPoolSize = (System.getenv("DB_MAX_POOL_SIZE") ?: "10").toInt()

        val jdbcUrl = "jdbc:mysql://$host:$port/$databaseName?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8"

        logger.info("Initializing Database connection pool to jdbc:mysql://$host:$port/$databaseName")

        val config = HikariConfig().apply {
            this.jdbcUrl = jdbcUrl
            this.driverClassName = "com.mysql.cj.jdbc.Driver"
            this.username = user
            this.password = password
            this.maximumPoolSize = maxPoolSize
            this.isAutoCommit = false
            this.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            this.connectionTimeout = 30000
            this.idleTimeout = 600000
            this.maxLifetime = 1800000
            this.validate()
        }

        val dataSource = HikariDataSource(config)
        Database.connect(dataSource)

        transaction {
            SchemaUtils.createMissingTablesAndColumns(
                UsersTable,
                WorkspacesTable,
                WorkspaceMembersTable,
                BoardsTable,
                BoardColumnsTable,
                CardsTable,
                TagsTable,
                NotesTable,
                NoteTagsTable,
                CardTagsTable,
                DrawingsTable
            )
        }

        logger.info("Database schemas and tables verified/created successfully.")
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}
