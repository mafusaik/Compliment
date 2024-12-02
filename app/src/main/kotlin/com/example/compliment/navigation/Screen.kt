package com.example.compliment.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Домашняя", Icons.Filled.Home)
    object Notifications : Screen("notifications", "Уведомления", Icons.Filled.Notifications)
    object Settings : Screen("settings", "Настройки", Icons.Filled.Settings)
}