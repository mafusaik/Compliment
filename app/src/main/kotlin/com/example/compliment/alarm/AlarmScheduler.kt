package com.example.compliment.alarm

import com.example.compliment.data.model.NotificationSchedule
import java.time.DayOfWeek

interface AlarmScheduler {
    fun createSchedule(schedule: NotificationSchedule)
    fun createRepeatSchedule(time: String, daysOfWeek: Set<DayOfWeek>)
    fun cancel(schedule: NotificationSchedule)
}