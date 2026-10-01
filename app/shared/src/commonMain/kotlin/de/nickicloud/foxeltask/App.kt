package de.nickicloud.foxeltask

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import de.nickicloud.foxeltask.presentation.screens.dashboard.DashboardScreen
import de.nickicloud.foxeltask.theme.FoxelTheme

@Composable
@Preview
fun App() {
    FoxelTheme {
        DashboardScreen()
    }
}