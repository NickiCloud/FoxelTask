package de.nickicloud.foxeltask.domain.repository

import de.nickicloud.foxeltask.model.*
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: Flow<UserProfile?>
    suspend fun login(request: LoginRequest): Result<AuthResponse>
    suspend fun register(request: RegisterRequest): Result<AuthResponse>
    suspend fun logout()
    suspend fun getProfile(): Result<UserProfile>
    suspend fun updateProfile(request: UpdateUserRequest): Result<UserProfile>
}

interface WorkspaceRepository {
    fun getWorkspaces(): Flow<List<WorkspaceSummary>>
    suspend fun getWorkspace(id: String): Result<Workspace>
    suspend fun createWorkspace(request: CreateWorkspaceRequest): Result<Workspace>
    suspend fun updateWorkspace(id: String, request: UpdateWorkspaceRequest): Result<Workspace>
    suspend fun deleteWorkspace(id: String): Result<Unit>
    suspend fun getMembers(workspaceId: String): Result<List<WorkspaceMember>>
    suspend fun addMember(workspaceId: String, request: AddMemberRequest): Result<WorkspaceMember>
    suspend fun removeMember(workspaceId: String, memberId: String): Result<Unit>
}

interface BoardRepository {
    fun getBoards(workspaceId: String): Flow<List<Board>>
    suspend fun getBoard(id: String): Result<Board>
    suspend fun createBoard(request: CreateBoardRequest): Result<Board>
    suspend fun createColumn(request: CreateColumnRequest): Result<BoardColumn>
    suspend fun createCard(request: CreateCardRequest): Result<KanbanCard>
    suspend fun moveCard(request: MoveCardRequest): Result<Unit>
    suspend fun moveColumn(request: MoveColumnRequest): Result<Unit>
    suspend fun deleteCard(cardId: String): Result<Unit>
}

interface NoteRepository {
    fun getNotes(workspaceId: String, folderId: String? = null): Flow<List<Note>>
    suspend fun getNote(id: String): Result<Note>
    suspend fun createNote(request: CreateNoteRequest): Result<Note>
    suspend fun updateNote(id: String, request: UpdateNoteRequest): Result<Note>
    suspend fun deleteNote(id: String): Result<Unit>
    suspend fun searchNotes(workspaceId: String, query: String): Result<List<Note>>
}

interface DrawingRepository {
    fun getDrawings(workspaceId: String): Flow<List<Drawing>>
    suspend fun getDrawing(id: String): Result<Drawing>
    suspend fun saveDrawing(request: SaveDrawingRequest): Result<Drawing>
    suspend fun deleteDrawing(id: String): Result<Unit>
}

interface SyncRepository {
    val syncEvents: Flow<WebSocketServerMessage>
    suspend fun connect(workspaceId: String, token: String)
    suspend fun disconnect()
    suspend fun sendSync(message: SyncMessage)
    suspend fun sendCursor(cursor: UserCursorPosition)
}
