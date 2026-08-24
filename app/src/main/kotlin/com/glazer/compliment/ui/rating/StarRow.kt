package com.glazer.compliment.ui.rating

import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.glazer.compliment.ui.theme.StarFilled

@Composable
fun StarRow() {
    val infiniteTransition = rememberInfiniteTransition(label = "stars")

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(5) { index ->
            val delay = index * 120
            val starScale by infiniteTransition.animateFloat(
                initialValue = 1f,
                targetValue = 1.2f,
                animationSpec = infiniteRepeatable(
                    animation = tween(600, delayMillis = delay, easing = EaseInOutSine),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "star_$index"
            )
            Text(
                text = "★",
                color = StarFilled,
                fontSize = 28.sp,
                modifier = Modifier.scale(starScale)
            )
        }
    }
}