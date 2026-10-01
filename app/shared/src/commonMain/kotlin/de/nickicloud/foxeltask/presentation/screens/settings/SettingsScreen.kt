package de.nickicloud.foxeltask.presentation.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import de.nickicloud.foxeltask.theme.FoxelColors
import de.nickicloud.foxeltask.theme.FoxelShapes

@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
    var serverHost by remember { mutableStateOf("http://localhost:8080") }
    var wsSyncEnabled by remember { mutableStateOf(true) }
    var offlineCacheLimit by remember { mutableStateOf("500 MB") }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("⚙️ Einstellungen & Systemkonfiguration", color = FoxelColors.TextPrimaryDark, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        // Account & RBAC Profile
        item {
            GlassSurface(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = FoxelColors.GlassSurfaceDarkSubtle,
                shape = FoxelShapes.ShapeMd,
                contentPadding = 16.dp
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Benutzerprofil & Berechtigungen (RBAC)", color = FoxelColors.TextPrimaryDark, fontSize = 16.sp, fontWeight = FontWeight.Bold)

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .background(FoxelColors.NeonPurple.copy(alpha = 0.3f))
                                .border(1.5.dp, FoxelColors.NeonPurple, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("A", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                        }

                        Column {
                            Text("Foxel Administrator", color = FoxelColors.TextPrimaryDark, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                            Text("admin@nickicloud.de", color = FoxelColors.TextSecondaryDark, fontSize = 13.sp)
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        RoleBadge(role = Role.GLOBAL_ADMIN)
                    }
                }
            }
        }

        // Backend & Sync Settings
        item {
            GlassSurface(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = FoxelColors.GlassSurfaceDarkSubtle,
                shape = FoxelShapes.ShapeMd,
                contentPadding = 16.dp
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Ktor Backend & WebSocket Synchronisation", color = FoxelColors.TextPrimaryDark, fontSize = 16.sp, fontWeight = FontWeight.Bold)

                    GlassTextField(
                        value = serverHost,
                        onValueChange = { serverHost = it },
                        placeholder = "Backend URL..."
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Echtzeit-Synchronisation (WebSockets)", color = FoxelColors.TextPrimaryDark, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            Text("Empfängt Live-Updates und Cursor-Bewegungen von Kollegen", color = FoxelColors.TextSecondaryDark, fontSize = 11.sp)
                        }

                        Box(
                            modifier = Modifier
                                .clip(FoxelShapes.ShapeSm)
                                .background(if (wsSyncEnabled) FoxelColors.NeonCyan.copy(alpha = 0.25f) else Color.Transparent)
                                .border(1.dp, if (wsSyncEnabled) FoxelColors.NeonCyan else FoxelColors.GlassBorderDark, FoxelShapes.ShapeSm)
                                .clickable { wsSyncEnabled = !wsSyncEnabled }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(if (wsSyncEnabled) "Aktiviert" else "Deaktiviert", color = if (wsSyncEnabled) FoxelColors.NeonCyan else FoxelColors.TextSecondaryDark, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Local Storage & SQLDelight Cache
        item {
            GlassSurface(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = FoxelColors.GlassSurfaceDarkSubtle,
                shape = FoxelShapes.ShapeMd,
                contentPadding = 16.dp
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Lokale Persistenz & Offline Cache (SQLDelight)", color = FoxelColors.TextPrimaryDark, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text("Status: Alle Workspaces, Kanban-Karten und Notizen lokal synchronisiert.", color = FoxelColors.NeonEmerald, fontSize = 12.sp)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Maximaler lokaler Cache-Speicher", color = FoxelColors.TextSecondaryDark, fontSize = 13.sp)
                        GlassBadge(text = offlineCacheLimit, accentColor = FoxelColors.NeonCyan)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    GlassButton(
                        text = "Lokalen Cache leeren",
                        onClick = {},
                        style = GlassButtonStyle.DESTRUCTIVE
                    )
                }
            }
        }
    }
}
