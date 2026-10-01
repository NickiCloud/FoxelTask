package de.nickicloud.foxeltask.presentation.screens.notes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.nickicloud.foxeltask.model.Note
import de.nickicloud.foxeltask.model.NoteType
import de.nickicloud.foxeltask.presentation.components.glass.*
import de.nickicloud.foxeltask.theme.FoxelColors
import de.nickicloud.foxeltask.theme.FoxelShapes
import kotlinx.datetime.Clock

@Composable
fun MarkdownEditorScreen(modifier: Modifier = Modifier) {
    var notes by remember {
        mutableStateOf(
            listOf(
                Note(
                    id = "n-1",
                    workspaceId = "ws-1",
                    title = "Systemarchitektur & Deployment",
                    content = """# FoxelTask Architektur
                    
## 1. Clean Architecture
- **UI-Layer**: Compose Multiplatform mit Glassmorphism
- **Domain-Layer**: Reusable Data Classes & Repositories
- **Data-Layer**: SQLDelight (Client) & Exposed ORM (Ktor Server)

## 2. Docker & MySQL
- Containerized Ktor-Backend auf Port 8080
- Multi-User WebSocket Channel mit Live-Cursor

```kotlin
fun Application.module() {
    configureDatabases()
    configureSockets()
}
```
                    """.trimIndent(),
                    type = NoteType.MARKDOWN,
                    isPinned = true,
                    createdById = "user-1",
                    createdAt = Clock.System.now(),
                    updatedAt = Clock.System.now()
                ),
                Note(
                    id = "n-2",
                    workspaceId = "ws-1",
                    title = "Meeting Notes - Sprint 1",
                    content = """# Sprint Planning
- [x] Backend-Tabellenschemata definieren
- [x] Glassmorphism Theme implementieren
- [ ] Stylus Drawing Canvas auf Tablets testen
                    """.trimIndent(),
                    type = NoteType.MARKDOWN,
                    createdById = "user-1",
                    createdAt = Clock.System.now(),
                    updatedAt = Clock.System.now()
                )
            )
        )
    }

    var selectedNoteId by remember { mutableStateOf(notes.first().id) }
    val currentNote = notes.find { it.id == selectedNoteId } ?: notes.first()
    var editContent by remember(selectedNoteId) { mutableStateOf(currentNote.content) }
    var editTitle by remember(selectedNoteId) { mutableStateOf(currentNote.title) }
    var isPreviewMode by remember { mutableStateOf(false) }

    Row(
        modifier = modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Notes Sidebar
        GlassSurface(
            modifier = Modifier
                .width(280.dp)
                .fillMaxHeight(),
            backgroundColor = FoxelColors.GlassSurfaceDarkSubtle,
            shape = FoxelShapes.ShapeMd,
            contentPadding = 12.dp
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("📝 Notizen", color = FoxelColors.TextPrimaryDark, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    GlassButton(
                        text = "+ Neu",
                        onClick = {
                            val newNote = Note(
                                id = "n-${Clock.System.now().toEpochMilliseconds()}",
                                workspaceId = "ws-1",
                                title = "Unbenannte Notiz",
                                content = "# Neue Notiz\n\nSchreibe hier deinen Markdown-Text...",
                                createdById = "user-1",
                                createdAt = Clock.System.now(),
                                updatedAt = Clock.System.now()
                            )
                            notes = listOf(newNote) + notes
                            selectedNoteId = newNote.id
                        },
                        style = GlassButtonStyle.PRIMARY_NEON
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(notes) { note ->
                        val isSelected = note.id == selectedNoteId
                        GlassCard(
                            onClick = { selectedNoteId = note.id },
                            baseColor = if (isSelected) FoxelColors.NeonCyan.copy(alpha = 0.15f) else FoxelColors.GlassSurfaceDarkSubtle,
                            borderColor = if (isSelected) FoxelColors.NeonCyan else FoxelColors.GlassBorderDark,
                            accentGlowColor = if (isSelected) FoxelColors.NeonCyan else null,
                            contentPadding = 10.dp
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (note.isPinned) {
                                    Text("📌 ", fontSize = 11.sp)
                                }
                                Text(
                                    text = note.title,
                                    color = FoxelColors.TextPrimaryDark,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    maxLines = 1
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = note.content.take(60).replace("\n", " ") + "...",
                                color = FoxelColors.TextSecondaryDark,
                                fontSize = 11.sp,
                                maxLines = 2
                            )
                        }
                    }
                }
            }
        }

        // Markdown Editor & Preview Container
        GlassSurface(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            backgroundColor = FoxelColors.GlassSurfaceDarkSubtle,
            shape = FoxelShapes.ShapeMd,
            contentPadding = 16.dp
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Toolbar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Note Title
                    BasicTextField(
                        value = editTitle,
                        onValueChange = {
                            editTitle = it
                            notes = notes.map { n -> if (n.id == selectedNoteId) n.copy(title = it) else n }
                        },
                        textStyle = TextStyle(
                            color = FoxelColors.TextPrimaryDark,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        cursorBrush = SolidColor(FoxelColors.NeonCyan),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        GlassButton(
                            text = if (isPreviewMode) "✏️ Editor" else "👁️ Vorschau",
                            onClick = { isPreviewMode = !isPreviewMode },
                            style = GlassButtonStyle.SECONDARY_GLASS
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Quick Markdown formatting buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf("# H1", "## H2", "**Fett**", "*Kursiv*", "`Code`", "• Liste", "[ ] Checkbox").forEach { action ->
                        Box(
                            modifier = Modifier
                                .clickable {
                                    val insertText = when (action) {
                                        "# H1" -> "# "
                                        "## H2" -> "## "
                                        "**Fett**" -> "**Text**"
                                        "*Kursiv*" -> "*Text*"
                                        "`Code`" -> "```\ncode\n```"
                                        "• Liste" -> "- "
                                        "[ ] Checkbox" -> "- [ ] "
                                        else -> ""
                                    }
                                    editContent = editContent + "\n" + insertText
                                    notes = notes.map { n -> if (n.id == selectedNoteId) n.copy(content = editContent) else n }
                                }
                                .background(FoxelColors.GlassSurfaceDarkElevated, FoxelShapes.ShapeXs)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(action, fontSize = 11.sp, color = FoxelColors.NeonCyan)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Editor / Preview Area
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(FoxelColors.DeepSpace.copy(alpha = 0.6f), FoxelShapes.ShapeSm)
                        .padding(14.dp)
                ) {
                    if (isPreviewMode) {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            item {
                                Text(
                                    text = editContent,
                                    color = FoxelColors.TextPrimaryDark,
                                    fontSize = 14.sp,
                                    lineHeight = 22.sp,
                                    fontFamily = FontFamily.Default
                                )
                            }
                        }
                    } else {
                        BasicTextField(
                            value = editContent,
                            onValueChange = {
                                editContent = it
                                notes = notes.map { n -> if (n.id == selectedNoteId) n.copy(content = it) else n }
                            },
                            textStyle = TextStyle(
                                color = FoxelColors.TextPrimaryDark,
                                fontSize = 14.sp,
                                lineHeight = 22.sp,
                                fontFamily = FontFamily.Monospace
                            ),
                            cursorBrush = SolidColor(FoxelColors.NeonCyan),
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}
