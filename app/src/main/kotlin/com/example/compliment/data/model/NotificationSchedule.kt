package com.example.compliment.data.model

import androidx.compose.runtime.Immutable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.collections.immutable.ImmutableSet
import java.time.DayOfWeek

@Immutable
@Entity(tableName = "notification_schedule")
data class NotificationSchedule(
    @PrimaryKey
    val time: String,
    val daysOfWeek: ImmutableSet<DayOfWeek>,
    val isActive: Boolean
)