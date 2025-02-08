package com.glazer.compliment.ui.bottommenu

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.glazer.compliment.navigation.Screen

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = remember { listOf(Screen.Home, Screen.Notifications, Screen.Settings) }
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    Log.d("RecompositionTracker", "BottomNavigationBar recomposition")

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .shadow(8.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
    ) {
        NavigationBar(
            modifier = Modifier
                .background(Color.Transparent)
                .padding(bottom = 0.dp)
                .height(60.dp),
            containerColor = MaterialTheme.colorScheme.background,
        ) {
            items.forEach { screen ->
                val backgroundColor by animateColorAsState(
                    targetValue = if (currentRoute == screen.route) MaterialTheme.colorScheme.primary
                    else Color.Transparent,
                    animationSpec = tween(durationMillis = 100), label = ""
                )

                NavigationBarItem(
                    icon = { Icon(painterResource(screen.icon), contentDescription = null) },
                    selected = currentRoute == screen.route,
                    colors = NavigationBarItemColors(
                        selectedIndicatorColor = Color.Transparent,
                        selectedIconColor = MaterialTheme.colorScheme.background,
                        unselectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.onPrimary,
                        unselectedTextColor = MaterialTheme.colorScheme.primary,
                        disabledIconColor = Color.LightGray,
                        disabledTextColor = Color.LightGray
                    ),
                    onClick = {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    modifier = Modifier
                        .padding(5.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(backgroundColor)
                )
            }
        }
    }
}

