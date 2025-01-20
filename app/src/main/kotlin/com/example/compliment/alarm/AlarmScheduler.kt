package com.example.compliment.alarm

import androidx.compose.runtime.Immutable
import com.example.compliment.data.model.NotificationSchedule
import kotlinx.collections.immutable.ImmutableSet
import java.time.DayOfWeek

@Immutable
interface AlarmScheduler {
    fun createSchedule(time: String, daysOfWeek: ImmutableSet<DayOfWeek>)
    fun createRepeatSchedule(time: String, daysOfWeek: ImmutableSet<DayOfWeek>)
    fun cancel(time: String, daysOfWeek: ImmutableSet<DayOfWeek>)
}