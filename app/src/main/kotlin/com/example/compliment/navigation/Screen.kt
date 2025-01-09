package com.example.compliment.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import com.example.compliment.R

sealed class Screen(val route: String, val title: String, val icon: Int) {
    data object Home : Screen("home", "Домашняя", R.drawable.icon_home)
    data object Notifications : Screen("notifications", "Уведомления", R.drawable.icon_notifications)
    data object Settings : Screen("settings", "Настройки", R.drawable.icon_settings)
}