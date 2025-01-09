package com.example.compliment.ui.bottommenu

import android.util.Log
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.compliment.R
import com.example.compliment.data.sharedprefs.PrefsManager
import com.example.compliment.navigation.Screen
import com.example.compliment.ui.home.HomeScreen
import com.example.compliment.ui.notifications.NotificationsScreen
import com.example.compliment.ui.settings.SettingsScreen
import com.example.compliment.ui.theme.MyAppTheme
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun MainScreen(initialText: String) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val viewModel = koinViewModel<MainScreenViewModel>(
        parameters = { parametersOf(PrefsManager(context)) }
    )
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()

    Log.d("RecompositionTracker", "MainScreen recomposition")

    LaunchedEffect(Unit) {
        viewModel.checkRecreate(context)
    }

    MyAppTheme(isDarkTheme = isDarkTheme) {
        Scaffold(
            modifier = Modifier,
            bottomBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()

                ) {
                    BottomNavigationBar(navController = navController)
                }
            }
        ) { innerPadding ->
            Box(
                Modifier.fillMaxSize()
            ) {
                Image(
                    painter = if(isDarkTheme) painterResource(R.drawable.background_dark_gradient)
                        else painterResource(R.drawable.background_red_gradient),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                NavHost(
                    navController = navController,
                    startDestination = Screen.Home.route,
                    modifier = Modifier,
                    enterTransition = { slideInHorizontally { it } },
                    exitTransition = { slideOutHorizontally { -it } },
                    popEnterTransition = { slideInHorizontally { -it } },
                    popExitTransition = { slideOutHorizontally { it } }
                ) {
                    composable(Screen.Home.route) { backStackEntry ->
                        HomeScreen(initialCompliment = initialText, innerPadding)
                    }
                    composable(Screen.Notifications.route) { NotificationsScreen() }
                    composable(Screen.Settings.route) { SettingsScreen() }
                }
            }

        }
    }
}