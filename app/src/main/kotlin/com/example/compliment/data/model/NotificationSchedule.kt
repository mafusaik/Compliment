package com.example.compliment.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.DayOfWeek

@Entity(tableName = "notification_schedule")
data class NotificationSchedule(
    @PrimaryKey
    val time: String,
    val daysOfWeek: Set<DayOfWeek>,
    val isActive: Boolean
)