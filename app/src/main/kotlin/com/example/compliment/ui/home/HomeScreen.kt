package com.example.compliment.ui.home

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
import androidx.compose.material3.Button
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun HomeScreen(initialCompliment: String) {
    val viewModel = koinViewModel<HomeViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    HomeScreen(
        state,
        onComplimentClicked = { viewModel.onComplimentClicked() },
        onGetCompliment = { viewModel.onGetComplimentClicked() }
    )
}

@Composable
private fun HomeScreen(
    state: HomeState,
    onGetCompliment: () -> Unit,
    onComplimentClicked: () -> Unit
) {
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
                visible = state.isTextVisible,
                enter = fadeIn(), // Анимация появления
                exit = fadeOut() // Анимация исчезновения
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .padding(horizontal = 20.dp)
                        .clickable { onComplimentClicked() },
                    contentAlignment = Alignment.Center // Центрируем текст
                ) {
                    Text(
                        text = state.compliment,
                        style = LocalTextStyle.current.copy(
                            fontSize = 26.sp,
                            color = Color.Black,
                            textAlign = TextAlign.Center
                        ),
                        textAlign = TextAlign.Center // Центрируем текст
                    )
                }
            }
        }

        GetCompliment(
            onClick = onGetCompliment,
            Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun GetCompliment(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .then(modifier)
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
