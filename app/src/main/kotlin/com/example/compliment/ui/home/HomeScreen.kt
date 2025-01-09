package com.example.compliment.ui.home

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.compliment.R
import com.example.compliment.extensions.showToast
import com.example.compliment.models.HomeState
import com.example.compliment.ui.theme.WhiteBackground
import com.example.compliment.ui.theme.customFontFamilyCursive
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(initialCompliment: String, innerPadding: PaddingValues) {
    val context = LocalContext.current
    val viewModel = koinViewModel<HomeViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    HomeScreen(
        innerPadding,
        state,
        initialCompliment,
        onComplimentClicked = {
            context.showToast(context.getString(R.string.compliment_copied))
            viewModel.onComplimentClicked()
        },
        onGetCompliment = { viewModel.onGetComplimentClicked() }
    )
}

@Composable
private fun HomeScreen(
    innerPadding: PaddingValues,
    state: HomeState,
    initialCompliment: String,
    onGetCompliment: () -> Unit,
    onComplimentClicked: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = stringResource(R.string.title_compliments),
            fontSize = 30.sp,
            color = MaterialTheme.colorScheme.onSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 94.dp)
                .background(MaterialTheme.colorScheme.background, RoundedCornerShape(20.dp)),
        ) {
                Text(
                    text = stringResource(R.string.title_nice_day),
                    fontSize = 19.sp,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                )

            AnimatedVisibility(
                visible = state.isTextVisible,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.align(Alignment.Center)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .clickable { onComplimentClicked() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initialCompliment.ifEmpty { state.compliment },
                        style = LocalTextStyle.current.copy(
                            fontSize = 38.sp,
                            lineHeight = 32.sp,
                            fontFamily = customFontFamilyCursive,
                            color = MaterialTheme.colorScheme.onSecondary,
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
        shape = RoundedCornerShape(16.dp),
        colors = ButtonColors(
           MaterialTheme.colorScheme.primary,
           MaterialTheme.colorScheme.primary,
           MaterialTheme.colorScheme.primary,
           MaterialTheme.colorScheme.primary
        ),
        modifier = Modifier
            .then(modifier)
            .padding(bottom = 100.dp, start = 30.dp, end = 30.dp)
            .height(70.dp)
            .fillMaxWidth(),
    ) {
        Text(
            text = stringResource(R.string.get_compliment),
            fontSize = 24.sp,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimary,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHomeScreen() {
    HomeScreen("hello", PaddingValues())
}
