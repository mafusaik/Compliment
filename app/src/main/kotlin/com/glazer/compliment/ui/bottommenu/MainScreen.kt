package com.glazer.compliment.ui.bottommenu

import android.util.Log
import androidx.compose.animation.core.tween
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
import com.glazer.compliment.R
import com.glazer.compliment.data.sharedprefs.PrefsManager
import com.glazer.compliment.navigation.Screen
import com.glazer.compliment.ui.home.HomeScreen
import com.glazer.compliment.ui.notifications.NotificationsScreen
import com.glazer.compliment.ui.settings.SettingsScreen
import com.glazer.compliment.ui.theme.MyAppTheme
import com.glazer.compliment.utils.Constants
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
    val screenOrder = listOf(Screen.Home.route, Screen.Notifications.route, Screen.Settings.route)

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
                    enterTransition = {
                        val fromIndex = screenOrder.indexOf(initialState.destination.route)
                        val toIndex = screenOrder.indexOf(targetState.destination.route)
                        val direction = toIndex.compareTo(fromIndex) // 1 - вправо, -1 - влево

                        slideInHorizontally(
                            animationSpec = tween(durationMillis = Constants.DELAY)
                        ) { fullWidth -> if (direction > 0) fullWidth else -fullWidth }
                    },
                    exitTransition = {
                        val fromIndex = screenOrder.indexOf(initialState.destination.route)
                        val toIndex = screenOrder.indexOf(targetState.destination.route)
                        val direction = toIndex.compareTo(fromIndex)

                        slideOutHorizontally(
                            animationSpec = tween(durationMillis = Constants.DELAY)
                        ) { fullWidth -> if (direction > 0) -fullWidth else fullWidth }
                    },
                    popEnterTransition = {
                        val fromIndex = screenOrder.indexOf(initialState.destination.route)
                        val toIndex = screenOrder.indexOf(targetState.destination.route)
                        val direction = toIndex.compareTo(fromIndex)

                        slideInHorizontally(
                            animationSpec = tween(durationMillis = Constants.DELAY)
                        ) { fullWidth -> if (direction > 0) fullWidth else -fullWidth }
                    },
                    popExitTransition = {
                        val fromIndex = screenOrder.indexOf(initialState.destination.route)
                        val toIndex = screenOrder.indexOf(targetState.destination.route)
                        val direction = toIndex.compareTo(fromIndex)

                        slideOutHorizontally(
                            animationSpec = tween(durationMillis = Constants.DELAY)
                        ) { fullWidth -> if (direction > 0) -fullWidth else fullWidth }
                    }
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