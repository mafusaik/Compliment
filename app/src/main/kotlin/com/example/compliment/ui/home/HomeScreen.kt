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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(initialCompliment: String) {
    val viewModel = koinViewModel<HomeViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    HomeScreen(
        state,
        initialCompliment,
        onComplimentClicked = { viewModel.onComplimentClicked() },
        onGetCompliment = { viewModel.onGetComplimentClicked() }
    )
}

@Composable
private fun HomeScreen(
    state: HomeState,
    initialCompliment: String,
    onGetCompliment: () -> Unit,
    onComplimentClicked: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedVisibility(
                visible = state.isTextVisible,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .padding(horizontal = 20.dp)
                        .clickable { onComplimentClicked() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initialCompliment.ifEmpty { state.compliment },
                        style = LocalTextStyle.current.copy(
                            fontSize = 26.sp,
                            color = Color.Black,
                            textAlign = TextAlign.Center
                        ),
                        textAlign = TextAlign.Center
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
            .height(70.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(50.dp)
    ) {
        Text(
            text = "Получить комплимент",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White,
            textAlign = TextAlign.Center
        )
    }
}
