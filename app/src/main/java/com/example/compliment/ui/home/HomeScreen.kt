package com.example.compliment.ui.home

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.compliment.MainActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(notificationText: String) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val viewModel = koinViewModel<HomeViewModel>()
    val displayedText by viewModel.currentComplimentFlow.collectAsState()
    var isVisible by remember { mutableStateOf(true) } // Для управления видимостью текста
    val coroutineScope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    if (notificationText.isEmpty()){
                        viewModel.getCompliment("")
                    }else {
                        viewModel.setCompliment(notificationText)
                    }
                }
                Lifecycle.Event.ON_STOP -> {
                    // Действия при сворачивании приложения
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Центрируем текст по вертикали и горизонтали
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp), // Отступ снизу для кнопки
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Анимация появления текста
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(), // Анимация появления
                exit = fadeOut() // Анимация исчезновения
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .padding(horizontal = 20.dp)
                        .clickable {
                            clipboardManager.setText(AnnotatedString(displayedText)) // Копирование текста в буфер обмена
                            Toast.makeText(context, "Текст скопирован", Toast.LENGTH_SHORT).show() // Уведомление
                        },
                    contentAlignment = Alignment.Center // Центрируем текст
                ) {
                    Text(
                        text = displayedText,
                        style = LocalTextStyle.current.copy(
                            fontSize = 26.sp,
                            color = Color.Black,
                            textAlign = TextAlign.Center
                        ),
                        textAlign = TextAlign.Center // Центрируем текст
                    )
                }
//                BasicTextField(
//                    value = displayedText,
//                    onValueChange = {},
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(horizontal = 20.dp)
//                        .height(300.dp),
//                    textStyle = LocalTextStyle.current.copy(
//                        fontSize = 26.sp,
//                        color = Color.Black,
//                        textAlign = TextAlign.Center
//                    ),
//                    decorationBox = { innerTextField ->
//                        Box(
//                            modifier = Modifier.fillMaxSize(),
//                            contentAlignment = Alignment.Center
//                        ) {
//                            innerTextField()
//                        }
//                    }
//                )
            }
        }

        Button(
            onClick = {
                coroutineScope.launch {
                    isVisible = false
                    delay(300)

                  viewModel.getCompliment(displayedText)
                    isVisible = true // Показать новый текст
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter) // Привязываем кнопку к низу
                .padding(bottom = 40.dp, start = 20.dp, end = 20.dp)
                .height(70.dp) // Высота кнопки
                .fillMaxWidth(), // Занимает всю ширину
            shape = RoundedCornerShape(50.dp) // Делаем кнопку округлой
        ) {
            Text(
                text = "Получить комплимент",
                style = MaterialTheme.typography.bodyLarge, // Можно использовать шрифт из темы
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
    }
}
