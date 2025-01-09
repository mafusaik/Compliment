package com.example.compliment.ui.notifications.dialog

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.compliment.R
import com.example.compliment.data.model.NotificationSchedule
import com.example.compliment.ui.theme.Black
import com.example.compliment.ui.theme.GreyLight
import com.example.compliment.ui.theme.WhiteBackground
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

    Log.d("RecompositionTracker", "ScheduleDialog recomposition")

    val (hour, minute) = existingData?.time?.let { time ->
        time.split(":").map { it.toInt() }
    } ?: listOf(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE))

    val days = existingData?.daysOfWeek ?: currentSelectedDays

    var selectedHour by remember { mutableIntStateOf(hour) }
    var selectedMinute by remember { mutableIntStateOf(minute) }
    var selectedDays by remember { mutableStateOf(days) }
    val shakeAnimation = remember { Animatable(0f) }
    var isShaking by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss
    ) {
        Surface(
            modifier = Modifier
                .width(340.dp)
                .wrapContentHeight()
                .padding(0.dp),
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier.padding(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CustomTitle(
                    existingData,
                    onConfirm = {
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
                    },
                    onDismiss = {
                        onDismiss()
                    }
                )

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.surface,
                    thickness = 1.dp,
                    modifier = Modifier.fillMaxWidth(0.95f)
                )

                WheelTimePicker(
                    initialHour = hour,
                    initialMinute = minute,
                    onHourSelected = { hour ->
                        selectedHour = hour
                    },
                    onMinuteSelected = { minute ->
                        selectedMinute = minute
                    }
                )

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.surface,
                    thickness = 1.dp,
                    modifier = Modifier.fillMaxWidth(0.95f)
                )

                DaysOfWeekSelector(
                    initialSelectedDays = selectedDays,
                    shakeAnimation = shakeAnimation
                ) { updatedDays ->
                    selectedDays = updatedDays
                }
            }
        }
    }

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
fun CustomTitle(
    existingData: NotificationSchedule? = null,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onDismiss) {
            Icon(
                painter = painterResource(id = R.drawable.icon_dialog_cross),
                contentDescription = stringResource(R.string.cancel),
                tint = MaterialTheme.colorScheme.onSecondary,

            )
        }

        Text(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp),
            text = if (existingData == null) stringResource(R.string.new_schedule)
            else stringResource(R.string.edit_schedule),
            textAlign = TextAlign.Center,
            fontSize = 22.sp,
            color = MaterialTheme.colorScheme.onSecondary
        )

        IconButton(onClick = onConfirm) {
            Icon(
                painter = painterResource(id = R.drawable.icon_dialog_tick),
                contentDescription = stringResource(R.string.confirm),
                tint = MaterialTheme.colorScheme.onSecondary
            )
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