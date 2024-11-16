package com.example.compliment

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.example.compliment.ui.bottommenu.MainScreen
import com.example.compliment.utils.Constants

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val notificationText = intent.getStringExtra(Constants.KEY_NOTIFICATION_TEXT) ?: ""

            MaterialTheme {
                Surface {
                    var textState by rememberSaveable { mutableStateOf(notificationText) }
                    if (textState.isNotEmpty()) {
                        LaunchedEffect(Unit) {
                            textState = ""
                        }
                    }
                    MainScreen(textState)
                }
            }
        }
    }
}