package com.example.compliment.ui.notifications.dialog

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.toImmutableSet
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun DaysOfWeekSelector(
    initialSelectedDays: Set<DayOfWeek>,
    shakeAnimation: Animatable<Float, AnimationVector1D>? = null,
    onDaysChanged: (ImmutableSet<DayOfWeek>) -> Unit,
) {
    var selectedDays by remember { mutableStateOf(initialSelectedDays) }
    val daysOfWeek = DayOfWeek.entries.toTypedArray()
    val dayOrder = listOf(
        DayOfWeek.MONDAY,
        DayOfWeek.TUESDAY,
        DayOfWeek.WEDNESDAY,
        DayOfWeek.THURSDAY,
        DayOfWeek.FRIDAY,
        DayOfWeek.SATURDAY,
        DayOfWeek.SUNDAY
    )

    Row(
        modifier = Modifier
            .wrapContentWidth()
            .height(48.dp)
            .offset { IntOffset(x = shakeAnimation?.value?.toInt() ?: 0, y = 0) },
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        daysOfWeek.forEach { day ->
            DayOfWeekCheckbox(
                day = day.getDisplayName(TextStyle.SHORT, Locale.getDefault()).take(2).uppercase(),
                isSelected = selectedDays.contains(day),
                onCheckedChange = { isSelected ->
                    selectedDays = if (isSelected) {
                        selectedDays + day
                    } else {
                        selectedDays - day
                    }
                    onDaysChanged(selectedDays.sortedBy { dayOrder.indexOf(it) }.toImmutableSet())
                }
            )
        }
    }
}

@Composable
fun DayOfWeekCheckbox(
    day: String,
    isSelected: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val backgroundColor =
    if (isSelected) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.secondary

    val textColor =
        if (isSelected) MaterialTheme.colorScheme.onPrimary
        else MaterialTheme.colorScheme.onSecondary

    val borderColor =
        if (isSelected) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.onSecondary

    Box(
        modifier = Modifier
            .padding(2.dp)
            .size(width = 36.dp, height = 30.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .clickable { onCheckedChange(!isSelected) },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day,
            color = textColor,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}