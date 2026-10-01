package de.nickicloud.foxeltask.presentation.components.glass

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.nickicloud.foxeltask.model.Priority
import de.nickicloud.foxeltask.model.Role
import de.nickicloud.foxeltask.theme.FoxelColors
import de.nickicloud.foxeltask.theme.FoxelShapes

@Composable
fun GlassBadge(
    text: String,
    modifier: Modifier = Modifier,
    accentColor: Color = FoxelColors.NeonCyan,
    shape: Shape = FoxelShapes.ShapeFull,
    icon: (@Composable () -> Unit)? = null
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(accentColor.copy(alpha = 0.16f))
            .border(
                width = 1.dp,
                color = accentColor.copy(alpha = 0.45f),
                shape = shape
            )
            .padding(horizontal = 8.dp, vertical = 3.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                icon()
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = text,
                color = accentColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun PriorityBadge(priority: Priority, modifier: Modifier = Modifier) {
    val (color, label) = when (priority) {
        Priority.LOW -> FoxelColors.NeonBlue to "Niedrig"
        Priority.MEDIUM -> FoxelColors.NeonAmber to "Mittel"
        Priority.HIGH -> FoxelColors.NeonRose to "Hoch"
        Priority.URGENT -> FoxelColors.NeonCoral to "Dringend"
    }
    GlassBadge(text = label, accentColor = color, modifier = modifier)
}

@Composable
fun RoleBadge(role: Role, modifier: Modifier = Modifier) {
    val (color, label) = when (role) {
        Role.GLOBAL_ADMIN -> FoxelColors.NeonRose to "Global Admin"
        Role.WORKSPACE_ADMIN -> FoxelColors.NeonPurple to "Admin"
        Role.EDITOR -> FoxelColors.NeonCyan to "Bearbeiter"
        Role.VIEWER -> FoxelColors.TextSecondaryDark to "Leser"
    }
    GlassBadge(text = label, accentColor = color, modifier = modifier)
}
