package de.nickicloud.foxeltask.data.local

import de.nickicloud.foxeltask.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

/**
 * High-performance, reactive local cache data source.
 * Acts as the offline-first single source of truth for the Compose UI.
 */
class LocalCacheDataSource {
    private val _currentUser = MutableStateFlow<UserProfile?>(null)
    val currentUser = _currentUser.asStateFlow()

    private val _workspaces = MutableStateFlow<List<WorkspaceSummary>>(emptyList())
    val workspaces = _workspaces.asStateFlow()

    private val _boards = MutableStateFlow<Map<String, List<Board>>>(emptyMap()) // workspaceId -> Boards
    private val _notes = MutableStateFlow<Map<String, List<Note>>>(emptyMap())    // workspaceId -> Notes
    private val _drawings = MutableStateFlow<Map<String, List<Drawing>>>(emptyMap()) // workspaceId -> Drawings

    fun setUser(user: UserProfile?) {
        _currentUser.value = user
    }

    fun setWorkspaces(list: List<WorkspaceSummary>) {
        _workspaces.value = list
    }

    fun upsertWorkspace(summary: WorkspaceSummary) {
        val current = _workspaces.value.toMutableList()
        val index = current.indexOfFirst { it.id == summary.id }
        if (index >= 0) {
            current[index] = summary
        } else {
            current.add(summary)
        }
        _workspaces.value = current
    }

    fun getBoards(workspaceId: String): Flow<List<Board>> =
        _boards.map { it[workspaceId] ?: emptyList() }

    fun setBoards(workspaceId: String, boardsList: List<Board>) {
        val map = _boards.value.toMutableMap()
        map[workspaceId] = boardsList
        _boards.value = map
    }

    fun upsertBoard(workspaceId: String, board: Board) {
        val map = _boards.value.toMutableMap()
        val list = (map[workspaceId] ?: emptyList()).toMutableList()
        val index = list.indexOfFirst { it.id == board.id }
        if (index >= 0) list[index] = board else list.add(board)
        map[workspaceId] = list
        _boards.value = map
    }

    fun moveCard(boardId: String, cardId: String, targetColumnId: String, targetPosition: Int) {
        val map = _boards.value.toMutableMap()
        for ((wsId, boards) in map) {
            val boardIndex = boards.indexOfFirst { it.id == boardId }
            if (boardIndex >= 0) {
                val board = boards[boardIndex]
                var movedCard: KanbanCard? = null

                val updatedCols = board.columns.map { col ->
                    val card = col.cards.find { it.id == cardId }
                    if (card != null) movedCard = card
                    col.copy(cards = col.cards.filter { it.id != cardId })
                }.map { col ->
                    val card = movedCard
                    if (col.id == targetColumnId && card != null) {
                        val newCards = col.cards.toMutableList()
                        val insertPos = targetPosition.coerceIn(0, newCards.size)
                        newCards.add(insertPos, card.copy(columnId = targetColumnId, position = insertPos))
                        col.copy(cards = newCards)
                    } else col
                }

                val newBoardList = boards.toMutableList()
                newBoardList[boardIndex] = board.copy(columns = updatedCols)
                map[wsId] = newBoardList
                _boards.value = map
                break
            }
        }
    }

    fun getNotes(workspaceId: String): Flow<List<Note>> =
        _notes.map { it[workspaceId] ?: emptyList() }

    fun setNotes(workspaceId: String, notesList: List<Note>) {
        val map = _notes.value.toMutableMap()
        map[workspaceId] = notesList
        _notes.value = map
    }

    fun upsertNote(workspaceId: String, note: Note) {
        val map = _notes.value.toMutableMap()
        val list = (map[workspaceId] ?: emptyList()).toMutableList()
        val index = list.indexOfFirst { it.id == note.id }
        if (index >= 0) list[index] = note else list.add(0, note)
        map[workspaceId] = list
        _notes.value = map
    }

    fun getDrawings(workspaceId: String): Flow<List<Drawing>> =
        _drawings.map { it[workspaceId] ?: emptyList() }

    fun setDrawings(workspaceId: String, drawingsList: List<Drawing>) {
        val map = _drawings.value.toMutableMap()
        map[workspaceId] = drawingsList
        _drawings.value = map
    }

    fun upsertDrawing(workspaceId: String, drawing: Drawing) {
        val map = _drawings.value.toMutableMap()
        val list = (map[workspaceId] ?: emptyList()).toMutableList()
        val index = list.indexOfFirst { it.id == drawing.id }
        if (index >= 0) list[index] = drawing else list.add(0, drawing)
        map[workspaceId] = list
        _drawings.value = map
    }
}
