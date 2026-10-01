package de.nickicloud.foxeltask.data.repository

import de.nickicloud.foxeltask.data.local.LocalCacheDataSource
import de.nickicloud.foxeltask.domain.repository.*
import de.nickicloud.foxeltask.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.datetime.Clock

class AuthRepositoryImpl(
    private val localCache: LocalCacheDataSource
) : AuthRepository {
    override val currentUser: Flow<UserProfile?> = localCache.currentUser

    override suspend fun login(request: LoginRequest): Result<AuthResponse> {
        val mockUser = UserProfile(
            id = "user-1",
            username = request.usernameOrEmail,
            email = if (request.usernameOrEmail.contains("@")) request.usernameOrEmail else "${request.usernameOrEmail}@foxeltask.de",
            fullName = "Foxel Administrator",
            isGlobalAdmin = true
        )
        val response = AuthResponse(
            token = "jwt-mock-token-foxeltask",
            expiresIn = 86400,
            user = mockUser
        )
        localCache.setUser(mockUser)
        return Result.success(response)
    }

    override suspend fun register(request: RegisterRequest): Result<AuthResponse> {
        val user = UserProfile(
            id = "user-new",
            username = request.username,
            email = request.email,
            fullName = request.fullName,
            isGlobalAdmin = false
        )
        val response = AuthResponse(
            token = "jwt-mock-token-foxeltask",
            expiresIn = 86400,
            user = user
        )
        localCache.setUser(user)
        return Result.success(response)
    }

    override suspend fun logout() {
        localCache.setUser(null)
    }

    override suspend fun getProfile(): Result<UserProfile> {
        val user = localCache.currentUser.value ?: UserProfile("guest", "Gast", "guest@foxel.de", "Gastbenutzer")
        return Result.success(user)
    }

    override suspend fun updateProfile(request: UpdateUserRequest): Result<UserProfile> {
        val current = localCache.currentUser.value ?: return Result.failure(Exception("Not logged in"))
        val updated = current.copy(
            fullName = request.fullName ?: current.fullName,
            email = request.email ?: current.email,
            avatarUrl = request.avatarUrl ?: current.avatarUrl
        )
        localCache.setUser(updated)
        return Result.success(updated)
    }
}

class WorkspaceRepositoryImpl(
    private val localCache: LocalCacheDataSource
) : WorkspaceRepository {
    override fun getWorkspaces(): Flow<List<WorkspaceSummary>> = localCache.workspaces

    override suspend fun getWorkspace(id: String): Result<Workspace> {
        val ws = Workspace(
            id = id,
            name = "Haupt-Workspace",
            slug = "haupt-workspace",
            description = "Zentraler Projekt- und Notizenbereich",
            icon = "🚀",
            ownerId = "user-1",
            createdAt = Clock.System.now(),
            updatedAt = Clock.System.now()
        )
        return Result.success(ws)
    }

    override suspend fun createWorkspace(request: CreateWorkspaceRequest): Result<Workspace> {
        val ws = Workspace(
            id = "ws-${Clock.System.now().toEpochMilliseconds()}",
            name = request.name,
            slug = request.name.lowercase().replace(" ", "-"),
            description = request.description,
            icon = request.icon ?: "📁",
            ownerId = "user-1",
            createdAt = Clock.System.now(),
            updatedAt = Clock.System.now()
        )
        localCache.upsertWorkspace(
            WorkspaceSummary(
                id = ws.id,
                name = ws.name,
                slug = ws.slug,
                description = ws.description,
                icon = ws.icon,
                role = Role.WORKSPACE_ADMIN
            )
        )
        return Result.success(ws)
    }

    override suspend fun updateWorkspace(id: String, request: UpdateWorkspaceRequest): Result<Workspace> {
        return getWorkspace(id)
    }

    override suspend fun deleteWorkspace(id: String): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun getMembers(workspaceId: String): Result<List<WorkspaceMember>> {
        return Result.success(emptyList())
    }

    override suspend fun addMember(workspaceId: String, request: AddMemberRequest): Result<WorkspaceMember> {
        val member = WorkspaceMember(
            id = "m-${Clock.System.now().toEpochMilliseconds()}",
            workspaceId = workspaceId,
            userId = request.userIdOrEmail,
            role = request.role,
            joinedAt = Clock.System.now()
        )
        return Result.success(member)
    }

    override suspend fun removeMember(workspaceId: String, memberId: String): Result<Unit> {
        return Result.success(Unit)
    }
}

