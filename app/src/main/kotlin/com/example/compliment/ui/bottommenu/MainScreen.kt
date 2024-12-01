package com.example.compliment.ui.bottommenu

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.compliment.navigation.Screen
import com.example.compliment.ui.home.HomeScreen
import com.example.compliment.ui.notifications.NotificationsScreen

@Composable
fun MainScreen(initialText: String) {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) { backStackEntry ->
                HomeScreen(initialCompliment = initialText)
            }
            composable(Screen.Notifications.route) { NotificationsScreen() }
        }
    }
}