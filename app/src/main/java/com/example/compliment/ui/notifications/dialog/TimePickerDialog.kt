package com.example.compliment.ui.notifications.dialog

import android.widget.Toast
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.compliment.R
import com.example.compliment.data.model.NotificationSchedule
import com.example.compliment.extensions.floorMod
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.util.Calendar

@Composable
fun ScheduleDialog(
    existingData: NotificationSchedule? = null,
    currentSelectedDays: Set<DayOfWeek>,
    onTimeSelected: (hour: Int, minute: Int, Set<DayOfWeek>) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()


    val (hour, minute) = existingData?.time?.let {time->
        time.split(":").map { it.toInt() }
    } ?: listOf(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE))

    val days = existingData?.daysOfWeek ?: currentSelectedDays

    var selectedHour by remember { mutableIntStateOf(hour) }
    var selectedMinute by remember { mutableIntStateOf(minute) }
    var selectedDays by remember { mutableStateOf(days) }
    val shakeAnimation = remember { Animatable(0f) }
    var isShaking by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = if (existingData == null) "New Schedule" else "Edit Schedule",
              //  text = stringResource(R.string.title_select_time),
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Включаем колесики выбора для часов и минут
                WheelTimePicker(
                    initialHour = selectedHour,
                    initialMinute = selectedMinute,
                    onTimeSelected = { hour, minute ->
                        selectedHour = hour
                        selectedMinute = minute
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(stringResource(R.string.title_select_days), fontSize = 18.sp)
                DaysOfWeekSelector(
                    initialSelectedDays = selectedDays,
                    shakeAnimation = shakeAnimation
                ) { updatedDays ->
                    if (updatedDays.isNotEmpty()) selectedDays = updatedDays
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (selectedDays.isEmpty()) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.message_no_selected_day),
                        Toast.LENGTH_SHORT
                    ).show()
                    isShaking = true
                } else {
                    onTimeSelected(selectedHour, selectedMinute, selectedDays)
                }
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }

    )

    LaunchedEffect(isShaking) {
        if (isShaking) {
            repeat(3) {
                shakeAnimation.animateTo(
                    targetValue = -5f,
                    animationSpec = tween(durationMillis = 10, easing = FastOutSlowInEasing)
                )
                shakeAnimation.animateTo(
                    targetValue = 5f,
                    animationSpec = tween(durationMillis = 10, easing = FastOutSlowInEasing)
                )
                shakeAnimation.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(durationMillis = 10, easing = FastOutSlowInEasing)
                )
            }
            isShaking = false
        }
    }
}


@Composable
fun WheelTimePicker(
    initialHour: Int = 0,
    initialMinute: Int = 0,
    onTimeSelected: (hour: Int, minute: Int) -> Unit
) {
    var selectedHour by remember { mutableIntStateOf(initialHour) }
    var selectedMinute by remember { mutableIntStateOf(initialMinute) }
    val debounceScope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            InfiniteWheel(
                range = 0..23,
                initialTime = selectedHour,
                onItemSelected = { hour ->
                    debounceScope.launch {
                        delay(300)
                        selectedHour = hour + 1
                        onTimeSelected(selectedHour, selectedMinute)
                    }
                }
            )

            Text(
                ":", fontSize = 32.sp,
                modifier = Modifier
                    .padding(horizontal = 0.dp)
                    .width(50.dp),
                textAlign = TextAlign.Center
            )

            InfiniteWheel(
                range = 0..59,
                initialTime = selectedMinute,
                onItemSelected = { minute ->
                    debounceScope.launch {
                        delay(300)
                        selectedMinute = minute + 1
                        onTimeSelected(selectedHour, selectedMinute)
                    }
                }
            )
        }

        HorizontalDivider(
            color = Color.Gray,
            thickness = 1.dp,
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(0.9f)
                .offset(y = (-20).dp)
        )
        HorizontalDivider(
            color = Color.Gray,
            thickness = 1.dp,
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(0.9f)
                .offset(y = 16.dp)
        )
    }
}

@Composable
fun InfiniteWheel(
    range: IntRange,
    initialTime: Int,
    onItemSelected: (Int) -> Unit
) {
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = Int.MAX_VALUE / 2 + initialTime - 2)
    val middleItemIndex = Int.MAX_VALUE / 2
    val visibleItems = range.count()

    LazyColumn(
        state = listState,
        modifier = Modifier
            .height(160.dp)
            .width(50.dp)
        ,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 32.dp)
    ) {
        items(Int.MAX_VALUE) { index ->
            val actualIndex = (index - middleItemIndex).floorMod(visibleItems)
            val value = range.elementAt(actualIndex)
            val isSelected = index == listState.firstVisibleItemIndex + 1

            Text(
                text = value.toString(),
                fontSize = if (isSelected) 26.sp else 20.sp,
                color = if (isSelected) Color.Black else Color.Gray,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 0.dp),
                textAlign = TextAlign.Center
            )

        }
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.isScrollInProgress }.collect { isScrolling ->
            if (!isScrolling) {
                val centerIndex = listState.firstVisibleItemIndex + 1
                val actualValue = (centerIndex - middleItemIndex).floorMod(visibleItems)
                onItemSelected(range.elementAt(actualValue))
                val offset = listState.layoutInfo.visibleItemsInfo.firstOrNull()?.offset ?: 0
                if (offset != 0) {
                    listState.animateScrollToItem(centerIndex)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewTimePickerDialog() {
    ScheduleDialog(
        null,
        emptySet(),
        onTimeSelected = { _, _, _ ->
        },
        onDismiss = {
        })
}