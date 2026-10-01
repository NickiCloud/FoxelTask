package de.nickicloud.foxeltask.presentation.screens.kanban

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.nickicloud.foxeltask.model.*
import de.nickicloud.foxeltask.presentation.components.glass.*
import de.nickicloud.foxeltask.theme.FoxelColors
import de.nickicloud.foxeltask.theme.FoxelShapes
import kotlinx.datetime.Clock

@Composable
fun KanbanBoardScreen(modifier: Modifier = Modifier) {
    var columns by remember {
        mutableStateOf(
            listOf(
                BoardColumn(
                    id = "col-1",
                    boardId = "b-1",
                    name = "Zu erledigen",
                    colorHex = "#64748B",
                    position = 0,
                    wipLimit = 5,
                    cards = listOf(
                        KanbanCard(
                            id = "c-1",
                            columnId = "col-1",
                            boardId = "b-1",
                            title = "Ktor WebSocket Sync Integration",
                            description = "Echtzeit-Synchronisation zwischen Mobile, Web und Desktop",
                            priority = Priority.HIGH,
                            createdAt = Clock.System.now(),
                            updatedAt = Clock.System.now()
                        ),
                        KanbanCard(
                            id = "c-2",
                            columnId = "col-1",
                            boardId = "b-1",
                            title = "SQLDelight Offline Cache",
                            description = "Lokale Tabellen und Sync-Queue einrichten",
                            priority = Priority.MEDIUM,
                            createdAt = Clock.System.now(),
                            updatedAt = Clock.System.now()
                        )
                    ),
                    createdAt = Clock.System.now(),
                    updatedAt = Clock.System.now()
                ),
                BoardColumn(
                    id = "col-2",
                    boardId = "b-1",
                    name = "In Bearbeitung",
                    colorHex = "#00F0FF",
                    position = 1,
                    wipLimit = 3,
                    cards = listOf(
                        KanbanCard(
                            id = "c-3",
                            columnId = "col-2",
                            boardId = "b-1",
                            title = "Glassmorphism UI Theme",
                            description = "Custom Modifiers für Blur, Glow und GlassCards",
                            priority = Priority.URGENT,
                            createdAt = Clock.System.now(),
                            updatedAt = Clock.System.now()
                        )
                    ),
                    createdAt = Clock.System.now(),
                    updatedAt = Clock.System.now()
                ),
                BoardColumn(
                    id = "col-3",
                    boardId = "b-1",
                    name = "Review & Test",
                    colorHex = "#A855F7",
                    position = 2,
                    wipLimit = 3,
                    cards = emptyList(),
                    createdAt = Clock.System.now(),
                    updatedAt = Clock.System.now()
                ),
                BoardColumn(
                    id = "col-4",
                    boardId = "b-1",
                    name = "Abgeschlossen",
                    colorHex = "#10B981",
                    position = 3,
                    cards = listOf(
                        KanbanCard(
                            id = "c-4",
                            columnId = "col-4",
                            boardId = "b-1",
                            title = "Exposed MySQL Datenbank-Schema",
                            description = "Tables für Users, Workspaces, Boards & Notes",
                            priority = Priority.LOW,
                            isCompleted = true,
                            createdAt = Clock.System.now(),
                            updatedAt = Clock.System.now()
                        )
                    ),
                    createdAt = Clock.System.now(),
                    updatedAt = Clock.System.now()
                )
            )
        )
    }

    var showNewCardDialog by remember { mutableStateOf(false) }
    var selectedColForNewCard by remember { mutableStateOf("col-1") }
    var newCardTitle by remember { mutableStateOf("") }
    var newCardDesc by remember { mutableStateOf("") }
    var newCardPriority by remember { mutableStateOf(Priority.MEDIUM) }

    fun moveCardToColumn(cardId: String, currentColId: String, targetColId: String) {
        val currentCol = columns.find { it.id == currentColId } ?: return
        val cardToMove = currentCol.cards.find { it.id == cardId } ?: return

        columns = columns.map { col ->
            when (col.id) {
                currentColId -> col.copy(cards = col.cards.filter { it.id != cardId })
                targetColId -> col.copy(cards = col.cards + cardToMove.copy(columnId = targetColId))
                else -> col
            }
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        // Board Controls Header
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("📋 Kanban: Produktentwicklung", color = FoxelColors.TextPrimaryDark, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                GlassBadge(text = "4 Spalten", accentColor = FoxelColors.NeonCyan)
            }

            GlassButton(
                text = "+ Neue Aufgabe",
                onClick = {
                    selectedColForNewCard = columns.firstOrNull()?.id ?: "col-1"
                    showNewCardDialog = true
                },
                style = GlassButtonStyle.PRIMARY_NEON
            )
        }

        // Columns LazyRow
        LazyRow(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(columns) { column ->
                val colColor = FoxelColors.parseHexColor(column.colorHex)

                GlassSurface(
                    modifier = Modifier
                        .width(320.dp)
                        .fillMaxHeight(),
                    backgroundColor = FoxelColors.GlassSurfaceDarkSubtle,
                    shape = FoxelShapes.ShapeMd,
                    contentPadding = 12.dp
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Column Header
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .background(colColor, FoxelShapes.ShapeFull)
                                )
                                Text(
                                    text = column.name,
                                    color = FoxelColors.TextPrimaryDark,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            GlassBadge(text = "${column.cards.size}", accentColor = colColor)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Cards List
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            items(column.cards) { card ->
                                val cardColIdx = columns.indexOf(column)

                                GlassCard(
                                    onClick = {},
                                    accentGlowColor = colColor,
                                    elevation = 3.dp
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        PriorityBadge(priority = card.priority)
                                        if (card.isCompleted) {
                                            GlassBadge(text = "✓ Erledigt", accentColor = FoxelColors.NeonEmerald)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = card.title,
                                        color = FoxelColors.TextPrimaryDark,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )

                                    if (!card.description.isNullOrBlank()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = card.description!!,
                                            color = FoxelColors.TextSecondaryDark,
                                            fontSize = 12.sp,
                                            lineHeight = 16.sp
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Quick Move Card Controls
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        if (cardColIdx > 0) {
                                            Text(
                                                text = "◀ Zurück",
                                                color = FoxelColors.NeonCyan,
                                                fontSize = 11.sp,
                                                modifier = Modifier.clickable {
                                                    moveCardToColumn(card.id, column.id, columns[cardColIdx - 1].id)
                                                }
                                            )
                                        } else {
                                            Spacer(modifier = Modifier.width(1.dp))
                                        }

                                        if (cardColIdx < columns.size - 1) {
                                            Text(
                                                text = "Vor ▶",
                                                color = FoxelColors.NeonCyan,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                modifier = Modifier.clickable {
                                                    moveCardToColumn(card.id, column.id, columns[cardColIdx + 1].id)
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Add card button in column
                        GlassButton(
                            text = "+ Aufgabe hinzufügen",
                            onClick = {
                                selectedColForNewCard = column.id
                                showNewCardDialog = true
                            },
                            style = GlassButtonStyle.OUTLINE_GLASS,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }

    // Modal Dialog to Add Card
    if (showNewCardDialog) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.65f))
                .clickable { showNewCardDialog = false },
            contentAlignment = Alignment.Center
        ) {
            GlassSurface(
                modifier = Modifier
                    .width(420.dp)
                    .clickable(enabled = false) {},
                backgroundColor = FoxelColors.DeepSpace,
                borderColor = FoxelColors.NeonCyan,
                shape = FoxelShapes.ShapeLg,
                contentPadding = 20.dp
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text("Neue Aufgabe erstellen", color = FoxelColors.TextPrimaryDark, fontSize = 18.sp, fontWeight = FontWeight.Bold)

                    GlassTextField(
                        value = newCardTitle,
                        onValueChange = { newCardTitle = it },
                        placeholder = "Aufgabentitel..."
                    )

                    GlassTextField(
                        value = newCardDesc,
                        onValueChange = { newCardDesc = it },
                        placeholder = "Optionale Beschreibung...",
                        singleLine = false
                    )

                    // Priority Selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Priority.entries.forEach { p ->
                            val isSelected = newCardPriority == p
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { newCardPriority = p }
                                    .background(
                                        if (isSelected) FoxelColors.NeonCyan.copy(alpha = 0.25f) else Color.Transparent,
                                        FoxelShapes.ShapeSm
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) FoxelColors.NeonCyan else FoxelColors.GlassBorderDark,
                                        FoxelShapes.ShapeSm
                                    )
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(p.name, fontSize = 11.sp, color = if (isSelected) FoxelColors.NeonCyan else FoxelColors.TextSecondaryDark)
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        GlassButton(
                            text = "Abbrechen",
                            onClick = { showNewCardDialog = false },
                            style = GlassButtonStyle.OUTLINE_GLASS
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        GlassButton(
                            text = "Erstellen",
                            onClick = {
                                if (newCardTitle.isNotBlank()) {
                                    val newCard = KanbanCard(
                                        id = "card-${Clock.System.now().toEpochMilliseconds()}",
                                        columnId = selectedColForNewCard,
                                        boardId = "b-1",
                                        title = newCardTitle,
                                        description = newCardDesc.ifBlank { null },
                                        priority = newCardPriority,
                                        createdAt = Clock.System.now(),
                                        updatedAt = Clock.System.now()
                                    )
                                    columns = columns.map { col ->
                                        if (col.id == selectedColForNewCard) col.copy(cards = col.cards + newCard) else col
                                    }
                                    newCardTitle = ""
                                    newCardDesc = ""
                                    showNewCardDialog = false
                                }
                            },
                            style = GlassButtonStyle.PRIMARY_NEON
                        )
                    }
                }
            }
        }
    }
}
