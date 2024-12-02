package com.example.compliment.ui.notifications.dialog

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.compliment.R
import com.example.compliment.data.model.NotificationSchedule
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

//    val recompositionCount = remember { mutableStateOf(0) }
//    recompositionCount.value++
//
//    Log.d("RecompositionTracker", "ScheduleDialog count: ${recompositionCount.value}")


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
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                WheelTimePicker(
                    initialHour = hour,
                    initialMinute = minute,
                    onHourSelected = { hour ->
                        selectedHour = hour
                    },
                    onMinuteSelected = {minute ->
                        selectedMinute = minute
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(stringResource(R.string.title_select_days), fontSize = 18.sp)
                DaysOfWeekSelector(
                    initialSelectedDays = selectedDays,
                    shakeAnimation = shakeAnimation
                ) { updatedDays ->
                    selectedDays = updatedDays
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