class BoardRepositoryImpl(
    private val localCache: LocalCacheDataSource
) : BoardRepository {
    override fun getBoards(workspaceId: String): Flow<List<Board>> =
        localCache.getBoards(workspaceId)

    override suspend fun getBoard(id: String): Result<Board> {
        val board = Board(
            id = id,
            workspaceId = "ws-1",
            title = "Hauptprojekt Board",
            createdById = "user-1",
            createdAt = Clock.System.now(),
            updatedAt = Clock.System.now()
        )
        return Result.success(board)
    }

    override suspend fun createBoard(request: CreateBoardRequest): Result<Board> {
        val board = Board(
            id = "b-${Clock.System.now().toEpochMilliseconds()}",
            workspaceId = request.workspaceId,
            title = request.title,
            description = request.description,
            type = request.type,
            createdById = "user-1",
            createdAt = Clock.System.now(),
            updatedAt = Clock.System.now()
        )
        localCache.upsertBoard(request.workspaceId, board)
        return Result.success(board)
    }

    override suspend fun createColumn(request: CreateColumnRequest): Result<BoardColumn> {
        val col = BoardColumn(
            id = "c-${Clock.System.now().toEpochMilliseconds()}",
            boardId = request.boardId,
            name = request.name,
            colorHex = request.colorHex ?: "#6366F1",
            createdAt = Clock.System.now(),
            updatedAt = Clock.System.now()
        )
        return Result.success(col)
    }

    override suspend fun createCard(request: CreateCardRequest): Result<KanbanCard> {
        val card = KanbanCard(
            id = "card-${Clock.System.now().toEpochMilliseconds()}",
            columnId = request.columnId,
            boardId = request.boardId,
            title = request.title,
            description = request.description,
            priority = request.priority,
            assigneeId = request.assigneeId,
            dueDate = request.dueDate,
            createdAt = Clock.System.now(),
            updatedAt = Clock.System.now()
        )
        return Result.success(card)
    }

    override suspend fun moveCard(request: MoveCardRequest): Result<Unit> {
        localCache.moveCard(
            boardId = "",
            cardId = request.cardId,
            targetColumnId = request.targetColumnId,
            targetPosition = request.targetPosition
        )
        return Result.success(Unit)
    }

    override suspend fun moveColumn(request: MoveColumnRequest): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun deleteCard(cardId: String): Result<Unit> {
        return Result.success(Unit)
    }
}

class NoteRepositoryImpl(
    private val localCache: LocalCacheDataSource
) : NoteRepository {
    override fun getNotes(workspaceId: String, folderId: String?): Flow<List<Note>> =
        localCache.getNotes(workspaceId)

    override suspend fun getNote(id: String): Result<Note> {
        val note = Note(
            id = id,
            workspaceId = "ws-1",
            title = "Notiz",
            content = "# Inhalt",
            createdById = "user-1",
            createdAt = Clock.System.now(),
            updatedAt = Clock.System.now()
        )
        return Result.success(note)
    }

    override suspend fun createNote(request: CreateNoteRequest): Result<Note> {
        val note = Note(
            id = "note-${Clock.System.now().toEpochMilliseconds()}",
            workspaceId = request.workspaceId,
            title = request.title,
            content = request.content,
            type = request.type,
            isPinned = request.isPinned,
            createdById = "user-1",
            createdAt = Clock.System.now(),
            updatedAt = Clock.System.now()
        )
        localCache.upsertNote(request.workspaceId, note)
        return Result.success(note)
    }

    override suspend fun updateNote(id: String, request: UpdateNoteRequest): Result<Note> {
        val note = Note(
            id = id,
            workspaceId = "ws-1",
            title = request.title ?: "Titel",
            content = request.content ?: "",
            createdById = "user-1",
            createdAt = Clock.System.now(),
            updatedAt = Clock.System.now()
        )
        localCache.upsertNote("ws-1", note)
        return Result.success(note)
    }

    override suspend fun deleteNote(id: String): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun searchNotes(workspaceId: String, query: String): Result<List<Note>> {
        return Result.success(emptyList())
    }
}

class DrawingRepositoryImpl(
    private val localCache: LocalCacheDataSource
) : DrawingRepository {
    override fun getDrawings(workspaceId: String): Flow<List<Drawing>> =
        localCache.getDrawings(workspaceId)

    override suspend fun getDrawing(id: String): Result<Drawing> {
        val drawing = Drawing(
            id = id,
            workspaceId = "ws-1",
            title = "Skizze",
            canvasData = DrawingCanvasData(),
            createdById = "user-1",
            createdAt = Clock.System.now(),
            updatedAt = Clock.System.now()
        )
        return Result.success(drawing)
    }

    override suspend fun saveDrawing(request: SaveDrawingRequest): Result<Drawing> {
        val drawing = Drawing(
            id = "draw-${Clock.System.now().toEpochMilliseconds()}",
            workspaceId = request.workspaceId,
            noteId = request.noteId,
            title = request.title,
            canvasData = request.canvasData,
            thumbnailDataUrl = request.thumbnailDataUrl,
            createdById = "user-1",
            createdAt = Clock.System.now(),
            updatedAt = Clock.System.now()
        )
        localCache.upsertDrawing(request.workspaceId, drawing)
        return Result.success(drawing)
    }

    override suspend fun deleteDrawing(id: String): Result<Unit> {
        return Result.success(Unit)
    }
}

class SyncRepositoryImpl : SyncRepository {
    private val _syncEvents = MutableSharedFlow<WebSocketServerMessage>()
    override val syncEvents: Flow<WebSocketServerMessage> = _syncEvents.asSharedFlow()

    override suspend fun connect(workspaceId: String, token: String) {}
    override suspend fun disconnect() {}
    override suspend fun sendSync(message: SyncMessage) {}
    override suspend fun sendCursor(cursor: UserCursorPosition) {}
}
