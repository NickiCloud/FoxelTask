package de.nickicloud.foxeltask.presentation.screens.dashboard

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.nickicloud.foxeltask.model.Role
import de.nickicloud.foxeltask.presentation.components.glass.*
import de.nickicloud.foxeltask.presentation.screens.canvas.CanvasDrawingScreen
import de.nickicloud.foxeltask.presentation.screens.kanban.KanbanBoardScreen
import de.nickicloud.foxeltask.presentation.screens.notes.MarkdownEditorScreen
import de.nickicloud.foxeltask.presentation.screens.settings.SettingsScreen
import de.nickicloud.foxeltask.theme.FoxelColors
import de.nickicloud.foxeltask.theme.FoxelShapes
import de.nickicloud.foxeltask.theme.pulsingGlow

enum class NavigationSection(val title: String, val icon: String) {
    KANBAN("Kanban Boards", "📋"),
    NOTES("Markdown Notizen", "📝"),
    CANVAS("Freihand & Stylus", "✏️"),
    SETTINGS("Einstellungen", "⚙️")
}

@Composable
fun DashboardScreen(modifier: Modifier = Modifier) {
    var selectedSection by remember { mutableStateOf(NavigationSection.KANBAN) }
    var searchQuery by remember { mutableStateOf("") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(FoxelColors.VoidDark)
    ) {
        // Subtle background ambiance glow spheres
        Box(
            modifier = Modifier
                .size(450.dp)
                .offset(x = (-100).dp, y = (-100).dp)
                .pulsingGlow(color = FoxelColors.NeonCyan, minAlpha = 0.04f, maxAlpha = 0.12f, durationMs = 3500)
        )
        Box(
            modifier = Modifier
                .size(500.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 120.dp, y = 120.dp)
                .pulsingGlow(color = FoxelColors.NeonPurple, minAlpha = 0.04f, maxAlpha = 0.10f, durationMs = 4200)
        )

        Row(modifier = Modifier.fillMaxSize()) {
            // Sidebar Navigation (Glassmorphic)
            GlassSurface(
                modifier = Modifier
                    .width(260.dp)
                    .fillMaxHeight()
                    .padding(12.dp),
                backgroundColor = FoxelColors.GlassSurfaceDarkSubtle,
                borderColor = FoxelColors.GlassBorderDark,
                shape = FoxelShapes.ShapeLg
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Logo & App Name
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(FoxelShapes.ShapeSm)
                                .background(FoxelColors.AccentGlassGradient),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🦊", fontSize = 18.sp)
                        }
                        Column {
                            Text(
                                text = "FoxelTask",
                                color = FoxelColors.TextPrimaryDark,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Produktivitätssystem",
                                color = FoxelColors.NeonCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Workspace Switcher Glass Badge
                    GlassSurface(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = FoxelColors.GlassSurfaceDarkSubtle,
                        shape = FoxelShapes.ShapeSm,
                        contentPadding = 8.dp
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text("🚀", fontSize = 14.sp)
                                Column {
                                    Text(
                                        "Haupt-Workspace",
                                        color = FoxelColors.TextPrimaryDark,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        "3 Boards • 12 Notizen",
                                        color = FoxelColors.TextSecondaryDark,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                            RoleBadge(role = Role.WORKSPACE_ADMIN)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Navigation Items
                    Text(
                        text = "NAVIGATION",
                        color = FoxelColors.TextMutedDark,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    NavigationSection.entries.forEach { section ->
                        val isSelected = selectedSection == section
                        val animatedBg by animateColorAsState(
                            targetValue = if (isSelected) FoxelColors.NeonCyan.copy(alpha = 0.18f) else Color.Transparent
                        )
                        val animatedBorder by animateColorAsState(
                            targetValue = if (isSelected) FoxelColors.NeonCyan.copy(alpha = 0.6f) else Color.Transparent
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(FoxelShapes.ShapeSm)
                                .background(animatedBg)
                                .border(1.dp, animatedBorder, FoxelShapes.ShapeSm)
                                .clickable { selectedSection = section }
                                .padding(horizontal = 12.dp, vertical = 10.dp)
                        ) {
                            Text(section.icon, fontSize = 16.sp)
                            Text(
                                text = section.title,
                                color = if (isSelected) FoxelColors.NeonCyan else FoxelColors.TextSecondaryDark,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Real-time WebSocket Status
                    GlassSurface(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = Color.Transparent,
                        borderColor = FoxelColors.GlassBorderDark.copy(alpha = 0.2f),
                        shape = FoxelShapes.ShapeSm,
                        contentPadding = 8.dp
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(FoxelColors.NeonEmerald)
                                    .pulsingGlow(color = FoxelColors.NeonEmerald, minAlpha = 0.3f, maxAlpha = 0.9f)
                            )
                            Text(
                                text = "Ktor WebSocket Live-Sync",
                                color = FoxelColors.TextSecondaryDark,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            // Main Content Area
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(top = 12.dp, end = 12.dp, bottom = 12.dp)
            ) {
                // Top Glass App Bar
                GlassSurface(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = FoxelColors.GlassSurfaceDarkSubtle,
                    borderColor = FoxelColors.GlassBorderDark,
                    shape = FoxelShapes.ShapeMd,
                    contentPadding = 12.dp
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Search Bar
                        GlassTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = "Volltextsuche in Boards, Markdown & Tags...",
                            modifier = Modifier.width(380.dp)
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            GlassButton(
                                text = "+ Neues Element",
                                onClick = {},
                                style = GlassButtonStyle.PRIMARY_NEON
                            )

                            // User Profile Avatar
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(FoxelColors.NeonPurple.copy(alpha = 0.3f))
                                    .border(1.dp, FoxelColors.NeonPurple, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("N", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Section Content
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    when (selectedSection) {
                        NavigationSection.KANBAN -> KanbanBoardScreen()
                        NavigationSection.NOTES -> MarkdownEditorScreen()
                        NavigationSection.CANVAS -> CanvasDrawingScreen()
                        NavigationSection.SETTINGS -> SettingsScreen()
                    }
                }
            }
        }
    }
}
