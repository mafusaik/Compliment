package com.example.compliment.ui.notifications.dialog

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.DayOfWeek

@Composable
fun DaysOfWeekSelector(
    initialSelectedDays: Set<DayOfWeek>,
    shakeAnimation: Animatable<Float, AnimationVector1D>? = null,
    onDaysChanged: (Set<DayOfWeek>) -> Unit,
) {
    var selectedDays by remember { mutableStateOf(initialSelectedDays) }
    val daysOfWeek = DayOfWeek.entries.toTypedArray()

    Row(
        modifier = Modifier.fillMaxWidth()
            .offset(x = shakeAnimation?.value?.dp ?: 0.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        daysOfWeek.forEach { day ->
            Column(horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)) {
                Checkbox(
                    checked = selectedDays.contains(day),
                    onCheckedChange = {
                        selectedDays = if (selectedDays.contains(day)) {
                            selectedDays - day
                        } else {
                            selectedDays + day
                        }
                        onDaysChanged(selectedDays)
                    }
                )
                Text(text = day.name.take(3))
            }
        }
    }
}