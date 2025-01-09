package com.example.compliment

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.compliment.data.sharedprefs.PrefsManager
import com.example.compliment.extensions.setAppLocale
import com.example.compliment.ui.bottommenu.MainScreen
import com.example.compliment.utils.Constants
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefsManager = PrefsManager(this)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        WindowCompat.getInsetsController(window, window.decorView).apply {
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            hide(WindowInsetsCompat.Type.navigationBars())
            if (prefsManager.isDarkTheme) isAppearanceLightStatusBars = false
            else isAppearanceLightStatusBars = true
        }

        setContent {
            val notificationText = intent.getStringExtra(Constants.KEY_NOTIFICATION_TEXT) ?: ""
            Surface {
                MainScreen(notificationText)
            }
        }
    }

    private fun hideSystemUI() {
        WindowCompat.getInsetsController(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.navigationBars())
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            hideSystemUI()
        }
    }

    override fun attachBaseContext(newBase: Context) {
        Log.i("MAIN_ACTIVITY", "attachBaseContext")
        val prefsManager = PrefsManager(newBase)
        val languageCode = prefsManager.currentLanguage
        val overrideConfiguration = Configuration(newBase.resources.configuration)
        overrideConfiguration.fontScale = 1.0f
        val newContext = newBase.createConfigurationContext(overrideConfiguration)
        val lang = languageCode.ifEmpty { Locale.getDefault().language }
        super.attachBaseContext(newContext.setAppLocale(lang))
    }
}