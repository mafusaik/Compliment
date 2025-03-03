package com.glazer.compliment.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat

@Composable
fun MyAppTheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (isDarkTheme) DarkColorScheme else LightColorScheme
    val window = (LocalContext.current as? Activity)?.window

    SideEffect {
        window?.let {
            WindowCompat.getInsetsController(window, window.decorView).apply {
//                systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
//                hide(WindowInsetsCompat.Type.navigationBars())
                if (isDarkTheme) isAppearanceLightStatusBars = false
                else isAppearanceLightStatusBars = true
            }
        }
    }

    MaterialTheme(
        colorScheme = colors,
        typography = customTypography,
        content = content
    )
}

private val DarkColorScheme = darkColorScheme(
    primary = WhiteBackground,
    onPrimary = DarkBlue,
    secondary = DarkBlue,
    onSecondary = WhiteBackground,
    background = DarkBlue,
    onSurface = Grey,
    surface = GreyLight,
    error = RedError,
    tertiary = DarkBlueLight,
    surfaceContainer = DarkBlueLight,
    inverseSurface = DarkBlueLight
)

private val LightColorScheme = lightColorScheme(
    primary = RedDark,
    onPrimary = WhiteBackground,
    secondary = WhiteBackground,
    onSecondary = Black,
    background = WhiteBackground,
    onSurface = Grey,
    surface = GreyLight,
    error = RedError,
    tertiary = Grey,
    surfaceContainer = WhiteEgg,
    inverseSurface = GreyLight
)