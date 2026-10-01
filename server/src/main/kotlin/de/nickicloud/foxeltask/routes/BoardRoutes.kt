package de.nickicloud.foxeltask.routes

import de.nickicloud.foxeltask.database.DatabaseFactory.dbQuery
import de.nickicloud.foxeltask.database.tables.BoardColumnsTable
import de.nickicloud.foxeltask.database.tables.BoardsTable
import de.nickicloud.foxeltask.database.tables.CardsTable
import de.nickicloud.foxeltask.model.*
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import java.util.*

fun Route.boardRoutes() {
    authenticate("auth-jwt") {
        route("/api/boards") {
            get("/workspace/{workspaceId}") {
                val wsId = call.parameters["workspaceId"] ?: return@get
                val boards = dbQuery {
                    BoardsTable.selectAll()
                        .where { (BoardsTable.workspaceId eq wsId) and (BoardsTable.isArchived eq false) }
                        .map { bRow ->
                            val bId = bRow[BoardsTable.id]
                            val cols = BoardColumnsTable.selectAll()
                                .where { BoardColumnsTable.boardId eq bId }
                                .orderBy(BoardColumnsTable.position)
                                .map { cRow ->
                                    val cId = cRow[BoardColumnsTable.id]
                                    val cards = CardsTable.selectAll()
                                        .where { CardsTable.columnId eq cId }
                                        .orderBy(CardsTable.position)
                                        .map { cardRow ->
                                            KanbanCard(
                                                id = cardRow[CardsTable.id],
                                                columnId = cardRow[CardsTable.columnId],
                                                boardId = cardRow[CardsTable.boardId],
                                                title = cardRow[CardsTable.title],
                                                description = cardRow[CardsTable.description],
                                                priority = Priority.valueOf(cardRow[CardsTable.priority]),
                                                assigneeId = cardRow[CardsTable.assigneeId],
                                                dueDate = cardRow[CardsTable.dueDate],
                                                position = cardRow[CardsTable.position],
                                                coverColor = cardRow[CardsTable.coverColor],
                                                isCompleted = cardRow[CardsTable.isCompleted],
                                                createdAt = cardRow[CardsTable.createdAt],
                                                updatedAt = cardRow[CardsTable.updatedAt]
                                            )
                                        }

                                    BoardColumn(
                                        id = cId,
                                        boardId = bId,
                                        name = cRow[BoardColumnsTable.name],
                                        colorHex = cRow[BoardColumnsTable.colorHex],
                                        position = cRow[BoardColumnsTable.position],
                                        wipLimit = cRow[BoardColumnsTable.wipLimit],
                                        cards = cards,
                                        createdAt = cRow[BoardColumnsTable.createdAt],
                                        updatedAt = cRow[BoardColumnsTable.updatedAt]
                                    )
                                }

                            Board(
                                id = bId,
                                workspaceId = bRow[BoardsTable.workspaceId],
                                title = bRow[BoardsTable.title],
                                description = bRow[BoardsTable.description],
                                type = BoardType.valueOf(bRow[BoardsTable.type]),
                                createdById = bRow[BoardsTable.createdById],
                                isArchived = bRow[BoardsTable.isArchived],
                                position = bRow[BoardsTable.position],
                                colorTheme = bRow[BoardsTable.colorTheme],
                                columns = cols,
                                createdAt = bRow[BoardsTable.createdAt],
                                updatedAt = bRow[BoardsTable.updatedAt]
                            )
                        }
                }
                call.respond(ApiResponse.success(boards))
            }

            post {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asString() ?: return@post
                val req = call.receive<CreateBoardRequest>()

                val boardId = UUID.randomUUID().toString()

                dbQuery {
                    BoardsTable.insert {
                        it[BoardsTable.id] = boardId
                        it[BoardsTable.workspaceId] = req.workspaceId
                        it[BoardsTable.title] = req.title
                        it[BoardsTable.description] = req.description
                        it[BoardsTable.type] = req.type.name
                        it[BoardsTable.createdById] = userId
                        it[BoardsTable.isArchived] = false
                        it[BoardsTable.position] = 0
                        it[BoardsTable.colorTheme] = req.colorTheme ?: "#6366F1"
                    }

                    // Create default Kanban columns (To Do, In Progress, Done)
                    val defaultCols = listOf(
                        "Zu erledigen" to "#64748B",
                        "In Bearbeitung" to "#00F0FF",
                        "Fertig" to "#10B981"
                    )
                    defaultCols.forEachIndexed { index, (colName, colColor) ->
                        BoardColumnsTable.insert {
                            it[BoardColumnsTable.id] = UUID.randomUUID().toString()
                            it[BoardColumnsTable.boardId] = boardId
                            it[BoardColumnsTable.name] = colName
                            it[BoardColumnsTable.colorHex] = colColor
                            it[BoardColumnsTable.position] = index
                        }
                    }
                }

                call.respond(HttpStatusCode.Created, ApiResponse.success(boardId))
            }

            post("/cards/move") {
                val req = call.receive<MoveCardRequest>()
                dbQuery {
                    CardsTable.update({ CardsTable.id eq req.cardId }) {
                        it[CardsTable.columnId] = req.targetColumnId
                        it[CardsTable.position] = req.targetPosition
                    }
                }
                call.respond(ApiResponse.success(true))
            }
        }
    }
}
