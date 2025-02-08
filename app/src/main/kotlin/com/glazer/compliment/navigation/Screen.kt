package com.glazer.compliment.navigation

import com.glazer.compliment.R

sealed class Screen(val route: String, val title: String, val icon: Int) {
    data object Home : Screen("home", "Домашняя", R.drawable.icon_home)
    data object Notifications : Screen("notifications", "Уведомления", R.drawable.icon_notifications)
    data object Settings : Screen("settings", "Настройки", R.drawable.icon_settings)
}